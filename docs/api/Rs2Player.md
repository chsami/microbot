# Rs2Player Class Documentation
## [Back](development.md)
## Overview
The `Rs2Player` class is responsible for managing the player's state and interactions within the game. It includes methods for managing player status like hitpoints, energy, and various potion effects, as well as player actions like logging out, eating, and toggling run energy.

## Methods

### `hasAntiFireActive`
- **Returns**: `boolean` - True if any antifire potion effect is active.

### `hasSuperAntiFireActive`
- **Returns**: `boolean` - True if a super antifire potion effect is active.

### `hasDivineRangedActive`
- **Returns**: `boolean` - True if a divine ranging potion effect is active.

### `hasRangingPotionActive`
- **Returns**: `boolean` - True if a ranging potion effect is active that boosts ranged skill beyond base levels.

### `hasAntiVenomActive`
- **Returns**: `boolean` - True if any antivenom effect is active, including effects from items like the serpentine helm.

### `hasAntiPoisonActive`
- **Returns**: `boolean` - True if any antipoison effect is active.

### `handlePotionTimers`
- **Parameters**:
    - `event`: `VarbitChanged` - The event triggered when a varbit changes, used to update potion timers.
- **Description**: Updates potion timers based on varbit changes.

### `waitForWalking`
- **Description**: Waits until the player starts and then stops walking.

### `isAnimating`
- **Returns**: `boolean` - True if the player is currently playing an animation.

### `isWalking`
- **Returns**: `boolean` - True if the player is currently walking.

### `isMember`
- **Returns**: `boolean` - True if the player has membership active.

### `logout`
- **Description**: Initiates a logout action for the player.

### `eatAt`
- **Parameters**:
    - `percentage`: `int` - The health percentage threshold below which the player should eat.
- **Returns**: `boolean` - True if the player successfully ate to maintain health above the specified percentage.

### `getPlayers`
- **Returns**: `List<Player>` - A list of players in the current game environment, excluding the player themselves.

### `getWorldLocation`
- **Returns**: `WorldPoint` - The current world location of the player, accounting for instance regions.

### `isFullHealth`
- **Returns**: `boolean` - True if the player is at full health.

### `isInMulti`
- **Returns**: `boolean` - True if the player is in a multi-combat area.

### `logoutIfPlayerDetected`
- **Parameters**:
  - `amountOfPlayers`: `int` - Amount of players to detect before logging out.
- **Returns**: `boolean` - True if a player is detected.

### `logoutIfPlayerDetected`
- **Parameters**:
  - `amountOfPlayers`: `int` - Amount of players to detect before logging out.
  - `time`: `int` - The time in milliseconds for a player to be detected before logging out.
- **Returns**: `boolean` - True if a player is detected.

### `logoutIfPlayerDetected`
- **Parameters**:
  - `amountOfPlayers`: `int` - Amount of players to detect before logging out.
  - `time`: `int` - The time in milliseconds for a player to be detected before logging out.
  - `distance`: `int` - The distance between a player required before logging out.
- **Returns**: `boolean` - True if a player is detected.

## Remarks
- **Game Interaction**: This class provides critical functionalities for interacting with various game mechanics, from potion effects to player movement.
- **Utility Methods**: Includes utilities for waiting on player actions (like walking or animation), which are essential for coordinating tasks in automation scripts.

## Conclusion
The `Rs2Player` class is essential for managing the player's interaction within the game, providing methods to handle health, status effects, and environmental awareness. It serves as a central point for scripting player behavior in response to game state changes and player conditions.
