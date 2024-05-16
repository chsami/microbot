# Rs2Prayer Class Documentation
## [Back](development.md)
## Overview
The `Rs2Prayer` class offers functionalities to manage and interact with prayer abilities in the game. It includes methods to toggle prayers on or off, check if prayers are active, and determine if the player is out of prayer points.

## Methods

### `toggle(Rs2PrayerEnum name)`
- **Parameters**:
    - `name`: `Rs2PrayerEnum` - The enum entry representing the prayer to toggle.
- **Description**: Toggles the specified prayer without checking its current state.

### `toggle(Rs2PrayerEnum name, boolean on)`
- **Parameters**:
    - `name`: `Rs2PrayerEnum` - The enum entry representing the prayer to toggle.
    - `on`: `boolean` - Desired state of the prayer; `true` to activate, `false` to deactivate.
- **Description**: Toggles the specified prayer to the desired state. Checks the current state to avoid unnecessary toggling.

### `isPrayerActive(Rs2PrayerEnum name)`
- **Parameters**:
    - `name`: `Rs2PrayerEnum` - The prayer to check.
- **Returns**: `boolean` - True if the specified prayer is currently active.
- **Description**: Checks if the specified prayer is active.

### `isQuickPrayerEnabled`
- **Returns**: `boolean` - True if quick prayers are currently enabled.
- **Description**: Checks if quick prayers are enabled.

### `isOutOfPrayer`
- **Returns**: `boolean` - True if the player has no prayer points left.
- **Description**: Checks if the player is out of prayer points.

## Usage Examples

### Toggling a Prayer
```java
Rs2Prayer.toggle(Rs2PrayerEnum.PIETY, true); // Activates Piety
Rs2Prayer.toggle(Rs2PrayerEnum.PIETY, false); // Deactivates Piety
