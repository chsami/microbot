# Rs2Reflection Class Documentation
## [Back](development.md)
The `Rs2Reflection` class provides utility methods for manipulating game elements through Java reflection. This allows for operations that are normally inaccessible through standard API calls, such as obtaining and modifying game entity attributes and invoking hidden actions within the game's client.

## Methods

### `getAnimation(NPC npc)`
- **Parameters**: `NPC npc` - The NPC whose animation value is to be retrieved.
- **Returns**: `int` - The animation identifier of the NPC, or `-1` if not found.
- **Description**: Retrieves the animation identifier of a given NPC by dynamically identifying the animation field via reflection.

### `getGroundItemActions(ItemComposition item)`
- **Parameters**: `ItemComposition item` - The item composition to analyze.
- **Returns**: `String[]` - An array of possible actions for the item on the ground.
- **Description**: Extracts ground item actions from an item's composition using reflection to access the relevant field.

### `setItemId(MenuEntry menuEntry, int itemId)`
- **Parameters**:
    - `MenuEntry menuEntry` - The menu entry to modify.
    - `int itemId` - The item ID to set.
- **Description**: Sets the item ID in a menu entry, presumably to modify the context of an interaction dynamically.

### `getObjectByName(String[] names, boolean exact)`
- **Parameters**:
    - `String[] names` - Array of object names to find.
    - `boolean exact` - Specifies whether to match names exactly.
- **Returns**: `ArrayList<Integer>` - A list of object IDs matching the names.
- **Description**: Finds object IDs from the game's object definitions using their names, with an option for exact or partial matches.

### `invokeMenu(int param0, int param1, int opcode, int identifier, int itemId, String option, String target, int x, int y)`
- **Parameters**: Parameters defining the context and details of the menu action to invoke.
- **Description**: Directly invokes a game menu action using reflection, typically used for simulating clicks and other interactions programmatically.

## Usage Example

This example demonstrates how to use `getAnimation` to retrieve the animation of an NPC and print it:

```java
NPC someNpc = ... // obtain NPC instance from the game client
int animationId = Rs2Reflection.getAnimation(someNpc);
System.out.println("Current NPC Animation ID: " + animationId);
