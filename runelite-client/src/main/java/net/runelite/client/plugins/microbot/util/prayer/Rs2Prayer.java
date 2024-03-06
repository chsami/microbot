package net.runelite.client.plugins.microbot.util.prayer;

import net.runelite.api.MenuAction;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.reflection.Rs2Reflection;

import static net.runelite.api.Varbits.QUICK_PRAYER;
import static net.runelite.client.plugins.microbot.util.globval.VarbitValues.QUICK_PRAYER_ENABLED;

public class Rs2Prayer {

    public static void toggle(Prayer name, boolean onOff) {
        final int varBit = name.getVarbit();
        if(!onOff) {
            if (Microbot.getClientThread().runOnClientThread(() ->
                    Microbot.getClient().getVarbitValue(varBit) == 0)) return;
        } else {
            if (Microbot.getClientThread().runOnClientThread(() ->
                    Microbot.getClient().getVarbitValue(varBit) == 1)) return;
        }
        Rs2Reflection.invokeMenu(-1, name.getIndex(), MenuAction.CC_OP.getId(), 1,-1, "Activate", "", -1, -1);
    }

    public static boolean isQuickPrayerEnabled() {
        return Microbot.getVarbitValue(QUICK_PRAYER) == QUICK_PRAYER_ENABLED.getValue();
    }
}
