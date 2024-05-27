package net.runelite.client.plugins.microbot.driftnet;

import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.runelite.api.ObjectID;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class DriftNetScript extends Script {

    public static double version = 1.0;

    public boolean run(DriftNetConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;

                if (!Rs2Inventory.hasItem(ItemID.DRIFT_NET)) {
                    Rs2GameObject.interact(ObjectID.ANNETTE, "Nets");
                    sleepUntil(() -> Rs2Widget.getWidget(20250629) != null);
                    Rs2Bank.withdrawAll(ItemID.DRIFT_NET);
                    sleep(1000);
                    Rs2Keyboard.keyPress(KeyEvent.VK_ESCAPE);
                    return;
                }

                if (DriftNetPlugin.getNETS().stream().anyMatch(x -> x.getStatus() == DriftNetStatus.FULL || x.getStatus() == DriftNetStatus.UNSET)) {
                    for (DriftNet net : DriftNetPlugin.getNETS()) {
                        final Shape polygon = net.getNet().getConvexHull();

                        if (polygon != null) {
                            if (net.getStatus() == DriftNetStatus.FULL) {
                                Rs2GameObject.interact(net.getNet());
                                sleep(500 * Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(net.getNet().getWorldLocation()));
                                break;
                            } else if (net.getStatus() == DriftNetStatus.UNSET) {
                                Rs2GameObject.interact(net.getNet());
                                sleep(500 * Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(net.getNet().getWorldLocation()));
                                break;
                            }
                        }
                    }
                    return;
                }

                for (NPC fish : DriftNetPlugin.getFish().stream().sorted(Comparator.comparingInt(value -> value.getLocalLocation().distanceTo(Microbot.getClient().getLocalPlayer().getLocalLocation()))).collect(Collectors.toList())) {
                    if (!DriftNetPlugin.getTaggedFish().containsKey(fish) &&  Rs2Npc.getNpcByIndex(fish.getIndex()) != null) {
                        Rs2Npc.interact(fish, "Chase");
                        sleepUntil(() -> DriftNetPlugin.getTaggedFish().containsKey(fish));
                        break;
                    }
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 600, TimeUnit.MILLISECONDS);

        return true;
    }
}
