package net.runelite.client.plugins.microbot.doughmaker.scripts;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.doughmaker.DoughMakerConfig;
import net.runelite.client.plugins.microbot.doughmaker.enums.DoughItem;
import net.runelite.client.plugins.microbot.util.antiban.Rs2AntibanSettings;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.util.concurrent.TimeUnit;

import static net.runelite.client.plugins.microbot.util.Global.sleepUntilTrue;

enum CookingState {
    RECOLLECTING,
    PROCESSING,
    COMBINING,
    WALKING,
    BANKING,
}

@Slf4j
public class DoughMakerScript extends Script {

    private CookingState playerState;
    private final int radius = 10;
    private final WorldPoint cookingGuildLocation = new WorldPoint(3143, 3442, 0);
    private final WorldPoint CGThirdFloorPoint = new WorldPoint(3142, 3450, 2);
    private final WorldPoint CGFirstFloorPoint = new WorldPoint(3143, 3446, 0);
    private final WorldPoint wheatFieldOutsidePoint = new WorldPoint(3142, 3456, 0);
    private final WorldPoint wheatFieldInsidePoint = new WorldPoint(3141, 3461, 0);
    private boolean init = true;

    public boolean run(DoughMakerConfig config) {
        Microbot.enableAutoRunOn = false;
//        Rs2Antiban.resetAntibanSettings();
//        Rs2Antiban.antibanSetupTemplates.applyCookingSetup();
//        Rs2Antiban.setActivity(Activity.GENERAL_COOKING);
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn() || !super.run()) return;
                if (Rs2AntibanSettings.actionCooldownActive) return;

                if (init) {
                    if (initialPlayerLocation == null) {
                        initialPlayerLocation = Rs2Player.getWorldLocation();
                    }

                    getPlayerState(config);
                    init = false;
                }
//
//                if (Rs2Player.isMoving() || Rs2Player.isAnimating() || Microbot.pauseAllScripts) return;

                log.info("Running");
                switch (playerState) {
                    case RECOLLECTING:
                        log.info("Is Recollecting");

                        // Go to wheat field
                        if (!playerIsInProvidedArea(wheatFieldInsidePoint)) {
                            boolean isInArea = walkToWheatField();
                            if (!isInArea) break;
                        }

                        // Recollect wheat
                        GameObject wheatItem = Rs2GameObject.findReachableObject("Wheat", true, 8, wheatFieldOutsidePoint);
                        if (wheatItem == null) return;

                        if (!Rs2Camera.isTileOnScreen(wheatItem.getLocalLocation())) {
                            Rs2Camera.turnTo(wheatItem.getLocalLocation());
                            return;
                        }

                        Rs2GameObject.interact(wheatItem, "Pick");
                        break;
                    case PROCESSING:
                        log.info("Is Processing");
                        // Go to Cooking Guild third floor
                        if (!playerIsInProvidedArea(CGThirdFloorPoint)) {
                            boolean isInArea = walkToCGThirdFloor();
                            if (!isInArea) break;
                        }

                        GameObject hopper = Rs2GameObject.findObject(2586, new WorldPoint(3142, 3452, 2));
                        if (hopper != null) {
                            log.info("Putting grain in hopper");
                            Rs2GameObject.interact(hopper, "Fill");
                            sleepUntil(Rs2Player::isInteracting);
                            sleepUntil(() -> !Rs2Player.isInteracting());
                        }

                        GameObject hopperControls = Rs2GameObject.findObject(2607, new WorldPoint(3141, 3453, 2));
                        if (hopperControls != null) {
                            log.info("Move hopper controls");
                            Rs2GameObject.interact(hopperControls, "Operate");
                            sleepUntil(Rs2Player::isInteracting);
                            sleepUntil(() -> !Rs2Player.isInteracting());
                        }

                        break;
                    case COMBINING:
                        log.info("Is Combining");
                        // Go to Cooking Guild first floor
                        if (!playerIsInProvidedArea(CGFirstFloorPoint)) {
                            boolean isInArea = walkToCGFirstFloor();
                            if (!isInArea) break;
                        }

                        GameObject basePlantMill = Rs2GameObject.findObject(14960, new WorldPoint(3140, 3449, 0));
                        Rs2Item pot = Rs2Inventory.get(ItemID.POT);
                        if (basePlantMill != null && pot != null) {
                            log.info("Recollecting flour from mill");
                            Rs2GameObject.interact(basePlantMill, "Empty");
                            sleepUntil(() -> !Rs2Player.isMoving());
                        }

                        GameObject sink = Rs2GameObject.findObject(ObjectID.SINK_1763, new WorldPoint(3138, 3449, 0));
                        Rs2Item emptyBucket = Rs2Inventory.get(ItemID.BUCKET);
                        if (sink != null && emptyBucket != null) {
                            // use empty bucket from inventory and then click sink
                            log.info("Filling empty bucket with water");
                            Rs2Inventory.useItemOnObject(emptyBucket.getId(), sink.getId());
                            sleepUntil(() -> !Rs2Player.isMoving());
                        }

                        // combine items
                        Rs2Item filledBucket = Rs2Inventory.get(ItemID.BUCKET_OF_WATER);
                        Rs2Item potOfFlour = Rs2Inventory.get(ItemID.POT_OF_FLOUR);
                        if (sink != null && filledBucket != null) {
                            log.info("Combining bucket of water and pot of flour");
                            Rs2Inventory.combine(filledBucket, potOfFlour);
                            sleepUntil(() -> Rs2Widget.findWidget("What sort of dough do you wish to make?", null, false) != null);
                            Rs2Keyboard.keyPress(config.doughItem().getKeyEvent());
                        }

                        break;
                    case BANKING:
                        log.info("Is Banking");

                        // Go to nearest bank
                        WorldPoint nearestBank = Rs2Bank.getNearestBank().getWorldPoint();
                        if (!playerIsInProvidedArea(nearestBank)) {
                            log.info("Walking to nearest bank");
                            boolean isInArea = walkToBank(nearestBank);
                            if (!isInArea) break;
                        }

                        // deposit all dough
                        int doughItem = config.doughItem().getItemId();
                        if (Rs2Inventory.hasItem(doughItem)) {
                            Rs2Bank.openBank();
                            Rs2Bank.depositAll(doughItem);
//                            Rs2Random.wait(800, 1600);
                        }
//                        Rs2Bank.withdrawAll(cookingItem.getRawItemName(), true);
//                        Rs2Random.wait(800, 1600);
                        Rs2Bank.closeBank();
                        break;
                    case WALKING:
                        log.info("Is Walking");
//                        if (!isNearCookingLocation(location, 10)) {
//                            boolean walkTo = Rs2Walker.walkTo(location.getCookingObjectWorldPoint(), 2);
//                            if (!walkTo) return;
//                        } else if (!isNearCookingLocation(location, 2)) {
//                            Rs2Walker.walkFastCanvas(location.getCookingObjectWorldPoint());
//                        }
//
////                        if (hasRawItem(cookingItem)) {
////                            state = CookingState.COOKING;
////                        } else {
////                            state = CookingState.BANKING;
////                        }
                        break;
                }

                getPlayerState(config);
            } catch (Exception ex) {
                Microbot.log(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public void shutdown() {
        super.shutdown();
//        Rs2Antiban.resetAntibanSettings();
    }

    private boolean walkToBank(WorldPoint bank) {
        Rs2Walker.walkTo(bank, 8);
        Microbot.pauseAllScripts = true;
        sleepUntil(() -> bank.equals(Rs2Player.getWorldLocation()));
        Microbot.pauseAllScripts = false;

        if (playerIsInProvidedArea(bank)) {
            log.info("Player is now on designated bank available");
            return true;
        }

        return false;
    }

    private void walkToCookingGuildOutside() {
        Rs2Walker.walkTo(cookingGuildLocation, 8);
        Microbot.pauseAllScripts = true;
        sleepUntil(this::isNearCookingGuild);
        log.info("Player is now outside Cooking Guild");
        Microbot.pauseAllScripts = false;
    }

    private boolean walkToWheatField() {
        Rs2Walker.walkTo(wheatFieldOutsidePoint, 8);
        Microbot.pauseAllScripts = true;
        sleepUntil(() -> wheatFieldOutsidePoint.equals(Rs2Player.getWorldLocation()));
        Microbot.pauseAllScripts = false;

        if (playerIsInProvidedArea(wheatFieldOutsidePoint)) {
            WallObject wheatFieldDoor = Rs2GameObject.findDoor(15512);
            if (wheatFieldDoor != null) {
                Rs2GameObject.interact(wheatFieldDoor, "Open");
                log.info("Player is now in wheat field");
                Rs2Walker.walkTo(wheatFieldInsidePoint, 1);
                return true;
            }
        }

        return false;
    }

    private boolean enterCookingGuild() {
        return false;
    }

    private boolean walkToCGFirstFloor() {
        if (!playerIsInProvidedArea(CGFirstFloorPoint)) {
            log.info("Player is not on Cooking Guild third floor");
        }

        GameObject thirdFloorStaircase = Rs2GameObject.findObject(2610, new WorldPoint(3144, 3447, 2));
        if (thirdFloorStaircase != null) {
            Rs2GameObject.interact(thirdFloorStaircase, "Climb-down");
        }

        GameObject secondFloorStaircase = Rs2GameObject.findObject(2609, new WorldPoint(3144, 3447, 1));
        if (secondFloorStaircase != null) {
            Rs2GameObject.interact(secondFloorStaircase, "Climb-down");
        }

        if (playerIsInProvidedArea(CGThirdFloorPoint)) {
            log.info("Player is now in Cooking Guild first floor");
            return true;
        }

        return false;
    }

    private boolean walkToCGThirdFloor() {
        if (!playerIsInProvidedArea(CGFirstFloorPoint)) {
            log.info("Player is not on Cooking Guild first floor, walk the player to the designated area before start the script");
        }

        GameObject firstFloorStaircase = Rs2GameObject.findObject(2608, new WorldPoint(3144, 3447, 0));
        if (firstFloorStaircase != null) {
            Rs2GameObject.interact(firstFloorStaircase, "Climb-up");
        }

        GameObject secondFloorStaircase = Rs2GameObject.findObject(2609, new WorldPoint(3144, 3447, 1));
        if (secondFloorStaircase != null) {
            Rs2GameObject.interact(secondFloorStaircase, "Climb-up");
        }

        if (playerIsInProvidedArea(CGThirdFloorPoint)) {
            log.info("Player is now in Cooking Guild third floor");
            return true;
        }

        return false;
    }

    private boolean playerIsInProvidedArea(WorldPoint point) {
        return Rs2Player.getWorldLocation().distanceTo(point) <= radius;
    }

    private void getPlayerState(DoughMakerConfig config) {
        if (needsToRecollect(config.doughItem())) {
            playerState = CookingState.RECOLLECTING;
            return;
        }

        if (hasGrainRemaining()) {
            playerState = CookingState.PROCESSING;
            return;
        }

        if (isReadyToCombine(config.doughItem())) {
            playerState = CookingState.COMBINING;
            return;
        }

        if (isReadyToBank(config.doughItem())) {
            playerState = CookingState.BANKING;
            return;
        }

        playerState = CookingState.RECOLLECTING;
    }

    private boolean needsToRecollect(DoughItem doughItem) {
        return Rs2Inventory.hasItemAmount(doughItem.getItemId(), 0);
    }

    private boolean isNearCookingGuild() {
        return cookingGuildLocation.equals(Rs2Player.getWorldLocation());
    }

    private boolean hasGrainRemaining() {
        return Rs2Inventory.hasItem("Grain");
    }

    private boolean isReadyToBank(DoughItem doughItem) {
        return Rs2Inventory.hasItemAmount(doughItem.getItemId(), 26);
    }

    private boolean isReadyToCombine(DoughItem doughItem) {
        return !hasGrainRemaining() && !isReadyToBank(doughItem) && playerIsInProvidedArea(CGThirdFloorPoint);
    }
}
