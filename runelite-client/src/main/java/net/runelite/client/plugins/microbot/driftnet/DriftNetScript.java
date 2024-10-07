package net.runelite.client.plugins.microbot.driftnet;

import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.runelite.api.ObjectID;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static net.runelite.client.plugins.microbot.util.Global.sleepGaussian;

public class DriftNetScript extends Script {

    public static double version = 1.1;

    int tries = 0;

    public boolean run(DriftNetConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;

                if (tries > 5) {
                    Microbot.log("Script shutdown, no nets found");
                    shutdown();
                }

                if (!Rs2Inventory.hasItem(ItemID.DRIFT_NET)) {
                    Rs2GameObject.interact(ObjectID.ANNETTE, "Nets");
                    sleepUntil(() -> Rs2Widget.getWidget(20250629) != null);
                    Rs2Widget.clickWidgetFast(Rs2Widget.getWidget(20250629), 0, 4);
                    sleepGaussian(1500, 300);
                    Rs2Keyboard.keyPress(KeyEvent.VK_ESCAPE);
                    tries++;
                    return;
                }

                // just a quick solution to avoid trying to fetch nets when there are no nets left
                // Proper solution would be to check the widget for nets available.
                if (tries > 0)
                    tries = 0;

                if (DriftNetPlugin.getNETS().stream().anyMatch(x -> x.getStatus() == DriftNetStatus.FULL || x.getStatus() == DriftNetStatus.UNSET)) {
                    for (DriftNet net : DriftNetPlugin.getNETS()) {
                        final Shape polygon = Microbot.getClientThread().runOnClientThread(() -> net.getNet().getConvexHull());

                        if (polygon != null) {
                            if (net.getStatus() == DriftNetStatus.FULL) {
                                Rs2GameObject.interact(net.getNet());
                                sleep(Random.randomGaussian(600, 150) * Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(net.getNet().getWorldLocation()));
                                break;
                            } else if (net.getStatus() == DriftNetStatus.UNSET) {
                                Rs2GameObject.interact(net.getNet());
                                sleep(Random.randomGaussian(600, 150) * Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(net.getNet().getWorldLocation()));
                                break;
                            }
                        }
                    }
                    return;
                }

                for (NPC fish : DriftNetPlugin.getFish().stream().sorted(Comparator.comparingInt(value -> value.getLocalLocation().distanceTo(Microbot.getClient().getLocalPlayer().getLocalLocation()))).collect(Collectors.toList())) {
                    if (!DriftNetPlugin.getTaggedFish().containsKey(fish) &&  Rs2Npc.getNpcByIndex(fish.getIndex()) != null) {
                        Rs2Npc.interact(fish, "Chase");
                        sleepGaussian(1500, 300);
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
