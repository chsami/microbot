# Rs2Magic Class Documentation
## [Back](development.md)
## Overview
The `Rs2Magic` class facilitates magic-related operations in the Microbot client, allowing for casting spells on various entities and performing alchemy. It handles interactions with the game's magic interface and provides utility functions for different spell actions.

## Methods

### `canCast`
- **Description**: Checks if the player has the required Magic level to cast a specified spell.
- **Parameters**:
    - `magicSpell`: `MagicAction` - The magic spell to check.
- **Returns**: `boolean` - True if the player can cast the spell, otherwise false.

### `cast`
- **Description**: Casts a specified spell.
- **Parameters**:
    - `magicSpell`: `MagicAction` - The magic spell to cast.
- **Behavior**: Switches to the magic tab, sets the status, and invokes the spell action.

### `castOn`
- **Description**: Casts a specified spell on an actor.
- **Parameters**:
    - `magicSpell`: `MagicAction` - The magic spell to cast.
    - `actor`: `Actor` - The target actor.
- **Behavior**: Ensures the target is in view before casting the spell.

### `alch`
- **Description**: Performs alchemy on an item, choosing High or Low Alchemy based on Magic level.
- **Overloads**:
    - **Item Name**: Casts alchemy on the first item with the specified name found in the inventory.
    - **Item Object**: Directly casts alchemy on the provided `Rs2Item` object.
- **Parameters**:
    - `itemName`: `String` - The name of the item to alchemize.
    - `item`: `Rs2Item` - The item object to alchemize.

### `highAlch` and `lowAlch`
- **Description**: Performs High Level Alchemy or Low Level Alchemy on an item.
- **Parameters**:
    - `item`: `Rs2Item` - The item to alchemize.
- **Behavior**: Switches to the magic tab, ensures the spell is available, and performs the alchemy.

## Static Members

### `sleepUntil`
- **Description**: Utility method that pauses execution until a condition becomes true, used to synchronize state changes like tab switches.

### `alch`
- **Description**: Helper method to perform the actual alchemy process on an item.
- **Parameters**:
    - `alch`: `Widget` - The alchemy spell widget.
    - `item`: `Rs2Item` - The item to alchemize (optional).

## Usage Examples

```java
Rs2Magic.cast(MagicAction.FIRE_STRIKE);
Rs2Magic.alch("Rune Platelegs");
Rs2Magic.castOn(MagicAction.TELEKINETIC_GRAB, someActor);
Rs2Magic.highAlch(someRs2Item);
