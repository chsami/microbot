package net.runelite.client.plugins.microbot.util.settings;

import net.runelite.api.Varbits;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.awt.event.KeyEvent;

import static net.runelite.client.plugins.microbot.util.Global.*;
import static net.runelite.client.plugins.microbot.globval.VarbitIndices.TOGGLE_ROOFS;

public class Rs2Settings {
    public static boolean enableDropShiftSetting() {
        if (Rs2Widget.hasWidget("Click here to continue")) {
            Rs2Keyboard.keyPress(KeyEvent.VK_SPACE);
        }
        final int DROP_SHIFT_SETTING = 5542;
        if (Microbot.getVarbitValue(DROP_SHIFT_SETTING) == 0) {
            final int ALL_SETTINGS_BUTTON = 7602208;
            final int SETTINGS_INTERFACE = 8781825;
            Rs2Tab.switchToSettingsTab();
            Rs2Widget.clickWidget(ALL_SETTINGS_BUTTON);
            sleepUntilOnClientThread(() -> Rs2Widget.getWidget(SETTINGS_INTERFACE) != null);
            final boolean isSettingsInterfaceVisible = Rs2Widget.getWidget(SETTINGS_INTERFACE) != null;
            if (isSettingsInterfaceVisible) {
                Rs2Widget.clickWidget("controls", true);
                sleep(600);
                Rs2Widget.clickWidget("Shift click to drop items");
                sleep(600);
                Rs2Keyboard.keyPress(KeyEvent.VK_ESCAPE);
                Rs2Tab.switchToInventoryTab();
            }
        }
        return Microbot.getVarbitValue(DROP_SHIFT_SETTING) == 1;
    }

    public static boolean isHideRoofsEnabled() {
        return Microbot.getVarbitValue(TOGGLE_ROOFS) == 1;
    }

    public static void hideRoofs() {
        if (!isHideRoofsEnabled()) {
            Rs2Tab.switchToSettingsTab();
            Rs2Widget.clickWidget(7602208);
            sleepUntil(() -> Rs2Widget.hasWidget("Hide roofs"));
            sleep(1000);
            Rs2Widget.clickWidget("Hide roofs");
        }
    }

    public static boolean isLevelUpNotificationsEnabled() { return Microbot.getVarbitValue(Varbits.DISABLE_LEVEL_UP_INTERFACE) == 0; }

    public static boolean disableLevelUpNotifications() {
        if(isLevelUpNotificationsEnabled()){
            Rs2Tab.switchToSettingsTab();
            Rs2Widget.clickWidget(7602208);
            final boolean isSettingsInterfaceVisible = Rs2Widget.getWidget(8781825) != null;
            sleepUntilOnClientThread(() -> isSettingsInterfaceVisible);
            if(isSettingsInterfaceVisible){
                Rs2Widget.clickWidget(8781834);
                Rs2Keyboard.typeString("level-");
                Rs2Widget.clickWidget("Disable level-up interface");
                sleep(600);
                Rs2Keyboard.keyPress(KeyEvent.VK_ESCAPE);
                Rs2Tab.switchToInventoryTab();
            }
        }
        return Microbot.getVarbitValue(Varbits.DISABLE_LEVEL_UP_INTERFACE) == 1;
    }

    public static void turnOffMusic() {
        Rs2Tab.switchToSettingsTab();
        sleep(600);
        Rs2Widget.clickWidget(116, 68);
        sleep(600);
        boolean isMusicTurnedOff = !Rs2Widget.getWidget(116, 93).getChildren()[1].isSelfHidden();
        boolean isSoundEffectOff = !Rs2Widget.getWidget(116, 107).getChildren()[1].isSelfHidden();
        boolean isAreaSoundEffectOff = !Rs2Widget.getWidget(116, 122).getChildren()[1].isSelfHidden();
        if (isMusicTurnedOff && isSoundEffectOff && isAreaSoundEffectOff)
            return;
        Rs2Widget.clickWidget(7602244);
        sleep(1000);
        if (!isMusicTurnedOff)
            Rs2Widget.clickWidget(7602269);
        if (!isSoundEffectOff)
            Rs2Widget.clickWidget(7602283);
        if (!isAreaSoundEffectOff)
            Rs2Widget.clickWidget(7602298);
    }
}
