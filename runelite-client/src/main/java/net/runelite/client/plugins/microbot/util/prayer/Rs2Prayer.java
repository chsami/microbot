package net.runelite.client.plugins.microbot.util.prayer;

import net.runelite.api.Varbits;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.tabs.Tab;

import static net.runelite.api.Varbits.PRAYER_PROTECT_FROM_MAGIC;

public class Rs2Prayer {
    public static void turnOnMagePrayer() {
        if (Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getVarbitValue(PRAYER_PROTECT_FROM_MAGIC) == 1)) return;
        Tab.switchToPrayerTab();
        Widget magePray = Microbot.getClient().getWidget(35454997);
        if (magePray == null) return;
        Microbot.getMouse().click(magePray.getBounds());
    }

    public static void turnOnRangePrayer() {
        if (Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getVarbitValue(Varbits.PRAYER_PROTECT_FROM_MISSILES) == 1)) return;
        Tab.switchToPrayerTab();
        Widget rangePray = Microbot.getClient().getWidget(35454998);
        if (rangePray == null) return;
        Microbot.getMouse().click(rangePray.getBounds());
    }
}
