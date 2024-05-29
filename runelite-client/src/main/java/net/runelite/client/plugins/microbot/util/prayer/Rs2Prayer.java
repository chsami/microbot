package net.runelite.client.plugins.microbot.util.prayer;

import net.runelite.api.MenuAction;
import net.runelite.api.Skill;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.menu.NewMenuEntry;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.awt.*;
import java.util.Arrays;

import static net.runelite.api.Varbits.QUICK_PRAYER;
import static net.runelite.client.plugins.microbot.globval.VarbitValues.QUICK_PRAYER_ENABLED;


/**
 * This class provides utility methods for managing prayers in the game.
 */
public class Rs2Prayer {

    /**
     * Toggles the specified prayer.
     * @param name The name of the prayer to toggle.
     */
    public static void toggle(Rs2PrayerEnum name) {
        Microbot.doInvoke(new NewMenuEntry(-1, name.getIndex(), MenuAction.CC_OP.getId(), 1,-1, "Activate"), new Rectangle(1, 1, Microbot.getClient().getCanvasWidth(), Microbot.getClient().getCanvasHeight()));
    }

    /**
     * Toggles the specified prayer on or off.
     * @param name The name of the prayer to toggle.
     * @param on Whether to turn the prayer on or off.
     */
    public static void toggle(Rs2PrayerEnum name, boolean on) {
        final int varBit = name.getVarbit();
        if(!on) {
            if (Microbot.getVarbitValue(varBit) == 0) return;
        } else {
            if (Microbot.getVarbitValue(varBit) == 1) return;
        }
        Microbot.doInvoke(new NewMenuEntry(-1, name.getIndex(), MenuAction.CC_OP.getId(), 1,-1, "Activate"), new Rectangle(1, 1, Microbot.getClient().getCanvasWidth(), Microbot.getClient().getCanvasHeight()));
    }

    /**
     * Toggles quick prayers on or off.
     * @param on Whether to turn quick prayers on or off.
     */
    public static void toggleQuickPrayer(boolean on) {
        if(!on) {
            if (!isQuickPrayerEnabled()) return;
        } else {
            if (isQuickPrayerEnabled()) return;
        }
        Rs2Widget.clickWidget(WidgetInfo.MINIMAP_QUICK_PRAYER_ORB);
    }

    /**
     * Checks if the specified prayer is active.
     * @param name The name of the prayer to check.
     * @return true if the prayer is active, false otherwise.
     */
    public static boolean isPrayerActive(Rs2PrayerEnum name) {
        final int varBit = name.getVarbit();
        return Microbot.getClientThread().runOnClientThread(() ->
                Microbot.getClient().getVarbitValue(varBit) == 1);
    }

    /**
     * Checks if quick prayers are enabled.
     * @return true if quick prayers are enabled, false otherwise.
     */
    public static boolean isQuickPrayerEnabled() {
        return Microbot.getVarbitValue(QUICK_PRAYER) == QUICK_PRAYER_ENABLED.getValue();
    }

    /**
     * Checks if the player is out of prayer.
     * @return true if the player is out of prayer, false otherwise.
     */
    public static boolean isOutOfPrayer() {
        return Microbot.getClient().getBoostedSkillLevel(Skill.PRAYER) <= 0;
    }

    /**
     * Disables all active prayers.
     */
    public static void disableAllPrayers() {
        Arrays.stream(Rs2PrayerEnum.values()).filter(Rs2Prayer::isPrayerActive).forEach(Rs2Prayer::toggle);
    }
}