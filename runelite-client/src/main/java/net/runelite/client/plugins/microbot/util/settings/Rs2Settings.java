package net.runelite.client.plugins.microbot.util.settings;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.tabs.Tab;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.awt.event.KeyEvent;

import static net.runelite.client.plugins.microbot.util.Global.sleep;
import static net.runelite.client.plugins.microbot.util.Global.sleepUntilOnClientThread;

public class Rs2Settings {
    public static boolean enableDropShiftSetting() {
        final int DROP_SHIFT_SETTING = 5542;
        if (Microbot.getVarbitValue(DROP_SHIFT_SETTING) == 0) {
            final int ALL_SETTINGS_BUTTON = 7602208;
            final int SETTINGS_INTERFACE = 8781825;
            Tab.switchToSettings();
            Rs2Widget.clickWidget(ALL_SETTINGS_BUTTON);
            sleepUntilOnClientThread(() -> Rs2Widget.getWidget(SETTINGS_INTERFACE) != null);
            final boolean isSettingsInterfaceVisible = Rs2Widget.getWidget(SETTINGS_INTERFACE) != null;
            if (isSettingsInterfaceVisible) {
                Rs2Widget.clickWidget("controls", true);
                sleep(600);
                Rs2Widget.clickWidget("Shift click to drop items");
                sleep(600);
                VirtualKeyboard.keyPress(KeyEvent.VK_ESCAPE);
                Tab.switchToInventoryTab();
            }
        }
        return Microbot.getVarbitValue(DROP_SHIFT_SETTING) == 1;
    }
}
