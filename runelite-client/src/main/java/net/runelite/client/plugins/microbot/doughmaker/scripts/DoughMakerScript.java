package net.runelite.client.plugins.microbot.doughmaker.scripts;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.doughmaker.DoughMakerConfig;
import net.runelite.client.plugins.microbot.doughmaker.enums.DoughItem;
import net.runelite.client.plugins.microbot.doughmaker.enums.Location;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.math.Rs2Random;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Getter
enum PlayerState {
    RECOLLECTING(Location.INSIDE_WHEAT_FIELD),
    PROCESSING(Location.COOKING_GUILD_THIRD_FLOOR),
    COMBINING(Location.COOKING_GUILD_FIRST_FLOOR),
    BANKING(Location.NEAREST_BANK);

    private final Location location;

    PlayerState(Location location) {
        this.location = location;
    }
}

@Slf4j
public class DoughMakerScript extends Script {

    private PlayerState playerState;
    private Location currentLocation;
    private final int RADIUS = 3;
    private int recollectedGrain = 0;
    private int processedGrain = 0;
    private boolean init = true;

    public boolean run(DoughMakerConfig config) {
        Microbot.enableAutoRunOn = false;
//        Rs2Antiban.resetAntibanSettings();
//        Rs2Antiban.antibanSetupTemplates.applyCookingSetup();
//        Rs2Antiban.setActivity(Activity.GENERAL_COOKING);
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!fulfillConditionsToRun()) return;
//                if (Rs2AntibanSettings.actionCooldownActive) return;

                if (init) {
                    if (initialPlayerLocation == null) {
                        initialPlayerLocation = Rs2Player.getWorldLocation();
                    }
                    checkPlayerInventory();
                    getPlayerState(config);
                    init = false;
                }
                log.info("Current Location: {}", currentLocation);
                log.info("Player State: {}", playerState);

                // check if player is in desired location
                checkCurrentPlayerLocation();
                if (!checkIfInDesiredLocation()) walkToDesiredLocation();

                Microbot.log(String.format("Recollected Grain: %d", recollectedGrain));
                Microbot.log(String.format("Processed Grain: %d", processedGrain));
                switch (playerState) {
                    case RECOLLECTING:
                        if (currentLocation != playerState.getLocation()) {
                            log.info("Player is not inside Wheat Field");
                        }

                        while(!Rs2Inventory.isFull() && fulfillConditionsToRun()) {
                            // if wheat field door is closed open
                            openWheatFieldDoor();

                            // Recollect wheat
                            GameObject wheatItem = Rs2GameObject.findReachableObject("Wheat", true, 8, Rs2Player.getWorldLocation());
                            if (wheatItem != null) {
                                Rs2GameObject.interact(wheatItem, "Pick");
                            }
                            Rs2Random.wait(800, 1600);
                            checkPlayerInventory();
                        }
                        break;
                    case PROCESSING:
                        if (currentLocation != playerState.getLocation()) {
                            log.info("Player is not in Cooking Guild third floor");
                            break;
                        }

                        while(recollectedGrain > 0 && fulfillConditionsToRun()) {
                            log.info("Recollected grain {}", recollectedGrain);
                            GameObject hopper = Rs2GameObject.findObject(2586, new WorldPoint(3142, 3452, 2));
                            if (hopper != null) {
                                log.info("Putting grain in hopper");
                                Rs2GameObject.interact(hopper, "Fill");
                                Rs2Random.wait(800, 1600);
                            }

                            GameObject hopperControls = Rs2GameObject.findObject(2607, new WorldPoint(3141, 3453, 2));
                            if (hopperControls != null) {
                                log.info("Move hopper controls");
                                Rs2GameObject.interact(hopperControls, "Operate");
                                Rs2Random.wait(800, 1600);
                            }
                            Rs2Random.wait(800, 1200);
                            checkPlayerInventory();
                        }

                        break;
                    case COMBINING:
                        if (currentLocation != playerState.getLocation()) {
                            log.info("Player is not in Cooking Guild first floor");
                        }

                        while (processedGrain > 0 && fulfillConditionsToRun()) {
                            GameObject basePlantMill = Rs2GameObject.findObject(14960, new WorldPoint(3140, 3449, 0));
                            Rs2Item pot = Rs2Inventory.get(ItemID.POT);
                            if (basePlantMill != null && pot != null) {
                                log.info("Recollecting flour from mill");
                                Rs2GameObject.interact(basePlantMill, "Empty");
                                sleepUntil(() -> !Rs2Player.isMoving());
                            }
                            Rs2Random.wait(800, 1200);

                            GameObject sink = Rs2GameObject.findObject(ObjectID.SINK_1763, new WorldPoint(3138, 3449, 0));
                            Rs2Item emptyBucket = Rs2Inventory.get(ItemID.BUCKET);
                            if (sink != null && emptyBucket != null) {
                                // use empty bucket from inventory and then click sink
                                log.info("Filling empty bucket with water");
                                Rs2Inventory.useItemOnObject(emptyBucket.getId(), sink.getId());
                                sleepUntil(() -> !Rs2Player.isMoving());
                            }
                            Rs2Random.wait(800, 1200);

                            // combine items
                            Rs2Item bucketOfWater = Rs2Inventory.get(ItemID.BUCKET_OF_WATER);
                            Rs2Item potOfFlour = Rs2Inventory.get(ItemID.POT_OF_FLOUR);
                            if (potOfFlour != null && bucketOfWater != null) {
                                log.info("Combining bucket of water and pot of flour");
                                Rs2Inventory.combine(bucketOfWater, potOfFlour);
                                boolean makeDoughWidget = Rs2Widget.hasWidget("What sort of dough do you wish to make?");
                                if (makeDoughWidget) {
                                    log.info("keyPress: {}", config.doughItem().getKeyEvent());
                                    Rs2Keyboard.keyPress(32);
                                    log.info("Make dough key has been pressed");
                                }
                            } else break;

                            Rs2Random.wait(800, 1200);
                            checkPlayerInventory();
                        }
                        break;
                    case BANKING:
                        if (currentLocation != playerState.getLocation()) {
                            log.info("Player is not in nearest bank location");
                        }

                        // deposit all dough
                        int doughItem = config.doughItem().getItemId();
                        if (Rs2Inventory.hasItem(doughItem)) {
                            Rs2Bank.openBank();
                            Rs2Bank.depositAll(doughItem);
                        }
                        Rs2Random.wait(800, 1200);
                        Rs2Bank.closeBank();
                        break;
                }

                getPlayerState(config);
            } catch (Exception ex) {
                Microbot.log(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

    public boolean fulfillConditionsToRun() {
        return Microbot.isLoggedIn() && !Microbot.pauseAllScripts && super.isRunning();
    }

    private void checkCurrentPlayerLocation() {
        Optional<Location> currentLoc = Location.stream().filter((location) -> {
            WorldArea area = location.getArea();
            return area.contains(Rs2Player.getWorldLocation());
        }).findFirst();
        currentLoc.ifPresentOrElse(location -> currentLocation = location, () -> currentLocation = Location.OUTSIDE_POINT);
    }

    // check player inventory and update grainRecollected and processedGrain
    private void checkPlayerInventory() {
        List<Rs2Item> grainItemList = Rs2Inventory.all((rs2Item) -> rs2Item.id == ItemID.GRAIN);
        int newSize = grainItemList.size();
        if (newSize < recollectedGrain) {
            recollectedGrain = newSize;
            processedGrain++;
        } else {
            recollectedGrain = newSize;
            if (processedGrain > 0) processedGrain--;
        }
    }

    @Override
    public void shutdown() {
        super.shutdown();
        recollectedGrain = 0;
        processedGrain = 0;
        init = true;
//        Rs2Antiban.resetAntibanSettings();
    }

    private boolean checkIfInDesiredLocation() {
        return playerState.getLocation() == currentLocation;
    }

    private void walkToDesiredLocation() {
        switch (playerState) {
            case RECOLLECTING:
                if (currentLocation == Location.COOKING_GUILD_FIRST_FLOOR) {
                    exitCookingGuild();
                }
                walkToWheatFieldOutsidePoint();
                openWheatFieldDoor();
                break;
            case PROCESSING:
                if (currentLocation == Location.INSIDE_WHEAT_FIELD) {
                    exitWheatField();
                    walkNearCookingGuildDoor();
                    enterCookingGuild();
                    walkToCGThirdFloor();
                } else if (currentLocation == Location.OUTSIDE_WHEAT_FIELD) {
                    exitWheatField();
                    walkNearCookingGuildDoor();
                    enterCookingGuild();
                    walkToCGThirdFloor();
                } else if (currentLocation == Location.NEAR_COOKING_GUILD_DOOR) {
                    enterCookingGuild();
                    walkToCGFirstFloor();
                } else {
                    walkNearCookingGuildDoor();
                    enterCookingGuild();
                    walkToCGThirdFloor();
                }
                break;
            case COMBINING:
                if (currentLocation == Location.COOKING_GUILD_THIRD_FLOOR) {
                    walkToCGFirstFloor();
                } else if (currentLocation == Location.COOKING_GUILD_SECOND_FLOOR) {
                    walkToCGFirstFloor();
                }
                break;
            case BANKING:
                if (currentLocation == Location.COOKING_GUILD_FIRST_FLOOR) {
                    exitCookingGuild();
                    walkToBank();
                } else {
                    walkToBank();
                }
                break;
        }
    }

    private void walkToBank() {
        // Go to nearest bank
        WorldPoint nearestBank = Location.NEAREST_BANK.getPoint();
        Rs2Walker.walkTo(nearestBank, 8);
        Microbot.pauseAllScripts = true;
        sleepUntil(() -> nearestBank.equals(Rs2Player.getWorldLocation()));
        Microbot.pauseAllScripts = false;
    }

    private void walkToWheatFieldInsidePoint() {
        WorldPoint point = Location.INSIDE_WHEAT_FIELD.getPoint();
        WorldArea area = Location.INSIDE_WHEAT_FIELD.getArea();
        Rs2Walker.walkTo(point, 0);
        Microbot.pauseAllScripts = true;
        sleepUntil(() -> area.contains(Rs2Player.getWorldLocation()));
        Microbot.pauseAllScripts = false;
    }

    private void walkToWheatFieldOutsidePoint() {
        WorldPoint point = Location.OUTSIDE_WHEAT_FIELD.getPoint();
        WorldArea area = Location.OUTSIDE_WHEAT_FIELD.getArea();
        Rs2Walker.walkTo(point, 0);
        Microbot.pauseAllScripts = true;
        sleepUntil(() -> area.contains(Rs2Player.getWorldLocation()));
        Microbot.pauseAllScripts = false;
    }

    private void openWheatFieldDoor() {
        WallObject wheatFieldDoor = Rs2GameObject.findDoor(15512);
        if (wheatFieldDoor != null) Rs2GameObject.interact(wheatFieldDoor, "Open");
        log.info("Player has open wheat field door");
    }

    private void exitWheatField() {
        WallObject wheatFieldDoor = Rs2GameObject.findDoor(15512);
        if (wheatFieldDoor != null) Rs2GameObject.interact(wheatFieldDoor, "Open");

        Rs2Walker.walkTo(Location.OUTSIDE_WHEAT_FIELD.getPoint());
        log.info("Player is now outside wheat field");
    }

    private void walkNearCookingGuildDoor() {
        WorldPoint point = Location.NEAR_COOKING_GUILD_DOOR.getPoint();
        Rs2Walker.walkTo(point, 0);
        Microbot.pauseAllScripts = true;
        sleepUntil(() -> point.equals(Rs2Player.getWorldLocation()));
        Microbot.pauseAllScripts = false;
    }

    private void enterCookingGuild() {
        WallObject entranceDoor = Rs2GameObject.findDoor(24958);
        if (entranceDoor != null) {
            Rs2GameObject.interact(entranceDoor, "Open");
            log.info("Player has enter Cooking Guild");
        }
        Rs2Random.wait(2000, 3000);
    }

    private void exitCookingGuild() {
        WallObject entranceDoor = Rs2GameObject.findDoor(24958);
        if (entranceDoor != null) {
            Rs2GameObject.interact(entranceDoor, "Open");
            Rs2Random.wait(800, 1600);
            log.info("Player has left Cooking Guild");
        }
    }

    private void walkToCGFirstFloor() {
        GameObject thirdFloorStaircase = Rs2GameObject.findObject(2610, new WorldPoint(3144, 3447, 2));
        if (thirdFloorStaircase != null) {
            Rs2GameObject.interact(thirdFloorStaircase, "Climb-down");
        }

        GameObject secondFloorStaircase = Rs2GameObject.findObject(2609, new WorldPoint(3144, 3447, 1));
        if (secondFloorStaircase != null) {
            Rs2GameObject.interact(secondFloorStaircase, "Climb-down");
        }
    }

    private void walkToCGThirdFloor() {
        if (currentLocation != Location.COOKING_GUILD_FIRST_FLOOR) {
            log.info("Player is not on Cooking Guild first floor, walk the player to the designated area before start the script");
        }

        WorldPoint point = Location.COOKING_GUILD_THIRD_FLOOR.getPoint();
        sleepUntil(() -> {
            GameObject firstFloorStaircase = Rs2GameObject.findObject(2608, new WorldPoint(3144, 3447, 0));
            if (firstFloorStaircase != null) {
                Rs2GameObject.interact(firstFloorStaircase, "Climb-up");
            }
            Rs2Random.wait(800, 1200);

            log.info("walktoCG execution finished");

            GameObject secondFloorStaircase = Rs2GameObject.findObject(2609, new WorldPoint(3144, 3447, 1));
            if (secondFloorStaircase != null) {
                Rs2GameObject.interact(secondFloorStaircase, "Climb-up");
            }

            if (playerIsInProvidedArea(point)) {
                log.info("Player is now in Cooking Guild third floor");
                return true;
            }

            return false;
        });
    }

    private boolean playerIsInProvidedArea(WorldPoint point, int radius) {
        return Rs2Player.getWorldLocation().distanceTo(point) <= radius;
    }

    private boolean playerIsInProvidedArea(WorldPoint point) {
        return playerIsInProvidedArea(point, RADIUS);
    }

    private void getPlayerState(DoughMakerConfig config) {
        if (hasGrainRemaining()) {
            playerState = PlayerState.PROCESSING;
            return;
        }

        if (isReadyToCombine()) {
            playerState = PlayerState.COMBINING;
            return;
        }

        if (isReadyToBank(config.doughItem())) {
            playerState = PlayerState.BANKING;
            return;
        }

        // Recollect grain until grainRecollected equals 26
        playerState =  PlayerState.RECOLLECTING;
    }

    private boolean hasGrainRemaining() {
        if (currentLocation == Location.INSIDE_WHEAT_FIELD || currentLocation == Location.OUTSIDE_WHEAT_FIELD) {
            return (Rs2Inventory.hasItem("Grain") && recollectedGrain >= 26);
        } else if (currentLocation == Location.COOKING_GUILD_THIRD_FLOOR) {
            return Rs2Inventory.hasItem("Grain");
        }
        return false;
    }

    private boolean checkFlourBin() {
        // check if base plant mill has flour
        GameObject basePlantMill = Rs2GameObject.findObject(14960, new WorldPoint(3140, 3449, 0));
        if (basePlantMill != null) {
            ObjectComposition obj = Rs2GameObject.convertGameObjectToObjectComposition(basePlantMill);
            if (Rs2GameObject.hasAction(obj, "Empty")) {
                processedGrain++;
                return true;
            }
        }

        // check if bucket of water and pot flour is available in inventory
        Rs2Item bucketOfWater = Rs2Inventory.get(ItemID.BUCKET_OF_WATER);
        Rs2Item potOfFlour = Rs2Inventory.get(ItemID.POT_OF_FLOUR);
        if (bucketOfWater != null && potOfFlour != null) {
            processedGrain++;
            return true;
        }

        return false;
    }

    private boolean isReadyToCombine() {
        if (processedGrain > 0) {
            return true;
        } else if (!Rs2Inventory.isFull()) {
            return checkFlourBin();
        }

        return false;
    }

    private boolean isReadyToBank(DoughItem doughItem) {
//        return Rs2Inventory.hasItemAmount(doughItem.getItemId(), 26);
        return Rs2Inventory.hasItem(doughItem.getItemId()) && recollectedGrain == 0 && processedGrain == 0;
    }
}
