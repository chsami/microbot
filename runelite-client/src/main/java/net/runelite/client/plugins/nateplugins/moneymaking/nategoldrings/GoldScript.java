package net.runelite.client.plugins.nateplugins.moneymaking.nategoldrings;



import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.bank.enums.BankLocation;

import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;

import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.util.concurrent.TimeUnit;

public class GoldScript extends Script {

    public static double version = 1.2;

    WorldPoint furnaceLocation = new WorldPoint(3274, 3186, 0);

    public boolean run(GoldConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            if (!Microbot.isLoggedIn()) return;
            try {
                Rs2Widget goldring = (Rs2Widget) Microbot.getClient().getWidget(446,7);
                TileObject furnace = (TileObject) Rs2GameObject.findObjectById(24009);
                boolean isBankVisible = Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(BankLocation.AL_KHARID.getWorldPoint()) < 5;
                boolean hasRunEnergy = Microbot.getClient().getEnergy() > 4000;
                boolean hasBars = Rs2Inventory.hasItem("gold bar");
                if (hasRunEnergy) Rs2Player.toggleRunEnergy(true);
                if (Microbot.pauseAllScripts) return;

                if (hasBars && Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(furnaceLocation) > 3) {
                    Rs2Walker.walkTo(furnaceLocation);
                }
                if (hasBars && Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(furnaceLocation) < 3) {
                    if (Rs2Widget.hasWidget("What would you like to make?")) {
                        Widget widget = Rs2Widget.getWidget(446,7);
                        sleepUntil(() -> Rs2Widget.hasWidget("What would you like to make?"));
                        if (widget != null) {
                            Microbot.getMouse().click(51,87);
                            sleepUntil(() -> !Rs2Inventory.hasItem("gold bar"),60000);
                        }
                    } else {
                        if (Rs2GameObject.interact("Furnace")) {
                            sleepUntil(() -> Rs2Widget.hasWidget("What would you like to make?"));
                            Widget widget = Rs2Widget.getWidget(446,7);
                            if (widget != null) {
                                Microbot.getMouse().click(51,87);
                                sleepUntil(() -> !Rs2Inventory.hasItem("gold bar"),60000);
                            }
                        }
                    }
                }
                if (!hasBars && !isBankVisible) {
                    Rs2Walker.walkTo(BankLocation.AL_KHARID.getWorldPoint());
                }
                if (!hasBars && isBankVisible) {
                    if(Rs2Bank.isOpen()) {
                        Rs2Bank.depositAll("gold ring");
                        sleepUntil(() -> !Rs2Inventory.hasItem("gold ring"));
                        if(Rs2Bank.hasItem("gold bar")){
                            Rs2Bank.withdrawItemAll("gold bar");
                            sleepUntil(() -> Rs2Inventory.hasItem("gold bar"));
                        } else {
                            Microbot.getNotifier().notify("Run out of Materials");
                            shutdown();
                        }
                        Rs2Bank.closeBank();
                    } else {
                        Rs2Bank.openBank();
                    }
                }

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }
}
