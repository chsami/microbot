package net.runelite.client.plugins.nateplugins.skilling.natefishing;

import net.runelite.api.NPC;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.nateplugins.skilling.natefishing.enums.Fishs;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static net.runelite.client.plugins.microbot.util.npc.Rs2Npc.validateInteractable;


public class AutoFishingScript extends Script {

    public static String version = "1.4.0";

    public boolean run(AutoFishingConfig config) {
        initialPlayerLocation = null;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {

                if (!super.run()) return;
                if (!Microbot.isLoggedIn()) return;

                if (initialPlayerLocation == null) {
                    initialPlayerLocation = Rs2Player.getWorldLocation();
                }

                if (Rs2Player.isMoving() || Rs2Player.isAnimating() || config.Fish() == Fishs.TROUT && !Rs2Inventory.hasItem("feather")) {
                    return;
                }

                List<String> itemNames = Arrays.stream(config.itemsToBank().split(",")).map(String::toLowerCase).collect(Collectors.toList());

                NPC fishingspot = null;
                for (int fishingSpotId : config.Fish().getFishingSpot()) {
                    fishingspot = Rs2Npc.getNpc(fishingSpotId);
                    if (fishingspot != null) {
                        break;
                    }
                }

                if (config.useBank()) {
                    if (fishingspot == null || Rs2Inventory.isFull()) {
                        if (!Rs2Bank.bankItemsAndWalkBackToOriginalPosition(itemNames, initialPlayerLocation))
                            return;
                    }
                } else if (Rs2Inventory.isFull()) {
                    Rs2Inventory.dropAllExcept("rod", "net", "pot", "harpoon", "feather", "bait", "vessel");
                    return;
                }


                if (fishingspot != null && !Rs2Camera.isTileOnScreen(fishingspot.getLocalLocation())) {
                    validateInteractable(fishingspot);
                }
                Rs2Npc.interact(fishingspot, config.Fish().getAction());

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }
}
