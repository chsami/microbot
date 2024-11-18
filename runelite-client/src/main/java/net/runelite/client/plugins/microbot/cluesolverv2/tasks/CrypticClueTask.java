package net.runelite.client.plugins.microbot.cluesolverv2.tasks;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.NPC;
import net.runelite.api.ObjectComposition;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.plugins.microbot.cluescrolls.ClueScrollPlugin;
import net.runelite.client.plugins.microbot.cluescrolls.clues.CrypticClue;
import net.runelite.client.plugins.microbot.cluesolverv2.taskinterface.ClueTask;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.tile.Rs2Tile;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import javax.inject.Inject;
import java.util.*;

import static net.runelite.client.plugins.microbot.cluesolverv2.utils.CrypticTaskMap.crypticTaskMap;
import static net.runelite.client.plugins.microbot.util.Global.sleepUntil;

@Slf4j
public class CrypticClueTask implements ClueTask {

    @Inject
    private EventBus eventBus;

    @Inject
    private ClueScrollPlugin plugin;

    private CrypticClue clue;

    private State state;

    private String currentNpcName;
    private List<WorldPoint> currentNpcLocations;
    private Iterator<WorldPoint> npcLocationIterator;
    private WorldPoint currentTargetNpcLocation;


    private enum State {
        RETRIEVING_ITEMS,
        NAVIGATING_TO_LOCATION,
        NAVIGATING_TO_NPC_LOCATION,
        KILLING_NPC,
        LOOTING_KEY,
        INTERACTING_WITH_NPC,
        INTERACTING_WITH_OBJECT,
        DIGGING,
        COMPLETED,
        FAILED
    }


    public void setClue(CrypticClue clue) {
        this.clue = clue;
        this.state = State.RETRIEVING_ITEMS;
        log.info("CrypticClueTask initialized for clue");

        // Set the current NPC name and locations
        initializeKillClueVariables();
    }

    @Override
    public void start() {
        if (clue == null) {
            log.error("CrypticClue instance is null. Cannot start task.");
            state = State.FAILED;
            return;
        }
        log.info("Starting task for clue: {}", clue.getText());
        eventBus.register(this);
    }

    @Override
    public boolean execute() {
        switch (state) {
            case RETRIEVING_ITEMS:
                retrieveItems();
                break;

            case NAVIGATING_TO_NPC_LOCATION:
                navigateToNpcLocation();
                break;

            case KILLING_NPC:
                killNpc();
                break;

            case LOOTING_KEY:
                lootKey();
                break;

            case NAVIGATING_TO_LOCATION:
                navigateToLocation();
                break;

            case INTERACTING_WITH_NPC:
                interactWithNpc();
                break;

            case INTERACTING_WITH_OBJECT:
                interactWithObject();
                break;

            case DIGGING:
                digForTreasure();
                break;

            case COMPLETED:
                log.info("CrypticClueTask completed.");
                eventBus.unregister(this);
                return true;

            case FAILED:
                log.error("CrypticClueTask failed.");
                eventBus.unregister(this);
                return true;

            default:
                log.error("Unknown state encountered: {}", state);
                state = State.FAILED;
                return true;
        }
        return false; // Continue the task
    }


    private void initializeKillClueVariables() {
        if (clue.getSolution(plugin) != null && !clue.getSolution(plugin).trim().isEmpty()) {
            // Attempt to match clue text to NPC in map
            Map<String, Object> npcData = findNpcFromClueText(clue.getSolution(plugin));

            if (npcData != null) {
                currentNpcName = (String) npcData.get("npc");
                currentNpcLocations = npcData.get("location") != null
                        ? List.of((WorldPoint) npcData.get("location"))
                        : Collections.emptyList();

                log.info("Matched clue to NPC '{}'. Using predefined locations: {}", currentNpcName, currentNpcLocations);
            } else {
                log.warn("No match found in map for clue text: {}", clue.getSolution(plugin));
                currentNpcName = null;
                currentNpcLocations = Collections.emptyList();
            }
        }
    }


    private Map<String, Object> findNpcFromClueText(String clueSolution) {
        // Normalize clue text for consistent matching
        String normalizedText = clueSolution.toLowerCase();

        for (Map<String, Object> entry : crypticTaskMap) {
            String npcName = (String) entry.get("npc");
            if (npcName != null && normalizedText.contains(npcName.toLowerCase())) {
                return entry; // Return the matching entry
            }
        }
        return null; // No match found
    }

    private boolean navigateToNpcLocation() {
        if (npcLocationIterator == null) {
            npcLocationIterator = currentNpcLocations.iterator();
            if (npcLocationIterator.hasNext()) {
                currentTargetNpcLocation = npcLocationIterator.next();
                log.info("Attempting to navigate to NPC location: {}", currentTargetNpcLocation);
            } else {
                log.error("No NPC locations available for NPC '{}'.", currentNpcName);
                state = State.FAILED;
                return true;
            }
        }

        if (Rs2Player.getWorldLocation().distanceTo(currentTargetNpcLocation) <= 8) {
            log.info("Arrived at NPC location: {}", currentTargetNpcLocation);
            state = State.KILLING_NPC;
            return true;
        }

        if (!Rs2Walker.walkTo(currentTargetNpcLocation, 5)) {
            log.warn("Failed to walk to NPC location: {}", currentTargetNpcLocation);
            // Optionally, try the next location if available
            if (npcLocationIterator.hasNext()) {
                currentTargetNpcLocation = npcLocationIterator.next();
                log.info("Trying next NPC location: {}", currentTargetNpcLocation);
            } else {
                log.error("All NPC locations checked. NPC '{}' not found.", currentNpcName);
                state = State.FAILED;
                return true;
            }
        }

        return false; // Still navigating
    }


    private boolean killNpc() {
        NPC targetNpc = Rs2Npc.getNpc(currentNpcName);
        log.info("Target NPC: {}", targetNpc);

        if (targetNpc == null) {
            log.warn("NPC '{}' not found at location {}", currentNpcName, currentTargetNpcLocation);
            state = State.NAVIGATING_TO_NPC_LOCATION;
            return false;
        }

        // Attempt to attack the NPC
        if (!Rs2Combat.inCombat() && Rs2Npc.attack(targetNpc)) {
            log.info("Engaged in combat with NPC: {}", targetNpc);
            sleepUntil(targetNpc::isDead);
            log.info("NPC '{}' has been killed.", targetNpc);
            state = State.LOOTING_KEY;
        }
        return false; // Continue checking
    }


    private boolean lootKey() {
        log.info("Attempting to loot key from killed NPC '{}'.", currentNpcName);

        List<String> keyNames = Arrays.asList("Key (medium)", "Key (elite)");
        int lootRange = 5; // Define the range for looting

        // Use exists() to verify presence of any key before looting
        for (String key : keyNames) {
            if (Rs2GroundItem.exists(key, lootRange)) {
                log.info("Key '{}' detected on the ground. Attempting to loot.", key);

                if (Rs2GroundItem.loot(key, lootRange)) {
                    sleepUntil(() -> Rs2Inventory.contains(key), 5000);

                    if (Rs2Inventory.contains(key)) {
                        log.info("Successfully looted key: '{}'", key);
                        state = State.NAVIGATING_TO_LOCATION; // Transition to next state
                        return true;
                    } else {
                        log.warn("Key '{}' looting failed. Retrying if needed.", key);
                    }
                } else {
                    log.warn("Interaction with key '{}' failed.", key);
                }
            }
        }

        log.warn("No matching keys found near {} after killing NPC '{}'.", currentTargetNpcLocation, currentNpcName);
        return false;
    }



    @Override
    public void stop() {
        log.info("Stopping CrypticClueTask.");
        state = State.FAILED;
        eventBus.unregister(this);
    }

    @Override
    public String getTaskDescription() {
        return "Solving cryptic clue: " + (clue != null ? clue.getText() : "No clue assigned");
    }

    private boolean retrieveItems() {
        log.info("Retrieving required items for clue");
        if (clue.isRequiresSpade() && !Rs2Inventory.contains("Spade")) {
            log.info("Spade is required. Walking to bank to retrieve.");
            Rs2Bank.walkToBankAndUseBank();
            Rs2Bank.withdrawItem("Spade");
            Rs2Bank.closeBank();
        }

        // Check if we need to kill an NPC and if we already have the required key
        if (currentNpcName != null && !currentNpcName.isEmpty()) {
            if (hasRequiredKey()) {
                log.info("Key already in inventory. Skipping NPC kill.");
                state = State.NAVIGATING_TO_LOCATION;
            } else {
                state = State.NAVIGATING_TO_NPC_LOCATION;
            }
        } else {
            state = State.NAVIGATING_TO_LOCATION;
        }
        return false;
    }


    private boolean hasRequiredKey() {
        List<String> keyNames = Arrays.asList("Key (medium)", "Key (elite)");
        return keyNames.stream().anyMatch(Rs2Inventory::contains);
    }

    private boolean navigateToLocation() {
        WorldPoint location = clue.getLocation(null);
        if (location == null) {
            log.error("Clue location is null. Cannot navigate.");
            state = State.FAILED;
            return true;
        }

        log.info("Navigating to clue location: {}", location);

        // Check if we are close to the target location
        int distanceToLocation = Rs2Player.distanceTo(location);

        if (distanceToLocation > 2) {
            // Use regular navigation to get close to the target
            boolean navigationSuccess = Rs2Walker.walkTo(location);
            if (!navigationSuccess) {
                log.warn("Failed to navigate to clue location.");
                return false; // Retry navigating
            }
            return false; // Continue navigating
        }

        // When close enough, use fast canvas walking for precision
        if (distanceToLocation > 1) {
            log.info("Close to the clue location. Using fast canvas walking for final approach.");
            boolean fastCanvasSuccess = Rs2Walker.walkFastCanvas(location);
            if (!fastCanvasSuccess) {
                log.warn("Fast canvas walking failed.");
                return false; // Retry final adjustment
            }
        }

        // Final state transitions upon arrival
        log.info("Arrived at clue location.");
        if (clue.getNpc() != null && crypticTaskMap.stream().noneMatch(entry -> entry.get("npc").equals(clue.getNpc()))) {
            state = State.INTERACTING_WITH_NPC;
        } else if (clue.getObjectId() > 0) {
            state = State.INTERACTING_WITH_OBJECT;
        } else if (clue.isRequiresSpade()) {
            state = State.DIGGING;
        } else {
            state = State.COMPLETED;
        }
        return false;
    }


    private boolean interactWithNpc() {
        log.info("Interacting with NPC: {}", clue.getNpc());
        NPC npc = Rs2Npc.getNpc(clue.getNpc());
        if (npc == null) {
            log.warn("NPC {} not found.", clue.getNpc());
            return false;
        }

        boolean interacted = Rs2Npc.interact(npc);
        if (interacted) {
            log.info("Successfully interacted with NPC: {}", clue.getNpc());
            state = State.COMPLETED;
        } else {
            log.warn("Failed to interact with NPC: {}", clue.getNpc());
        }
        return interacted;
    }

    private boolean interactWithObject() {
        log.info("Attempting to interact with object ID: {}", clue.getObjectId());

        // Step 1: Locate the target object
        TileObject targetObject = Rs2GameObject.findObjectById(clue.getObjectId());
        if (targetObject == null) {
            log.warn("Target object with ID {} not found.", clue.getObjectId());
            return false;
        }

        WorldPoint objectLocation = targetObject.getWorldLocation();

        // Step 2: Check if the object is reachable
        if (!Rs2Tile.isTileReachable(objectLocation)) {
            log.info("Target object is not reachable. Handling obstacles...");

            // Step 3: Find surrounding objects for potential obstacles
            List<TileObject> obstacles = getSurroundingObjects(objectLocation, 3);
            for (TileObject obstacle : obstacles) {
                ObjectComposition objComp = Rs2GameObject.convertGameObjectToObjectComposition(obstacle);
                if (objComp != null && hasRelevantAction(objComp)) {
                    String actionToPerform = getRelevantAction(objComp);
                    log.info("Obstacle '{}' detected at {}. Attempting to '{}'", objComp.getName(), obstacle.getWorldLocation(), actionToPerform);

                    if (Rs2GameObject.interact(obstacle, actionToPerform)) {
                        log.info("Successfully interacted with the obstacle: {}", obstacle.getWorldLocation());
                        sleepUntil(() -> Rs2Tile.isTileReachable(objectLocation), 5000);
                        // Re-check if the target object is now reachable
                        if (Rs2Tile.isTileReachable(objectLocation)) {
                            break;
                        }
                    } else {
                        log.warn("Failed to interact with obstacle at {}", obstacle.getWorldLocation());
                        return false;
                    }
                }
            }
        }

        // Step 4: Walk to the target object
        log.info("Walking to the target object at {}", objectLocation);
        if (!Rs2Walker.walkFastCanvas(objectLocation)) {
            log.warn("Failed to walk to the target object.");
            return false;
        }
        sleepUntil(() -> Rs2Player.getWorldLocation().equals(objectLocation), 5000);

        // Step 5: Ensure line of sight
        if (!Rs2GameObject.hasLineOfSight(targetObject)) {
            log.info("Moving closer to get line of sight...");
            Rs2Walker.walkFastCanvas(targetObject.getWorldLocation());
            sleepUntil(() -> Rs2GameObject.hasLineOfSight(targetObject), 5000);
        }

        // Step 6: Interact with the target object
        if (Rs2GameObject.hasLineOfSight(targetObject)) {
            log.info("Object is now in line of sight. Attempting to interact...");
            if (Rs2GameObject.interact(targetObject, "Search")) {
                log.info("Successfully interacted with the object.");
                state = State.COMPLETED;
                return true;
            } else {
                log.warn("Interaction with the object failed.");
            }
        } else {
            log.warn("Object is still not interactable after navigating closer.");
        }

        return false;
    }


    public static List<TileObject> getSurroundingObjects(WorldPoint centerTile, int radius) {
        List<TileObject> surroundingObjects = new ArrayList<>();

        // Get all tiles within the radius around the centerTile
        List<WorldPoint> tiles = Rs2Tile.getWalkableTilesAroundTile(centerTile, radius);

        for (WorldPoint tile : tiles) {
            // Check for game objects at each tile
            TileObject object = Rs2GameObject.findObjectByLocation(tile);
            if (object != null) {
                surroundingObjects.add(object);
            }
        }

        return surroundingObjects;
    }

    private boolean hasRelevantAction(ObjectComposition objComp) {
        String[] actions = objComp.getActions();
        return Arrays.stream(actions).anyMatch(action ->
                action != null && (action.equalsIgnoreCase("Open") || action.equalsIgnoreCase("Unlock") || action.equalsIgnoreCase("Climb-over")));
    }

    private String getRelevantAction(ObjectComposition objComp) {
        String[] actions = objComp.getActions();
        for (String action : actions) {
            if (action != null && (action.equalsIgnoreCase("Open") || action.equalsIgnoreCase("Unlock") || action.equalsIgnoreCase("Climb-over"))) {
                return action;
            }
        }
        return null;
    }




    private boolean digForTreasure() {
        log.info("Digging for treasure.");
        boolean digSuccess = Rs2Inventory.interact("Spade", "Dig");
        if (digSuccess) {
            log.info("Successfully dug at clue location.");
            state = State.COMPLETED;
        } else {
            log.warn("Failed to dig at clue location.");
        }
        return digSuccess;
    }
}
