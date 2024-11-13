package net.runelite.client.plugins.microbot.cluesolverv2.tasks;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.plugins.cluescrolls.clues.CoordinateClue;
import net.runelite.client.plugins.cluescrolls.clues.item.ItemRequirement;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.cluesolverv2.taskinterface.ClueTask;
import net.runelite.client.plugins.microbot.cluesolverv2.util.ClueHelperV2;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static net.runelite.client.plugins.microbot.util.Global.sleep;

@Slf4j
public class CoordinateClueTask implements ClueTask {
    private enum State {
        CHECKING_ITEMS,
        RETRIEVING_ITEMS,
        VERIFYING_WITHDRAWAL,
        EQUIPPING_ITEMS,
        NAVIGATING_TO_LOCATION,
        COMPLETED,
        DIGGING,
        INTERACTING_WITH_NPC,
        FAILED
    }

    @Inject
    private Client client;

    @Inject
    private ClueHelperV2 clueHelper;


    private CoordinateClue clue;

    @Inject
    private EventBus eventBus;

    private List<ItemRequirement> requiredItems;

    List<ItemRequirement> missingItems;


    private Map<Integer, String> requiredItemsMap;


    private State state;

    public CoordinateClueTask() {
        this.state = State.CHECKING_ITEMS;
    }

    public void setClue(CoordinateClue clue) {
        this.clue = clue;
        this.requiredItems = clueHelper.determineRequiredItems(clue);
        this.requiredItemsMap = clueHelper.getRequiredItemsMap();

        log.info("Coordinate Clue task initialized for clue: {}", clue.getClass().getSimpleName());
        log.info("Number of required items: {}", requiredItems.size());
    }

    @Override
    public void start() {

        if (clue == null) {
            log.error("Coordinate Clue task instance is null. Cannot start task.");
            state = State.FAILED;
            return;
        }
        log.info("Starting Coordinate Clue task for {}", clue.getClass().getSimpleName());
        eventBus.register(this);
    }


    @Override
    public boolean execute() {
        log.debug("Executing Coordinate Clue task in state: {}", state);
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

            case DIGGING:
                return handleDigging();

            case COMPLETED:
                log.info("Coordinate Clue task completed.");
                eventBus.unregister(this);
                return true;

            case FAILED:
                log.error("Coordinate Clue task failed.");
                eventBus.unregister(this);
                return true;

            default:
                log.error("Unknown state encountered: {}", state);
                state = State.FAILED;
                eventBus.unregister(this);
                return true;
        }
    }

    private boolean handleDigging() {
        //TODO Implement digging
        log.info("Digging not implemented yet. Completing task.");
        state = State.COMPLETED;
        return true;
    }

    @Override
    public void stop() {
        log.info("Stopping Coordinate Clue task.");
        state = State.FAILED;
        eventBus.unregister(this);
    }

    private boolean checkAndHandleMissingItems() {
        log.info("Checking for missing items");
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
        log.info("Equipping items...");
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
        state = State.DIGGING;
        return false;

    }



    @Override
    public String getTaskDescription() {
        return "";
    }
}
