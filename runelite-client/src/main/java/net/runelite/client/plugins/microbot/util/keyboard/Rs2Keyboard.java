package net.runelite.client.plugins.microbot.util.keyboard;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.Global;
import net.runelite.client.plugins.microbot.util.math.Rs2Random;

import java.awt.*;
import java.awt.event.KeyEvent;

import static java.awt.event.KeyEvent.CHAR_UNDEFINED;

public class Rs2Keyboard {

    public static Canvas getCanvas() {
        return Microbot.getClient().getCanvas();
    }

    public static void typeString(final String word) {
        boolean originalFocusValue = Microbot.getClient().getCanvas().isFocusable();
        if (!originalFocusValue) {
            Microbot.getClient().getCanvas().setFocusable(!originalFocusValue);
        }

        for (int i = 0; i < word.length(); i++) {
            final int randomizer = Rs2Random.between(20, 200);

            KeyEvent keyEvent = new KeyEvent(getCanvas(), KeyEvent.KEY_TYPED, System.currentTimeMillis() + randomizer, 0, KeyEvent.VK_UNDEFINED, word.charAt(i));

            getCanvas().dispatchEvent(keyEvent);

            Global.sleep(100, 200);
        }

        if (!originalFocusValue) {
            Microbot.getClient().getCanvas().setFocusable(originalFocusValue);
        }
    }


    public static void keyPress(final char key) {
        boolean originalFocusValue = Microbot.getClient().getCanvas().isFocusable();
        if (!originalFocusValue) {
            Microbot.getClient().getCanvas().setFocusable(!originalFocusValue);
        }

        final int randomizer = Rs2Random.between(20, 200);

        KeyEvent keyEvent = new KeyEvent(getCanvas(), KeyEvent.KEY_TYPED, System.currentTimeMillis() + randomizer, 0, KeyEvent.VK_UNDEFINED, key);

        getCanvas().dispatchEvent(keyEvent);

        if (!originalFocusValue) {
            Microbot.getClient().getCanvas().setFocusable(originalFocusValue);
        }

    }

    public static void holdShift() {
        boolean originalFocusValue = Microbot.getClient().getCanvas().isFocusable();
        if (!originalFocusValue) {
            Microbot.getClient().getCanvas().setFocusable(!originalFocusValue);
        }

        final int randomizer = Rs2Random.between(20, 200);

        KeyEvent keyEvent = new KeyEvent(getCanvas(), KeyEvent.KEY_PRESSED, System.currentTimeMillis() + randomizer, 0, KeyEvent.VK_SHIFT, CHAR_UNDEFINED);

        getCanvas().dispatchEvent(keyEvent);

        if (!originalFocusValue) {
            Microbot.getClient().getCanvas().setFocusable(originalFocusValue);
        }

    }

    public static void releaseShift() {
        boolean originalFocusValue = Microbot.getClient().getCanvas().isFocusable();
        if (!originalFocusValue) {
            Microbot.getClient().getCanvas().setFocusable(!originalFocusValue);
        }

        final int randomizer = Rs2Random.between(20, 200);

        KeyEvent keyEvent = new KeyEvent(getCanvas(), KeyEvent.KEY_RELEASED, System.currentTimeMillis() + randomizer, 0, KeyEvent.VK_SHIFT);

        getCanvas().dispatchEvent(keyEvent);

        if (!originalFocusValue) {
            Microbot.getClient().getCanvas().setFocusable(originalFocusValue);
        }

    }

    public static void keyHold(int keyCode) {
        boolean originalFocusValue = Microbot.getClient().getCanvas().isFocusable();
        if (!originalFocusValue) {
            Microbot.getClient().getCanvas().setFocusable(!originalFocusValue);
        }

        final int randomizer = Rs2Random.between(20, 200);
        KeyEvent keyEvent = new KeyEvent(getCanvas(), KeyEvent.KEY_PRESSED, System.currentTimeMillis() + randomizer, 0, keyCode);

        getCanvas().dispatchEvent(keyEvent);

        if (!originalFocusValue) {
            Microbot.getClient().getCanvas().setFocusable(originalFocusValue);
        }

    }

    public static void keyRelease(int keyCode) {
        boolean originalFocusValue = Microbot.getClient().getCanvas().isFocusable();
        if (!originalFocusValue) {
            Microbot.getClient().getCanvas().setFocusable(!originalFocusValue);
        }

        final int randomizer = Rs2Random.between(20, 200);

        KeyEvent keyEvent = new KeyEvent(getCanvas(), KeyEvent.KEY_RELEASED, System.currentTimeMillis() + randomizer, 0, keyCode);

        getCanvas().dispatchEvent(keyEvent);

        if (!originalFocusValue) {
            Microbot.getClient().getCanvas().setFocusable(originalFocusValue);
        }

    }

    public static void keyPress(int keyCode) {
        keyHold(keyCode);
        keyRelease(keyCode);
    }

    public static void enter() {
        keyPress(KeyEvent.VK_ENTER);

        //FIX: this is to avoid automatically login with jagex account when you are on the login screen
        KeyEvent keyEvent = new KeyEvent(getCanvas(), KeyEvent.KEY_TYPED, System.currentTimeMillis() , 0, KeyEvent.VK_UNDEFINED, '\n');

        getCanvas().dispatchEvent(keyEvent);
    }
}
