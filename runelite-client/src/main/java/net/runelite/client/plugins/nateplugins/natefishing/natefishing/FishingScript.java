package net.runelite.client.plugins.nateplugins.natefishing.natefishing;

import net.runelite.api.NPC;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.camera.Camera;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;

import java.util.concurrent.TimeUnit;

import static net.runelite.client.plugins.microbot.util.npc.Rs2Npc.validateInteractable;


public class FishingScript extends Script {

    public static double version = 1.3;

    public boolean run(FishingConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;

            try {
                if (Microbot.isWalking() || Microbot.isAnimating() || Microbot.pauseAllScripts) {
                    return;
                }
                if (Inventory.isFull()) {
                    if (config.Fish().getName().equals("shrimp")) {
                        Inventory.dropAllStartingFrom(1);
                        return;
                    } else {
                        Inventory.dropAllStartingFrom(5);
                        return;
                    }

                } else {
                    for (int fishingSpotId:
                            config.Fish().getFishingSpot() ) {
                        NPC fishingspot = Rs2Npc.getNpc(fishingSpotId);
                        if(fishingspot != null && !Camera.isTileOnScreen(fishingspot.getLocalLocation())){
                            validateInteractable(fishingspot);
                        }
                        if (fishingspot == null){
                            Microbot.status = "Finding Fishing Spot...";
                            continue;
                        }
                        Rs2Npc.interact(fishingspot,config.Fish().getAction());
                        Microbot.status = "Fishing...";
                        return;
                    }

                }

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }
}
