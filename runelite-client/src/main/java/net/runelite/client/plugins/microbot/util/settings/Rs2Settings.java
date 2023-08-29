package net.runelite.client.plugins.microbot.util.settings;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.tabs.Tab;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.awt.event.KeyEvent;
import java.util.Arrays;

import static net.runelite.client.plugins.microbot.util.Global.*;
import static net.runelite.client.plugins.microbot.util.globval.VarbitIndices.TOGGLE_ROOFS;

public class Rs2Settings {
    public static boolean enableDropShiftSetting() {
        final int DROP_SHIFT_SETTING = 5542;
        if (Microbot.getVarbitValue(DROP_SHIFT_SETTING) == 0) {
            final int ALL_SETTINGS_BUTTON = 7602208;
            final int SETTINGS_INTERFACE = 8781825;
            Tab.switchToSettingsTab();
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

    public static boolean isHideRoofsEnabled() {
        return Microbot.getVarbitValue(TOGGLE_ROOFS) == 1;
    }

    public static void enableHideRoofs() {
        if (!isHideRoofsEnabled()) {
            Tab.switchToSettingsTab();
            Rs2Widget.clickWidget(7602208);
            sleepUntil(() -> Rs2Widget.hasWidget("Hide roofs"));
            Rs2Widget.clickWidget("Hide roofs");
        }
    }

    public static void turnOffMusic() {
        boolean isMusicTurnedOff = Microbot.getClientThread().runOnClientThread(() -> Arrays.stream(Rs2Widget.getWidget(116, 93).getChildren()).anyMatch(x -> x.isHidden()));
        boolean isSoundEffectOff = Microbot.getClientThread().runOnClientThread(() -> Arrays.stream(Rs2Widget.getWidget(116, 107).getChildren()).anyMatch(x -> x.isHidden()));
        boolean isAreaSoundEffectOff = Microbot.getClientThread().runOnClientThread(() -> Arrays.stream(Rs2Widget.getWidget(116, 122).getChildren()).anyMatch(x -> x.isHidden()));
        if (isMusicTurnedOff && isSoundEffectOff && isAreaSoundEffectOff)
            return;
        Tab.switchToSettingsTab();
        Rs2Widget.clickWidget(7602244);
        sleep(1000);
        if (!isMusicTurnedOff)
            Rs2Widget.clickWidget(7602269);
        if (!isSoundEffectOff)
            Rs2Widget.clickWidget(7602283);
        if (isAreaSoundEffectOff)
            Rs2Widget.clickWidget(7602298);
    }
}
