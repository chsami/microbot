package net.runelite.client.plugins.microbot.cluesolverv2.tasks;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.NPC;
import net.runelite.api.ScriptID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.cluescrolls.ClueScrollPlugin;
import net.runelite.client.plugins.cluescrolls.clues.EmoteClue;
import net.runelite.client.plugins.cluescrolls.clues.emote.STASHUnit;
import net.runelite.client.plugins.cluescrolls.clues.item.ItemRequirement;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.cluesolverv2.taskinterface.ClueTask;
import net.runelite.client.plugins.microbot.cluesolverv2.util.ClueHelperV2;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static net.runelite.client.plugins.microbot.util.Global.sleep;

@Slf4j
public class EmoteClueTask implements ClueTask {
    @Inject
    private Client client;

    @Inject
    private ClueHelperV2 clueHelper;

    @Inject
    private EventBus eventBus;

    @Inject
    private ClueScrollPlugin clueScrollPlugin;


    private EmoteClue clue;

    private List<ItemRequirement> requiredItems;

    List<ItemRequirement> missingItems;


    private Map<Integer, String> requiredItemsMap;


    private State state;

    private static final int URI_ID = 7206; // NPC ID for Uri (if needed)

    private boolean stashUnitBuilt = false;


    // Define the states for the task
    private enum State {
        CHECKING_ITEMS,
        RETRIEVING_ITEMS,
        VERIFYING_WITHDRAWAL,
        EQUIPPING_ITEMS,
        NAVIGATING_TO_LOCATION,
        COMPLETED,
        PERFORMING_EMOTES,
        INTERACTING_WITH_NPC,
        FAILED
    }

    /**
     * Default constructor for EmoteClueTask.
     * Dependencies are injected via fields.
     */
    public EmoteClueTask() {
        this.state = State.CHECKING_ITEMS;
    }

    /**
     * Sets the EmoteClue and initializes required items.
     *
     * @param clue The EmoteClue instance to solve.
     */
    public void setClue(EmoteClue clue) {
        this.clue = clue;
        this.requiredItems = clueHelper.determineRequiredItems(clue);
        this.requiredItemsMap = clueHelper.getRequiredItemsMap();

        log.info("EmoteClueTask initialized for clue: {}", clue.getClass().getSimpleName());
        log.info("Number of required items: {}", requiredItems.size());
    }


    @Override
    public void start() {
        if (clue == null) {
            log.error("EmoteClue instance is null. Cannot start task.");
            state = State.FAILED;
            return;
        }
        log.info("Starting EmoteClueTask for {}", clue.getClass().getSimpleName());
        eventBus.register(this);
    }

    @Override
    public boolean execute() {
        log.debug("Executing EmoteClueTask in state: {}", state);
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

            case PERFORMING_EMOTES:
                return performEmotes();

            case INTERACTING_WITH_NPC:
                return interactWithNPC();

            case COMPLETED:
                log.info("EmoteClueTask completed.");
                eventBus.unregister(this);
                return true;

            case FAILED:
                log.error("EmoteClueTask failed.");
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
        log.info("Stopping EmoteClueTask.");
        state = State.FAILED;
        eventBus.unregister(this);
    }

    @Override
    public String getTaskDescription() {
        return "Performing Emote Clue Task";
    }

    private boolean checkAndHandleMissingItems() {
        log.info("Checking for missing items & Stash Unit");
        CountDownLatch latch = new CountDownLatch(1);
        Microbot.getClientThread().invoke(() -> {
            try {
            this.missingItems = clueHelper.getMissingItems(requiredItems);
            log.info("Number of missing items: {}", missingItems.size());
            stashUnitBuilt = checkStashUnit();
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
                transitionToNaviState();
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

        // Iterate the required items map and withdraw the items
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
     * Helper method to transition to navigation state if all items are equipped.
     */
    private void transitionToNaviState() {
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
            state = State.COMPLETED;
            return true;
        }

        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
        if (!playerLocation.equals(location)) {
            log.info("Navigating to location: {}", location);
            boolean navigationStarted = Rs2Walker.walkTo(location, 1);
            if (navigationStarted) {
                log.info("Navigation to {} started.", location);
            } else {
                log.warn("Failed to initiate navigation to location: {}", location);
            }
            return false; // Wait for navigation to complete
        }

        log.info("Player has arrived at the clue location.");
        state = State.PERFORMING_EMOTES;
        return false;
    }

    /**
     * Step 6: Perform the required emotes for the clue.
     *
     * @return true if emotes are performed successfully; false otherwise.
     */
    private boolean performEmotes() {
        log.info("Performing required emotes for the clue.");

        boolean firstEmoteDone = performFirstEmote();
        if (!firstEmoteDone) {
            log.warn("Failed to perform the first emote.");
            return false;
        }

        boolean secondEmoteDone = performSecondEmote();
        if (!secondEmoteDone) {
            log.warn("Failed to perform the second emote.");
            return false;
        }

        log.info("All required emotes performed successfully.");
        state = State.INTERACTING_WITH_NPC;
        return true;
    }


    /**
     * Step 7: Interact with the NPC to complete the clue.
     *
     * @return true if interaction is successful and task is completed; false otherwise.
     */
    private boolean interactWithNPC() {
        log.info("Interacting with the NPC to complete the clue.");
        Rs2Npc target = (Rs2Npc) Rs2Npc.getNpc(URI_ID);
        // Find the NPC
        boolean interactionInitiated = Rs2Npc.interact((NPC) target, "Talk-to");
        if (interactionInitiated) {
            log.info("NPC interaction initiated.");
            sleep(1000);
            if (Rs2Dialogue.isInDialogue()) {
                if (Rs2Dialogue.hasContinue()) {
                    Rs2Dialogue.clickContinue();
                    sleep(1000);
                }
            }
            // Optionally, handle dialogue or further interactions
            state = State.COMPLETED;
            return true;
        } else {
            log.warn("Failed to interact with the NPC.");
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
                // Proceed to navigation
                // This is already handled in verifyItemsPresence()
            }
        }
    }

    /**
     * Perform the first emote required for the clue.
     *
     * @return true if the first emote was performed successfully; false otherwise.
     */
    public boolean performFirstEmote() {
        log.info("Performing first emote...");
        String firstEmote = clue.getFirstEmote().getName();
        //open emotes tab if not open
        if (Rs2Tab.switchToEmotesTab()) {
            sleep(1200);
            log.info("Switched to Emotes tab.");
            if (Rs2Widget.clickWidget(firstEmote)) {
                log.info("Clicked on first emote: {}", firstEmote);
                sleep(1000);
                return true;
            } else {
                log.warn("Failed to click on first emote: {}", firstEmote);
                return false;
            }
        }
        return false;
    }

    /**
     * Perform the second emote required for the clue.
     *
     * @return true if the second emote was performed successfully; false otherwise.
     */
    public boolean performSecondEmote() {
        log.info("Performing second emote...");
        String secondEmote = clue.getSecondEmote().getName();
        //open emotes tab if not open
        if (Rs2Tab.switchToEmotesTab()) {
            sleep(1200);
            log.info("Switched to Emotes tab.");
            if (Rs2Widget.clickWidget(secondEmote)) {
                log.info("Clicked on second emote: {}", secondEmote);
                sleep(1000);
                return true;
            } else {
                log.warn("Failed to click on second emote: {}", secondEmote);
                return false;
            }
        }
        return false;
    }

    /**
     * Check if the STASH unit is built in the player's POH.
     *
     * @return true if the STASH unit is built; false otherwise.
     */
    private boolean checkStashUnit() {
        STASHUnit stashUnit = clue.getStashUnit();
        if (stashUnit != null) {
            log.info("Checking for STASH unit availability...");

            Microbot.getClientThread().invoke(() -> {
                Client client = Microbot.getClient();
                client.runScript(ScriptID.WATSON_STASH_UNIT_CHECK, stashUnit.getObjectId(), 0, 0, 0);

                int[] intStack = client.getIntStack();
                stashUnitBuilt = intStack[0] == 1;
                log.info("STASH unit built: {}", stashUnitBuilt);
            });

        }
        return stashUnitBuilt;
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned) {
        if (state == State.INTERACTING_WITH_NPC) {
            NPC npc = npcSpawned.getNpc();
            if (npc.getId() == URI_ID) {
                log.info("NPC spawned: {}", npc.getName());
                interactWithNPC();
                state = State.COMPLETED;
            }
        }
    }

}

