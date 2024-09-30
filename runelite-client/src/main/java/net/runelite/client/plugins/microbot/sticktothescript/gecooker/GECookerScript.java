package net.runelite.client.plugins.microbot.sticktothescript.gecooker;

import net.runelite.api.Skill;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.cooking.enums.CookingItem;
import net.runelite.client.plugins.microbot.sticktothescript.common.enums.GEWorkLocation;
import net.runelite.client.plugins.microbot.sticktothescript.common.enums.LogType;
import net.runelite.client.plugins.microbot.util.antiban.Rs2Antiban;
import net.runelite.client.plugins.microbot.util.antiban.Rs2AntibanSettings;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.microbot.sticktothescript.common.Functions;

import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

enum State {
    BANKING,
    COOKING,
    BANKING_FOR_FIRE_SUPPLIES,
    BUILDING_FIRE,
}

public class GECookerScript extends Script {

    public static String version = "1.0.0";
    public State state = State.BANKING;
    public String debug = "";

    private static List<WorldPoint> FireSpots = Arrays.asList(
            GEWorkLocation.NORTH_EAST.getWorldPoint(),
            GEWorkLocation.SOUTH_EAST.getWorldPoint(),
            GEWorkLocation.NORTH_WEST.getWorldPoint(),
            GEWorkLocation.SOUTH_WEST.getWorldPoint()
    );
    private static List<Integer> FireIDs = Arrays.asList(26185, 49927);

    public boolean run(GECookerConfig config) {
        CookingItem cookingItem = config.sCookItem();
        LogType logType = config.sLogType();
        GEWorkLocation desiredLocation = config.sLocation();

        Microbot.enableAutoRunOn = false;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run() || !Microbot.isLoggedIn()) {
                debug("Not running");
                return;
            }

            if (Rs2AntibanSettings.actionCooldownActive) {
                debug("Cool down active");
                return;
            }

            determineState(cookingItem, logType);

            if (Rs2Dialogue.hasContinue()) {
                debug("Click to continue");
                Rs2Dialogue.clickContinue();
                return;
            }

           switch (state) {
            case COOKING:
                if (Rs2Inventory.count(cookingItem.getRawItemID()) == 0) {
                    debug("Out of cooking item in inventory");
                    return;
                }

                if (Rs2Player.waitForXpDrop(Skill.COOKING, 4500)) {
                    debug("Interacting");
                    Rs2Antiban.actionCooldown();
                    Rs2Antiban.takeMicroBreakByChance();
                    return;
                }

                WorldPoint activeFireLocation = Functions.isGameObjectOnTile(FireSpots, FireIDs);

                if (activeFireLocation != null && Rs2Player.distanceTo(activeFireLocation) > 3) {
                    debug("Walking to existing fire");
                    Rs2Walker.walkTo(activeFireLocation);
                    sleep(180, 540);
                    return;
                }

                debug("Looking for fires to use");

                boolean interacted = false;
                for (WorldPoint FireSpot : FireSpots) {
                    TileObject fireTile = Rs2GameObject.findGameObjectByLocation(FireSpot);
                    if (fireTile != null && FireIDs.contains(fireTile.getId())) {
                        debug("Using object on fire");
                        Rs2Inventory.useItemOnObject(cookingItem.getRawItemID(), fireTile.getId());
                        interacted = true;
                        break;
                    }
                }

                if (interacted) {
                    sleepUntil(() -> !Rs2Player.isMoving() && Rs2Widget.findWidget("How many would you like to cook?", null, false) != null, 5000);
                    sleep(180, 540);
                    Rs2Keyboard.keyPress(KeyEvent.VK_SPACE);
                    Rs2Player.waitForXpDrop(Skill.COOKING, 5000);
                }
                break;
            
            case BANKING:
                debug("Banking");
                bank(cookingItem);
                break;
            
               case BANKING_FOR_FIRE_SUPPLIES:
                debug("Banking for fire supplies");
                bankForFireSupplies(logType);
                break;
            
               case BUILDING_FIRE:
                debug("Building fire");

                   if (Rs2Player.isInteracting()) {
                       debug("Interacting");
                       return;
                   }

                   if (Rs2Player.distanceTo(desiredLocation.getWorldPoint()) > 1) {
                       debug("Walking to desired fire location");
                       Rs2Walker.walkTo(desiredLocation.getWorldPoint(), 0);
                       sleep(180, 540);
                       return;
                   }

                   Rs2Inventory.combine("Tinderbox", logType.getLogName());
                   Rs2Player.waitForXpDrop(Skill.FIREMAKING, 5000);
                break;

            default:
                break;
           }

            Rs2Antiban.actionCooldown();
            Rs2Antiban.takeMicroBreakByChance();
            return;
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

    // Determine the state of the script
    private void determineState(CookingItem cookingItem, LogType logType) {
        debug("Determine state");
        // Check each fire spot to see if any of them have a fire ID on them
        for (WorldPoint fireSpot : FireSpots) {
            TileObject fireTile = Rs2GameObject.findGameObjectByLocation(fireSpot);

            // If the fire tile has no game object, we can skip this iteration
            if (fireTile == null) {
                continue;
            }

            if (FireIDs.contains(fireTile.getId())) {
                debug("Fire found");
                if (Rs2Inventory.hasItem(cookingItem.getRawItemID())) {
                    debug("Cooking");
                    state = State.COOKING;
                } else {
                    debug("Banking");
                    state = State.BANKING;
                }
                return;
            }
        }

        if (!Rs2Inventory.hasItem("Tinderbox") || !Rs2Inventory.hasItem(logType.getLogName())) {
            debug("Need fire supplies");
            state = State.BANKING_FOR_FIRE_SUPPLIES;
        } else {
            debug("Building fire");
            state = State.BUILDING_FIRE;
        }
    }

    // Handle all banking actions
    private void bank(CookingItem cookingItem) {
        if (Rs2Bank.openBank()) {
            sleepUntil(Rs2Bank::isOpen);
            debug("Bank is open");
            Rs2Bank.depositAll();
            debug("Items deposited");
            sleep(180, 540);

            Rs2Bank.withdrawAll(cookingItem.getRawItemID());
            sleepUntil(() -> Rs2Inventory.hasItem(cookingItem.getRawItemID()), 3500);

            // Exit if we did not end up finding it.
            if (!Rs2Inventory.hasItem(cookingItem.getRawItemID())) {
                debug("Could not find cooking item in bank.");
                Microbot.showMessage("Could not find cooking item in bank.");
                shutdown();
            }
            sleep(180, 540);
            Rs2Bank.closeBank();
        }
    }

    private void bankForFireSupplies(LogType logType) {
        if (Rs2Bank.openBank()) {
            sleepUntil(Rs2Bank::isOpen);
            debug("Bank is open");
            Rs2Bank.depositAll();
            debug("Items deposited");
            sleep(180, 540);

            Rs2Bank.withdrawOne(590);
            debug("Withdrew a tinderbox");

            sleepUntil(() -> Rs2Inventory.hasItem("Tinderbox"), 3500);

            // Exit if we did not end up finding it.
            if (!Rs2Inventory.hasItem("Tinderbox")) {
                debug("Could not find Tinderbox in bank.");
                Microbot.showMessage("Could not find Tinderbox in bank.");
                shutdown();
            }

            sleep(180, 540);
            Rs2Bank.withdrawOne(logType.getLogID());
            debug("Withdrew a log");

            sleepUntil(() -> Rs2Inventory.hasItem(logType.getLogID()), 3500);

            // Exit if we did not end up finding it.
            if (!Rs2Inventory.hasItem(logType.getLogID())) {
                debug("Could not find logs in bank.");
                Microbot.showMessage("Could not find logs in bank.");
                shutdown();
            }
            sleep(180, 540);
            Rs2Bank.closeBank();
        }
    }

    private void debug(String msg) {
        debug = msg;
        System.out.println(msg);
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }
}
