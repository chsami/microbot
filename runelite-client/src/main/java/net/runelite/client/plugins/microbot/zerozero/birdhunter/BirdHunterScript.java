package net.runelite.client.plugins.microbot.zerozero.birdhunter;

import net.runelite.api.GameObject;
import net.runelite.api.Skill;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.antiban.Rs2Antiban;
import net.runelite.client.plugins.microbot.util.antiban.Rs2AntibanSettings;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.tile.Rs2Tile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class BirdHunterScript extends Script {


    private static final int SUCCESSFUL_TRAP_1 = 9348;
    private static final int SUCCESSFUL_TRAP_2 = 9376;
    private static final int SUCCESSFUL_TRAP_3 = 9378;
    private static final int SUCCESSFUL_TRAP_4 = 9374;
    private static final int SUCCESSFUL_TRAP_5 = 9373;

    private static final int CATCHING_TRAP_1 = 9349;
    private static final int CATCHING_TRAP_2 = 9347;
    private static final int CATCHING_TRAP_3 = 9377;
    private static final int CATCHING_TRAP_4 = 9379;
    private static final int CATCHING_TRAP_5 = 9375;

    private static final int BIRD_SNARE = 10006;
    private static final int FAILED_TRAP = 9344;
    private static final int IDLE_TRAP = 9345;


    public static String version = "1.0.0";
    private WorldArea dynamicHuntingArea;
    private WorldPoint huntingCenter;

    public boolean run(BirdHunterConfig config) {
        Microbot.log("Bird Hunter script started.");

        if (!hasRequiredSnares()) {
            Microbot.log("Not enough bird snares in inventory. Stopping the script.");
            return false;  // Stop the script if there aren't enough snares
        }
        huntingCenter = Rs2Player.getWorldLocation();
        updateHuntingArea(config);

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            Rs2Antiban.resetAntibanSettings();
            Rs2Antiban.antibanSetupTemplates.applyHunterSetup();
            Rs2AntibanSettings.actionCooldownChance = 0.1;

            try {
                if (!super.run() || !Microbot.isLoggedIn()) return;

                if (!isInHuntingArea()) {
                    Microbot.log("Player is outside the designated hunting area.");
                    walkBackToArea();
                    return;
                }

                handleTraps(config);
                checkForBonesAndHandleInventory(config);

            } catch (Exception ex) {
                Microbot.log(ex.getMessage());
            }
        }, 0, 100, TimeUnit.MILLISECONDS);

        return true;
    }

    private boolean hasRequiredSnares() {
        int hunterLevel = Rs2Player.getRealSkillLevel(Skill.HUNTER);
        int allowedSnares = getAvailableTraps(hunterLevel);  // Calculate the allowed number of snares

        int snaresInInventory = Rs2Inventory.count(BIRD_SNARE);
        Microbot.log("Allowed snares: " + allowedSnares + ", Snares in inventory: " + snaresInInventory);

        return snaresInInventory >= allowedSnares;  // Return true if enough snares, false otherwise
    }

    public void updateHuntingArea(BirdHunterConfig config) {
        int radius = config.radius();
        dynamicHuntingArea = new WorldArea(
                huntingCenter.getX() - radius,
                huntingCenter.getY() - radius,
                radius * 2, radius * 2,
                huntingCenter.getPlane()
        );
        Microbot.log("Hunting area radius updated to " + radius + " around center: " + huntingCenter);
    }

    public WorldArea getDynamicHuntingArea() {
        return dynamicHuntingArea;
    }

    private boolean isInHuntingArea() {
        WorldPoint playerLocation = Rs2Player.getWorldLocation();
        return dynamicHuntingArea.contains(playerLocation);
    }

    private void walkBackToArea() {
        WorldPoint centerTile = dynamicHuntingArea.toWorldPoint();
        WorldPoint walkableTile = getSafeWalkableTile(centerTile, dynamicHuntingArea);

        if (walkableTile != null) {
            Rs2Walker.walkFastCanvas(walkableTile);
            Rs2Player.waitForWalking();
            Microbot.log("Walking back to the center of the hunting area: " + walkableTile);
        } else {
            Microbot.log("No safe walkable tile found inside the hunting area.");
        }
    }

    private void handleTraps(BirdHunterConfig config) {
        List<GameObject> successfulTraps = new ArrayList<>();
        successfulTraps.addAll(Rs2GameObject.getGameObjects(SUCCESSFUL_TRAP_1));
        successfulTraps.addAll(Rs2GameObject.getGameObjects(SUCCESSFUL_TRAP_2));
        successfulTraps.addAll(Rs2GameObject.getGameObjects(SUCCESSFUL_TRAP_3));
        successfulTraps.addAll(Rs2GameObject.getGameObjects(SUCCESSFUL_TRAP_4));
        successfulTraps.addAll(Rs2GameObject.getGameObjects(SUCCESSFUL_TRAP_5));

        List<GameObject> catchingTraps = new ArrayList<>();
        catchingTraps.addAll(Rs2GameObject.getGameObjects(CATCHING_TRAP_1));
        catchingTraps.addAll(Rs2GameObject.getGameObjects(CATCHING_TRAP_2));
        catchingTraps.addAll(Rs2GameObject.getGameObjects(CATCHING_TRAP_3));
        catchingTraps.addAll(Rs2GameObject.getGameObjects(CATCHING_TRAP_4));
        catchingTraps.addAll(Rs2GameObject.getGameObjects(CATCHING_TRAP_5));

        List<GameObject> failedTraps = Rs2GameObject.getGameObjects(FAILED_TRAP);
        List<GameObject> idleTraps = Rs2GameObject.getGameObjects(IDLE_TRAP);

        int availableTraps = getAvailableTraps(Rs2Player.getRealSkillLevel(Skill.HUNTER));
        int totalTraps = successfulTraps.size() + failedTraps.size() + idleTraps.size() + catchingTraps.size();

        if (Rs2GroundItem.exists(BIRD_SNARE, 20)) {
            pickUpBirdSnare();
            return;
        }

        if (totalTraps < availableTraps) {
            setTrap(config);
            return;
        }

        if (!successfulTraps.isEmpty()) {
            for (GameObject successfulTrap : successfulTraps) {
                if (interactWithTrap(successfulTrap)) {
                    setTrap(config);
                    return;
                }
            }
        }

        if (!failedTraps.isEmpty()) {
            for (GameObject failedTrap : failedTraps) {
                if (interactWithTrap(failedTrap)) {
                    setTrap(config);
                    return;
                }
            }
        }
    }


    private void setTrap(BirdHunterConfig config) {
        if (!Rs2Inventory.contains(BIRD_SNARE)) return;

        if (Rs2Player.isStandingOnGameObject()) {
            movePlayerOffObject();
        }

        layBirdSnare();
    }

    private void layBirdSnare() {
        Rs2Item birdSnare = Rs2Inventory.get(BIRD_SNARE);
        if (Rs2Inventory.interact(birdSnare, "Lay")) {
            if (sleepUntil(Rs2Player::isAnimating, 2000)) {
                sleepUntil(() -> !Rs2Player.isAnimating(), 3000);
                Microbot.log("Bird snare was successfully laid.");
                sleep(1000, 1500);
            }
        } else {
            Microbot.log("Failed to interact with the bird snare.");
        }
    }

    private boolean isGameObjectAt(WorldPoint point) {
        return Rs2GameObject.findObjectByLocation(point) != null;
    }


    private WorldPoint getSafeWalkableTile(WorldPoint targetPoint, WorldArea huntingArea) {
        int searchRadius = 1;

        for (int x = targetPoint.getX() - searchRadius; x <= targetPoint.getX() + searchRadius; x++) {
            for (int y = targetPoint.getY() - searchRadius; y <= targetPoint.getY() + searchRadius; y++) {
                WorldPoint candidateTile = new WorldPoint(x, y, targetPoint.getPlane());
                LocalPoint localPoint = LocalPoint.fromWorld(Microbot.getClient(), candidateTile);

                if (localPoint != null && huntingArea.contains(candidateTile)) {
                    if (Rs2Tile.isWalkable(localPoint) && !isGameObjectAt(candidateTile)) {
                        return candidateTile;
                    }
                }
            }
        }
        return null;
    }

    private void movePlayerOffObject() {
        WorldPoint nearestWalkable = getSafeWalkableTile(Rs2Player.getWorldLocation(), dynamicHuntingArea);
        if (nearestWalkable != null) {
            Rs2Walker.walkFastCanvas(nearestWalkable);
            Rs2Player.waitForWalking();
        } else {
            Microbot.log("No safe walkable tile found inside the hunting area.");
        }
    }



    private boolean interactWithTrap(GameObject birdSnare) {
        Microbot.log("Attempting to interact with bird snare at: " + birdSnare.getWorldLocation());

        if (Rs2GameObject.interact(birdSnare)) {
            Microbot.log("Interaction initiated successfully.");

            if (!sleepUntil(Rs2Player::isAnimating, 2000)) {
                Microbot.log("Failed to start animating during interaction with bird snare.");
                return false;
            }

            if (!sleepUntil(() -> !Rs2Player.isAnimating(), 3000)) {
                Microbot.log("Failed to finish interacting with the bird snare.");
                return false;
            }

            if (Rs2Player.waitForXpDrop(Skill.HUNTER, true)) {
                Microbot.log("Bird snare interaction was successful.");
                return true;
            } else {
                Microbot.log("No Hunter XP drop detected after interacting with bird snare.");
            }
        } else {
            Microbot.log("Failed to initiate interaction with bird snare.");
        }

        return false;
    }

    private void pickUpBirdSnare() {
        if (Rs2GroundItem.exists(BIRD_SNARE, 20)) {
            Rs2GroundItem.loot(BIRD_SNARE);
            Microbot.log("Picked up bird snare from the ground.");
        }
    }

    private void checkForBonesAndHandleInventory(BirdHunterConfig config) {
        int randomBoneThreshold = ThreadLocalRandom.current().nextInt(8, 13);

        if (Rs2Inventory.count("Bones") > randomBoneThreshold) {
            handleInventory(config);
        }

        if (Rs2Inventory.isFull()) {
            handleInventory(config);
        }
    }

    private void handleInventory(BirdHunterConfig config) {
        if (config.buryBones()) {
            buryBones(config);
        }
        dropItems(config);
    }

    private void buryBones(BirdHunterConfig config) {
        if (!config.buryBones() || !Rs2Inventory.hasItem("Bones")) return;

        List<Rs2Item> bones = Rs2Inventory.getBones();
        for (Rs2Item bone : bones) {
            if (Rs2Inventory.interact(bone, "Bury")) {
                if (Rs2Player.waitForXpDrop(Skill.PRAYER, true)) {
                    Microbot.log("Bone was successfully buried.");
                } else {
                    Microbot.log("Failed to bury bone.");
                }
            }
            sleep(300, 600);
        }
    }

    private void dropItems(BirdHunterConfig config) {
        String keepItemsConfig = config.keepItemNames();
        List<String> keepItemNames = List.of(keepItemsConfig.split("\\s*,\\s*"));

        if (!keepItemNames.contains("Bird snare")) {
            keepItemNames.add("Bird snare");
        }
        Rs2Inventory.dropAllExcept(keepItemNames.toArray(new String[0]));
    }

    public int getAvailableTraps(int hunterLevel) {
        if (hunterLevel >= 80) return 5;
        if (hunterLevel >= 60) return 4;
        if (hunterLevel >= 40) return 3;
        if (hunterLevel >= 20) return 2;
        return 1;
    }
}
