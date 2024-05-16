# Rs2Inventory Class Documentation
## [Back](development.md)
## Overview
`Rs2Inventory` manages the player's inventory in the game, offering methods for interacting with items, checking inventory status, and performing complex item manipulations.

## Methods

### `inventory`
- **Description**: Retrieves the current inventory container.
- **Returns**: `ItemContainer` - The current inventory.

### `inventoryItems`
- **Description**: Holds the list of items currently in the inventory.

### `storeInventoryItemsInMemory`
- **Description**: Updates the memory with the current inventory items whenever there's a change.
- **Parameters**:
    - `e`: `ItemContainerChanged` - The event triggered when inventory changes.

### `items`
- **Description**: Provides a list of all items in the inventory.
- **Returns**: `List<Rs2Item>` - List of inventory items.

### `all`
- **Description**: Returns a list of all items in the inventory.
- **Returns**: `List<Rs2Item>` - List of all items.

### `all`
- **Description**: Returns a list of all items that match a given filter.
- **Parameters**:
    - `filter`: `Predicate<Rs2Item>` - The filter to apply.
- **Returns**: `List<Rs2Item>` - Filtered list of items.

### `capacity`
- **Description**: Returns the total capacity of the inventory.
- **Returns**: `int` - The capacity of the inventory.

### `combine`
- **Description**: Combines two items in the inventory by their IDs.
- **Parameters**:
    - `primaryItemId`: `int` - ID of the primary item.
    - `secondaryItemId`: `int` - ID of the secondary item.
- **Returns**: `boolean` - True if successful, false otherwise.

### `combine`
- **Description**: Combines two items in the inventory by their names.
- **Parameters**:
    - `primaryItemName`: `String` - Name of the primary item.
    - `secondaryItemName`: `String` - Name of the secondary item.
- **Returns**: `boolean` - True if successful, false otherwise.

### `contains`
- **Description**: Checks if the inventory contains an item with a specific ID.
- **Parameters**:
    - `id`: `int` - The ID to check.
- **Returns**: `boolean` - True if the item is present, false otherwise.

### `contains`
- **Description**: Checks if the inventory contains items with specific IDs.
- **Parameters**:
    - `ids`: `int[]` - The IDs to check.
- **Returns**: `boolean` - True if all items are present, false otherwise.

### `contains`
- **Description**: Checks if the inventory contains an item with a specific name.
- **Parameters**:
    - `name`: `String` - The name to check.
- **Returns**: `boolean` - True if the item is present, false otherwise.

### `count`
- **Description**: Counts the number of items matching a specific ID.
- **Parameters**:
    - `id`: `int` - The ID to match.
- **Returns**: `int` - Count of matching items.

### `deselect`
- **Description**: Deselects any selected item in the inventory.
- **Returns**: `boolean` - True if an item was deselected, false otherwise.

### `drop`
- **Description**: Drops an item with a specific ID.
- **Parameters**:
    - `id`: `int` - The ID of the item to drop.
- **Returns**: `boolean` - True if the item was dropped, false otherwise.

### `dropAll`
- **Description**: Drops all items in the inventory.
- **Returns**: `boolean` - True if all items were dropped, false otherwise.

### `emptySlotCount`
- **Description**: Returns the count of empty slots in the inventory.
- **Returns**: `int` - Number of empty slots.

### `get`
- **Description**: Retrieves an item by ID.
- **Parameters**:
    - `id`: `int` - The item's ID.
- **Returns**: `Rs2Item` - The item, or null if not found.

### `getActionsForSlot`
- **Description**: Retrieves available actions for an item in a specified slot.
- **Parameters**:
    - `slot`: `int` - The slot to check.
- **Returns**: `String[]` - Array of actions.

### `isFull`
- **Description**: Checks if the inventory is full.
- **Returns**: `boolean` - True if the inventory is full, false otherwise.

### `isEmpty`
- **Description**: Checks if the inventory is empty.
- **Returns**: `boolean` - True if the inventory is empty, false otherwise.

### `interact`
- **Description**: Interacts with an item by ID.
- **Parameters**:
    - `id`: `int` - The item's ID.
    - `action`: `String` - The action to perform (optional).
- **Returns**: `boolean` - True if the interaction was successful, false otherwise.

## Additional Methods
The class includes numerous other methods for more specific or advanced inventory manipulations, such as filtering items, checking for item presence by different criteria, interacting with multiple items, and handling special cases like noted or unnoted items.

## Usage Example
```java
Rs2Inventory.interact(12345, "Use");
boolean hasItem = Rs2Inventory.contains("Magic potion");
int emptySlots = Rs2Inventory.emptySlotCount();
