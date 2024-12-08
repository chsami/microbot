package net.runelite.client.plugins.microbot.grapefarmer;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static net.runelite.client.plugins.microbot.Microbot.log;


public class GrapeFarmerScript extends Script {

    private enum State {
        ADD_SALTPETRE,
        PLANT_GRAPE_SEED,
        CHECK_HEALTH,
        PICK_GRAPES,
        CLEAR_VINE,
        NONE
    }

    // State varbits
    private static final int ADD_SALTPETRE = 0;
    private static final int PLANT_GRAPE_SEED = 1;
    private static final int CHECK_HEALTH = 9;
    private static final int PICK_GRAPES = 10; // it changes from 10 - 14 during the picking
    private static final int CLEAR_VINE = 15;

    // HashMap for patches
    private static final Map<Integer, Integer> patchMap = new LinkedHashMap<>() {{
        put(Varbits.GRAPES_4959, 11816); // a1
        put(Varbits.GRAPES_4960, 11817); // a2
        put(Varbits.GRAPES_4961, 11947); // a3
        put(Varbits.GRAPES_4962, 12598); // a4
        put(Varbits.GRAPES_4963, 12599); // a5
        put(Varbits.GRAPES_4964, 12600); // a6
        put(Varbits.GRAPES_4958, 11815); // b1
        put(Varbits.GRAPES_4957, 11814); // b2
        put(Varbits.GRAPES_4956, 11813); // b3
        put(Varbits.GRAPES_4955, 11812); // b4
        put(Varbits.GRAPES_4954, 11811); // b5
        put(Varbits.GRAPES_4953, 11810); // b6
    }};


    public boolean run(GrapeFarmerConfig config) {
        Microbot.enableAutoRunOn = false;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;
                long startTime = System.currentTimeMillis();

                if (config.GEARING()) {
                    if (!this.isRunning()) return;
                    gearing();
                }

                for (Map.Entry<Integer, Integer> entry : patchMap.entrySet()) {
                    int varbitKey = entry.getKey();
                    int gameObjectId = entry.getValue();

                    // Process the current patch until its state transitions to DEFAULT
                    while (true) {

                        if (!this.isRunning()) return;
                        if (Rs2Player.isMoving() || Rs2Player.isAnimating() ||
                                Rs2Player.isInteracting() || Rs2Player.isWalking()) {
                            continue; // Wait for the player to be idle
                        }

                        // Get the current state of the patch
                        int currentVarbitValue = Microbot.getVarbitValue(varbitKey);
                        State currentState = getStateForVarbit(currentVarbitValue);

                        // If the state is DEFAULT, break out of the inner loop and move to the next patch
                        if (currentState == State.NONE) {
                            System.out.println("State is DEFAULT for GameObject ID: " + gameObjectId + ". Moving to the next patch.");
                            break;
                        }

                        // Perform actions based on the current state
                        switch (currentState) {
                            case CHECK_HEALTH:
                                System.out.println("Checking health of GameObject ID: " + gameObjectId);
                                checkHealth(gameObjectId);
                                break;

                            case PICK_GRAPES:
                                System.out.println("Picking grapes at GameObject ID: " + gameObjectId);
                                pickGrapes(gameObjectId);
                                waitForCompletion();
                                break;

                            case CLEAR_VINE:
                                System.out.println("Clearing vine at GameObject ID: " + gameObjectId);
                                clearVine(gameObjectId);
                                waitForCompletion();
                                break;

                            case ADD_SALTPETRE:
                                System.out.println("Adding salt petre to GameObject ID: " + gameObjectId);
                                addSaltpetre(gameObjectId);
                                waitForCompletion();
                                break;

                            case PLANT_GRAPE_SEED:
                                System.out.println("Planting grape seed at GameObject ID: " + gameObjectId);
                                plantSeed(gameObjectId);
                                waitForCompletion();
                                break;

                            default:
                                System.out.println("Unhandled state for GameObject ID: " + gameObjectId);
                                break;
                        }
                    }
                }

                long endTime = System.currentTimeMillis();
                long totalTime = endTime - startTime;
                System.out.println("Total time for loop " + totalTime);

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

    private void plantSeed(int gameObjectId) {
        if (Rs2Inventory.use(ItemID.GRAPE_SEED)) {
            Rs2GameObject.interact(gameObjectId);
            Rs2Player.waitForXpDrop(Skill.FARMING);
        }
    }

    private void addSaltpetre(int gameObjectId) {
        if (Rs2GameObject.interact(gameObjectId)) {
            sleep(4000, 4500);
        }

    }

    private void clearVine(int gameObjectId) {
        if (!Rs2Player.isMoving() && !Rs2Player.isAnimating(5000) && !Rs2Player.isInteracting()) {
            if (Rs2GameObject.interact(gameObjectId)) {
                Rs2Player.waitForAnimation(2500);
                sleepUntil(() -> !Rs2Player.isAnimating() &&
                        !Rs2Player.isMoving() &&
                        !Rs2Player.isInteracting());
            }
        }
    }

    private void pickGrapes(int gameObjectId) {
        if (Rs2Inventory.isFull()) {
            Rs2Inventory.use(ItemID.ZAMORAKS_GRAPES);
            Rs2Npc.interact(0);
            sleepUntil(() -> !Rs2Inventory.contains(ItemID.ZAMORAKS_GRAPES), 5000);
            if (Rs2Inventory.contains(ItemID.GRAPES)) {
                Rs2Inventory.use(ItemID.GRAPES);
                Rs2Npc.interact(0);
                sleepUntil(() -> !Rs2Inventory.contains(ItemID.GRAPES), 5000);
            }

        }
        if (Rs2GameObject.interact(gameObjectId)) {
            Rs2Player.waitForAnimation(500);
        }

    }

    private static State getStateForVarbit(int varbitValue) {
        if (varbitValue == 0) {
            // Empty, empty+fertilizer
            return State.ADD_SALTPETRE;
        }
        if (varbitValue == 1) {
            return State.PLANT_GRAPE_SEED;
        }
        if (varbitValue >= 2 && varbitValue < 9) {
            // Growing grape
            return State.NONE;
        }
        if (varbitValue == 9) {
            return State.CHECK_HEALTH;
        }
        if (varbitValue == 10) {
            return State.CHECK_HEALTH;
        }
        if (varbitValue >= 11 && varbitValue < 15) {
            // Harvestable grape
            return State.PICK_GRAPES;
        }
        if (varbitValue == 15) {
            return State.CLEAR_VINE;
        }
        return null;
    }

    // Simulates interaction with a game object
    private static void checkHealth(int gameObjectId) {
        // Replace this with actual game interaction logic
        System.out.println("Interacting with GameObject ID: " + gameObjectId + " using action: Check-health");
        Rs2GameObject.interact(gameObjectId);
        Rs2Player.waitForXpDrop(Skill.FARMING);
    }


    private void waitForCompletion() {
        sleepUntil(() -> !Rs2Player.isMoving() && !Rs2Player.isAnimating() && !Rs2Player.isInteracting());
    }

    public void gearing() {
        if (!Rs2Bank.isOpen()) {
            Rs2Bank.useBank();
            Rs2Bank.depositAll();
            Rs2Bank.withdrawX(ItemID.GRAPE_SEED, 12);
            Rs2Bank.withdrawOne(ItemID.GARDENING_TROWEL);
            Rs2Bank.withdrawOne(ItemID.SEED_DIBBER);
            Rs2Bank.withdrawOne(ItemID.SPADE);
            Rs2Bank.withdrawAllButOne(ItemID.BOLOGAS_BLESSING);
            Rs2Bank.withdrawX(ItemID.SALTPETRE, 12);
        }
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }
}