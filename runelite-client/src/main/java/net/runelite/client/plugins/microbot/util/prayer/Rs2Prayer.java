package net.runelite.client.plugins.microbot.util.prayer;

import net.runelite.api.MenuAction;
import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.menu.NewMenuEntry;

import java.awt.*;

import static net.runelite.api.Varbits.QUICK_PRAYER;
import static net.runelite.client.plugins.microbot.globval.VarbitValues.QUICK_PRAYER_ENABLED;

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
        Microbot.doInvoke(new NewMenuEntry(-1, name.getIndex(), MenuAction.CC_OP.getId(), 1,-1, "Activate"), new Rectangle(1, 1, Microbot.getClient().getCanvasWidth(), Microbot.getClient().getCanvasHeight()));
        //Rs2Reflection.invokeMenu(-1, name.getIndex(), MenuAction.CC_OP.getId(), 1,-1, "Activate", "", -1, -1);
    }

    public static boolean isPrayerActive(Rs2PrayerEnum name) {
        final int varBit = name.getVarbit();
        return Microbot.getClientThread().runOnClientThread(() ->
                Microbot.getClient().getVarbitValue(varBit) == 1);
    }

    public static boolean isQuickPrayerEnabled() {
        return Microbot.getVarbitValue(QUICK_PRAYER) == QUICK_PRAYER_ENABLED.getValue();
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
}
