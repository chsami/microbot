package net.runelite.client.plugins.microbot.fishing.eel;

import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.runelite.client.game.FishingSpot;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.fishing.eel.enums.EelFishingSpot;
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

import java.util.concurrent.TimeUnit;

import static net.runelite.client.plugins.microbot.util.npc.Rs2Npc.validateInteractable;

public class EelFishingScript extends Script {

    public static String version = "1.0.0";
    private EelFishingConfig config;

    public static boolean hasRequiredGloves() {
        return Rs2Equipment.isWearing(ItemID.ICE_GLOVES) || Rs2Equipment.isWearing(ItemID.SMITHS_GLOVES_I);
    }

    public boolean run(EelFishingConfig config) {
        this.config = config;
        //Rs2Antiban.resetAntiban();
        //Rs2Antiban.advancedPlayStyleSetup(Activity.GENERAL_FISHING);
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run() || !Microbot.isLoggedIn() || !Rs2Inventory.hasItem("bait") || !Rs2Inventory.hasItem("rod")) {
                return;
            }

            //if(Rs2Antiban.isActionCooldownActive)
            //    return;

            if (Rs2Player.isInteracting())
                return;

            if (config.fishingSpot().equals(EelFishingSpot.INFERNAL_EEL) && !hasRequiredGloves()) {
                Microbot.log("You need ice gloves to fish infernal eels.");
                return;
            }

            if (Rs2Inventory.isFull()) {
                processEels(config);
                return;
            }

            NPC fishingspot = findFishingSpot();
            if (fishingspot == null) {
                return;
            }

            if (!Rs2Camera.isTileOnScreen(fishingspot.getLocalLocation())) {
                validateInteractable(fishingspot);
            }

            if (Rs2Npc.interact(fishingspot)) {
                //Rs2Antiban.actionCooldown();
            }


        }, 0, 600, TimeUnit.MILLISECONDS);
        return true;
    }

    public void onGameTick() {

    }

    private NPC findFishingSpot() {
        for (int fishingSpotId : getFishingSpotIds(config.fishingSpot())) {
            NPC fishingspot = Rs2Npc.getNpc(fishingSpotId);
            if (fishingspot != null) {
                return fishingspot;
            }
        }
        return null;
    }

    private int[] getFishingSpotIds(EelFishingSpot spot) {
        switch (spot) {
            case INFERNAL_EEL:
                return FishingSpot.INFERNAL_EEL.getIds();
            case SACRED_EEL:
                return FishingSpot.SACRED_EEL.getIds();
            default:
                return new int[0];
        }
    }

    private void processEels(EelFishingConfig config) {
        if (config.fishingSpot() == EelFishingSpot.INFERNAL_EEL) {
            if (Rs2Inventory.hasItem(ItemID.HAMMER)) {
                Rs2Inventory.combineClosest("Infernal eel", "Hammer");
                sleepUntil(() -> !Rs2Inventory.hasItem("Infernal eel"), 40000); // Wait until all eels are processed
            }
        } else if (config.fishingSpot() == EelFishingSpot.SACRED_EEL) {
            if (Rs2Inventory.hasItem(ItemID.KNIFE)) {
                Rs2Inventory.combineClosest("Sacred eel", "Knife");
                sleepUntil(() -> !Rs2Inventory.hasItem("Sacred eel"), 40000); // Wait until all eels are processed
            }
        }
    }

    public void shutdown() {
        //Rs2Antiban.resetAntiban();
        super.shutdown();
    }
}
