package net.runelite.client.plugins.microbot.cluesolverv2.tasks;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.cluescrolls.clues.EmoteClue;
import net.runelite.client.plugins.cluescrolls.clues.item.ItemRequirement;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.cluesolverv2.taskinterface.ClueTask;
import net.runelite.client.plugins.microbot.cluesolverv2.util.ClueHelperV2;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.api.events.ItemContainerChanged;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Slf4j
public class EmoteClueTask implements ClueTask {
    @Inject
    private Client client;

    @Inject
    private ClueHelperV2 clueHelper;

    @Inject
    private EventBus eventBus;

    private EmoteClue clue;

    private List<ItemRequirement> requiredItems;

    private Map<Integer, String> requiredItemsMap;


    private State state;

    private static final int URI_ID = 7206; // NPC ID for Uri (if needed)

    // Define the states for the task
    private enum State {
        CHECKING_ITEMS,
        RETRIEVING_ITEMS,
        VERIFYING_WITHDRAWAL,
        NAVIGATING_TO_LOCATION,
        COMPLETED
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
        log.info("Cached items: {}", requiredItemsMap);
    }


    @Override
    public void start() {
        if (clue == null) {
            log.error("EmoteClue instance is null. Cannot start task.");
            state = State.COMPLETED;
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

            case NAVIGATING_TO_LOCATION:
                return navigateToLocation();

            case COMPLETED:
                log.info("EmoteClueTask completed.");
                return true;

            default:
                log.error("Unknown state encountered: {}", state);
                state = State.COMPLETED;
                return true;
        }
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

        if (!requiredItemsMap.isEmpty()) {
            log.info("All required items are present. Proceeding to navigation.");
            state = State.NAVIGATING_TO_LOCATION;
            return true; // Proceed to navigation
        } else {
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
            log.warn("Failed to open the bank. Will retry in the next cycle.");
            return;
        }

        // Iterate the required items map and withdraw the items
        for (Map.Entry<Integer, String> entry : requiredItemsMap.entrySet()) {
            Integer itemId = entry.getKey();
            String itemName = entry.getValue();
            log.info("Attempting to withdraw item: {} With ID: {}", itemName, itemId);
            Rs2Bank.withdrawItem(itemName);
            if (Rs2Inventory.contains(itemName)) {
                log.info("Withdrawal initiated for item: {} With ID: {}", itemName, itemId);
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
                state = State.NAVIGATING_TO_LOCATION;
            } else {
                log.warn("Some items are still missing: {}", clueHelper.getMissingItems(requiredItems));
                // Optionally, you can decide to retry withdrawal for missing items
            }
        });

        return false;
    }


    /**
     * Step 4: Navigate to the specified location.
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
            boolean navigationStarted = Rs2Walker.walkTo(location);
            if (navigationStarted) {
                log.info("Navigation to {} started.", location);
            } else {
                log.warn("Failed to initiate navigation to location: {}", location);
            }
            return false; // Wait for navigation to complete
        }

        log.info("Player has arrived at the clue location.");
        state = State.COMPLETED;
        return true;
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
}
