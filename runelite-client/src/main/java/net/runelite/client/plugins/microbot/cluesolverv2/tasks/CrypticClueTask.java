package net.runelite.client.plugins.microbot.cluesolverv2.tasks;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.cluescrolls.clues.CrypticClue;
import net.runelite.client.plugins.microbot.cluesolverv2.taskinterface.ClueTask;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.models.RS2Item;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import javax.inject.Inject;
import java.util.*;

import static net.runelite.client.plugins.microbot.cluesolverv2.utils.CrypticTaskMap.crypticTaskMap;
import static net.runelite.client.plugins.microbot.util.Global.sleepUntil;

@Slf4j
public class CrypticClueTask implements ClueTask {

    @Inject
    private EventBus eventBus;

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
        log.info("CrypticClueTask initialized for clue: {}", clue.getText());

        // Set the current NPC name and locations
        Microbot.getClientThread().invoke(this::initializeKillClueVariables);
    }

    @Override
    public void start() {
        if (clue == null) {
            log.error("CrypticClue instance is null. Cannot start task.");
            state = State.FAILED;
            return;
        }
        log.info("Starting CrypticClueTask for clue: {}", clue.getText());
        eventBus.register(this);
    }

    @Override
    public boolean execute() {
        log.debug("Executing CrypticClueTask in state: {}", state);
        switch (state) {
            case RETRIEVING_ITEMS:
                return retrieveItems();

            case NAVIGATING_TO_NPC_LOCATION:
                return navigateToNpcLocation();

            case KILLING_NPC:
                return killNpc();

            case LOOTING_KEY:
                return lootKey();

            case NAVIGATING_TO_LOCATION:
                return navigateToLocation();

            case INTERACTING_WITH_NPC:
                return interactWithNpc();

            case INTERACTING_WITH_OBJECT:
                return interactWithObject();

            case DIGGING:
                return digForTreasure();

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
    }

    private void initializeKillClueVariables() {
        if (clue.getText() != null && !clue.getText().trim().isEmpty()) {
            // Attempt to match clue text to NPC in map
            Map<String, Object> npcData = findNpcFromClueText(clue.getText());

            if (npcData != null) {
                currentNpcName = (String) npcData.get("npc");
                currentNpcLocations = npcData.get("location") != null
                        ? List.of((WorldPoint) npcData.get("location"))
                        : Collections.emptyList();

                log.info("Matched clue to NPC '{}'. Using predefined locations: {}", currentNpcName, currentNpcLocations);
            } else {
                log.warn("No match found in map for clue text: {}", clue.getText());
                currentNpcName = null;
                currentNpcLocations = Collections.emptyList();
            }
        }
    }


    private Map<String, Object> findNpcFromClueText(String clueText) {
        // Normalize clue text for consistent matching
        String normalizedText = clueText.toLowerCase();

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
        }

        if (!npcLocationIterator.hasNext()) {
            log.error("All NPC locations checked. NPC '{}' not found.", currentNpcName);
            state = State.FAILED;
            return true;
        }

        currentTargetNpcLocation = npcLocationIterator.next();
        log.info("Attempting to navigate to NPC location: {}", currentTargetNpcLocation);

        if (!Rs2Walker.walkTo(currentTargetNpcLocation, 5)) {
            log.warn("Failed to walk to NPC location: {}", currentTargetNpcLocation);
            return false;
        }

        if (Rs2Player.getWorldLocation().distanceTo(currentTargetNpcLocation) <= 1) {
            log.info("Arrived at NPC location: {}", currentTargetNpcLocation);
            state = State.KILLING_NPC;
            return true;
        }

        return false; // Still navigating
    }


    private boolean killNpc() {
        log.info("Killing NPC: {}", currentNpcName);
        NPC npc = Rs2Npc.getNpc(currentNpcName);
        if (npc == null) {
            log.warn("NPC '{}' not found at location {}", currentNpcName, currentTargetNpcLocation);
            state = State.NAVIGATING_TO_NPC_LOCATION;
            return false;
        }

        if (!Rs2Combat.inCombat() && Rs2Npc.attack(npc)) {
            log.info("Engaged in combat with NPC: {}", currentNpcName);
            return false; // Wait for combat to finish
        }

        if (!npc.isDead()) {
            log.info("NPC '{}' defeated. Proceeding to loot.", currentNpcName);
            state = State.LOOTING_KEY;
            return true;
        }

        return false; // Continue checking
    }


    private boolean lootKey() {
        log.info("Attempting to loot key from killed NPC '{}'.", currentNpcName);

        // Define the array of Rs2Items for potential keys
        List<String> keyNames = Arrays.asList("Key (medium)", "Key (elite)");


        // Fetch all ground items in the area
        RS2Item[] groundItems = Rs2GroundItem.getAll(10);

        if (groundItems.length == 0) {
            log.warn("No ground items found near {}.", currentTargetNpcLocation);
            return false;
        }

        // Loop through the Rs2Items and try to loot matching keys
        for (String key : keyNames) {
            for (RS2Item groundItem : groundItems) {
                if (groundItem.getItem().getName().equalsIgnoreCase(key)) {
                    log.info("Found key '{}' on the ground. Attempting to loot it.", key);

                    // Loot the item using Rs2GroundItem.loot()
                    if (Rs2GroundItem.loot(groundItem.getItem().getId())) {
                        // Wait for the item to appear in the inventory
                        sleepUntil(() -> Rs2Inventory.contains(key), 5000);

                        if (Rs2Inventory.contains(key)) {
                            log.info("Successfully looted key: '{}'", key);
                            state = State.NAVIGATING_TO_LOCATION; // Transition to the next state
                            return true;
                        } else {
                            log.warn("Looting key '{}' failed. Retrying...", key);
                        }
                    } else {
                        log.warn("Interaction with key '{}' failed. Retrying...", key);
                    }
                }
            }
        }

        log.warn("No matching keys found near {} after killing NPC '{}'.", currentTargetNpcLocation, currentNpcName);
        return false; // Retry looting or handle failure
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
        log.info("Retrieving required items for clue: {}", clue.getText());
        if (clue.isRequiresSpade() && !Rs2Inventory.contains("Spade")) {
            log.info("Spade is required. Walking to bank to retrieve.");
            Rs2Bank.walkToBankAndUseBank();
            Rs2Bank.withdrawItem("Spade");
            Rs2Bank.closeBank();
        }
        state = State.NAVIGATING_TO_LOCATION;
        return false;
    }

    private boolean navigateToLocation() {
        WorldPoint location = clue.getLocation(null);
        if (location == null) {
            log.error("Clue location is null. Cannot navigate.");
            state = State.FAILED;
            return true;
        }

        log.info("Navigating to clue location: {}", location);
        if (Rs2Player.distanceTo(location) > 1) {
            boolean navigationSuccess = Rs2Walker.walkTo(location, 5);
            if (!navigationSuccess) {
                log.warn("Failed to navigate to clue location.");
                return false;
            }
            return false; // Still navigating
        }

        log.info("Arrived at clue location.");
        if (clue.getNpc() != null) {
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
        log.info("Interacting with object ID: {}", clue.getObjectId());
        boolean interactionSuccess = Rs2GameObject.interact(clue.getObjectId(), "Search");
        if (interactionSuccess) {
            log.info("Successfully interacted with object.");
            state = State.COMPLETED;
        } else {
            log.warn("Failed to interact with object.");
        }
        return interactionSuccess;
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
