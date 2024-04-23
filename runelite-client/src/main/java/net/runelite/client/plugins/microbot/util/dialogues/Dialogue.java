package net.runelite.client.plugins.microbot.util.dialogues;

import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.awt.event.KeyEvent;

public class Dialogue {

    public static boolean isInDialogue() {
        return Rs2Widget.hasWidget("Click here to continue") || Rs2Widget.hasWidget("please wait...");
    }
    public static void clickContinue() {
        if (Rs2Widget.hasWidget("Click here to continue"))
            VirtualKeyboard.keyPress(KeyEvent.VK_SPACE);
    }

    public static boolean hasSelectAnOption() {
        return Rs2Widget.hasWidget("Select an Option");
    }

}
