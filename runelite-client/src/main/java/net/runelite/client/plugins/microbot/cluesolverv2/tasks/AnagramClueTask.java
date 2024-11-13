package net.runelite.client.plugins.microbot.cluesolverv2.tasks;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.NPC;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.cluescrolls.clues.AnagramClue;
import net.runelite.client.plugins.cluescrolls.clues.item.ItemRequirement;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.cluesolverv2.taskinterface.ClueTask;
import net.runelite.client.plugins.microbot.cluesolverv2.util.ClueHelperV2;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static net.runelite.client.plugins.microbot.util.Global.sleep;

@Slf4j
public class AnagramClueTask implements ClueTask {
    @Inject
    private Client client;

    @Inject
    private ClueHelperV2 clueHelper;

    @Inject
    private EventBus eventBus;

    private AnagramClue clue;

    private List<ItemRequirement> requiredItems;

    List<ItemRequirement> missingItems;

    private Map<Integer, String> requiredItemsMap;

    private State state;

    // Define the states for the task
    private enum State {
        CHECKING_ITEMS,
        RETRIEVING_ITEMS,
        VERIFYING_WITHDRAWAL,
        EQUIPPING_ITEMS,
        NAVIGATING_TO_LOCATION,
        INTERACTING_WITH_NPC,
        INTERACTING_WITH_OBJECT,
        ANSWERING_QUESTIONS,
        COMPLETED,
        FAILED
    }

    /**
     * Default constructor for AnagramClueTask.
     * Dependencies are injected via fields.
     */
    public AnagramClueTask() {
        this.state = State.CHECKING_ITEMS;
    }

    /**
     * Sets the AnagramClue and initializes required items.
     *
     * @param clue The AnagramClue instance to solve.
     */
    public void setClue(AnagramClue clue) {
        this.clue = clue;
        this.requiredItems = clueHelper.determineRequiredItems(clue);
        this.requiredItemsMap = clueHelper.getRequiredItemsMap();

        log.info("AnagramClueTask initialized for clue: {}", clue.getClass().getSimpleName());
        log.info("Number of required items: {}", requiredItems.size());
    }

    @Override
    public void start() {
        if (clue == null) {
            log.error("AnagramClue instance is null. Cannot start task.");
            state = State.FAILED;
            return;
        }
        log.info("Starting AnagramClueTask for {}", clue.getClass().getSimpleName());
        eventBus.register(this);
    }

    @Override
    public boolean execute() {
        log.debug("Executing AnagramClueTask in state: {}", state);
        switch (state) {
            case CHECKING_ITEMS:
                return checkAndHandleMissingItems();

            case RETRIEVING_ITEMS:
                retrieveRequiredItems();
                return false; // Wait for inventory change event to verify

            case VERIFYING_WITHDRAWAL:
                return verifyItemsPresence();

            case EQUIPPING_ITEMS:
                return equipItems();

            case NAVIGATING_TO_LOCATION:
                return navigateToLocation();

            case INTERACTING_WITH_NPC:
                return interactWithNPC();

            case INTERACTING_WITH_OBJECT:
                return interactWithObject();

            case ANSWERING_QUESTIONS:
                return answerQuestions();

            case COMPLETED:
                log.info("AnagramClueTask completed.");
                eventBus.unregister(this);
                return true;

            case FAILED:
                log.error("AnagramClueTask failed.");
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
        log.info("Stopping AnagramClueTask.");
        state = State.FAILED;
        eventBus.unregister(this);
    }

    @Override
    public String getTaskDescription() {
        return "Performing Anagram Clue Task";
    }

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
            state = State.NAVIGATING_TO_LOCATION;
            return false; // Proceed to navigation
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
     * Step 3: Verify that all required items have been successfully withdrawn.
     *
     * @return true if all items are present; false otherwise.
     */
    private boolean verifyItemsPresence() {
        Microbot.getClientThread().invoke(() -> {
            log.info("Verifying presence of withdrawn items in inventory.");
            List<ItemRequirement> missingItems = clueHelper.getMissingItems(requiredItems);

            if (missingItems.isEmpty()) {
                log.info("All required items have been successfully withdrawn.");
                transitionToEquipState();
            } else {
                log.warn("Some items are still missing");
                state = State.RETRIEVING_ITEMS;
            }
        });

        return false;
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

        return allItemsEquipped;
    }

    /**
     * Helper method to transition to equip state if all items are present.
     */
    private void transitionToEquipState() {
        if (equipItems()) {
            log.info("All items equipped successfully. Transitioning to navigation state...");
            state = State.NAVIGATING_TO_LOCATION;
        } else {
            log.warn("Not all items were equipped. Cannot transition to navigation state.");
        }
    }

    /**
     * Step 5: Navigate to the specified location.
     *
     * @return true if navigation is complete; false otherwise.
     */
    private boolean navigateToLocation() {
        WorldPoint location = clueHelper.getClueLocation(clue);
        if (location == null) {
            log.error("Clue location is null. Cannot navigate.");
            state = State.FAILED;
            return true;
        }

        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
        int distance = location.distanceTo2D(playerLocation);

        // Navigate to the location if outside a 10x10 tile range
        if (distance > 10) {
            log.info("Navigating to location: {}", location);
            boolean navigationStarted = Rs2Walker.walkTo(location, 5);  // Start walking within 2 tiles of the target

            if (navigationStarted) {
                log.info("Navigation to {} started.", location);
            } else {
                log.warn("Failed to initiate navigation to location: {}", location);
            }
            return false;  // Wait for navigation to complete
        }
        // Use walkFastCanvas for precise adjustment within a 10-tile range but outside 2 tiles
        else if (distance > 5) {
            log.info("Fine-tuning position with walkFastCanvas to {}", location);
            Rs2Walker.walkFastCanvas(location);
            return false;  // Allow time for the final adjustment
        }

        log.info("Player has arrived at the clue location.");

        // Determine the next state based on the clue requirements
        if (clue.getNpc() != null && !clue.getNpc().isEmpty()) {
            state = State.INTERACTING_WITH_NPC;
        }
        if (clue.getObjectId() != -1) {
            state = State.INTERACTING_WITH_OBJECT;
        }
        if ((clue.getNpc() == null || clue.getNpc().isEmpty()) && clue.getObjectId() == -1) {
            // No NPC or Object to interact with; mark as completed
            state = State.COMPLETED;
            return true;
        }
        return false;
    }


    /**
     * Step 6: Interact with the NPC to complete the clue.
     *
     * @return true if interaction is successful and task is completed; false otherwise.
     */
    private boolean interactWithNPC() {
        log.info("Interacting with the NPC to complete the clue.");
        NPC target = Rs2Npc.getNpc(clue.getNpc());

        if (target == null) {
            log.warn("NPC {} not found in the vicinity.", clue.getNpc());
            return false;
        }

        boolean interactionInitiated = Rs2Npc.interact(target, "Talk-to");
        if (interactionInitiated) {
            log.info("NPC interaction initiated.");
            sleep(1000);
            state = State.ANSWERING_QUESTIONS;
            return false; // Proceed to answer questions if any
        } else {
            log.warn("Failed to interact with the NPC.");
            return false;
        }
    }

    /**
     * Step 7: Interact with the Game Object to complete the clue.
     *
     * @return true if interaction is successful; false otherwise.
     */
    private boolean interactWithObject() {
        log.info("Interacting with the Game Object to complete the clue.");
        Rs2GameObject targetObject = (Rs2GameObject) Rs2GameObject.getGameObjects(clue.getObjectId(), Rs2Player.getWorldLocation()).stream().findFirst().orElse(null);

        if (targetObject == null) {
            log.warn("Game Object with ID {} not found in the vicinity.", clue.getObjectId());
            return false;
        }

        boolean interactionInitiated = Rs2GameObject.interact((TileObject) targetObject, "Use");
        if (interactionInitiated) {
            log.info("Game Object interaction initiated.");
            sleep(1000);
            state = State.COMPLETED;
            return true;
        } else {
            log.warn("Failed to interact with the Game Object.");
            return false;
        }
    }


    /**
     * Step 8: Answer any questions posed by the NPC.
     *
     * @return true if questions are answered successfully; false otherwise.
     */
    private boolean answerQuestions() {
        if (clue.getQuestion() != null && clue.getAnswer() != null) {
            log.info("Answering NPC question: {}", clue.getQuestion());
            if (Rs2Dialogue.isInDialogue()) {
                if (Rs2Dialogue.hasCombinationDialogue()) {
                    Rs2Keyboard.typeString(clue.getAnswer());
                    sleep(500);
                }
                if (Rs2Dialogue.hasContinue()) {
                    Rs2Dialogue.clickContinue();
                    sleep(500);
                }
                // Verify if the answer was correct
                if (!Rs2Dialogue.isInDialogue()) {
                    log.info("Answered the question successfully.");
                    state = (clue.getObjectId() != -1) ? State.INTERACTING_WITH_OBJECT : State.COMPLETED;
                    return true;
                } else {
                    log.warn("Dialogue still active. Possible incorrect answer or additional steps required.");
                    return false;
                }
            } else {
                log.warn("Not currently in dialogue. Unable to answer question.");
                return false;
            }
        } else {
            // No question to answer; mark as completed
            log.info("No questions to answer. Marking task as completed.");
            state = (clue.getObjectId() != -1) ? State.INTERACTING_WITH_OBJECT : State.COMPLETED;
            return true;
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
        if (state == State.INTERACTING_WITH_NPC || state == State.ANSWERING_QUESTIONS) {
            NPC npc = npcSpawned.getNpc();
            if (npc.getName().equalsIgnoreCase(clue.getNpc())) {
                log.info("NPC spawned: {}", npc.getName());
                if (state == State.INTERACTING_WITH_NPC) {
                    interactWithNPC();
                } else if (state == State.ANSWERING_QUESTIONS) {
                    answerQuestions();
                }
            }
        }
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
}
