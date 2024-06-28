package net.runelite.client.plugins.microbot.fishing.barbarian;

import net.runelite.api.NPC;
import net.runelite.client.game.FishingSpot;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera;
import net.runelite.client.plugins.microbot.util.inventory.DropOrder;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

import java.util.concurrent.TimeUnit;

import static net.runelite.client.plugins.microbot.util.npc.Rs2Npc.validateInteractable;

public class BarbarianFishingScript extends Script {

    public static String version = "1.0.0";

    public boolean run(BarbarianFishingConfig config) {
        initialPlayerLocation = null;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {

                if (!super.run()) return;
                if (!Microbot.isLoggedIn()) return;

                if (initialPlayerLocation == null) {
                    initialPlayerLocation = Rs2Player.getWorldLocation();
                }

                if (Rs2Player.isMoving() || Rs2Player.isAnimating() || !Rs2Inventory.hasItem("feather")) {
                    return;
                }


                NPC fishingspot = null;
                for (int fishingSpotId : FishingSpot.BARB_FISH.getIds()) {
                    fishingspot = Rs2Npc.getNpc(fishingSpotId);
                    if (fishingspot != null) {
                        break;
                    }
                }

                if (Rs2Inventory.isFull()) {
                    if (config.dropOrder() == DropOrder.RANDOM)
                        Rs2Inventory.dropAllExcept(false, DropOrder.random(), "rod", "net", "pot", "harpoon", "feather", "bait", "vessel");
                    else
                        Rs2Inventory.dropAllExcept(false, config.dropOrder(), "rod", "net", "pot", "harpoon", "feather", "bait", "vessel");
                    return;
                }


                if (fishingspot != null && !Rs2Camera.isTileOnScreen(fishingspot.getLocalLocation())) {
                    validateInteractable(fishingspot);
                }
                Rs2Npc.interact(fishingspot);

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }
}