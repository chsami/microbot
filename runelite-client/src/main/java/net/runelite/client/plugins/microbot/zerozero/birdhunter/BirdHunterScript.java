package net.runelite.client.plugins.microbot.zerozero.birdhunter;

import net.runelite.api.GameObject;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.tile.Rs2Tile;
import net.runelite.client.plugins.microbot.zerozero.enums.hunter.Birds;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class BirdHunterScript extends Script {

    private static final int BIRD_SNARE = 10006;
    private static final int SUCCESSFUL_TRAP = 9373;
    private static final int FAILED_TRAP = 9344;
    private static final int IDLE_TRAP = 9345;

    public static String version = "1.0.0";

    public boolean run(BirdHunterConfig config) {
        WorldArea birdArea = getBirdArea(config.BIRD());
        if (birdArea == null) {
            Microbot.log("No valid hunting area for the selected bird.");
            return false;
        }

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!super.run() || !Microbot.isLoggedIn()) return;

                // Ensure the player stays inside the bird's area
                if (!birdArea.contains(Rs2Player.getWorldLocation())) {
                    Microbot.log("Player left the designated bird hunting area.");
                    return;  // Stop any further actions if the player leaves the area
                }

                handleTraps(config);
                checkForBonesAndHandleInventory(config);

            } catch (Exception ex) {
                Microbot.log(ex.getMessage());
            }
        }, 0, 100, TimeUnit.MILLISECONDS);

        return true;
    }

    private WorldArea getBirdArea(Birds bird) {
        return bird.getArea();
    }

    private void handleTraps(BirdHunterConfig config) {
        // Prioritize interacting with traps (successful, then failed)
        List<GameObject> successfulTraps = Rs2GameObject.getGameObjects(SUCCESSFUL_TRAP);
        List<GameObject> failedTraps = Rs2GameObject.getGameObjects(FAILED_TRAP);

        // Prioritize picking up successful traps first
        if (!successfulTraps.isEmpty()) {
            interactWithTraps(successfulTraps);
            return; // Return immediately after interacting with traps
        }

        // Then prioritize failed traps
        if (!failedTraps.isEmpty()) {
            interactWithTraps(failedTraps);
            return; // Return immediately after interacting with traps
        }

        // Pick up bird snares from the ground
        if (Rs2GroundItem.exists(BIRD_SNARE, 20)) {
            pickUpBirdSnare();
            return;
        }

        // If traps are less than allowed, set a trap after dealing with all existing ones
        int availableTraps = getAvailableTraps();
        List<GameObject> idleTraps = Rs2GameObject.getGameObjects(IDLE_TRAP);
        int totalTraps = successfulTraps.size() + failedTraps.size() + idleTraps.size();

        if (totalTraps < availableTraps) {
            setTrap(config);
        }
    }

    private void interactWithTraps(List<GameObject> traps) {
        for (GameObject trap : traps) {
            if (interactWithTrap(trap)) {
                continue;
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

                // Introduce a short delay after laying the trap to avoid rushing to interact with traps too quickly
                sleep(1000, 1500);  // Wait for 1 to 1.5 seconds before interacting with successful traps
            }
        } else {
            Microbot.log("Failed to interact with the bird snare.");
        }
    }

    private void movePlayerOffObject() {
        WorldPoint nearestWalkable = Rs2Tile.getNearestWalkableTileWithLineOfSight(Rs2Player.getWorldLocation());
        Rs2Walker.walkFastCanvas(nearestWalkable);
        Rs2Player.waitForWalking();
    }

    private boolean interactWithTrap(GameObject birdSnare) {
        if (Rs2GameObject.interact(birdSnare)) {
            // Wait for the player to start the interaction (animation)
            if (!sleepUntil(Rs2Player::isAnimating, 3000)) {  // Increased to 3 seconds
                Microbot.log("Failed to start interacting with the trap.");
                return false;
            }

            // Wait for the player to finish interacting with the trap
            if (!sleepUntil(() -> !Rs2Player.isAnimating(), 4000)) {  // Increased to 4 seconds
                Microbot.log("Failed to finish interacting with the trap.");
                return false;
            }

            // Wait for the XP drop to confirm trap success (Hunter XP drop)
            if (Rs2Player.waitForXpDrop(Skill.HUNTER, true)) {
                Microbot.log("Bird snare interaction was successful.");
                return true;
            }
        }
        Microbot.log("Failed to interact with the bird snare.");
        return false;
    }


    private void pickUpBirdSnare() {
        if (Rs2GroundItem.exists(BIRD_SNARE, 20)) {
            Rs2GroundItem.loot(BIRD_SNARE);
            Microbot.log("Picked up bird snare from the ground.");
        }
    }

    private void checkForBonesAndHandleInventory(BirdHunterConfig config) {
        // Randomized threshold between 8 and 12 for handling inventory
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
            sleep(300, 600);  // Slight pause after each bone burial
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


    public int getAvailableTraps() {
        int hunterLevel = Rs2Player.getRealSkillLevel(Skill.HUNTER);
        if (hunterLevel >= 80) return 5;
        if (hunterLevel >= 60) return 4;
        if (hunterLevel >= 40) return 3;
        if (hunterLevel >= 20) return 2;
        return 1;
    }
}
