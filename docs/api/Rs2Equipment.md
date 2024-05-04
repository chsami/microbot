# Rs2Equipment Class Documentation
## [Back](development.md)
## Overview
The `Rs2Equipment` class provides methods to interact with the player's equipment, allowing for checking equipment status, manipulating specific items, and managing actions related to equipment slots.

## Methods

### `equipment`
- **Signature**: `public static ItemContainer equipment()`
- **Description**: Retrieves the current equipment item container from the game client.

### `getEquippedItem`
- **Signature**: `public static Rs2Item getEquippedItem(EquipmentInventorySlot slot)`
- **Description**: Retrieves an equipped item from a specified equipment slot.

### `hasEquipped`
- **Signature**: `public static boolean hasEquipped(String itemName)`
- **Description**: Checks if an item with a specific name is equipped. This method is deprecated and will be removed in favor of `isWearing`.

### `hasEquippedContains`
- **Signature**: `public static boolean hasEquippedContains(String itemName)`
- **Description**: Checks if any equipped item's name contains the specified substring.

### `hasEquipped`
- **Signature**: `public static boolean hasEquipped(int id)`
- **Description**: Determines if an item with a specific ID is equipped.

### `hasEquippedSlot`
- **Signature**: `public static boolean hasEquippedSlot(EquipmentInventorySlot slot)`
- **Description**: Checks if any item is equipped in the specified slot.

### `hasGuthanBodyEquiped`
- **Signature**: `public static boolean hasGuthanBodyEquiped()`
- **Description**: Checks if "Guthan's platebody" is equipped.

### `hasGuthanHelmEquiped`
- **Signature**: `public static boolean hasGuthanHelmEquiped()`
- **Description**: Checks if "Guthan's helm" is equipped.

### `hasGuthanLegsEquiped`
- **Signature**: `public static boolean hasGuthanLegsEquiped()`
- **Description**: Checks if "Guthan's chainskirt" is equipped.

### `hasGuthanWeaponEquiped`
- **Signature**: `public static boolean hasGuthanWeaponEquiped()`
- **Description**: Checks if "Guthan's warspear" is equipped.

### `isEquipped`
- **Signature**: `public static boolean isEquipped(String name, EquipmentInventorySlot slot, boolean exact)`
- **Description**: Determines if a specific item by name is equipped in a given slot, with an option for exact matching.

### `isEquipped`
- **Signature**: `public static boolean isEquipped(int id, EquipmentInventorySlot slot)`
- **Description**: Checks if a specific item by ID is equipped in a given slot.

### `isWearing`
- **Signature**: `public static boolean isWearing(String name, boolean exact)`
- **Description**: Checks across all equipment slots to see if a specific item by name is worn, with an option for exact matching.

### `isWearing`
- **Signature**: `public static boolean isWearing(int id)`
- **Description**: Checks across all equipment slots to see if a specific item by ID is worn.

### `isWearingFullGuthan`
- **Signature**: `public static boolean isWearingFullGuthan()`
- **Description**: Determines if the player is wearing the full set of Guthan's equipment.

### `storeEquipmentItemsInMemory`
- **Signature**: `public static void storeEquipmentItemsInMemory(ItemContainerChanged e)`
- **Description**: Updates the stored list of equipment items based on changes detected in the equipment item container.

### `useAmuletAction`
- **Signature**: `public static void useAmuletAction(JewelleryLocationEnum jewelleryLocationEnum)`
- **Description**: Performs an action on an amulet, such as equipping it, based on the specified jewellery location.

### `useRingAction`
- **Signature**: `public static void useRingAction(JewelleryLocationEnum jewelleryLocationEnum)`
- **Description**: Performs an action on a ring, such as equipping it, based on the specified jewellery location.

## Additional Details
This class is critical for scripts that need to interact with the player's equipped items, allowing for efficient checks and manipulations related to gear and accessories.
