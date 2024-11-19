package net.runelite.client.plugins.microbot.cluesolverv2.tasks;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ObjectComposition;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.plugins.microbot.cluescrolls.clues.CoordinateClue;
import net.runelite.client.plugins.microbot.cluesolverv2.taskinterface.ClueTask;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.tile.Rs2Tile;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static net.runelite.client.plugins.microbot.util.Global.sleepUntil;

@Slf4j
public class CoordinateClueTask implements ClueTask {

    private enum State {
        RETRIEVING_ITEMS,
        NAVIGATING_TO_LOCATION,
        HANDLING_OBSTACLES,
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
        this.state = State.RETRIEVING_ITEMS;
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
            case RETRIEVING_ITEMS:
                return retrieveItems();

            case NAVIGATING_TO_LOCATION:
                return navigateToLocation();

            case HANDLING_OBSTACLES:
                return handleObstacles();

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

    private boolean retrieveItems() {
        log.info("Retrieving required items (spade and light source, if needed).");

        boolean requiresLight = clue.isRequiresLight();
        boolean requiresSpade = clue.isRequiresSpade();
        boolean hasSpade = Rs2Inventory.contains("Spade");
        boolean hasLight = hasLightSource();

        if ((!hasSpade && requiresSpade) || (requiresLight && !hasLight)) {
            log.info("Walking to bank to retrieve required items.");

            if (!Rs2Bank.walkToBankAndUseBank()) {
                log.error("Failed to navigate to bank.");
                state = State.FAILED;
                return true;
            }

            if (requiresSpade && !hasSpade) {
                log.info("Withdrawing Spade from bank.");
                Rs2Bank.withdrawItem("Spade");
                sleepUntil(() -> Rs2Inventory.contains("Spade"), 5000);

                if (!Rs2Inventory.contains("Spade")) {
                    log.error("Failed to withdraw Spade.");
                    state = State.FAILED;
                    return true;
                }
            }

            if (requiresLight && !hasLight) {
                log.info("Withdrawing light source from bank.");
                Rs2Bank.withdrawItem("Bullseye lantern");
                if (!Rs2Inventory.contains("Bullseye lantern")) {
                    log.error("Failed to withdraw a light source.");
                    state = State.FAILED;
                    return true;
                }
            }

            Rs2Bank.closeBank();
        }

        log.info("All required items retrieved. Proceeding to navigation.");
        state = State.NAVIGATING_TO_LOCATION;
        return false;
    }


    private boolean hasLightSource() {
        return Rs2Inventory.contains("Bullseye lantern") || Rs2Inventory.contains("Lit candle");
    }

    private boolean navigateToLocation() {
        WorldPoint location = clue.getLocation(null);
        if (location == null) {
            log.error("Clue location is null. Cannot navigate.");
            state = State.FAILED;
            return true;
        }

        log.info("Navigating to clue location: {}", location);
        if (Rs2Player.distanceTo(location) > 2) {
            if (!Rs2Walker.walkTo(location, 5)) {
                log.warn("Failed to navigate to clue location.");
                return false;
            }
            return false; // Continue navigating
        }

        log.info("Arrived at clue location.");
        state = State.DIGGING;
        return false;
    }

    private boolean handleObstacles() {
        log.info("Checking for obstacles near clue location.");

        WorldPoint location = clue.getLocation(null);
        List<TileObject> obstacles = getSurroundingObjects(location, 3);

        for (TileObject obstacle : obstacles) {
            ObjectComposition objComp = Rs2GameObject.convertGameObjectToObjectComposition(obstacle);
            if (objComp != null && hasRelevantAction(objComp)) {
                String actionToPerform = getRelevantAction(objComp);
                log.info("Obstacle '{}' detected at {}. Attempting to '{}'", objComp.getName(), obstacle.getWorldLocation(), actionToPerform);

                if (Rs2GameObject.interact(obstacle, actionToPerform)) {
                    log.info("Successfully handled obstacle at {}", obstacle.getWorldLocation());
                    sleepUntil(() -> Rs2Tile.isTileReachable(location), 5000);
                } else {
                    log.warn("Failed to interact with obstacle at {}", obstacle.getWorldLocation());
                    state = State.FAILED;
                    return false;
                }
            }
        }

        log.info("No significant obstacles detected or all obstacles cleared.");
        state = State.DIGGING;
        return false;
    }

    private List<TileObject> getSurroundingObjects(WorldPoint centerTile, int radius) {
        List<TileObject> surroundingObjects = new ArrayList<>();
        List<WorldPoint> tiles = Rs2Tile.getWalkableTilesAroundTile(centerTile, radius);

        for (WorldPoint tile : tiles) {
            TileObject object = Rs2GameObject.findObjectByLocation(tile);
            if (object != null) {
                surroundingObjects.add(object);
            }
        }

        return surroundingObjects;
    }

    private boolean hasRelevantAction(ObjectComposition objComp) {
        String[] actions = objComp.getActions();
        return actions != null && actions.length > 0 && (actions[0].equalsIgnoreCase("Open") || actions[0].equalsIgnoreCase("Climb-over"));
    }

    private String getRelevantAction(ObjectComposition objComp) {
        String[] actions = objComp.getActions();
        for (String action : actions) {
            if (action != null && (action.equalsIgnoreCase("Open") || action.equalsIgnoreCase("Climb-over"))) {
                return action;
            }
        }
        return null;
    }

    private boolean digAtLocation() {
        log.info("Attempting to dig at clue location.");
        if (Rs2Inventory.interact("Spade", "Dig")) {
            log.info("Successfully dug at clue location.");
            state = State.COMPLETED;
            return true;
        }

        log.warn("Failed to dig at clue location. Retrying.");
        return false;
    }
}
