package net.runelite.client.plugins.microbot.zerozero.birdhunter;

import net.runelite.api.GameObject;
import net.runelite.api.Skill;
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
import net.runelite.client.plugins.microbot.breakhandler.BreakHandlerScript;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

enum State {
    HANDLE_TRAP,
    MOVE_AWAY

}

//TODO: if player is around, pickup traps and world hop.


public class BirdHunterScript extends Script {

    public final int BIRD_SNARE = 10006;

    public final int IDLE_TRAP = 9345;
    public final int MID_TRAP = 9347;
    public final int SUCCESSFUL_TRAP = 9348;
    public final int FAILED_TRAP = 9344;

    public static String version = "1.0.0";
    State state = State.HANDLE_TRAP;

    WorldPoint playerStartingLocation = null;
    
    int availableBirdTrapsPerLevel = 1;
    boolean hasRanAway = false;
    boolean recheckTraps = false;

    
    public boolean run(BirdHunterConfig config) {
        Rs2Antiban.resetAntibanSettings();
        Rs2Antiban.antibanSetupTemplates.applyHunterSetup();
        Rs2AntibanSettings.actionCooldownChance = 0.1;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!super.run()) return;
                if (!Microbot.isLoggedIn()) return;
                if (Rs2AntibanSettings.actionCooldownActive) return;
                if (playerStartingLocation == null) {
                    Microbot.log("Setting player starting location.");
                    playerStartingLocation = Rs2Player.getWorldLocation();
                    Microbot.log(String.valueOf(playerStartingLocation));
                }

                if(BreakHandlerScript.breakIn <= 60){
                    pickupAllTraps();
                    sleep(1000, 2000);
                    return;
                }

                if (!config.BIRD().hasRequiredLevel()) {
                    Microbot.showMessage("You do not have the required hunter level to trap this bird.");
                    shutdown();
                    return;
                }

                availableBirdTrapsPerLevel = getAvailableTraps();


                if (Rs2Player.isMoving() || Rs2Player.isAnimating() || Microbot.pauseAllScripts) return;
                switch (state) {
                    case HANDLE_TRAP:
                        sleep(100, 600);
                        /*
                                1) check if caught/fallen trap is near
                                    - if caught/fallen trap, pick it up
                                        - random chance of burying bones right then (0.2% of the time) if enabled
                                    - if not caught/fallen trap
                                        - check if another trap is near if proper level allows it
                                            - if another trap is near and level doesn't allow more, MOVE_AWAY
                                        - if no trap found, set trap
                                            - check if trap is set
                                        - MOVE_AWAY
                         */
                        if (Rs2Inventory.isFull()) {
                            handleInventory(config);
                            break;
                        }

                        //TODO: FIX
//                        if (Rs2GroundItem.exists(BIRD_SNARE, 20)) {
//                            pickUpBirdSnare(config);
//                            break;
//                        }


                        List<GameObject> successfulBirdSnares = Rs2GameObject.getGameObjects(SUCCESSFUL_TRAP);
                        if (!successfulBirdSnares.isEmpty()) {
                            handleInventory(config);
                            if (interactWithTrap(successfulBirdSnares.get(0))) {
                                Rs2Player.waitForXpDrop(Skill.HUNTER, true);
                                Rs2Antiban.actionCooldown();
                                Rs2Antiban.takeMicroBreakByChance();
                            } else {
                                break;
                            }
                        }

                        List<GameObject> middleBirdSnares = Rs2GameObject.getGameObjects(MID_TRAP);
                        if (!middleBirdSnares.isEmpty()) {
                            break;
                        }

                        List<GameObject> failedBirdSnares = Rs2GameObject.getGameObjects(FAILED_TRAP);
                        if (!failedBirdSnares.isEmpty()) {
                            if (interactWithTrap(failedBirdSnares.get(0))) {
                                Rs2Antiban.actionCooldown();
                                Rs2Antiban.takeMicroBreakByChance();
                            } else {
                                break;
                            }
                        }


                        List<GameObject> idleTraps = Rs2GameObject.getGameObjects(IDLE_TRAP);
                        if (idleTraps.size() < availableBirdTrapsPerLevel) {
                            if (!recheckTraps) {
                                recheckTraps = true;
                                return;
                            }
                            setTrap(config);
                        } else {
                            state = State.MOVE_AWAY;
                        }


                        break;
                    case MOVE_AWAY:
                        if (hasRanAway) {
                            state = State.HANDLE_TRAP;
                            break;
                        }

                        // Get current player position
                        WorldPoint currentPosition = Rs2Player.getWorldLocation();
                        
                        Random randomX = new Random();
                        Random randomY = new Random();
                        int randomXOffset = 1 + randomX.nextInt(config.distanceToStray());
                        int randomYOffset = 1 + randomY.nextInt(config.distanceToStray());

// Randomly choose the direction (positive or negative)
                        if (Math.random() < 0.5) randomXOffset *= -1;
                        if (Math.random() < 0.5) randomYOffset *= -1;

// Calculate the target position
                        WorldPoint targetPosition = new WorldPoint(
                                currentPosition.getX() + randomXOffset,
                                currentPosition.getY() + randomYOffset,
                                currentPosition.getPlane()
                        );


// Move to the target position
                        Rs2Walker.walkFastCanvas(targetPosition);
                        sleepUntil(Rs2Player::isAnimating, 3000);
                        handleInventory(config);
                        hasRanAway = true;
                        state = State.HANDLE_TRAP;
                        break;
                }
            } catch (Exception ex) {
                Microbot.log(ex.getMessage());
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
        return true;
    }

    public void pickupAllTraps(){
        BreakHandlerScript.setLockState(true);

        List<GameObject> successfulBirdSnares = Rs2GameObject.getGameObjects(SUCCESSFUL_TRAP);
        List<GameObject> middleBirdSnares = Rs2GameObject.getGameObjects(MID_TRAP);
        List<GameObject> failedBirdSnares = Rs2GameObject.getGameObjects(FAILED_TRAP);
        List<GameObject> idleTraps = Rs2GameObject.getGameObjects(IDLE_TRAP);

        List<GameObject> allTraps = new ArrayList<>();
        allTraps.addAll(successfulBirdSnares);
        allTraps.addAll(middleBirdSnares);
        allTraps.addAll(failedBirdSnares);
        allTraps.addAll(idleTraps);


        for (GameObject trap : allTraps) {
            interactWithTrap(trap);
            sleepUntil(Rs2Player::isAnimating);
            sleep(500, 1000);
        }

        BreakHandlerScript.setLockState(false);
    }

    public void setTrap(BirdHunterConfig config) {
        hasRanAway = false;
        recheckTraps = false;

        if (!Rs2Inventory.contains(BIRD_SNARE)) {
            System.out.println("No bird snare found in inventory.");
            return;
        }

        if(Rs2Player.getWorldLocation().distanceTo(playerStartingLocation) > config.distanceToStray()){
            Rs2Walker.walkTo(playerStartingLocation, availableBirdTrapsPerLevel);
            sleepUntil(Rs2Player::isAnimating, 3000);
        }

        if (isObjectOnTile(null)) {
          moveRandomly();
            return;
        }

        // Get the bird snare item from the inventory
        Rs2Item birdSnare = Rs2Inventory.get(BIRD_SNARE);

        // Interact with the bird snare to lay it
        if (Rs2Inventory.interact(birdSnare, "Lay")) {
            // Wait for the trap to be placed
            sleepUntil(Rs2Player::isAnimating, 3000);

            //todo: implement success check
            System.out.println("Bird snare was successfully laid.");
        } else {
            System.out.println("Failed to interact with the bird snare.");
        }
    }

    public void handleInventory(BirdHunterConfig config){
        if (Math.random() < 0.5){
            buryBones(config);
            sleep(100, 800);
            dropItems(config);
            sleep(100, 500);
        } else {
            dropItems(config);
            sleep(100, 800);
            buryBones(config);
            sleep(100, 500);
        }
    }

    public void pickUpBirdSnare(BirdHunterConfig config) {
//        if (!Rs2GroundItem.exists(BIRD_SNARE, 20)) {
//            return;
//        }
//        handleInventory(config);
//
//        Rs2GroundItem.loot(BIRD_SNARE);
    }

    public boolean isObjectOnTile(WorldPoint point) {
        // Get the player's current location
        WorldPoint location = point != null ? point : Rs2Player.getWorldLocation();
        // Check if there is a bird snare at the player's location
        return Rs2GameObject.getGameObject(location) != null;
    }


    public boolean interactWithTrap(GameObject birdSnare) {
        Rs2GameObject.interact(birdSnare);

        // Wait a moment to allow the game to process the click
        sleepUntil(Rs2Player::isAnimating, 3000);

        // Check for success
        boolean success = !Rs2GameObject.getGameObjects(birdSnare.getId()).contains(birdSnare); // Or check for a chat message

        if (success) {
            System.out.println("Bird snare interaction was successful.");
            return true;
        } else {
            System.out.println("Bird snare interaction failed.");
        }
        return false;
    }

    //TODO: check if something is in the new location before going there. if something is there, generate a new location (recursion?)
    public void moveRandomly() {
        WorldPoint currentLocation = Rs2Player.getWorldLocation();
        Random random = new Random();

        // Calculate the new location based on the direction
        int direction = random.nextInt(4); // 0 = North, 1 = East, 2 = South, 3 = West
        WorldPoint newLocation;
        switch (direction) {
            case 0: // North
                newLocation = new WorldPoint(currentLocation.getX(), currentLocation.getY() + 1, currentLocation.getPlane());
                break;
            case 1: // East
                newLocation = new WorldPoint(currentLocation.getX() + 1, currentLocation.getY(), currentLocation.getPlane());
                break;
            case 2: // South
                newLocation = new WorldPoint(currentLocation.getX(), currentLocation.getY() - 1, currentLocation.getPlane());
                break;
            case 3: // West
                newLocation = new WorldPoint(currentLocation.getX() - 1, currentLocation.getY(), currentLocation.getPlane());
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + direction);
        }

        if(isObjectOnTile(newLocation)){
            moveRandomly();
            return;
        }

        // Move the player to the new location
        Rs2Walker.walkFastCanvas(newLocation);
        sleepUntil(Rs2Player::isAnimating, 3000);
    }

    public void dropItems(BirdHunterConfig config) {
        // drop all items except bird snares, coins, etc
        if (config.buryBones()) {
            Rs2Inventory.dropAllExcept("bird snare", "coins", "bones");
        } else {
            Rs2Inventory.dropAllExcept("bird snare", "coins");
        }

    }

    public void buryBones(BirdHunterConfig config) {
        //
        if (!config.buryBones() || !Rs2Inventory.hasItem("Bones")) {
            return;
        }
        List<Rs2Item> bones = Rs2Inventory.getBones();
        for (Rs2Item bone : bones) {
            Rs2Inventory.interact(bone, "Bury");
        }
        sleep(400, 2000);
    }

    public int getAvailableTraps() {
        int hunterLevel = Rs2Player.getRealSkillLevel(Skill.HUNTER);
        if (hunterLevel >= 80) {
            return 5;
        } else if (hunterLevel >= 60) {
            return 4;
        } else if (hunterLevel >= 40) {
            return 3;
        } else if (hunterLevel >= 20) {
            return 2;
        } else {
            return 1;
        }
    }
}
