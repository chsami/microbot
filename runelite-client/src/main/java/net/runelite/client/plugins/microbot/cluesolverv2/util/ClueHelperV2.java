package net.runelite.client.plugins.microbot.cluesolverv2.util;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.client.plugins.cluescrolls.clues.ClueScroll;
import net.runelite.client.plugins.cluescrolls.clues.item.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class ClueHelperV2 {

    @Inject
    private Client client;

    private final ConcurrentHashMap<Class<?>, Field> fieldCache = new ConcurrentHashMap<>();

    private static final ItemRequirement HAS_SPADE = new AnyRequirementCollection(
            "Spade", new SingleItemRequirement(ItemID.SPADE),
            new SingleItemRequirement(ItemID.EASTFLOOR_SPADE));
    private static final ItemRequirement HAS_LIGHT = new AnyRequirementCollection(
            "Light Source", new SingleItemRequirement(ItemID.LIT_TORCH),
            new SingleItemRequirement(ItemID.LIT_CANDLE),
            new SingleItemRequirement(ItemID.CANDLE_LANTERN_4531),
            new SingleItemRequirement(ItemID.OIL_LAMP_4524),
            new SingleItemRequirement(ItemID.BULLSEYE_LANTERN_4550));

    // Retrieves and caches fields from the class hierarchy
    private Field getCachedField(Class<?> clazz, String fieldName) {
        return fieldCache.computeIfAbsent(clazz, c -> getFieldFromClassHierarchy(c, fieldName));
    }

    // A generic helper method to retrieve field values using reflection
    private <T> T getFieldValue(Object obj, String fieldName, Class<T> fieldType) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return fieldType.cast(field.get(obj));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("Error accessing field '{}' in '{}'", fieldName, obj.getClass().getSimpleName(), e);
        }
        return null;
    }

    private <T> T getFieldValue(Object obj, String fieldName, Class<T> fieldType, T defaultValue) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            T value = fieldType.cast(field.get(obj));
            return value != null ? value : defaultValue;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("Error accessing field '{}' in '{}'", fieldName, obj.getClass().getSimpleName(), e);
        }
        return defaultValue;
    }



    private Field getFieldFromClassHierarchy(Class<?> clazz, String fieldName) {
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }

    // Retrieve a field value using reflection
    public Object getFieldValue(ClueScroll clue, String fieldName) {
        Field field = getCachedField(clue.getClass(), fieldName);
        if (field != null) {
            try {
                return field.get(clue);
            } catch (IllegalAccessException e) {
                log.error("Error accessing field {} on clue {}", fieldName, clue.getClass().getSimpleName(), e);
            }
        }
        return null;
    }

    // Determine required items for a given clue, including specific logic for spades and light sources
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
            Object itemRequirements = getFieldValue(clue, "itemRequirements");
            if (itemRequirements != null) {
                log.info("Unpacking item requirements for clue type: {}", clue.getClass().getSimpleName());
                unpackItemRequirements(itemRequirements, requiredItems);
            } else {
                log.info("No 'itemRequirements' field found for clue type: {}", clue.getClass().getSimpleName());
            }
        } catch (Exception e) {
            log.error("Error determining required items for clue: {}", clue.getClass().getSimpleName(), e);
        }

        return requiredItems;
    }

    // Unpack item requirements, handling various collection types
    private void unpackItemRequirements(Object requirement, List<ItemRequirement> requiredItems) {
        if (requirement instanceof ItemRequirement) {
            requiredItems.add((ItemRequirement) requirement);
            handleItemRequirement((ItemRequirement) requirement);
        } else if (requirement instanceof ItemRequirement[]) {
            for (ItemRequirement req : (ItemRequirement[]) requirement) {
                unpackItemRequirements(req, requiredItems);
            }
        } else if (requirement instanceof List) {
            for (Object req : (List<?>) requirement) {
                unpackItemRequirements(req, requiredItems);
            }
        } else {
            log.warn("Unexpected item requirement type: {}", requirement.getClass().getSimpleName());
        }
    }

    // Check if required items are available in inventory or equipment
    public List<ItemRequirement> getMissingItems(List<ItemRequirement> requirements) {
        ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
        ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
        log.info("Checking for missing items in inventory and equipment.");
        log.info("Inventory: {}", inventory);
        log.info("Equipment: {}", equipment);

        List<Item> allItems = new ArrayList<>();
        if (inventory != null) {
            allItems.addAll(List.of(inventory.getItems()));
            log.info("Inventory items: {}", allItems);
        }
        if (equipment != null) {
            allItems.addAll(List.of(equipment.getItems()));
            log.info("Equipment items: {}", allItems);
        }

        return requirements.stream()
                .filter(req -> !req.fulfilledBy(allItems.toArray(new Item[0])))
                .collect(Collectors.toList());
    }

    // Handle different item requirement types
    private void handleItemRequirement(ItemRequirement req) {
        if (req instanceof SingleItemRequirement) {
            handleSingleItemRequirement((SingleItemRequirement) req);
        } else if (req instanceof AnyRequirementCollection) {
            handleAnyRequirementCollection((AnyRequirementCollection) req);
        } else if (req instanceof MultipleOfItemRequirement) {
            handleMultipleOfItemRequirement((MultipleOfItemRequirement) req);
        } else if (req instanceof RangeItemRequirement) {
            handleRangeItemRequirement((RangeItemRequirement) req);
        } else if (req instanceof AllRequirementsCollection) {
            handleAllRequirementsCollection((AllRequirementsCollection) req);
        } else if (req instanceof SlotLimitationRequirement) {
            handleSlotLimitationRequirement((SlotLimitationRequirement) req);
        } else {
            log.warn("Unknown ItemRequirement type: {}", req.getClass().getSimpleName());
        }
    }

    private void handleSingleItemRequirement(SingleItemRequirement singleReq) {
        int itemId = getFieldValue(singleReq, "itemId", Integer.class, -1);
        if (itemId != -1) {
            log.info("SingleItemRequirement - Item ID: {}", itemId);
        } else {
            log.warn("Failed to retrieve item ID for SingleItemRequirement.");
        }
    }

    private void handleAnyRequirementCollection(AnyRequirementCollection anyReq) {
        log.info("AnyRequirementCollection - Name: {}", anyReq.getCollectiveName(client));
        ItemRequirement[] requirements = getFieldValue(anyReq, "requirements", ItemRequirement[].class);
        if (requirements != null) {
            for (ItemRequirement req : requirements) {
                handleItemRequirement(req);
            }
        }
    }

    private void handleMultipleOfItemRequirement(MultipleOfItemRequirement multipleReq) {
        int itemId = getFieldValue(multipleReq, "itemId", Integer.class, -1);
        int quantity = getFieldValue(multipleReq, "quantity", Integer.class, -1);

        if (itemId != -1 && quantity != -1) {
            log.info("MultipleOfItemRequirement - Item ID: {}, Quantity: {}", itemId, quantity);
        } else {
            log.warn("Failed to retrieve item ID or quantity for MultipleOfItemRequirement.");
        }
    }

    private void handleRangeItemRequirement(RangeItemRequirement rangeReq) {
        int startItemId = getFieldValue(rangeReq, "startItemId", Integer.class, -1);
        int endItemId = getFieldValue(rangeReq, "endItemId", Integer.class, -1);

        if (startItemId != -1 && endItemId != -1) {
            log.info("RangeItemRequirement - Start Item ID: {}, End Item ID: {}", startItemId, endItemId);
        } else {
            log.warn("Failed to retrieve start or end item ID for RangeItemRequirement.");
        }
    }

    private void handleAllRequirementsCollection(AllRequirementsCollection allReq) {
        log.info("AllRequirementsCollection - Name: {}", allReq.getCollectiveName(client));
        ItemRequirement[] requirements = getFieldValue(allReq, "requirements", ItemRequirement[].class);
        if (requirements != null) {
            for (ItemRequirement req : requirements) {
                handleItemRequirement(req);
            }
        }
    }

    private void handleSlotLimitationRequirement(SlotLimitationRequirement slotReq) {
        log.info("SlotLimitationRequirement - Description: {}", slotReq.getCollectiveName(client));
        EquipmentInventorySlot[] slots = getFieldValue(slotReq, "slots", EquipmentInventorySlot[].class);
        if (slots != null) {
            for (EquipmentInventorySlot slot : slots) {
                log.info("SlotLimitationRequirement - Slot: {}", slot.name());
            }
        }
    }

    /**
     * Retrieves the collective names of the given item requirements.
     *
     * @param requirements the list of ItemRequirement objects
     * @return a list of item names as Strings
     */
    public List<String> getItemNames(List<ItemRequirement> requirements) {
        return requirements.stream()
                .map(req -> {
                    try {
                        return req.getCollectiveName(client); // Get the readable name of the item
                    } catch (Exception e) {
                        log.error("Error retrieving name for item requirement: {}", req, e);
                        return "Unknown Item"; // Default in case of error
                    }
                })
                .collect(Collectors.toList());
    }

}
