package net.runelite.client.plugins.microbot.util.prayer;

import net.runelite.api.MenuAction;
import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.menu.NewMenuEntry;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.awt.*;
import java.util.Arrays;

import static net.runelite.api.Varbits.QUICK_PRAYER;
import static net.runelite.client.plugins.microbot.globval.VarbitIndices.SELECTED_QUICK_PRAYERS;
import static net.runelite.client.plugins.microbot.globval.VarbitValues.QUICK_PRAYER_ENABLED;
import static net.runelite.client.plugins.microbot.util.Global.sleepUntil;

public class Rs2Prayer {

    public static void toggle(Rs2PrayerEnum name) {
        Microbot.doInvoke(new NewMenuEntry(-1, name.getIndex(), MenuAction.CC_OP.getId(), 1,-1, "Activate"), new Rectangle(1, 1, Microbot.getClient().getCanvasWidth(), Microbot.getClient().getCanvasHeight()));
        // Rs2Reflection.invokeMenu(-1, name.getIndex(), MenuAction.CC_OP.getId(), 1,-1, "Activate", "", -1, -1);
    }

    public static void toggle(Rs2PrayerEnum name, boolean on) {
        final int varBit = name.getVarbit();
        if(!on) {
            if (Microbot.getVarbitValue(varBit) == 0) return;
        } else {
            if (Microbot.getVarbitValue(varBit) == 1) return;
        }

        if (!Rs2Player.hasPrayerPoints()) return;

        Microbot.doInvoke(new NewMenuEntry(-1, name.getIndex(), MenuAction.CC_OP.getId(), 1,-1, "Activate"), new Rectangle(1, 1, Microbot.getClient().getCanvasWidth(), Microbot.getClient().getCanvasHeight()));
        //Rs2Reflection.invokeMenu(-1, name.getIndex(), MenuAction.CC_OP.getId(), 1,-1, "Activate", "", -1, -1);
    }

    public static boolean isQuickPrayerSet(Rs2PrayerEnum prayer) {
        int selectedQuickPrayersVarbit = Microbot.getVarbitValue(SELECTED_QUICK_PRAYERS);
        return (selectedQuickPrayersVarbit & (1 << prayer.getQuickPrayerIndex())) != 0;
    }
    public static boolean isPrayerActive(Rs2PrayerEnum name) {
        final int varBit = name.getVarbit();
        return Microbot.getClientThread().runOnClientThread(() ->
                Microbot.getClient().getVarbitValue(varBit) == 1);
    }

    public static boolean isQuickPrayerEnabled() {
        return Microbot.getVarbitValue(QUICK_PRAYER) == QUICK_PRAYER_ENABLED.getValue();
    }

    public static boolean setQuickPrayers(Rs2PrayerEnum[] prayers) {
        if (Rs2Widget.isHidden(10485779)) return false;

        // Open the menu
        Microbot.doInvoke(new NewMenuEntry("Setup",-1, 10485779, MenuAction.CC_OP.getId(), 2, -1, "Quick-prayers"), new Rectangle(1, 1, Microbot.getClient().getCanvasWidth(), Microbot.getClient().getCanvasHeight()));

        sleepUntil(() -> !Rs2Widget.isHidden(5046276));

        for (Rs2PrayerEnum prayer : prayers) {
            if(isQuickPrayerSet(prayer)) continue;
            Microbot.doInvoke(new NewMenuEntry(prayer.getName(),prayer.getQuickPrayerIndex(), 5046276, MenuAction.CC_OP.getId(), 1, -1, "Toggle"), new Rectangle(1, 1, Microbot.getClient().getCanvasWidth(), Microbot.getClient().getCanvasHeight()));
        }

        Microbot.doInvoke(new NewMenuEntry("Done",-1, 5046277, MenuAction.CC_OP.getId(), 1, -1, ""), new Rectangle(1, 1, Microbot.getClient().getCanvasWidth(), Microbot.getClient().getCanvasHeight()));
        return true;
    }



    public static boolean toggleQuickPrayer(boolean on) {
        boolean bit = Microbot.getVarbitValue(QUICK_PRAYER) == QUICK_PRAYER_ENABLED.getValue();

        boolean isQuickPrayerSet = Microbot.getVarbitValue(4102) > 0;
        if (!isQuickPrayerSet) return false;

        if (Rs2Widget.isHidden(10485779)) return false;
        if (on == bit) return true;

        Microbot.doInvoke(new NewMenuEntry(-1, 10485779, MenuAction.CC_OP.getId(), 1, -1, "Quick-prayers"), new Rectangle(1, 1, Microbot.getClient().getCanvasWidth(), Microbot.getClient().getCanvasHeight()));
        return true;
    }

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