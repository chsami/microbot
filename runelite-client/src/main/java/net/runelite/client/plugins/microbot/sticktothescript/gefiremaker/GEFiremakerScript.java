package net.runelite.client.plugins.microbot.sticktothescript.gefiremaker;

import net.runelite.api.Skill;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.sticktothescript.common.Functions;
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

import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

enum State {
    FIREMAKING,
    BANKING,
    BANK_FOR_FIRE_SUPPLIES,
    BUILDING_FIRE
}

public class GEFiremakerScript extends Script {

    public static String version = "1.0.0";
    public State state = State.BANKING;
    public String debug = "";
    private boolean expectingXPDrop = false;

    private static List<WorldPoint> FireSpots = Arrays.asList(
            GEWorkLocation.NORTH_EAST.getWorldPoint(),
            GEWorkLocation.SOUTH_EAST.getWorldPoint(),
            GEWorkLocation.NORTH_WEST.getWorldPoint(),
            GEWorkLocation.SOUTH_WEST.getWorldPoint()
    );
    private static List<Integer> FireIDs = Arrays.asList(26185, 49927);

    public boolean run(GEFiremakerConfig config) {
        LogType logType = config.sLogType();
        GEWorkLocation desiredLocation = config.sLocation();

        Microbot.enableAutoRunOn = false;

        Rs2Antiban.resetAntibanSettings();
        Rs2Antiban.antibanSetupTemplates.applyFiremakingSetup();
        Rs2AntibanSettings.dynamicActivity = true;
        Rs2AntibanSettings.dynamicIntensity = true;
        Rs2AntibanSettings.actionCooldownChance = 0.1;
        Rs2AntibanSettings.microBreakChance = 0.01;
        Rs2AntibanSettings.microBreakDurationLow = 0;
        Rs2AntibanSettings.microBreakDurationHigh = 3;

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run() || !Microbot.isLoggedIn()) {
                debug("Not running");
                return;
            }

            if (Rs2AntibanSettings.actionCooldownActive) {
                debug("Cooldown active");
                Rs2Antiban.actionCooldown();
                return;
            }

            determineState(logType);

            // If the state is not firemaking, then let's reset the variable as we are not expecting an XP drop
            if (state != State.FIREMAKING) {
                expectingXPDrop = false;
            }

            if (Rs2Dialogue.hasContinue()) {
                debug("Click to continue");
                Rs2Dialogue.clickContinue();
                return;
            }

           switch (state) {
            case FIREMAKING:
                if (Rs2Inventory.count(logType.getLogID()) == 0) {
                    debug("Out of logs in inventory");
                    return;
                }

                if (expectingXPDrop && Rs2Player.waitForXpDrop(Skill.FIREMAKING, 4500)) {
                    debug("Firemaking in progress");
                    Rs2Antiban.actionCooldown();
                    Rs2Antiban.takeMicroBreakByChance();
                    sleep(256, 789);
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
                        Rs2Inventory.useItemOnObject(logType.getLogID(), fireTile.getId());
                        interacted = true;
                        break;
                    }
                }

                if (interacted) {
                    sleepUntil(() -> (!Rs2Player.isMoving() && Rs2Widget.findWidget("How many would you like to burn?", null, false) != null), 5000);
                    sleep(180, 540);
                    Rs2Keyboard.keyPress(KeyEvent.VK_SPACE);
                    expectingXPDrop = true;
                    sleep(2220, 5511);
                }
                break;
            
            case BANKING:
                debug("Banking");
                bank(logType);
                break;
            
               case BANK_FOR_FIRE_SUPPLIES:
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
                   Rs2Player.waitForXpDrop(Skill.FIREMAKING, 20000);
                break;

            default:
                break;
           }

            Rs2Antiban.actionCooldown();
            Rs2Antiban.takeMicroBreakByChance();
            sleep(256, 789);
            return;
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

    // Determine the state of the script
    private void determineState(LogType logType) {
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
                if (Rs2Inventory.hasItem(logType.getLogID())) {
                    debug("Adding logs to fire");
                    state = State.FIREMAKING;
                } else {
                    debug("Out of logs");
                    state = State.BANKING;
                }
                return;
            }
        }

        if (Rs2Inventory.hasItem("Tinderbox") && Rs2Inventory.hasItem(logType.getLogID())) {
            debug("Building fire");
            state = State.BUILDING_FIRE;
        } else {
            debug("Banking for firemaking supplies");
            state = State.BANK_FOR_FIRE_SUPPLIES;
        }
    }

    // Handle all banking actions
    private void bank(LogType logType) {
        if (Rs2Bank.openBank()) {
            sleepUntil(Rs2Bank::isOpen);
            debug("Bank is open");
            Rs2Bank.depositAll();
            debug("Items deposited");
            sleep(180, 540);

            Rs2Bank.withdrawAll(logType.getLogID());
            sleepUntil(() -> Rs2Inventory.hasItem(logType.getLogID()), 3500);

            // Exit if we did not end up finding it.
            if (!Rs2Inventory.hasItem(logType.getLogID())) {
                debug("Could not find log type in bank.");
                Microbot.showMessage("Could not find log type in bank.");
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
            Rs2Bank.withdrawAll(logType.getLogID());
            debug("Withdrew logs");

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
        Rs2Antiban.resetAntibanSettings();
    }
}
