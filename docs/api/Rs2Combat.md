# Rs2Combat Class Documentation

## [Back](development.md)

## Overview
The `Rs2Combat` class provides methods for controlling combat settings in the game, such as setting attack styles, managing auto retaliate, and handling special attacks.

## Methods

### `enableAutoRetialiate`
- **Signature**: `public static boolean enableAutoRetialiate()`
- **Description**: Ensures auto retaliate is enabled by checking the current state and interacting with the combat options tab if necessary.

### `getSpecState`
- **Signature**: `public static boolean getSpecState()`
- **Description**: Checks the state of the special attack widget to determine if the special attack is enabled.

### `inCombat`
- **Signature**: `public static boolean inCombat()`
- **Description**: Determines if the player's character is currently engaged in combat by checking interaction and animation states.

### `isSelected`
- **Signature**: `private static boolean isSelected(int widgetId)`
- **Description**: Verifies if a widget is selected by checking its background color for the selection indicator.

### `setAttackStyle`
- **Signature**: `public static boolean setAttackStyle(WidgetInfo style)`
- **Description**: Sets the player's attack style by clicking on the corresponding widget. Returns true if successful or already set.

### `setAutoRetaliate`
- **Signature**: `public static boolean setAutoRetaliate(boolean state)`
- **Description**: Enables or disables auto retaliate based on the provided state by interacting with the corresponding widget.

### `setSpecState`
- **Signature**: `public static boolean setSpecState(boolean state, int specialAttackEnergyRequired)`
- **Description**: Sets the special attack state, enabling or disabling it based on the required energy level compared to the current energy level.

### `setSpecState (overloaded)`
- **Signature**: `public static boolean setSpecState(boolean state)`
- **Description**: Overloaded method that sets the special attack state without requiring a specific energy level.

## Additional Details
These methods facilitate the management of combat-related settings and statuses within the game, enhancing the player's ability to adapt and respond to various combat scenarios programmatically.
