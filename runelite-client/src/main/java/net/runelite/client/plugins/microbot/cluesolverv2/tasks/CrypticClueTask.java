package net.runelite.client.plugins.microbot.cluesolverv2.tasks;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.cluescrolls.clues.CrypticClue;
import net.runelite.client.plugins.cluescrolls.clues.item.ItemRequirement;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.cluesolverv2.taskinterface.ClueTask;
import net.runelite.client.plugins.microbot.cluesolverv2.util.ClueHelperV2;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.models.RS2Item;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static net.runelite.client.plugins.microbot.util.Global.sleep;

@Slf4j
public class CrypticClueTask implements ClueTask {
    @Inject
    private Client client;

    @Inject
    private ClueHelperV2 clueHelper;

    @Inject
    private EventBus eventBus;



    private CrypticClue clue;

    private List<ItemRequirement> requiredItems;

    private List<ItemRequirement> missingItems;

    private Map<Integer, String> requiredItemsMap;

    private State state;

    // Define the states for the task
    private enum State {
        CHECKING_ITEMS,
        RETRIEVING_ITEMS,
        VERIFYING_WITHDRAWAL,
        EQUIPPING_ITEMS,
        NAVIGATING_TO_OBJECT_LOCATION,
        FINDING_AND_KILLING_NPC,
        LOOTING_KEY,
        NAVIGATING_TO_OBJECT_LOCATION_AFTER_KILL,
        ANSWERING_QUESTIONS,
        INTERACTING_WITH_OBJECT,
        COMPLETED,
        FAILED
    }

    /**
     * Default constructor for CrypticClueTask.
     * Dependencies are injected via fields.
     */
    public CrypticClueTask() {
        this.state = State.CHECKING_ITEMS;
    }

    /**
     * Sets the CrypticClue and initializes required items.
     *
     * @param clue The CrypticClue instance to solve.
     */
    public void setClue(CrypticClue clue) {
        this.clue = clue;
        if (clue.isRequiresLight()) {

        }

        this.requiredItems = clueHelper.determineRequiredItems(clue);
        this.requiredItemsMap = clueHelper.getRequiredItemsMap();


        log.info("CrypticClueTask initialized for clue: {}", clue.getClass().getSimpleName());
        log.info("Number of required items: {}", requiredItems.size());
    }

    @Override
    public void start() {
        if (clue == null) {
            log.error("CrypticClue instance is null. Cannot start task.");
            state = State.FAILED;
            return;
        }
        log.info("Starting CrypticClueTask for {}", clue.getClass().getSimpleName());
        eventBus.register(this);
    }

    @Override
    public boolean execute() {
        log.debug("Executing CrypticClueTask in state: {}", state);
        switch (state) {
            case CHECKING_ITEMS:
                return checkAndHandleMissingItems();

            case RETRIEVING_ITEMS:
                retrieveRequiredItems();
                return false; // Wait for inventory change event to verify

            case VERIFYING_WITHDRAWAL:
                return verifyItemsPresence();

            case EQUIPPING_ITEMS:
                boolean equipped = equipItems();
                if (equipped) {
                    state = State.NAVIGATING_TO_OBJECT_LOCATION;
                    log.info("Transitioning to state: NAVIGATING_TO_OBJECT_LOCATION");
                } else {
                    log.warn("Failed to equip items. Transitioning to FAILED state.");
                    state = State.FAILED;
                }
                return false;

            case NAVIGATING_TO_OBJECT_LOCATION:
                if (navigateToLocation()) {
                    log.info("Navigation complete. Transitioning to next state.");
                    if (state == State.NAVIGATING_TO_OBJECT_LOCATION) {
                        state = State.FINDING_AND_KILLING_NPC;
                    } else if (state == State.NAVIGATING_TO_OBJECT_LOCATION_AFTER_KILL) {
                        state = State.INTERACTING_WITH_OBJECT;
                    }
                }
                return false;

            case FINDING_AND_KILLING_NPC:
                return findAndKillNpc();

            case LOOTING_KEY:
                return lootKey();

            case INTERACTING_WITH_OBJECT:
                return interactWithObject();

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
                eventBus.unregister(this);
                return true;
        }
    }

    @Override
    public void stop() {
        log.info("Stopping CrypticClueTask.");
        state = State.FAILED;
        eventBus.unregister(this);
    }

    @Override
    public String getTaskDescription() {
        return "Performing Cryptic Clue Task";
    }

    /**
     * Step 1: Check for missing items and handle retrieval if necessary.
     *
     * @return true if task should terminate; false otherwise.
     */
    private boolean checkAndHandleMissingItems() {
        log.info("Checking for missing items.");
        CountDownLatch latch = new CountDownLatch(1);
        Microbot.getClientThread().invoke(() -> {
            try {
                this.missingItems = clueHelper.getMissingItems(requiredItems);
                log.info("Number of missing items: {}", missingItems.size());
            } catch (Exception e) {
                log.error("Error during missing items check", e);
                state = State.FAILED;
            } finally {
                latch.countDown();
            }
        });

        try {
            latch.await(); // Wait for the invoke method to complete
        } catch (InterruptedException e) {
            log.error("Interrupted while waiting for missing items check", e);
            Thread.currentThread().interrupt();
            state = State.FAILED;
            return true;
        }

        if (state == State.FAILED) {
            return true; // Terminate task execution
        }

        if (missingItems.isEmpty()) {
            log.info("All required items are present. Proceeding to navigation.");
            state = State.EQUIPPING_ITEMS;
            return false; // Proceed to equip items
        } else {
            log.warn("Some items are missing. Initiating retrieval.");
            state = State.RETRIEVING_ITEMS;
            return false; // Transition to retrieve items
        }
    }

    /**
     * Step 2: Retrieve required items from the bank if any are missing.
     * Initiates withdrawal attempts for missing items.
     */
    private void retrieveRequiredItems() {
        log.info("Attempting to retrieve required items from the bank.");

        boolean success = Rs2Bank.walkToBankAndUseBank();

        if (!success) {
            log.warn("Failed to walk to the bank. Will retry in the next cycle.");
            return;
        }

        if (!Rs2Bank.openBank()) {
            log.warn("Failed to open the bank. Will retry in the next cycle.");
            return;
        }

        // Iterate the required items map and withdraw the items
        for (Map.Entry<Integer, String> entry : requiredItemsMap.entrySet()) {
            Integer itemId = entry.getKey();
            String itemName = entry.getValue();
            log.info("Attempting to withdraw item: {} With ID: {}", itemName, itemId);
            Rs2Bank.withdrawItem(itemName);
            sleep(300);
            if (Rs2Inventory.contains(itemName)) {
                sleep(300);
            } else {
                log.warn("Failed to initiate withdrawal for item: {} With ID: {}", itemName, itemId);
            }
        }

        Rs2Bank.closeBank();
        state = State.VERIFYING_WITHDRAWAL;
        log.info("Withdrawal attempts initiated. Waiting to verify item presence.");
    }

    /**
     * Step 4: Equip / wield / wear the items withdrawn from bank.
     *
     * @return true if items are equipped worn or wielded; false otherwise.
     */
    private boolean equipItems() {
        boolean allItemsEquipped = true;

        // Iterate the required items map and equip the items
        for (Map.Entry<Integer, String> entry : requiredItemsMap.entrySet()) {
            Integer itemId = entry.getKey();
            String itemName = entry.getValue();
            log.info("Attempting to equip item: {} With ID: {}", itemName, itemId);
            Rs2Inventory.equip(itemName);
            sleep(300);
            if (Rs2Equipment.hasEquipped(itemId)) {
                log.info("Item: {} With ID: {} is already equipped.", itemName, itemId);
            } else {
                log.warn("Failed to equip item: {} With ID: {}", itemName, itemId);
                allItemsEquipped = false;
            }
        }

        if (allItemsEquipped) {
            log.info("All items equipped successfully.");
            state = State.NAVIGATING_TO_OBJECT_LOCATION;
        } else {
            log.warn("Not all items were equipped. Cannot proceed.");
            state = State.FAILED;
        }

        return allItemsEquipped;
    }

    /**
     * Step 5: Navigate to the specified location.
     *
     * @return true if navigation is complete; false otherwise.
     */
    private boolean navigateToLocation() {
        WorldPoint location = clueHelper.getClueLocation(clue);
        log.info("Navigating to location: {}", location);
        if (location == null) {
            log.error("Clue location is null. Cannot navigate.");
            state = State.FAILED;
            return true;
        }

        if (Rs2Player.distanceTo(location) > 10) {
            log.info("Navigating to location: {}", location);
            boolean navigationStarted = Rs2Walker.walkTo(location, 5);

            if (navigationStarted) {
                log.info("Navigation to {} started.", location);
            } else {
                log.warn("Failed to initiate navigation to location: {}", location);
            }
            return false;
        } else if (Rs2Player.distanceTo(location) > 5) {
            log.info("Fine-tuning position with walkFastCanvas to {}", location);
            Rs2Walker.walkFastCanvas(location);
            return false;
        }

        log.info("Player has arrived at the clue location.");
        return true;
    }

    /**
     * Step 6: Find and kill the specified NPC near the object location.
     *
     * @return true if NPC is killed; false otherwise.
     */
    private boolean findAndKillNpc() {
        log.info("Attempting to locate and kill NPC: {}", clue.getNpc());
        NPC target = Rs2Npc.getNpc(clue.getNpc());

        if (target == null) {
            log.warn("NPC {} not found near the location. Scanning for NPC...", clue.getNpc());
            // Implement scanning logic if necessary
            // For simplicity, attempt to find the NPC within a certain radius
            target = Rs2Npc.getNpc(clue.getNpc()); // Search within 10 tiles
            if (target == null) {
                log.warn("NPC {} not found within scanning radius.", clue.getNpc());
                return false;
            }
        }

        if (!Rs2Combat.inCombat()) {
            boolean attackInitiated = Rs2Npc.attack(target);
            if (attackInitiated) {
                log.info("Attack on NPC {} initiated.", clue.getNpc());
                sleep(1000); // Wait for combat to start
                return false; // Continue monitoring combat
            } else {
                log.warn("Failed to initiate attack on NPC {}.", clue.getNpc());
                return false;
            }
        } else {
            // Check if NPC is still alive
            if (!target.isDead()) {
                log.info("Currently in combat with NPC {}.", clue.getNpc());
                return false; // Wait until combat is over
            } else {
                log.info("NPC {} has been defeated.", clue.getNpc());
                state = State.LOOTING_KEY;
                return false;
            }
        }
    }

    /**
     * Step 7: Loot the key dropped by the defeated NPC.
     *
     * @return true if key is looted; false otherwise.
     */
    private boolean lootKey() {
        log.info("Attempting to loot the key from NPC: {}", clue.getNpc());

        String keyItemName = "Clue Scroll (Medium)"; // Example key name
        int keyItemId = 22782; // Example key item ID

        // Check if the key is on the ground within a 5-tile radius
        RS2Item groundKeyItem = Arrays.stream(Rs2GroundItem.getAll(5))
                .filter(item -> item.getItem().getId() == keyItemId)
                .findFirst()
                .orElse(null);

        if (groundKeyItem != null) {
            boolean pickupInitiated = Rs2GroundItem.pickup(groundKeyItem.getItem().getId());
            if (pickupInitiated) {
                log.info("Key picked up successfully.");
                state = State.INTERACTING_WITH_OBJECT;
                return true;
            } else {
                log.warn("Failed to pick up the key.");
                return false;
            }
        } else {
            log.warn("Key not found on the ground. Checking inventory.");
            if (Rs2Inventory.contains(keyItemName)) {
                log.info("Key found in inventory.");
                state = State.INTERACTING_WITH_OBJECT;
                return true;
            } else {
                log.warn("Key not found. Possibly already looted or not dropped.");
                state = State.FAILED;
                return false;
            }
        }
    }

    /**
     * Interacts with the specified object using the object's ID.
     *
     * @return true if interaction with the object was successful; false otherwise.
     */
    private boolean interactWithObject() {
        log.info("Attempting to interact with object: Drawers.");

        // Retrieve all game objects with the specified ID within a 10-tile radius (adjust radius as needed)
        List<GameObject> objects = Rs2GameObject.getGameObjects(clue.getObjectId());

        if (objects.isEmpty()) {
            log.warn("Object with ID {} not found within the search radius.", clue.getObjectId());
            return false;
        }

        // Assuming the closest object to interact with
        TileObject targetObject = objects.get(0);

        // Attempt interaction
        boolean interactionSuccess = Rs2GameObject.interact(targetObject, "Search");
        if (interactionSuccess) {
            log.info("Successfully interacted with the object.");
            state = State.INTERACTING_WITH_OBJECT;
            return true;
        } else {
            log.warn("Failed to interact with the object.");
            return false;
        }
    }

    /**
     * Event listener for inventory changes to verify item withdrawals.
     *
     * @param event The ItemContainerChanged event.
     */
    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event) {
        if (event.getContainerId() != InventoryID.INVENTORY.getId()) {
            return;
        }

        if (state == State.VERIFYING_WITHDRAWAL) {
            log.info("Inventory updated. Verifying items...");
            boolean allItemsPresent = verifyItemsPresence();
            if (allItemsPresent) {
                // Proceed to equip
                // This is already handled in verifyItemsPresence()
            }
        }
    }

    /**
     * Event listener for NPC spawns to handle interactions when NPC is spawned.
     *
     * @param npcSpawned The NpcSpawned event.
     */
    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned) {
        if (state == State.FINDING_AND_KILLING_NPC || state == State.ANSWERING_QUESTIONS) {
            NPC npc = npcSpawned.getNpc();
            if (npc.getName().equalsIgnoreCase(clue.getNpc())) {
                log.info("NPC spawned: {}", npc.getName());
                if (state == State.FINDING_AND_KILLING_NPC) {
                    findAndKillNpc();
                } else if (state == State.ANSWERING_QUESTIONS) {
                    answerQuestions();
                }
            }
        }
    }

    private void answerQuestions() {
        // Implement question answering logic here
        log.info("Placeholder for answering questions");
    }

    /**
     * Event listener for Game Object spawns to handle interactions when the object is spawned.
     *
     * @param gameObjectSpawned The GameObjectSpawned event.
     */
    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned gameObjectSpawned) {
        if (state == State.INTERACTING_WITH_OBJECT) {
            net.runelite.api.GameObject gameObject = gameObjectSpawned.getGameObject();
            if (gameObject.getId() == clue.getObjectId()) {
                log.info("Game Object spawned: ID {}", gameObject.getId());
                interactWithObject();
            }
        }
    }

    /**
     * Verifies if all required items are present in the inventory.
     *
     * @return true if all items are present; false otherwise.
     */
    private boolean verifyItemsPresence() {
        List<ItemRequirement> stillMissingItems = clueHelper.getMissingItems(requiredItems);
        if (stillMissingItems.isEmpty()) {
            log.info("All required items are present.");
            state = State.EQUIPPING_ITEMS;
            return true;
        } else {
            log.warn("Still missing items: {}", stillMissingItems);
            state = State.RETRIEVING_ITEMS;
            return false;
        }
    }
}
