package net.runelite.client.plugins.microbot.cluesolverv2.util;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.cluescrolls.clues.ClueScroll;
import net.runelite.client.plugins.cluescrolls.clues.item.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class ClueHelperV2 {

    private final Client client;
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
     * Generic method to retrieve the value of a specified field with an optional default value.
     * Adds detailed logging for tracing.
     */
    private <T> T getFieldValue(Object obj, String fieldName, Class<T> fieldType, T defaultValue) {
        try {
            Field field = getCachedField(obj.getClass(), fieldName);
            if (field != null) {
                T value = fieldType.cast(field.get(obj));
                if (value != null) {
                    log.debug("Retrieved field '{}' with value: {}", fieldName, value);
                    return value;
                } else {
                    log.warn("Field '{}' in '{}' is null, using default value.", fieldName, obj.getClass().getSimpleName());
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
     * Retrieves the clue location from a ClueScroll.
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
     * Caches and retrieves a Field object from a class.
     */
    private Field getCachedField(Class<?> clazz, String fieldName) {
        return fieldCache.computeIfAbsent(clazz, c -> getFieldFromClassHierarchy(c, fieldName));
    }

    /**
     * Traverses the class hierarchy to find a declared field.
     */
    private Field getFieldFromClassHierarchy(Class<?> clazz, String fieldName) {
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                log.debug("Cached field '{}' from class '{}'", fieldName, clazz.getSimpleName());
                return field;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }

    /**
     * Determines the required items for a given clue.
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

        Object itemRequirements = getFieldValue(clue, "itemRequirements", Object.class, null);
        if (itemRequirements != null) {
            log.info("Unpacking item requirements for clue type: {}", clue.getClass().getSimpleName());
            unpackItemRequirements(itemRequirements, requiredItems);
        } else {
            log.info("No 'itemRequirements' field found for clue type: {}", clue.getClass().getSimpleName());
        }

        log.info("Total required items determined: {}", requiredItems.size());
        for (ItemRequirement req : requiredItems) {
            try {
                String name = req.getCollectiveName(client);
                log.info("Required Item: {}", name);
            } catch (Exception e) {
                log.error("Error retrieving name for ItemRequirement: {}", req.getClass().getSimpleName(), e);
                log.info("Required Item: Unknown Item");
            }
        }

        return requiredItems;
    }

    /**
     * Unpacks item requirements from various collection types.
     */
    private void unpackItemRequirements(Object requirement, List<ItemRequirement> requiredItems) {
        try {
            log.info("Entering unpackItemRequirements with requirement type: {}", requirement.getClass().getSimpleName());
            if (requirement instanceof ItemRequirement) {
                log.debug("Processing ItemRequirement of type: {}", requirement.getClass().getSimpleName());
                requiredItems.add((ItemRequirement) requirement);
                logRequirementDetails((ItemRequirement) requirement);
            } else if (requirement instanceof ItemRequirement[]) {
                log.info("Processing ItemRequirement array.");
                for (ItemRequirement req : (ItemRequirement[]) requirement) {
                    log.debug("Processing ItemRequirement array element of type: {}", req.getClass().getSimpleName());
                    unpackItemRequirements(req, requiredItems);
                }
            } else if (requirement instanceof List) {
                for (Object req : (List<?>) requirement) {
                    log.debug("Processing List element of type: {}", req.getClass().getSimpleName());
                    if (req instanceof ItemRequirement) {
                        log.debug("Processing ItemRequirement list element of type: {}", req.getClass().getSimpleName());
                        unpackItemRequirements(req, requiredItems);
                    } else {
                        log.warn("List contains non-ItemRequirement element of type: {}", req.getClass().getSimpleName());
                    }
                }
            } else {
                log.warn("Unexpected item requirement type: {}", requirement.getClass().getSimpleName());
            }
            log.info("Exiting unpackItemRequirements with requirement type: {}", requirement.getClass().getSimpleName());
        } catch (Exception e) {
            log.error("Exception in unpackItemRequirements with requirement type: {}", requirement.getClass().getSimpleName(), e);
        }
    }

    /**
     * Logs the details of an ItemRequirement.
     */
    private void logRequirementDetails(ItemRequirement requirement) {
        log.info("Logging details for ItemRequirement of type: {}", requirement.getClass().getSimpleName());
        try {
            String collectiveName;
            log.info("Processing ItemRequirement of type: {}", requirement.getClass().getSimpleName());

            // Log before calling getCollectiveName
            log.debug("About to call getCollectiveName(client) for requirement: {}", requirement.getClass().getSimpleName());
            try {
                collectiveName = requirement.getCollectiveName(client);
                log.info("Processing requirement: {}", collectiveName);
            } catch (Exception e) {
                log.error("Error retrieving collective name for requirement: {}", requirement.getClass().getSimpleName(), e);
                collectiveName = "Unknown Item";
            }

            // Log whether client is null
            if (client == null) {
                log.error("Client instance is null!");
            } else {
                log.debug("Client instance is active.");
            }

            if (requirement instanceof SingleItemRequirement) {
                log.debug("Handling SingleItemRequirement.");
                int itemId = getFieldValue(requirement, "itemId", Integer.class, -1);
                log.info("SingleItemRequirement - Item ID: {} (Name: {})", itemId, collectiveName);
            } else if (requirement instanceof MultipleOfItemRequirement) {
                log.debug("Handling MultipleOfItemRequirement.");
                int itemId = getFieldValue(requirement, "itemId", Integer.class, -1);
                int quantity = getFieldValue(requirement, "quantity", Integer.class, -1);
                log.info("MultipleOfItemRequirement - Item ID: {}, Quantity: {}, Name: {}", itemId, quantity, collectiveName);
            } else if (requirement instanceof RangeItemRequirement) {
                log.debug("Handling RangeItemRequirement.");
                int startItemId = getFieldValue(requirement, "startItemId", Integer.class, -1);
                int endItemId = getFieldValue(requirement, "endItemId", Integer.class, -1);
                log.info("RangeItemRequirement - Start Item ID: {}, End Item ID: {}", startItemId, endItemId);
            } else if (requirement instanceof AnyRequirementCollection || requirement instanceof AllRequirementsCollection) {
                log.debug("Handling Requirement Collection.");
                ItemRequirement[] subRequirements = getFieldValue(requirement, "requirements", ItemRequirement[].class, new ItemRequirement[0]);
                logCollectionRequirementDetails(subRequirements);
            } else if (requirement instanceof SlotLimitationRequirement) {
                log.debug("Handling SlotLimitationRequirement.");
                String description = getFieldValue(requirement, "description", String.class, "Unknown Slot Requirement");
                log.info("SlotLimitationRequirement - Description: {}", description);
            } else {
                log.warn("Unknown ItemRequirement type: {}", requirement.getClass().getSimpleName());
            }
        } catch (Exception e) {
            log.error("Exception while processing ItemRequirement of type: {}", requirement.getClass().getSimpleName(), e);
        }
    }

    /**
     * Logs details of a collection of ItemRequirements.
     */
    private void logCollectionRequirementDetails(ItemRequirement[] requirements) {
        for (ItemRequirement req : requirements) {
            logRequirementDetails(req);
        }
    }

    /**
     * Retrieves a list of missing items based on the current inventory and equipment.
     */
    public List<ItemRequirement> getMissingItems(List<ItemRequirement> requirements) {
        ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
        ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);

        List<Item> allItems = new ArrayList<>();
        if (inventory != null) allItems.addAll(Arrays.asList(inventory.getItems()));
        if (equipment != null) allItems.addAll(Arrays.asList(equipment.getItems()));

        List<ItemRequirement> missingItems = requirements.stream()
                .filter(req -> !req.fulfilledBy(allItems.toArray(new Item[0])))
                .collect(Collectors.toList());

        log.info("Missing items count: {}", missingItems.size());
        for (ItemRequirement req : missingItems) {
            try {
                String name = req.getCollectiveName(client);
                log.info("Missing Item: {}", name);
            } catch (Exception e) {
                log.error("Error retrieving name for missing ItemRequirement: {}", req.getClass().getSimpleName(), e);
                log.info("Missing Item: Unknown Item");
            }
        }

        return missingItems;
    }

    /**
     * Retrieves the names of the required items.
     */
    public List<String> getItemNames(List<ItemRequirement> requirements) {
        return requirements.stream()
                .map(req -> {
                    try {
                        return req.getCollectiveName(client);
                    } catch (Exception e) {
                        log.error("Error retrieving name for item requirement: {}", req.getClass().getSimpleName(), e);
                        return "Unknown Item";
                    }
                })
                .collect(Collectors.toList());
    }
}
