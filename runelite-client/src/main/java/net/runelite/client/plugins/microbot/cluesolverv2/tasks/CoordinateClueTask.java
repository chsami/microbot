package net.runelite.client.plugins.microbot.cluesolverv2.tasks;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.plugins.microbot.cluescrolls.clues.CoordinateClue;
import net.runelite.client.plugins.microbot.cluesolverv2.taskinterface.ClueTask;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import javax.inject.Inject;

@Slf4j
public class CoordinateClueTask implements ClueTask {

    private enum State {
        CHECKING_ITEMS,
        NAVIGATING_TO_LOCATION,
        DIGGING,
        COMPLETED,
        FAILED
    }

    @Inject
    private EventBus eventBus;

    private CoordinateClue clue;
    private State state;

    public void setClue(CoordinateClue clue) {
        this.clue = clue;
        this.state = State.CHECKING_ITEMS;
        log.info("CoordinateClueTask initialized for clue at location: {}", clue.getLocation(null));
    }

    @Override
    public void start() {
        if (clue == null) {
            log.error("CoordinateClue is null. Cannot start task.");
            state = State.FAILED;
            return;
        }
        log.info("Starting Coordinate Clue Task for location: {}", clue.getLocation(null));
        eventBus.register(this);
    }

    @Override
    public boolean execute() {
        log.debug("Executing Coordinate Clue Task in state: {}", state);
        switch (state) {
            case CHECKING_ITEMS:
                return checkRequiredItems();

            case NAVIGATING_TO_LOCATION:
                return navigateToLocation();

            case DIGGING:
                return digAtLocation();

            case COMPLETED:
                log.info("Coordinate Clue Task completed.");
                eventBus.unregister(this);
                return true;

            case FAILED:
                log.error("Coordinate Clue Task failed.");
                eventBus.unregister(this);
                return true;

            default:
                log.error("Unknown state encountered: {}", state);
                state = State.FAILED;
                return true;
        }
    }

    @Override
    public void stop() {
        log.info("Stopping Coordinate Clue Task.");
        state = State.FAILED;
        eventBus.unregister(this);
    }

    @Override
    public String getTaskDescription() {
        return "Solving Coordinate Clue at location: " + clue.getLocation(null);
    }

    /**
     * Step 1: Check if the player has the required items (spade or light source).
     */
    private boolean checkRequiredItems() {
        log.info("Checking for required items (spade or light source).");

        // Check for spade
        if (!Rs2Inventory.contains("Spade")) {
            log.error("Player does not have a spade. Task cannot continue.");
            state = State.FAILED;
            return true;
        }

        // Check if a light source is required
        if (clue.isRequiresLight() && !Rs2Inventory.contains("Bullseye lantern") && !Rs2Inventory.contains("Lit candle")) {
            log.error("Player does not have a required light source. Task cannot continue.");
            state = State.FAILED;
            return true;
        }

        log.info("All required items are present. Proceeding to navigation.");
        state = State.NAVIGATING_TO_LOCATION;
        return false;
    }

    /**
     * Step 2: Navigate to the clue location.
     */
    private boolean navigateToLocation() {
        WorldPoint location = clue.getLocation(null);
        if (location == null) {
            log.error("Clue location is null. Cannot navigate.");
            state = State.FAILED;
            return true;
        }

        if (Rs2Player.distanceTo(location) > 1) {
            log.info("Navigating to clue location: {}", location);
            boolean success = Rs2Walker.walkTo(location, 5);
            if (!success) {
                log.warn("Failed to navigate to location. Retrying.");
                return false;
            }
        }

        log.info("Arrived at clue location.");
        state = State.DIGGING;
        return false;
    }

    /**
     * Step 3: Perform digging at the clue location.
     */
    private boolean digAtLocation() {
        log.info("Attempting to dig at clue location.");

        if (Rs2Inventory.interact("Spade", "Dig")) {
            log.info("Successfully dug at the clue location.");
            state = State.COMPLETED;
            return true;
        }

        log.warn("Failed to dig at the clue location. Retrying.");
        return false;
    }
}
