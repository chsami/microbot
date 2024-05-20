package net.runelite.client.plugins.microbot.util.dialogues;

import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.awt.event.KeyEvent;

public class Rs2Dialogue {

    public static boolean isInDialogue() {
        return Rs2Widget.isWidgetVisible(231, 0) || Rs2Widget.isWidgetVisible(217, 0);
    }
    public static void clickContinue() {
        if (Rs2Widget.hasWidget("Click here to continue"))
            Rs2Keyboard.keyPress(KeyEvent.VK_SPACE);
    }

    public static boolean hasSelectAnOption() {
        return Rs2Widget.hasWidget("Select an Option");
    }

}
