package net.runelite.client.plugins.microbot.tanner;

import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.tanner.enums.Location;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.bank.enums.BankLocation;
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.util.concurrent.TimeUnit;

public class TannerScript extends Script {

    public static double version = 1.0;

    WorldPoint tannerLocation = new WorldPoint(3276, 3192, 0);


    public boolean run(TannerConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;

                if (config.LOCATION() == Location.AL_KHARID)
                    tanInAlkharid(config);

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 600, TimeUnit.MILLISECONDS);
        return true;
    }

    public void tanInAlkharid(TannerConfig config) {
        boolean hasHides = Rs2Inventory.hasItem(config.HIDE_TYPE().getItemName());
        boolean hasMoney = Rs2Inventory.hasItem(995);
        boolean hasStamina = Rs2Inventory.hasItem("stamina");
        NPC tanner = Rs2Npc.getNpc(NpcID.ELLIS);
        boolean isTannerVisibleOnScreen = tanner != null && Rs2Camera.isTileOnScreen(tanner.getLocalLocation());
        boolean isBankVisible = Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(BankLocation.AL_KHARID.getWorldPoint()) < 5;
        boolean hasRunEnergy = Microbot.getClient().getEnergy() > 4000;
        if (hasRunEnergy) Rs2Player.toggleRunEnergy(true);
        if (isBankVisible) {
            if ((!hasRunEnergy && !hasStamina) || !hasMoney || !hasHides) {
                Rs2Bank.openBank();
            }

            if (Rs2Bank.isOpen()) {

                if (!hasMoney) {
                    Rs2Bank.withdrawAll(false,"Coins");
                }

                if (!hasHides || !hasRunEnergy) {
                    Rs2Bank.depositAll(config.HIDE_TYPE().getName());
                    Rs2Bank.depositAll("vial");
                    if (!hasRunEnergy && !hasStamina) {
                        Rs2Bank.withdrawItem( "Stamina potion(4)");
                    }
                    if (!Rs2Bank.hasItem(config.HIDE_TYPE().getItemName())) {
                        Rs2Bank.closeBank();
                        Rs2Player.logout();
                        shutdown();
                        return;
                    }
                    Rs2Bank.withdrawAll(false, config.HIDE_TYPE().getItemName());
                }
            }
        }
        if (hasHides && !isTannerVisibleOnScreen) {
            Rs2Walker.walkTo(tannerLocation);
        }
        if (hasHides && isTannerVisibleOnScreen) {
            if (Rs2Widget.hasWidget("What hides would you like tanning?")) {
                Widget widget = Rs2Widget.findWidget((config.HIDE_TYPE().getWidgetName()));
                if (widget != null) {
                    // TODO: needs to be reworked to specificy all option
                    Microbot.showMessage("needs to be reworked to specificy all option");
                    //Rs2Widget.clickWidget(widget.getId(), "all");
                    sleepUntil(() -> Rs2Inventory.hasItem(config.HIDE_TYPE().getItemName()));
                }
            } else {
                if (Rs2Npc.interact(NpcID.ELLIS, "trade")) {
                    sleepUntil(() -> Rs2Widget.hasWidget("What hides would you like tanning?"));
                    Widget widget = Rs2Widget.findWidget((config.HIDE_TYPE().getWidgetName()));
                    if (widget != null) {
                        // TODO: needs to be reworked to specificy all option
                        Microbot.showMessage("needs to be reworked to specificy all option");
                        //Rs2Widget.clickWidget(widget.getId(), "all");
                        sleepUntil(() -> Rs2Inventory.hasItem(config.HIDE_TYPE().getItemName()));
                    }
                }
            }
        }
        if (!hasHides && !isBankVisible) {
            Rs2Walker.walkTo(BankLocation.AL_KHARID.getWorldPoint());
        }
    }
}
