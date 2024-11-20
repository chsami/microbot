package net.runelite.client.plugins.microbot.cluesolverv2.tasks;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.cluescrolls.clues.EmoteClue;
import net.runelite.client.plugins.microbot.cluescrolls.clues.emote.Emote;
import net.runelite.client.plugins.microbot.cluescrolls.clues.item.ItemRequirement;
import net.runelite.client.plugins.microbot.cluesolverv2.taskinterface.ClueTask;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

import static net.runelite.client.plugins.microbot.util.Global.sleep;

@Slf4j
public class EmoteClueTask implements ClueTask {
    private static final int MAX_RETRIES = 5;

    @Inject
    private Client client;

    @Inject
    private EventBus eventBus;

    private EmoteClue clue;
    private State state;

    private List<String> itemsToWithdraw = new ArrayList<>();
    private List<String> itemsToEquip = new ArrayList<>();
    private Map<String, ItemRequirement> itemRequirementMap = new HashMap<>(); // NEW: Map to store item requirements
    private int retryCounter = 0;
    private WorldPoint clueLocation;

    private enum State {
        CHECKING_ITEMS,
        WALKING_TO_BANK,
        RETRIEVING_ITEMS,
        VERIFYING_WITHDRAWAL,
        VERIFYING_EQUIPMENT,
        EQUIPPING_ITEMS,
        NAVIGATING_TO_LOCATION,
        PERFORMING_EMOTES,
        INTERACTING_WITH_NPC,
        COMPLETED,
        FAILED
    }

    /**
     * Sets the EmoteClue for this task and populates the item requirement map.
     *
     * @param clue The EmoteClue associated with this task.
     */
    public void setClue(EmoteClue clue) {
        this.clue = clue;
        log.info("Clue set for EmoteClueTask: {}", clue.getText());
        populateItemRequirementMap();
    }

    /**
     * Populates the itemRequirementMap with items from the clue.
     */
    private void populateItemRequirementMap() {
        Microbot.getClientThread().invoke(() -> {
            clueLocation = clue.getLocation();
            itemRequirementMap.clear();
            if (clue != null && clue.getItemRequirements() != null) {
                for (ItemRequirement requirement : clue.getItemRequirements()) {
                    String itemName = requirement.getCollectiveName(client);
                    itemRequirementMap.put(itemName, requirement);
                }
                log.info("Populated itemRequirementMap with {} items.", itemRequirementMap.size());
            }
        });
    }

    @Override
    public void start() {
        if (clue == null) {
            log.error("Clue is null. Cannot start task.");
            setState(State.FAILED);
            return;
        }
        log.info("Starting EmoteClueTask for clue: {}", clue.getText());
        eventBus.register(this);
        setState(State.CHECKING_ITEMS);
    }

    /**
     * Synchronized method to get the current state of the task.
     *
     * @return The current state.
     */
    private synchronized State getState() {
        return this.state;
    }

    @Override
    public boolean execute() {
        log.debug("Executing EmoteClueTask in state: {}", state);

        switch (state) {
            case CHECKING_ITEMS:
                return checkMissingItems();

            case RETRIEVING_ITEMS:
                retrieveRequiredItems();
                return false;

            case VERIFYING_WITHDRAWAL:
                verifyWithdrawals();
                return false; // Continue processing after verification

            case EQUIPPING_ITEMS:
                return equipRequiredItems();

            case VERIFYING_EQUIPMENT: // New case for verifying equipment
                verifyEquipment();
                return false;

            case NAVIGATING_TO_LOCATION:
                return navigateToLocation();

            case PERFORMING_EMOTES:
                return performEmotes();

            case INTERACTING_WITH_NPC:
                return interactWithNPC();

            case COMPLETED:
                log.info("EmoteClueTask completed successfully.");
                stop();
                return true;

            case FAILED:
                log.error("EmoteClueTask failed.");
                stop();
                return true;

            case WALKING_TO_BANK:
                log.debug("Walking to bank. Awaiting arrival.");
                return false;

            default:
                log.error("Unknown state: {}", state);
                setState(State.FAILED);
                return true;
        }
    }

    private boolean performEmotes() {
        log.info("Performing required emotes...");

        Emote firstEmote = clue.getFirstEmote();
        if (clue.getFirstEmote() != null && !performEmote(firstEmote)) {
            log.warn("Failed to perform first emote.");
            return false;
        }


        Emote secondEmote = clue.getSecondEmote();
        if (clue.getSecondEmote() != null && !performEmote(secondEmote)) {
            log.warn("Failed to perform second emote.");
            return false;
        }

        log.info("Emotes performed successfully. Interacting with NPC...");
        setState(State.INTERACTING_WITH_NPC);
        return false;
    }

    private boolean performEmote(Emote emote) {
        log.info("Performing emote: {}", emote.getName());
        boolean tabSwitched = Rs2Tab.switchToEmotesTab();
        boolean widgetClicked = Rs2Widget.clickWidget(emote.getName());
        sleep(500); // Wait for the emote to perform
        return tabSwitched && widgetClicked;
    }


    private boolean navigateToLocation() {

        if (clueLocation == null) {
            log.error("Clue location is null. Cannot navigate.");
            setState(State.FAILED);
            return true;
        }

        log.info("Navigating to clue location: {}", clueLocation);
        if (!Rs2Walker.walkTo(clueLocation)) {
            log.warn("Failed to navigate to clue location.");
            return false;
        }

        log.info("Arrived at clue location. Performing emotes...");
        setState(State.PERFORMING_EMOTES);
        return false;
    }

    private boolean equipRequiredItems() {
        log.info("Starting to equip required items...");
        itemsToEquip.clear(); // Reset the list

        boolean allItemsEquipped = true;

        for (String itemName : itemRequirementMap.keySet()) {
            if (Rs2Inventory.contains(itemName) && !Rs2Equipment.isWearing(itemName)) {
                itemsToEquip.add(itemName);
            }
        }

        for (String itemName : itemsToEquip) {
            log.info("Equipping item: {}", itemName);

            try {
                Rs2Inventory.equip(itemName);

                // Use dynamic wait to ensure the item is equipped
                boolean equipped = waitForCondition(() -> Rs2Equipment.isWearing(itemName), 5000, 500);
                if (!equipped) {
                    log.error("Failed to equip item: {}", itemName);
                    allItemsEquipped = false;
                } else {
                    log.info("Successfully equipped item: {}", itemName);
                }
            } catch (Exception e) {
                log.error("Exception occurred while equipping item: {}", itemName, e);
                allItemsEquipped = false;
            }
        }

        if (allItemsEquipped) {
            Rs2Bank.closeBank(); // Close the bank if it's open
            log.info("All items equipped successfully. Proceeding to navigate to clue location.");
            setState(State.NAVIGATING_TO_LOCATION);
        } else {
            log.warn("Some items failed to equip. Retrying...");
            if (++retryCounter > MAX_RETRIES) {
                log.error("Exceeded maximum retries for equipping items.");
                setState(State.FAILED);
            } else {
                setState(State.EQUIPPING_ITEMS); // Retry equipping failed items
            }
        }
        return false;
    }


    @Override
    public void stop() {
        log.info("Stopping EmoteClueTask.");
        setState(State.FAILED);
        eventBus.unregister(this);
    }

    @Override
    public String getTaskDescription() {
        return "Performing Emote Clue Task";
    }

    /**
     * Verifies that all required items are equipped using Rs2 methods.
     * Transitions to the next state or marks the task as failed.
     */
    private void verifyEquipment() {
        log.info("Verifying equipment...");

        boolean allEquipped = true;

        for (String itemName : itemRequirementMap.keySet()) {
            if (!Rs2Equipment.hasEquippedContains(itemName)) {
                log.warn("Item not equipped: {}", itemName);

                // Attempt to equip the item if it's in the inventory
                if (Rs2Inventory.contains(itemName)) {
                    log.info("Equipping missing item: {}", itemName);
                    Rs2Inventory.equip(itemName);

                    // Use dynamic wait
                    boolean equipped = waitForCondition(() -> Rs2Equipment.hasEquippedContains(itemName), 5000, 600);
                    if (!equipped) {
                        log.error("Failed to equip item: {}", itemName);
                        allEquipped = false;
                        break;
                    }
                } else {
                    log.error("Required item not found in inventory: {}", itemName);
                    allEquipped = false;
                    break;
                }
            }
        }

        if (allEquipped) {
            log.info("All items successfully equipped. Proceeding to navigate to clue location.");
            setState(State.NAVIGATING_TO_LOCATION);
            retryCounter = 0; // Reset retry counter
        } else {
            log.warn("Failed to equip all required items.");
            setState(State.FAILED);
        }
    }


    /**
     * Verifies that all required items have been withdrawn from the bank.
     */
    private void verifyWithdrawals() {
        log.info("Verifying item withdrawals...");

        boolean allPresent = true;

        for (String itemName : itemRequirementMap.keySet()) {
            if (!Rs2Inventory.contains(itemName)) {
                log.warn("Item still missing: {}", itemName);
                allPresent = false;
            }
        }

        if (allPresent) {
            log.info("All items verified in inventory. Proceeding to equip items.");
            setState(State.EQUIPPING_ITEMS);
            retryCounter = 0; // Reset retry counter
        } else {
            log.warn("Some items are still missing after verification.");
            if (++retryCounter > MAX_RETRIES) {
                log.error("Exceeded maximum retries for verifying withdrawals.");
                setState(State.FAILED);
            } else {
                log.warn("Retrying item withdrawal...");
                setState(State.RETRIEVING_ITEMS); // Retry retrieval
            }
        }
    }


    /**
     * Event handler for item container changes.
     * Reacts to inventory or equipment changes to verify withdrawals or equipment.
     *
     * @param event The item container changed event.
     */
    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event) {
        log.info("ItemContainerChanged event received for container ID: {}", event.getContainerId());

        if (event.getContainerId() == InventoryID.INVENTORY.getId()) {
            log.debug("Inventory change detected. Current state: {}", state);
            if (getState() == State.VERIFYING_WITHDRAWAL) {
                verifyWithdrawals();
            }
        } else if (event.getContainerId() == InventoryID.EQUIPMENT.getId()) {
            log.debug("Equipment change detected. Current state: {}", state);
            if (getState() == State.EQUIPPING_ITEMS) {
                verifyEquipment();
            }
        }
    }


    private boolean interactWithNPC() {
        log.info("Interacting with NPC...");
        NPC npc = Rs2Npc.getNpc("Uri");
        if (npc == null) {
            log.warn("NPC not found. Waiting...");
            return false;
        }

        if (!Rs2Npc.interact(npc, "Talk-to")) {
            log.warn("Failed to interact with NPC.");
            return false;
        }

        log.info("NPC interaction successful. Task completed.");
        setState(State.COMPLETED);
        return true;
    }


    /**
     * Synchronized method to update the current state of the task.
     * Logs the transition and ensures valid state changes.
     *
     * @param newState The new state to transition to.
     */
    private synchronized void setState(State newState) {
        if (this.state != newState) {
            log.info("State transition: {} -> {}", this.state, newState);
        } else {
            log.debug("State remains unchanged: {}", this.state);
        }
        this.state = newState;
    }


    /**
     * Checks for missing items using Rs2 methods and updates itemsToWithdraw.
     * Transitions the state based on the findings.
     *
     * @return true if all items are present, false otherwise.
     */
    private boolean checkMissingItems() {
        log.info("Checking for missing items...");
        itemsToWithdraw.clear(); // Reset the list

        boolean allItemsPresent = true;

        for (String itemName : itemRequirementMap.keySet()) {
            if (!Rs2Inventory.contains(itemName)) {
                log.warn("Missing item: {}", itemName);
                itemsToWithdraw.add(itemName);
                allItemsPresent = false;
            }
        }

        if (allItemsPresent) {
            log.info("All required items are present. Proceeding to equip items.");
            setState(State.EQUIPPING_ITEMS);
        } else {
            log.warn("Missing items detected. Proceeding to retrieve from bank...");
            setState(State.RETRIEVING_ITEMS);
        }

        return allItemsPresent;
    }


    /**
     * Retrieves required items from the bank using itemsToWithdraw.
     */
    private void retrieveRequiredItems() {
        log.info("Retrieving required items from bank...");

        boolean success = Rs2Bank.walkToBankAndUseBank();

        if (!success) {
            log.warn("Failed to walk to bank. Retrying...");
            return;
        }


        if (!Rs2Bank.openBank()) {
            log.warn("Failed to open bank. Retrying...");
            return;
        }

        // Wait until the bank is open
        boolean bankOpened = waitForCondition(Rs2Bank::isOpen, 5000, 500);

        if (!bankOpened) {
            log.warn("Bank did not open within the expected time. Retrying...");
            return;
        }

        // Log all available bank items for verification
        List<Rs2Item> bankItems = Rs2Bank.bankItems();
        log.info("Available items in bank: {}", bankItems);

        boolean allWithdrawn = true;

        for (String itemName : itemsToWithdraw) {
            log.info("Attempting to withdraw item: {}", itemName);

            if (Rs2Inventory.contains(itemName)) {
                log.info("Item already in inventory: {}", itemName);
                continue;
            }

            if (!Rs2Bank.hasBankItem(itemName)) {
                log.error("Item not found in bank: {}", itemName);
                allWithdrawn = false;
                continue; // Proceed to the next item
            } else {
                Rs2Bank.withdrawItem(itemName);
                log.info("Initiated withdrawal for item: {}", itemName);

                boolean itemWithdrawn = waitForCondition(() -> Rs2Inventory.contains(itemName), 5000, 500);
                if (!itemWithdrawn) {
                    log.warn("Failed to withdraw item: {}", itemName);
                    allWithdrawn = false;
                } else {
                    log.info("Successfully withdrew item: {}", itemName);
                }
            }
        }

        if (allWithdrawn) {
            log.info("All required items successfully withdrawn. Proceeding to verify withdrawals.");
            setState(State.VERIFYING_WITHDRAWAL);
        } else {
            log.warn("Some items are still missing. Retrying withdrawal...");
            if (++retryCounter > MAX_RETRIES) {
                log.error("Exceeded maximum retries for withdrawing items.");
                setState(State.FAILED);
            } else {
                setState(State.RETRIEVING_ITEMS); // Retry withdrawal
            }
        }
    }

    /**
     * Waits until the specified condition is true or the timeout is reached.
     *
     * @param condition       The condition to evaluate.
     * @param timeoutMs       The maximum time to wait in milliseconds.
     * @param checkIntervalMs The interval between condition checks in milliseconds.
     * @return true if the condition was met within the timeout, false otherwise.
     */
    private boolean waitForCondition(BooleanSupplier condition, long timeoutMs, long checkIntervalMs) {
        long waited = 0;
        while (waited < timeoutMs) {
            if (condition.getAsBoolean()) {
                return true;
            }
            sleep(Math.toIntExact(checkIntervalMs));
            waited += checkIntervalMs;
        }
        return false;
    }
}
