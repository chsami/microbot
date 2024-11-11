package net.runelite.client.plugins.microbot.cluesolverv2.tasks;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.plugins.cluescrolls.clues.EmoteClue;
import net.runelite.client.plugins.cluescrolls.clues.item.ItemRequirement;
import net.runelite.client.plugins.microbot.cluesolverv2.taskinterface.ClueTask;
import net.runelite.client.plugins.microbot.cluesolverv2.util.ClueHelperV2;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.InventoryID;
import net.runelite.client.eventbus.Subscribe;

import java.util.List;

@Slf4j
public class EmoteClueTask implements ClueTask {

    private EmoteClue clue;
    private ClueHelperV2 clueHelper;
    private Client client;
    private List<ItemRequirement> requiredItems;
    private EventBus eventBus;
    private State state;

    private static final int URI_ID = 7206; // NPC ID for Uri

    private enum State {
        CHECKING_ITEMS,
        RETRIEVING_ITEMS,
        VERIFYING_WITHDRAWAL,
        NAVIGATING_TO_LOCATION,
        COMPLETED
    }

    public EmoteClueTask() {
        this.state = State.CHECKING_ITEMS;  // Initial state is checking items
    }

    /**
     * Initializes the EmoteClueTask with necessary dependencies and data.
     *
     * @param client        RuneLite client instance.
     * @param clue          The EmoteClue instance to solve.
     * @param clueHelper    Helper class for clue-related operations.
     * @param requiredItems List of required items for the clue.
     * @param eventBus      EventBus instance.
     * @return The initialized EmoteClueTask instance.
     */
    public EmoteClueTask initialize(Client client, EmoteClue clue, ClueHelperV2 clueHelper, List<ItemRequirement> requiredItems, EventBus eventBus) {
        this.client = client;
        this.clue = clue;
        this.clueHelper = clueHelper;
        this.requiredItems = requiredItems;
        this.eventBus = eventBus;
        this.state = State.CHECKING_ITEMS;

        log.info("EmoteClueTask initialized with {} required items.", requiredItems.size());
        for (ItemRequirement req : requiredItems) {
            try {
                String name = req.getCollectiveName(client);
                log.info("Initialized with Required Item: {}", name);
            } catch (Exception e) {
                log.error("Error retrieving name for ItemRequirement: {}", req.getClass().getSimpleName(), e);
                log.info("Initialized with Required Item: Unknown Item");
            }
        }
        return this;
    }

    @Override
    public void start() {
        log.info("Starting EmoteClueTask for {}", clue.getClass().getSimpleName());
        eventBus.register(this);
    }

    @Override
    public boolean execute() {
        log.info("Executing EmoteClueTask in state: {}", state);
        switch (state) {
            case CHECKING_ITEMS:
                return checkAndHandleMissingItems();

            case RETRIEVING_ITEMS:
                retrieveRequiredItems();
                return false; // Wait for next execution to verify

            case VERIFYING_WITHDRAWAL:
                return verifyItemsPresence();

            case NAVIGATING_TO_LOCATION:
                if (navigateToLocation()) {
                    log.info("Task completed.");
                    state = State.COMPLETED;
                }
                break;
        }
        log.info("Current state after execution: {}", state);
        return state == State.COMPLETED;
    }

    @Override
    public void stop() {
        log.info("Stopping EmoteClueTask.");
        state = State.COMPLETED;
        eventBus.unregister(this);
    }

    @Override
    public String getTaskDescription() {
        return "Performing Emote Clue Task";
    }

    /**
     * Step 1: Check for missing items. If missing items are found, initiate retrieval from the bank.
     *
     * @return true if all items are present and task can proceed to navigation; false otherwise.
     */
    private boolean checkAndHandleMissingItems() {
        log.info("Checking for missing items...");
        List<ItemRequirement> missingItems = clueHelper.getMissingItems(requiredItems);

        if (missingItems.isEmpty()) {
            log.info("All required items are already present. Proceeding to navigation.");
            state = State.NAVIGATING_TO_LOCATION;
            return true; // Proceed to navigation
        } else {
            log.info("Missing items detected: {}", clueHelper.getItemNames(missingItems));
            state = State.RETRIEVING_ITEMS; // Transition to retrieve items
            return false;
        }
    }

    /**
     * Step 2: Retrieve required items from the bank if any are missing.
     * Initiates withdrawal attempts for missing items.
     */
    private void retrieveRequiredItems() {
        log.info("Attempting to retrieve required items from the bank.");
        if (!Rs2Bank.openBank()) {
            log.warn("Failed to open bank. Will retry in the next cycle.");
            return;
        }

        List<ItemRequirement> missingItems = clueHelper.getMissingItems(requiredItems);
        List<String> missingItemNames = clueHelper.getItemNames(missingItems);

        for (String itemName : missingItemNames) {
            log.info("Attempting to withdraw item: {}", itemName);
            Rs2Bank.withdrawItem(itemName); // Withdraw the item without expecting a boolean
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
        log.info("Verifying presence of withdrawn items in inventory.");
        List<ItemRequirement> missingItems = clueHelper.getMissingItems(requiredItems);

        if (missingItems.isEmpty()) {
            log.info("All required items have been successfully withdrawn.");
            state = State.NAVIGATING_TO_LOCATION;
            return true;
        } else {
            log.warn("Some items are still missing: {}", clueHelper.getItemNames(missingItems));
            // Optionally, you can decide to retry withdrawal for missing items
            return false;
        }
    }

    /**
     * Step 4: Navigate to the specified location.
     *
     * @return true if navigation is complete; false otherwise.
     */
    private boolean navigateToLocation() {
        WorldPoint location = clueHelper.getClueLocation(clue);
        if (location != null && !client.getLocalPlayer().getWorldLocation().equals(location)) {
            log.info("Navigating to location: {}", location);
            boolean navigationStarted = Rs2Walker.walkTo(location); // Ensure this method returns a boolean
            if (!navigationStarted) {
                log.warn("Failed to initiate navigation to location: {}", location);
                return false;
            }
            return false; // Indicate that navigation is ongoing
        }
        log.info("Player has arrived at the clue location.");
        return true;
    }

    /**
     * Event listener for inventory changes to verify item withdrawals.
     *
     * @param event The ItemContainerChanged event.
     */
    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event) {
        if (event.getContainerId() == InventoryID.INVENTORY.getId()) {
            if (state == State.VERIFYING_WITHDRAWAL) {
                log.info("Inventory updated. Verifying items...");
                boolean allItemsPresent = verifyItemsPresence();
                if (allItemsPresent) {
                    // Proceed to navigation
                    // This is already handled in verifyItemsPresence()
                }
            }
        }
    }
}
