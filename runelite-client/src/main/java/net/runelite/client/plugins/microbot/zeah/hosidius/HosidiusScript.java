package net.runelite.client.plugins.microbot.zeah.hosidius;

import net.runelite.api.GameObject;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class HosidiusScript extends Script {
    int repairCounter = 0;
    final int hosidiousFavour = 4895;

    public boolean run(HosidiusConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;
                if (Microbot.getVarbitValue(hosidiousFavour) < 50) {
                    plough(); //south east field the middle plough
                } else {
                    fertiliser();
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 600, TimeUnit.MILLISECONDS);
        return true;
    }


    public void plough() {
        GameObject plough = Rs2GameObject.findObjectById(27437, 1774);

        if (plough == null) return;

        WorldPoint standLocation1 = new WorldPoint(1774, 3521, 0);
        WorldPoint standLocation2 = new WorldPoint(1774, 3539, 0);

        if (plough.getWorldLocation().equals(new WorldPoint(1774, 3523, 0)) && !Microbot.getClient().getLocalPlayer().getWorldLocation().equals(standLocation1)) {
            sleep(1000, 1800);
            Polygon poly = Perspective
                    .getCanvasTilePoly(Microbot.getClient(),
                            LocalPoint.fromWorld(Microbot.getClient(), standLocation1));
            Microbot.getMouse().click(poly.getBounds());
            sleep(2000);
            sleepUntil(() -> Microbot.getClient().getLocalPlayer().getWorldLocation().equals(standLocation1), 5000);
            Rs2GameObject.interact(plough);
            repairCounter = 0;
            return;
        }
        if (plough.getWorldLocation().equals(new WorldPoint(1774, 3537, 0)) && !Microbot.getClient().getLocalPlayer().getWorldLocation().equals(standLocation2)) {
            sleep(1000, 1800);
            Polygon poly = Perspective
                    .getCanvasTilePoly(Microbot.getClient(),
                            LocalPoint.fromWorld(Microbot.getClient(), standLocation2));
            Microbot.getMouse().click(poly.getBounds());
            sleep(2000);
            sleepUntil(() -> Microbot.getClient().getLocalPlayer().getWorldLocation().equals(standLocation2), 5000);
            Rs2GameObject.interact(plough);
            repairCounter = 0;
            return;
        }

        if (Microbot.getClient().getLocalPlayer().getAnimation() != 3065 && !Microbot.isMoving()) {
            //looks like we are frozen
            repairCounter++;
        } else {
            repairCounter = 0;
        }

        if (repairCounter >= 5 && !Microbot.isMoving()) {
            Rs2GameObject.interact(plough);
            sleep(3000, 4000);
            Rs2GameObject.interact(plough);
            repairCounter = 0;
        }
    }

    public void fertiliser() {
        String saltpetre = "saltpetre";
        String compost = "compost";
        if (!Rs2Inventory.hasItem(saltpetre) || !Rs2Inventory.hasItem(compost)) {
            Rs2Bank.openBank();
            if (Rs2Bank.isOpen()) {
                Rs2Bank.depositAll();
                Rs2Bank.withdrawX(false, "saltpetre", 14);
                Rs2Bank.withdrawX(false, "compost", 14);
                Rs2Bank.closeBank();
            }
        } else {
            if (!Rs2Inventory.isFull()) {
                Microbot.getNotifier().notify("Hosidius script has finished");
                shutDown();
                return;
            }
            if (!Rs2Bank.isOpen()) {
                while (Rs2Inventory.hasItem(saltpetre) && Rs2Inventory.hasItem(compost)) {
                    for (int i = 0; i < 14; i++) {
                        Rs2Item rs2Item1 = Rs2Inventory.getItemInSlot(i);
                        Rs2Item rs2Item2 = Rs2Inventory.getItemInSlot(i + 14);
                        Rs2Inventory.combine(rs2Item1, rs2Item2);
                        sleep(400, 600);
                    }
                    sleep(1000);
                }
            }
        }
    }

    public void shutDown() {
        super.shutdown();
    }
}
