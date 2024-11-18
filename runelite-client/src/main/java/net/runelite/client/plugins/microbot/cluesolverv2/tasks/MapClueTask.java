package net.runelite.client.plugins.microbot.cluesolverv2.tasks;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.plugins.microbot.cluescrolls.clues.MapClue;
import net.runelite.client.plugins.microbot.cluesolverv2.taskinterface.ClueTask;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import javax.inject.Inject;

import static net.runelite.client.plugins.microbot.util.Global.sleepUntil;

@Slf4j
public class MapClueTask implements ClueTask {

    private enum State {
        CHECKING_ITEMS,
        NAVIGATING_TO_LOCATION,
        PERFORMING_ACTION,
        COMPLETED,
        FAILED
    }

    @Inject
    private EventBus eventBus;

    private MapClue clue;
    private State state;

    public void setClue(MapClue clue) {
        this.clue = clue;
        this.state = State.CHECKING_ITEMS;
        log.info("Map clue task set to clue: {}", clue);
    }

    @Override
    public void start() {
        log.info("Starting MapClueTask for clue: {}", clue.getItemId());
        eventBus.register(this);
    }

    @Override
    public boolean execute() {
        switch (state) {
            case CHECKING_ITEMS:
                checkItems();
                break;
            case NAVIGATING_TO_LOCATION:
                navigateToLocation();
                break;
            case PERFORMING_ACTION:
                performAction();
                break;
            case COMPLETED:
                log.info("MapClueTask completed.");
                eventBus.unregister(this);
                return true; // Task completed
            case FAILED:
                log.error("MapClueTask failed.");
                eventBus.unregister(this);
                return true; // Task failed
            default:
                log.error("Unknown state encountered: {}", state);
                state = State.FAILED;
                return true;
        }
        return false; // Task not yet completed
    }

    @Override
    public void stop() {
        log.info("Stopping MapClueTask.");
        state = State.FAILED;
        eventBus.unregister(this);
    }

    @Override
    public String getTaskDescription() {
        return "Solving map clue: " + clue.getItemId();
    }

    private void checkItems() {
        log.info("Checking required items for Map Clue.");

        // Check if a Spade is required and not in the inventory
        if (clue.isRequiresSpade() && !Rs2Inventory.contains("Spade")) {
            log.info("Spade is required. Attempting to retrieve it from the bank.");

            WorldPoint bankLocation = Rs2Bank.getNearestBank().getWorldPoint();

            boolean canWalkToBank = Rs2Walker.walkTo(bankLocation, 5);
            if (!canWalkToBank) {
                log.error("Failed to walk to the bank.");
                return;
            }

            log.info("Distance from bankLocation: {}", Rs2Player.distanceTo(bankLocation));

            // Open the bank and withdraw the spade
            if (Rs2Bank.openBank()) {
                log.info("Bank opened successfully.");
                Rs2Bank.withdrawItem("Spade"); // Attempt to withdraw the spade
                sleepUntil(() -> Rs2Inventory.contains("Spade"), 5000);

                // Verify if the spade was successfully added to inventory
                if (Rs2Inventory.contains("Spade")) {
                    log.info("Successfully retrieved Spade from the bank.");
                    Rs2Bank.closeBank(); // Close the bank after successful withdrawal
                    sleepUntil(() -> !Rs2Bank.isOpen(), 5000);
                } else {
                    log.error("Spade is still not in inventory after withdrawal attempt.");
                    state = State.FAILED;
                    return;
                }
            } else {
                log.error("Failed to open the bank.");
                state = State.FAILED;
                return;
            }
        }

        // Transition to the next state
        log.info("All required items are in the inventory.");
        state = State.NAVIGATING_TO_LOCATION;
    }


    private void navigateToLocation() {
        WorldPoint location = clue.getLocation(null);
        if (location == null) {
            log.error("Clue location is null. Cannot navigate.");
            state = State.FAILED;
            return;
        }

        log.info("Navigating to clue location: {}", location);

        if (Rs2Player.distanceTo(location) > 1) {
            if (!Rs2Walker.walkTo(location, 1)) {
                log.warn("Failed to navigate to clue location.");
                return; // Retry navigating
            }
        }

        if (Rs2Player.distanceTo(location) > 1) {
            Rs2Walker.walkFastCanvas(location);
            sleepUntil(() -> Rs2Player.distanceTo(location) <= 1, 10000);
        }


        state = State.PERFORMING_ACTION;
    }

    private void performAction() {
        if (clue.getObjectId() != -1) {
            // Interact with object
            interactWithObject();
        } else if (clue.isRequiresSpade()) {
            // Dig at location
            digAtLocation();
        } else {
            log.warn("No action defined for this clue.");
            state = State.COMPLETED;
        }
    }

    private void interactWithObject() {
        log.info("Attempting to interact with object ID: {}", clue.getObjectId());

        TileObject targetObject = Rs2GameObject.findObjectById(clue.getObjectId());
        if (targetObject == null) {
            log.warn("Target object with ID {} not found. Retrying...", clue.getObjectId());
            return; // Retry finding the object
        }

        if (!Rs2GameObject.hasLineOfSight(targetObject)) {
            log.info("Object not in line of sight. Moving closer...");
            Rs2Walker.walkFastCanvas(targetObject.getWorldLocation());
            sleepUntil(() -> Rs2GameObject.hasLineOfSight(targetObject), 5000);
        }

        if (Rs2GameObject.interact(targetObject, "Search")) {
            log.info("Successfully interacted with the object.");
            state = State.COMPLETED;
        } else {
            log.warn("Failed to interact with the object. Retrying...");
        }
    }

    private void digAtLocation() {
        log.info("Digging at clue location.");
        if (Rs2Inventory.interact("Spade", "Dig")) {
            log.info("Successfully dug at clue location.");
            state = State.COMPLETED;
        } else {
            log.warn("Failed to dig at clue location. Retrying...");
        }
    }
}
