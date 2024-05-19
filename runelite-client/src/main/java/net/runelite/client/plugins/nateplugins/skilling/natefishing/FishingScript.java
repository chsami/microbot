package net.runelite.client.plugins.nateplugins.skilling.natefishing;

import net.runelite.api.NPC;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

import java.util.concurrent.TimeUnit;

import static net.runelite.client.plugins.microbot.util.npc.Rs2Npc.validateInteractable;


public class FishingScript extends Script {

    public static double version = 1.3;

    public boolean run(FishingConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            if (!Microbot.isLoggedIn()) return;
            try {
                    if (Rs2Player.isMoving() || Rs2Player.isAnimating()) {
                        return;
                    }
                    if (Random.random(1, 300) == 2) {
                        Microbot.pauseAllScripts = true;
                        if (Random.random(1, 10) > 7)
                            Rs2Player.logout();
                        sleep(60000, 600000);
                        Microbot.pauseAllScripts = false;
                        return;
                    }
                    if (Rs2Inventory.isFull()) {
                        if (config.Fish().getName().equals("shrimp")) {
                            Rs2Inventory.dropAll(x -> x.slot > 0);
                        } else {
                            Rs2Inventory.dropAll(x -> x.slot > 4);
                        }

                    } else {
                        for (int fishingSpotId :
                                config.Fish().getFishingSpot()) {
                            NPC fishingspot = Rs2Npc.getNpc(fishingSpotId);
                            if (fishingspot != null && !Rs2Camera.isTileOnScreen(fishingspot.getLocalLocation())) {
                                validateInteractable(fishingspot);
                            }
                            Rs2Npc.interact(fishingSpotId, config.Fish().getAction());
                            Microbot.status = "Fishing...";
                        }

                    }

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }
}
