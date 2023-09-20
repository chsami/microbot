package net.runelite.client.plugins.microbot.util.prayer;

import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.Varbits;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.tabs.Tab;

import static net.runelite.api.Varbits.PRAYER_PROTECT_FROM_MAGIC;
import static net.runelite.api.Varbits.QUICK_PRAYER;
import static net.runelite.client.plugins.microbot.util.Global.sleep;
import static net.runelite.client.plugins.microbot.util.globval.VarbitValues.QUICK_PRAYER_ENABLED;

public class Rs2Prayer {

    public static int prayIndex = 0;

    public static void fastPray(Prayer name, boolean onOff) {
        if(!onOff) {
            if (Microbot.getClientThread().runOnClientThread(() ->
                    Microbot.getClient().getVarbitValue(name.getVarbit()) == 0)) return;
        } else {
            if (Microbot.getClientThread().runOnClientThread(() ->
                    Microbot.getClient().getVarbitValue(name.getVarbit()) == 1)) return;
        }
        prayIndex = name.getIndex();
        Microbot.getMouse().click();
        sleep(100);
        prayIndex = 0;
    }
    @Deprecated(since = "Use fastPray instead", forRemoval = true)
    public static void turnOffFastMeleePrayer() {
        if (Microbot.getClientThread().runOnClientThread(() ->
                Microbot.getClient().getVarbitValue(Varbits.PRAYER_PROTECT_FROM_MELEE) == 0)) return;
        prayIndex = 35454999;
        Microbot.getMouse().click();
        sleep(100);
        prayIndex = 0;
    }
    @Deprecated(since = "Use fastPray instead", forRemoval = true)
    public static void turnOffFastRangePrayer() {
        if (Microbot.getClientThread().runOnClientThread(() ->
                Microbot.getClient().getVarbitValue(Varbits.PRAYER_PROTECT_FROM_MISSILES) == 0)) return;
        prayIndex = 35454998;
        Microbot.getMouse().click();
        sleep(100);
        prayIndex = 0;
    }
    @Deprecated(since = "Use fastPray instead", forRemoval = true)
    public static void turnOffFastMagicPrayer() {
        if (Microbot.getClientThread().runOnClientThread(() ->
                Microbot.getClient().getVarbitValue(PRAYER_PROTECT_FROM_MAGIC) == 0)) return;
        prayIndex = 35454997;
        Microbot.getMouse().click();
        sleep(100);
        prayIndex = 0;
    }
    @Deprecated(since = "Use fastPray instead", forRemoval = true)
    public static void turnOffFastRigour() {
        if (Microbot.getClientThread().runOnClientThread(() ->
                Microbot.getClient().getVarbitValue(Varbits.PRAYER_RIGOUR) == 0)) return;
        prayIndex = 35455009;
        Microbot.getMouse().click();
        sleep(100);
        prayIndex = 0;
    }

    @Deprecated(since = "Use fastPray instead", forRemoval = true)
    public static void turnOffFastPiety(){
        if (Microbot.getClientThread().runOnClientThread(() ->
                Microbot.getClient().getVarbitValue(Varbits.PRAYER_PIETY) == 0)) return;
        prayIndex = 35455011;
        Microbot.getMouse().click();
        sleep(100);
        prayIndex = 0;
    }

    @Deprecated(since = "Use fastPray instead", forRemoval = true)
    public static void turnOffFastAugury(){
        if (Microbot.getClientThread().runOnClientThread(() ->
                Microbot.getClient().getVarbitValue(Varbits.PRAYER_AUGURY) == 0)) return;
        prayIndex = 35455012;
        Microbot.getMouse().click();
        sleep(100);
        prayIndex = 0;
    }
    @Deprecated(since = "Use fastPray instead", forRemoval = true)
    public static void turnOnFastMeleePrayer() {
        if (Microbot.getClientThread().runOnClientThread(() ->
                Microbot.getClient().getVarbitValue(Varbits.PRAYER_PROTECT_FROM_MELEE) == 1)) return;
        prayIndex = 35454999;
        Microbot.getMouse().click();
        sleep(100);
        prayIndex = 0;
    }
    @Deprecated(since = "Use fastPray instead", forRemoval = true)
    public static void turnOnFastRangePrayer() {
        if (Microbot.getClientThread().runOnClientThread(() ->
                Microbot.getClient().getVarbitValue(Varbits.PRAYER_PROTECT_FROM_MISSILES) == 1)) return;
        prayIndex = 35454998;
        Microbot.getMouse().click();
        sleep(100);
        prayIndex = 0;
    }
    @Deprecated(since = "Use fastPray instead", forRemoval = true)
    public static void turnOnFastMagicPrayer() {
        if (Microbot.getClientThread().runOnClientThread(() ->
                Microbot.getClient().getVarbitValue(PRAYER_PROTECT_FROM_MAGIC) == 1)) return;
        prayIndex = 35454997;
        Microbot.getMouse().click();
        sleep(100);
        prayIndex = 0;
    }

    @Deprecated(since = "Use fastPray instead", forRemoval = true)
    public static void turnOnFastRigour() {
        if (Microbot.getClientThread().runOnClientThread(() ->
                Microbot.getClient().getVarbitValue(Varbits.PRAYER_RIGOUR) == 1)) return;
        prayIndex = 35455009;
        Microbot.getMouse().click();
        sleep(100);
        prayIndex = 0;
    }

    @Deprecated(since = "Use fastPray instead", forRemoval = true)
    public static void turnOnFastPiety(){
        if (Microbot.getClientThread().runOnClientThread(() ->
                Microbot.getClient().getVarbitValue(Varbits.PRAYER_PIETY) == 1)) return;
        prayIndex = 35455011;
        Microbot.getMouse().click();
        sleep(100);
        prayIndex = 0;
    }

    @Deprecated(since = "Use fastPray instead", forRemoval = true)
    public static void turnOnFastAugury(){
        if (Microbot.getClientThread().runOnClientThread(() ->
                Microbot.getClient().getVarbitValue(Varbits.PRAYER_AUGURY) == 1)) return;
        prayIndex = 35455012;
        Microbot.getMouse().click();
        sleep(100);
        prayIndex = 0;
    }

    public static void turnOffMagePrayer() {
        if (Microbot.getClientThread().runOnClientThread(() ->
                Microbot.getClient().getVarbitValue(PRAYER_PROTECT_FROM_MAGIC) == 0)) return;
        clickMagePrayer();
    }
    public static void turnOffMeleePrayer() {
        if (Microbot.getClientThread().runOnClientThread(() ->
                Microbot.getClient().getVarbitValue(Varbits.PRAYER_PROTECT_FROM_MELEE) == 0)) return;
        clickMeleePrayer();
    }
    public static void turnOffRangedPrayer() {
        if (Microbot.getClientThread().runOnClientThread(() ->
                Microbot.getClient().getVarbitValue(Varbits.PRAYER_PROTECT_FROM_MISSILES) == 0)) return;
        clickRangePrayer();
    }
    public static void turnOnMagePrayer() {
        if (Microbot.getClientThread().runOnClientThread(() ->
                Microbot.getClient().getVarbitValue(PRAYER_PROTECT_FROM_MAGIC) == 1)) return;
        clickMagePrayer();
    }
    public static void turnOnMeleePrayer() {
        if (Microbot.getClientThread().runOnClientThread(() ->
                Microbot.getClient().getVarbitValue(Varbits.PRAYER_PROTECT_FROM_MELEE) == 1)) return;
        clickMeleePrayer();
    }
    public static void turnOnRangedPrayer() {
        if (Microbot.getClientThread().runOnClientThread(() ->
                Microbot.getClient().getVarbitValue(Varbits.PRAYER_PROTECT_FROM_MISSILES) == 1)) return;
        clickRangePrayer();
    }
    public static void clickMagePrayer() {
        Tab.switchToPrayerTab();
        Widget magePray = Microbot.getClient().getWidget(35454997);
        if (magePray == null) return;
        Microbot.getMouse().click(magePray.getBounds());
    }

    public static void clickRangePrayer() {
        Tab.switchToPrayerTab();
        Widget rangePray = Microbot.getClient().getWidget(35454998);
        if (rangePray == null) return;
        Microbot.getMouse().click(rangePray.getBounds());
    }

    public static void clickMeleePrayer() {
        Tab.switchToPrayerTab();
        Widget rangePray = Microbot.getClient().getWidget(35454999);
        if (rangePray == null) return;
        Microbot.getMouse().click(rangePray.getBounds());
    }

    public static boolean isQuickPrayerEnabled() {
        return Microbot.getVarbitValue(QUICK_PRAYER) == QUICK_PRAYER_ENABLED.getValue();
    }

    public static void handleMenuSwapper(MenuEntry menuEntry) {
        if (prayIndex == 0) return;
        menuEntry.setOption("Activate");
        menuEntry.setIdentifier(1);
        menuEntry.setParam0(-1);
        menuEntry.setType(MenuAction.CC_OP);
        menuEntry.setParam1(Rs2Prayer.prayIndex);
    }
}
