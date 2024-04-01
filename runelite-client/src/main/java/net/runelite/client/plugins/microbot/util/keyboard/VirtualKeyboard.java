package net.runelite.client.plugins.microbot.util.keyboard;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.Global;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import static java.awt.event.KeyEvent.CHAR_UNDEFINED;
import static net.runelite.client.plugins.microbot.util.math.Random.random;

public class VirtualKeyboard {

    public static Canvas getCanvas() {
        return Microbot.getClient().getCanvas();
    }

    public static void typeString(final String word) {
        for (int i = 0; i < word.length(); i++) {
            final int randomizer = random(20, 200);

            KeyEvent keyEvent = new KeyEvent(getCanvas(), KeyEvent.KEY_TYPED, System.currentTimeMillis() + randomizer, 0, KeyEvent.VK_UNDEFINED, word.charAt(i));

            getCanvas().dispatchEvent(keyEvent);

            Global.sleep(100, 200);
        }

    }


    public static void keyPress(final char key) {
        final int randomizer = random(20, 200);

        KeyEvent keyEvent = new KeyEvent(getCanvas(), KeyEvent.KEY_TYPED, System.currentTimeMillis() + randomizer, 0, KeyEvent.VK_UNDEFINED, key);

        getCanvas().dispatchEvent(keyEvent);

    }

    public static void holdShift() {
        final int randomizer = random(20, 200);

        KeyEvent keyEvent = new KeyEvent(getCanvas(), KeyEvent.KEY_PRESSED, System.currentTimeMillis() + randomizer, 0, KeyEvent.VK_SHIFT, CHAR_UNDEFINED);

        getCanvas().dispatchEvent(keyEvent);

    }

    public static void releaseShift() {
        final int randomizer = random(20, 200);

        KeyEvent keyEvent = new KeyEvent(getCanvas(), KeyEvent.KEY_RELEASED, System.currentTimeMillis() + randomizer, 0, KeyEvent.VK_SHIFT);

        getCanvas().dispatchEvent(keyEvent);

    }

    public static void keyHold(int key) {

        KeyEvent keyEvent = new KeyEvent(getCanvas(), KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, key);

        getCanvas().dispatchEvent(keyEvent);

    }

    public static void keyRelease(int key) {
        final int randomizer = random(20, 200);

        KeyEvent keyEvent = new KeyEvent(getCanvas(), KeyEvent.KEY_RELEASED, System.currentTimeMillis() + randomizer, 0, key);

        getCanvas().dispatchEvent(keyEvent);

    }

    public static void keyPress(int key) {
        keyHold(key);
        keyRelease(key);
    }

    public static void enter() {
        keyHold(KeyEvent.VK_ENTER);
        keyRelease(KeyEvent.VK_ENTER);
    }

    private static final Map<Integer, Boolean> pressedKeys = new HashMap<>();

    static {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(event -> {
            synchronized (VirtualKeyboard.class) {
                if (event.getID() == KeyEvent.KEY_PRESSED) pressedKeys.put(event.getKeyCode(), true);
                else if (event.getID() == KeyEvent.KEY_RELEASED) pressedKeys.put(event.getKeyCode(), false);
                return false;
            }
        });
    }

    public static boolean isKeyPressed(int keyCode) { // Any key code from the KeyEvent class
        return pressedKeys.getOrDefault(keyCode, false);
    }
}
