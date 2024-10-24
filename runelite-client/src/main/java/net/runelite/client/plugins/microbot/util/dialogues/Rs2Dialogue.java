package net.runelite.client.plugins.microbot.util.dialogues;

import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Rs2Dialogue {

    public static boolean isInDialogue() {
        return Rs2Widget.isWidgetVisible(231, 5)
                || Rs2Widget.isWidgetVisible(229, 0)
                || Rs2Widget.isWidgetVisible(229, 2)
                || Rs2Widget.isWidgetVisible(219, 1)
                || Rs2Widget.isWidgetVisible(217, 5)
                || Rs2Widget.isWidgetVisible(193, 0)
                || Rs2Widget.isWidgetVisible(11, 4)
                || hasContinue();
    }
    public static void clickContinue() {
        if (hasContinue())
            Rs2Keyboard.keyPress(KeyEvent.VK_SPACE);
    }

    public static boolean hasContinue(){
        return Rs2Widget.hasWidget("Click here to continue");
    }

    public static boolean hasSelectAnOption() {
        return Rs2Widget.hasWidget("Select an Option");
    }

    public static Widget getOption(String option) {
        Widget parent =  Rs2Widget.getWidget(219, 1);
        if (parent == null) return null;
        return Rs2Widget.findWidget(option, Arrays.stream(parent.getDynamicChildren()).collect(Collectors.toList()));
    }

    public static boolean clickOption(String option) {
        Widget widgetOption = getOption(option);
        if (widgetOption != null) {
            return Rs2Widget.clickWidget(getOption(option));
        }
        return false;
    }
}
