package net.runelite.client.plugins.microbot.util.keyboard;

import net.runelite.client.plugins.microbot.Microbot;

import java.awt.*;
import java.awt.event.KeyEvent;

import static java.awt.event.KeyEvent.CHAR_UNDEFINED;
import static net.runelite.client.plugins.microbot.util.math.Random.random;

public class VirtualKeyboard {

    public static Canvas getCanvas() {
        return Microbot.getClient().getCanvas();
    }

    public static void keyPress(final char key) {
        if (!getCanvas().hasFocus())
            getCanvas().requestFocus();

        final int randomizer = random(20, 200);

        KeyEvent keyEvent = new KeyEvent(getCanvas(), KeyEvent.KEY_TYPED, System.currentTimeMillis() + randomizer, 0, KeyEvent.VK_UNDEFINED, key);

        getCanvas().dispatchEvent(keyEvent);
    }

    public static void holdShift() {
        if (!getCanvas().hasFocus())
            getCanvas().requestFocus();

        final int randomizer = random(20, 200);

        KeyEvent keyEvent = new KeyEvent(getCanvas(), KeyEvent.KEY_PRESSED, System.currentTimeMillis() + randomizer, 0, KeyEvent.VK_SHIFT, CHAR_UNDEFINED);

        getCanvas().dispatchEvent(keyEvent);
    }

    public static void releaseShift() {
        if (!getCanvas().hasFocus())
            getCanvas().requestFocus();

        final int randomizer = random(20, 200);

        KeyEvent keyEvent = new KeyEvent(getCanvas(), KeyEvent.KEY_RELEASED, System.currentTimeMillis() + randomizer, 0, KeyEvent.VK_SHIFT);

        getCanvas().dispatchEvent(keyEvent);
    }

    public static void keyHold(int key) {
        if (!getCanvas().hasFocus())
            getCanvas().requestFocus();

        KeyEvent keyEvent = new KeyEvent(getCanvas(), KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, key);

        getCanvas().dispatchEvent(keyEvent);
    }

    public static void keyRelease(int key) {
        if (!getCanvas().hasFocus())
            getCanvas().requestFocus();

        KeyEvent keyEvent = new KeyEvent(getCanvas(), KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, key);

        getCanvas().dispatchEvent(keyEvent);
    }

    public static void keyPress(int key) {
        if (!getCanvas().hasFocus())
            getCanvas().requestFocus();

        keyHold(key);
        keyRelease(key);
    }
}
