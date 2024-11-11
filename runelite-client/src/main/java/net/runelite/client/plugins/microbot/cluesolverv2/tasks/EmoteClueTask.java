package net.runelite.client.plugins.microbot.cluesolverv2.tasks;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.InteractingChanged;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.cluescrolls.clues.EmoteClue;
import net.runelite.client.plugins.cluescrolls.clues.item.ItemRequirement;
import net.runelite.client.plugins.microbot.cluesolverv2.ClueSolverScriptV2;
import net.runelite.client.plugins.microbot.cluesolverv2.taskinterface.ClueTask;
import net.runelite.client.plugins.microbot.cluesolverv2.util.ClueHelperV2;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import javax.inject.Inject;
import java.util.List;

@Slf4j
public class EmoteClueTask implements ClueTask {

    private final EmoteClue clue;
    private final ClueHelperV2 clueHelper;
    private final Client client;
    private final ClueSolverScriptV2 clueSolverScriptV2;
    private List<ItemRequirement> requiredItems;
    private NPC doubleAgent;
    private State state;

    @Inject
    private EventBus eventBus;

    private static final String DOUBLE_AGENT_NAME = "Double Agent";
    private static final int URI_ID = 7206; // NPC ID for Uri

    private enum State {
        RETRIEVING_ITEMS,
        NAVIGATING_TO_LOCATION,
        PERFORMING_EMOTES,
        WAITING_FOR_ENEMY_SPAWN,
        FIGHTING_ENEMY,
        COMPLETED
    }

    @Inject
    public EmoteClueTask(Client client, EmoteClue clue, ClueHelperV2 clueHelper, EventBus eventBus, ClueSolverScriptV2 clueSolverScriptV2) {
        this.client = client;
        this.clue = clue;
        this.clueHelper = clueHelper;
        this.state = State.RETRIEVING_ITEMS;  // Initial state is retrieving items
        this.eventBus = eventBus;
        this.clueSolverScriptV2 = clueSolverScriptV2;
    }

    @Override
    public void start() {
        log.info("Starting EmoteClueTask for {}", clue.getClass().getSimpleName());
        eventBus.register(this);
        this.requiredItems = clueHelper.determineRequiredItems(clue);
    }

    @Override
    public boolean execute() {
        if (state == State.COMPLETED) {
            return true;
        }

        switch (state) {
            case RETRIEVING_ITEMS:
                log.info("Retrieving required items from the bank.");
                if (retrieveRequiredItems()) {
                    state = State.NAVIGATING_TO_LOCATION;
                    log.info("All items retrieved. Moving to next state: NAVIGATING_TO_LOCATION");
                } else {
                    log.warn("Unable to retrieve all required items. Retrying...");
                    return false;
                }
                break;

            case NAVIGATING_TO_LOCATION:
                log.info("Navigating to location for clue.");
                if (navigateToLocation()) {
                    state = State.PERFORMING_EMOTES;
                    log.info("Arrived at location. Moving to next state: PERFORMING_EMOTES");
                }
                break;

            case PERFORMING_EMOTES:
                handlePerformingEmotes();
                break;

            case FIGHTING_ENEMY:
                attackDoubleAgent();
                break;

            case WAITING_FOR_ENEMY_SPAWN:
                log.info("Waiting for Double Agent to spawn...");
                break;
        }

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
        return "Performing Emote Clue Task: ";
    }

    private boolean retrieveRequiredItems() {
        List<ItemRequirement> missingItems = clueHelper.getMissingItems(requiredItems);
        log.info("Missing items (count: {}): {}", missingItems.size(), missingItems);

        if (missingItems.isEmpty()) {
            log.info("All required items are already present.");
            return true;
        }
        log.info("Missing items: {}", missingItems);

        List<String> missingItemNames = clueHelper.getItemNames(missingItems);
        log.info("Attempting to retrieve missing items from bank: {}", String.join(", ", missingItemNames));

        if (!Rs2Bank.openBank()) {
            log.warn("Failed to open bank. Retrying...");
            return false;
        }

       if (!Rs2Bank.walkToBankAndUseBank()) {
            log.warn("Failed to walk to bank and use bank. Retrying...");
            return false;
       }

        for (String itemName : missingItemNames) {
            log.info("Attempting to withdraw item: {}", itemName);

            // Try withdrawing the item from the bank
            Rs2Bank.withdrawItem(itemName);

            // Double-check if the item is in the inventory after withdrawal
            if (!Rs2Inventory.contains(itemName)) {
                log.warn("Failed to retrieve item: {}. Closing bank and retrying.", itemName);
                Rs2Bank.closeBank();
                return false; // Exit if unable to get the item
            }
            log.info("Item {} retrieved successfully.", itemName);
        }

        Rs2Bank.closeBank(); // Close bank after all items are retrieved
        log.info("All required items have been successfully retrieved from the bank.");
        return true;
    }


    private boolean navigateToLocation() {
        WorldPoint location = (WorldPoint) clueHelper.getFieldValue(clue, "location");
        if (location != null && !client.getLocalPlayer().getWorldLocation().equals(location)) {
            Rs2Walker.walkTo(location);
            return false;
        }
        return true;
    }

    private void handlePerformingEmotes() {
        performEmote(clue.getFirstEmote().getName());
        if (clue.getEnemy() != null) {
            state = State.WAITING_FOR_ENEMY_SPAWN;
            log.info("Waiting for Double Agent to spawn.");
        } else {
            interactWithUri();
            state = State.COMPLETED;
        }
    }

    private void performEmote(String emoteName) {
        log.info("Performing emote: {}", emoteName);
        // Assume Rs2Tab.switchToEmotesTab() and widget clicking are handled separately
    }

    private void interactWithUri() {
        if (Rs2Npc.interact(URI_ID, "Talk-to")) {
            log.info("Interacted with Uri.");
        } else {
            log.warn("Failed to interact with Uri.");
        }
    }

    private void attackDoubleAgent() {
        if (doubleAgent != null) {
            if (Rs2Npc.interact(doubleAgent, "Attack")) {
                log.info("Attacking Double Agent.");
            } else {
                log.warn("Failed to attack Double Agent.");
            }
        }
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned event) {
        NPC npc = event.getNpc();
        if (npc.getName() != null && state == State.WAITING_FOR_ENEMY_SPAWN && npc.getName().equalsIgnoreCase(DOUBLE_AGENT_NAME)) {
            doubleAgent = npc;
            log.info("Double Agent spawned.");
            state = State.FIGHTING_ENEMY;
        }
    }

    @Subscribe
    public void onInteractingChanged(InteractingChanged event) {
        if (state == State.FIGHTING_ENEMY && event.getSource() == client.getLocalPlayer() && event.getTarget() == null) {
            if (doubleAgent == null || Rs2Npc.getHealth(doubleAgent) <= 0) {
                log.info("Double Agent defeated.");
                state = State.PERFORMING_EMOTES;
            }
        }
    }
}
