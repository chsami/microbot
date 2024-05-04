# Rs2Keyboard Class Documentation
## [Back](development.md)
## Overview
The `Rs2Keyboard` class simulates keyboard actions such as typing strings, pressing, holding, and releasing keys. It directly interacts with the game's canvas to dispatch keyboard events.

## Methods

### `getCanvas`
- **Description**: Retrieves the game canvas from the Microbot client.
- **Returns**: `Canvas` - The game's canvas used for dispatching keyboard events.

### `typeString`
- **Description**: Types a string by dispatching a sequence of `KEY_TYPED` events for each character.
- **Parameters**:
    - `word`: `String` - The string to type.
- **Behavior**: Simulates typing with delays between key presses.

### `keyPress`
- **Description**: Presses a single character key.
- **Parameters**:
    - `key`: `char` - The character key to press.
- **Behavior**: Dispatches a `KEY_TYPED` event for the character.

### `holdShift`
- **Description**: Holds the shift key down.
- **Behavior**: Dispatches a `KEY_PRESSED` event for the shift key.

### `releaseShift`
- **Description**: Releases the shift key.
- **Behavior**: Dispatches a `KEY_RELEASED` event for the shift key.

### `keyHold`
- **Description**: Holds down a specified key.
- **Parameters**:
    - `key`: `int` - The key code (from `KeyEvent`) to hold.
- **Behavior**: Dispatches a `KEY_PRESSED` event for the specified key.

### `keyRelease`
- **Description**: Releases a specified key.
- **Parameters**:
    - `key`: `int` - The key code (from `KeyEvent`) to release.
- **Behavior**: Dispatches a `KEY_RELEASED` event for the specified key.

### `keyPress`
- **Description**: Simulates a complete key press (press and release).
- **Parameters**:
    - `key`: `int` - The key code to press.
- **Behavior**: Calls `keyHold` and `keyRelease` successively.

### `enter`
- **Description**: Simulates pressing the Enter key.
- **Behavior**: Calls `keyHold` and `keyRelease` for `KeyEvent.VK_ENTER`.

### `isKeyPressed`
- **Description**: Checks if a specific key is currently pressed.
- **Parameters**:
    - `keyCode`: `int` - The key code to check.
- **Returns**: `boolean` - True if the key is pressed, false otherwise.

## Static Members

### `pressedKeys`
- **Type**: `Map<Integer, Boolean>`
- **Description**: Keeps track of the pressed state of keys.

## Usage Example
```java
Rs2Keyboard.typeString("Hello, world!");
Rs2Keyboard.keyPress('a');
Rs2Keyboard.holdShift();
Rs2Keyboard.releaseShift();
Rs2Keyboard.enter();
boolean isShiftPressed = Rs2Keyboard.isKeyPressed(KeyEvent.VK_SHIFT);
