# Rs2Camera Class Documentation

## [Back](development.md)

## Overview
The `Rs2Camera` class provides methods to manipulate the camera view in the game, including setting camera angles, pitching, and determining if a tile is visible on the screen.

## Methods

### `angleToTile(Actor t)`
- **Signature**: `public static int angleToTile(Actor t)`
- **Description**: Computes the angle from the player's location to a given actor's position.

### `angleToTile(TileObject t)`
- **Signature**: `public static int angleToTile(TileObject t)`
- **Description**: Computes the angle from the player's location to a given tile object's position.

### `angleToTile(LocalPoint localPoint)`
- **Signature**: `public static int angleToTile(LocalPoint localPoint)`
- **Description**: Computes the angle from the player's location to a specified local point.

### `getAngle()`
- **Signature**: `public static int getAngle()`
- **Description**: Retrieves the current absolute angle of the camera.

### `getAngleTo(int degrees)`
- **Signature**: `public static int getAngleTo(int degrees)`
- **Description**: Calculates the relative angle difference to a specific degree value from the current camera angle.

### `getCharacterAngle(Actor actor)`
- **Signature**: `public static int getCharacterAngle(Actor actor)`
- **Description**: Retrieves the camera angle needed to focus directly on an actor.

### `getObjectAngle(TileObject tileObject)`
- **Signature**: `public static int getObjectAngle(TileObject tileObject)`
- **Description**: Retrieves the camera angle needed to focus directly on a tile object.

### `getTileAngle(Actor actor)`
- **Signature**: `public static int getTileAngle(Actor actor)`
- **Description**: Calculates the modified angle necessary to focus the camera directly on an actor.

### `getTileAngle(TileObject tileObject)`
- **Signature**: `public static int getTileAngle(TileObject tileObject)`
- **Description**: Calculates the modified angle necessary to focus the camera directly on a tile object.

### `isTileOnScreen(TileObject tileObject)`
- **Signature**: `public static boolean isTileOnScreen(TileObject tileObject)`
- **Description**: Checks if a tile object is currently visible within the game's viewport.

### `isTileOnScreen(LocalPoint localPoint)`
- **Signature**: `public static boolean isTileOnScreen(LocalPoint localPoint)`
- **Description**: Checks if a location represented by a LocalPoint is currently visible within the game's viewport.

### `setAngle(int degrees)`
- **Signature**: `public static void setAngle(int degrees)`
- **Description**: Sets the camera angle to a specific degree, adjusting the view accordingly.

### `setAngle(int degrees, Actor actor)`
- **Signature**: `public static void setAngle(int degrees, Actor actor)`
- **Description**: Sets the camera to focus on an actor at a specific angle.

### `setAngle(int degrees, TileObject tileObject)`
- **Signature**: `public static void setAngle(int degrees, TileObject tileObject)`
- **Description**: Sets the camera to focus on a tile object at a specific angle.

### `setAngle(int degrees, LocalPoint localPoint)`
- **Signature**: `public static void setAngle(int degrees, LocalPoint localPoint)`
- **Description**: Sets the camera to focus on a specific local point at a certain angle.

### `setPitch(float percentage)`
- **Signature**: `public static void setPitch(float percentage)`
- **Description**: Adjusts the camera pitch to a specified percentage of the maximum allowable pitch.

### `turnTo(Actor actor)`
- **Signature**: `public static void turnTo(final Actor actor)`
- **Description**: Rotates the camera to face an actor.

### `turnTo(TileObject tileObject)`
- **Signature**: `public static void turnTo(final TileObject tileObject)`
- **Description**: Rotates the camera to focus on a tile object.

### `turnTo(LocalPoint localPoint)`
- **Signature**: `public static void turnTo(final LocalPoint localPoint)`
- **Description**: Rotates the camera to focus on a specific local point in the game world.

## Additional Details
The methods provided facilitate various camera operations critical for enhancing player interaction and visualization within the game environment. This class utilizes keyboard inputs to adjust camera settings dynamically based on game events and object locations.
