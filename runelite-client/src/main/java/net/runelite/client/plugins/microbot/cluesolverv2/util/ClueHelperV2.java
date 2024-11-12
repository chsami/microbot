package net.runelite.client.plugins.microbot.cluesolverv2.util;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.cluescrolls.clues.ClueScroll;
import net.runelite.client.plugins.cluescrolls.clues.item.*;
import net.runelite.client.plugins.microbot.Microbot;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class ClueHelperV2 {

    private final Client client;
    private final ConcurrentHashMap<Class<?>, Field> fieldCache = new ConcurrentHashMap<>();

    // Predefined Item Requirements
    private static final ItemRequirement HAS_SPADE = new AnyRequirementCollection(
            "Spade",
            new SingleItemRequirement(ItemID.SPADE),
            new SingleItemRequirement(ItemID.EASTFLOOR_SPADE)
    );

    private static final ItemRequirement HAS_LIGHT = new AnyRequirementCollection(
            "Light Source",
            new SingleItemRequirement(ItemID.LIT_TORCH),
            new SingleItemRequirement(ItemID.LIT_CANDLE),
            new SingleItemRequirement(ItemID.CANDLE_LANTERN_4531),
            new SingleItemRequirement(ItemID.OIL_LAMP_4524),
            new SingleItemRequirement(ItemID.BULLSEYE_LANTERN_4550)
    );

    @Inject
    public ClueHelperV2(Client client) {
        this.client = client;
        if (this.client == null) {
            log.error("Client instance was not injected correctly!");
        } else {
            log.debug("Client instance successfully injected.");
        }
    }

    /**
     * Retrieves the clue location from a ClueScroll.
     *
     * @param clue The ClueScroll instance.
     * @return The WorldPoint location of the clue or null if unavailable.
     */
    public WorldPoint getClueLocation(ClueScroll clue) {
        WorldPoint location = getFieldValue(clue, "location", WorldPoint.class, null);
        if (location == null) {
            log.warn("Clue location is null for clue: {}", clue.getClass().getSimpleName());
        } else {
            log.debug("Clue location retrieved: {}", location);
        }
        return location;
    }

    /**
     * Determines the required items for a given clue.
     *
     * @param clue The ClueScroll instance.
     * @return A list of ItemRequirements necessary to solve the clue.
     */
    public List<ItemRequirement> determineRequiredItems(ClueScroll clue) {
        List<ItemRequirement> requiredItems = new ArrayList<>();

        if (clue.isRequiresSpade()) {
            log.info("Adding spade requirement.");
            requiredItems.add(HAS_SPADE);
        }

        if (clue.isRequiresLight()) {
            log.info("Adding light source requirement.");
            requiredItems.add(HAS_LIGHT);
        }

        try {
            Field itemRequirementsField = getFieldFromClassHierarchy(clue.getClass(), "itemRequirements");

            if (itemRequirementsField != null) {
                itemRequirementsField.setAccessible(true);
                ItemRequirement[] clueItemRequirements = (ItemRequirement[]) itemRequirementsField.get(clue);

                if (clueItemRequirements != null) {
                    log.info("Added item requirements via direct field access. Number of items: {}", clueItemRequirements.length);

                    for (ItemRequirement req : clueItemRequirements) {
                        if (req != null) {
                            requiredItems.add(req);
                            log.info("Item requirement (toString): {}", req);
                            handleItemRequirement(req);
                        } else {
                            log.warn("Encountered a null item requirement in itemRequirements array.");
                        }
                    }
                } else {
                    log.warn("The itemRequirements field is null.");
                }
            } else {
                log.info("The clue does not have an 'itemRequirements' field.");
            }

            log.info("About to process required items.");
            requiredItems.forEach(requirement -> {
                if (client == null) {
                    log.error("Client is null when getting collective name for requirement: {}", requirement);
                } else {
                    try {
                        Microbot.getClientThread().invoke(() -> {
                            String collectiveName = requirement.getCollectiveName(client);
                            log.info("Final Required item: {}", collectiveName);
                        });
                    } catch (Throwable t) {
                        log.error("Error getting collective name for requirement: {}", requirement, t);
                    }
                }
            });
            log.info("Finished processing required items.");

        } catch (Throwable t) {
            log.error("Error determining required items", t);
        }

        return requiredItems;
    }


    private void handleItemRequirement(ItemRequirement req) {
        if (req instanceof SingleItemRequirement) {
            handleSingleItemRequirement((SingleItemRequirement) req);
        } else if (req instanceof AnyRequirementCollection) {
            handleAnyRequirementCollection((AnyRequirementCollection) req);
        } else if (req instanceof MultipleOfItemRequirement) {
            handleMultipleOfItemRequirement((MultipleOfItemRequirement) req);
        } else if (req instanceof RangeItemRequirement) {
            handleRangeItemRequirement((RangeItemRequirement) req);
        } else if (req instanceof SlotLimitationRequirement) {
            handleSlotLimitationRequirement((SlotLimitationRequirement) req);
        } else if (req instanceof AllRequirementsCollection) {
            handleAllRequirementsCollection((AllRequirementsCollection) req);
        } else {
            log.warn("Unknown ItemRequirement type: {}", req.getClass().getSimpleName());
        }
    }

    private void handleSingleItemRequirement(SingleItemRequirement singleReq) {
        try {
            log.info("Attempting to access itemId field for SingleItemRequirement.");
            Field itemIdField = SingleItemRequirement.class.getDeclaredField("itemId");
            itemIdField.setAccessible(true);
            int itemId = itemIdField.getInt(singleReq);

            log.info("Successfully accessed itemId. SingleItemRequirement - Item ID: {}", itemId);
        } catch (NoSuchFieldException e) {
            log.error("No such field 'itemId' found in SingleItemRequirement. Check for typos or incorrect field name.", e);
        } catch (IllegalAccessException e) {
            log.error("Unable to access itemId field in SingleItemRequirement due to illegal access.", e);
        }
    }

    private void handleAnyRequirementCollection(AnyRequirementCollection anyReq) {
        log.info("AnyRequirementCollection - Name: {}", anyReq.getCollectiveName(client));

        try {
            Field requirementsField = AnyRequirementCollection.class.getDeclaredField("requirements");
            requirementsField.setAccessible(true);
            ItemRequirement[] requirements = (ItemRequirement[]) requirementsField.get(anyReq);

            if (requirements != null) {
                log.info("AnyRequirementCollection - Number of contained requirements: {}", requirements.length);
                for (ItemRequirement req : requirements) {
                    if (req != null) {
                        handleItemRequirement(req);
                    } else {
                        log.warn("Encountered a null item requirement in AnyRequirementCollection.");
                    }
                }
            } else {
                log.warn("AnyRequirementCollection has no contained requirements.");
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("Unable to access requirements field in AnyRequirementCollection", e);
        }
    }

    private void handleMultipleOfItemRequirement(MultipleOfItemRequirement multipleReq) {
        try {
            log.info("Attempting to access fields in MultipleOfItemRequirement.");
            Field itemIdField = MultipleOfItemRequirement.class.getDeclaredField("itemId");
            itemIdField.setAccessible(true);
            int itemId = itemIdField.getInt(multipleReq);

            Field quantityField = MultipleOfItemRequirement.class.getDeclaredField("quantity");
            quantityField.setAccessible(true);
            int quantity = quantityField.getInt(multipleReq);

            log.info("Successfully accessed fields. MultipleOfItemRequirement - Item ID: {}, Required Quantity: {}", itemId, quantity);
        } catch (NoSuchFieldException e) {
            log.error("One of the fields (itemId or quantity) not found in MultipleOfItemRequirement. Check for typos or incorrect field name.", e);
        } catch (IllegalAccessException e) {
            log.error("Unable to access fields in MultipleOfItemRequirement due to illegal access.", e);
        }
    }

    private void handleRangeItemRequirement(RangeItemRequirement rangeReq) {
        try {
            Field startItemIdField = RangeItemRequirement.class.getDeclaredField("startItemId");
            Field endItemIdField = RangeItemRequirement.class.getDeclaredField("endItemId");

            startItemIdField.setAccessible(true);
            endItemIdField.setAccessible(true);

            int startItemId = startItemIdField.getInt(rangeReq);
            int endItemId = endItemIdField.getInt(rangeReq);

            log.info("RangeItemRequirement - Start Item ID: {}, End Item ID: {}", startItemId, endItemId);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("Unable to access fields in RangeItemRequirement", e);
        }
    }

    private void handleAllRequirementsCollection(AllRequirementsCollection allReq) {
        log.info("AllRequirementsCollection - Name: {}", allReq.getCollectiveName(client));
        try {
            Field requirementsField = AllRequirementsCollection.class.getDeclaredField("requirements");
            requirementsField.setAccessible(true);
            ItemRequirement[] requirements = (ItemRequirement[]) requirementsField.get(allReq);

            if (requirements != null) {
                log.info("AllRequirementsCollection - Number of contained requirements: {}", requirements.length);
                for (ItemRequirement req : requirements) {
                    if (req != null) {
                        handleItemRequirement(req);
                    }
                }
            } else {
                log.warn("AllRequirementsCollection has no contained requirements.");
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("Unable to access requirements field in AllRequirementsCollection", e);
        }
    }

    private void handleSlotLimitationRequirement(SlotLimitationRequirement slotReq) {
        log.info("SlotLimitationRequirement - Description: {}", slotReq.getCollectiveName(client));
        try {
            Field slotsField = SlotLimitationRequirement.class.getDeclaredField("slots");
            slotsField.setAccessible(true);
            EquipmentInventorySlot[] slots = (EquipmentInventorySlot[]) slotsField.get(slotReq);

            StringBuilder slotNames = new StringBuilder();
            for (EquipmentInventorySlot slot : slots) {
                slotNames.append(slot.name()).append(" ");
            }
            log.info("SlotLimitationRequirement - Slots: {}", slotNames.toString().trim());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("Unable to access slots field in SlotLimitationRequirement", e);
        }
    }

    /**
     * Retrieves a list of missing items based on the current inventory and equipment.
     *
     * @param requirements The list of required ItemRequirements.
     * @return A list of ItemRequirements that are missing from the player's inventory and equipment.
     */
    public List<ItemRequirement> getMissingItems(List<ItemRequirement> requirements) {
        log.info("Checking for missing items.");

        Item[] inventoryItemsArray = client.getItemContainer(InventoryID.INVENTORY) != null
                ? Objects.requireNonNull(client.getItemContainer(InventoryID.INVENTORY)).getItems()
                : new Item[0];
        Item[] equippedItemsArray = client.getItemContainer(InventoryID.EQUIPMENT) != null
                ? Objects.requireNonNull(client.getItemContainer(InventoryID.EQUIPMENT)).getItems()
                : new Item[0];

        List<Item> allItems = new ArrayList<>();
        Collections.addAll(allItems, inventoryItemsArray);
        Collections.addAll(allItems, equippedItemsArray);

        return requirements.stream()
                .filter(req -> !req.fulfilledBy(allItems.toArray(new Item[0])))
                .collect(Collectors.toList());
    }

    /**
     * Generic method to retrieve the value of a specified field with an optional default value.
     * Adds detailed logging for tracing.
     *
     * @param obj          The object instance containing the field.
     * @param fieldName    The name of the field to retrieve.
     * @param fieldType    The Class type of the field.
     * @param defaultValue The default value to return if retrieval fails.
     * @param <T>          The type parameter.
     * @return The value of the field or the default value.
     */
    @SuppressWarnings("unchecked")
    private <T> T getFieldValue(Object obj, String fieldName, Class<T> fieldType, T defaultValue) {
        try {
            Field field = getCachedField(obj.getClass(), fieldName);
            if (field != null) {
                Object value = field.get(obj);
                if (fieldType.isInstance(value)) {
                    log.debug("Retrieved field '{}' with value: {}", fieldName, value);
                    return (T) value;
                } else {
                    log.warn("Field '{}' in '{}' is not of type '{}'. Actual type: '{}'", fieldName, obj.getClass().getSimpleName(), fieldType.getSimpleName(), value != null ? value.getClass().getSimpleName() : "null");
                }
            } else {
                log.warn("Field '{}' not found in class '{}'", fieldName, obj.getClass().getSimpleName());
            }
        } catch (IllegalAccessException e) {
            log.error("Error accessing field '{}' in '{}'", fieldName, obj.getClass().getSimpleName(), e);
        } catch (ClassCastException e) {
            log.error("Type mismatch when casting field '{}' in '{}'", fieldName, obj.getClass().getSimpleName(), e);
        }
        return defaultValue;
    }

    /**
     * Caches and retrieves a Field object from a class.
     *
     * @param clazz     The Class to search for the field.
     * @param fieldName The name of the field.
     * @return The Field object or null if not found.
     */
    private Field getCachedField(Class<?> clazz, String fieldName) {
        return fieldCache.computeIfAbsent(clazz, c -> getFieldFromClassHierarchy(c, fieldName));
    }

    /**
     * Traverses the class hierarchy to find a declared field.
     *
     * @param clazz     The Class to start searching from.
     * @param fieldName The name of the field.
     * @return The Field object or null if not found.
     */
    private Field getFieldFromClassHierarchy(Class<?> clazz, String fieldName) {
        Class<?> currentClass = clazz;
        while (currentClass != null) {
            try {
                Field field = currentClass.getDeclaredField(fieldName);
                field.setAccessible(true);
                log.debug("Cached field '{}' from class '{}'", fieldName, currentClass.getSimpleName());
                return field;
            } catch (NoSuchFieldException e) {
                currentClass = currentClass.getSuperclass();
            }
        }
        log.warn("Field '{}' not found in class hierarchy of '{}'", fieldName, clazz.getSimpleName());
        return null;
    }
}
