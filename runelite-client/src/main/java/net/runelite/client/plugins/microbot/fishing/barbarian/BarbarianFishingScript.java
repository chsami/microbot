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

    public static String version = "1.1.0";
    public static int timeout = 0;
    private BarbarianFishingConfig config;

    public boolean run(BarbarianFishingConfig config) {
        this.config = config;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run() || !Microbot.isLoggedIn() || !Rs2Inventory.hasItem("feather") || !Rs2Inventory.hasItem("rod")) {
                return;
            }

            if (timeout > 0) {
                return;
            }

            if (Rs2Inventory.isFull()) {
                dropInventoryItems(config);
                return;
            }

            NPC fishingspot = findFishingSpot();
            if (fishingspot == null) {
                return;
            }

            if (!Rs2Camera.isTileOnScreen(fishingspot.getLocalLocation())) {
                validateInteractable(fishingspot);
            }

            Rs2Npc.interact(fishingspot);

        }, 0, 600, TimeUnit.MILLISECONDS);
        return true;
    }

    public void onGameTick() {
        if (timeout > 0 && !Rs2Player.isInteracting()) {
            timeout--;
        }
        if (Rs2Player.isInteracting() && timeout == 0) {
            timeout = config.playStyle().getRandomTickInterval();
        }
    }

    private NPC findFishingSpot() {
        for (int fishingSpotId : FishingSpot.BARB_FISH.getIds()) {
            NPC fishingspot = Rs2Npc.getNpc(fishingSpotId);
            if (fishingspot != null) {
                return fishingspot;
            }
        }
        return null;
    }

    private void dropInventoryItems(BarbarianFishingConfig config) {
        DropOrder dropOrder = config.dropOrder() == DropOrder.RANDOM ? DropOrder.random() : config.dropOrder();
        Rs2Inventory.dropAllExcept(false, dropOrder, "rod", "net", "pot", "harpoon", "feather", "bait", "vessel");
    }
}