package net.runelite.client.plugins.microbot.cluesolver.cluetask;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.cluescrolls.ClueScrollPlugin;
import net.runelite.client.plugins.cluescrolls.clues.FaloTheBardClue;
import net.runelite.client.plugins.microbot.cluesolver.ClueSolverPlugin;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import java.util.concurrent.ExecutorService;

@Slf4j
public class FaloTheBardClueTask extends ClueTask {
    private final FaloTheBardClue clue;
    private final EventBus eventBus;
    private final ExecutorService backgroundExecutor;

    private enum State {
        WALKING_TO_LOCATION,
        INTERACTING_WITH_NPC,
        CONFIRMING_CLUE_COMPLETION,
        COMPLETED
    }

    private State state = State.WALKING_TO_LOCATION;

    public FaloTheBardClueTask(Client client, FaloTheBardClue clue, ClueScrollPlugin clueScrollPlugin,
                               ClueSolverPlugin clueSolverPlugin, EventBus eventBus, ExecutorService backgroundExecutor) {
        super(client, clueScrollPlugin, clueSolverPlugin);
        this.clue = clue;
        this.eventBus = eventBus;
        this.backgroundExecutor = backgroundExecutor;
    }


    @Override
    protected boolean executeTask() {
        log.info("Executing Falo the Bard clue task.");
        if (navigateToLocation()) {
            return true;
        } else {
            completeTask(false);
            return false;
        }
    }

    private boolean navigateToLocation() {
        WorldPoint location = client.getLocalPlayer().getWorldLocation(); // Placeholder for actual location retrieval logic
        if (location == null) {
            log.error("Location for Falo the Bard clue is null.");
            return false;
        }

        log.info("Navigating to Falo the Bard's location: {}", location);
        return Rs2Walker.walkTo(location, 1);
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        Player player = client.getLocalPlayer();
        if (player == null) return;

        switch (state) {
            case WALKING_TO_LOCATION:
                if (isPlayerAtLocation()) {
                    log.info("Player has arrived at Falo the Bard's location.");
                    state = State.INTERACTING_WITH_NPC;
                    interactWithNpc();
                }
                break;

            case INTERACTING_WITH_NPC:
                if (interactWithNpc()) {
                    state = State.CONFIRMING_CLUE_COMPLETION;
                } else {
                    log.warn("Failed to interact with Falo the Bard.");
                    completeTask(false);
                }
                break;

            case CONFIRMING_CLUE_COMPLETION:
                if (confirmClueCompletion()) {
                    state = State.COMPLETED;
                    completeTask(true);
                } else {
                    log.warn("Failed to confirm clue completion with Falo the Bard.");
                    completeTask(false);
                }
                break;

            case COMPLETED:
                log.info("Falo the Bard clue task completed.");
                completeTask(true);
                break;

            default:
                break;
        }
    }

    private boolean isPlayerAtLocation() {
        return false; // Placeholder for actual location check logic
    }

    private boolean interactWithNpc() {
        log.info("Interacting with Falo the Bard NPC.");
        return Rs2Npc.interact("Falo the Bard", "Talk-to");
    }

    private boolean confirmClueCompletion() {
        log.info("Confirming clue completion with Falo the Bard.");
        return true; // Placeholder for actual confirmation logic
    }

    @Override
    protected void completeTask(boolean success) {
        super.completeTask(success);
        eventBus.unregister(this);
        log.info("Falo the Bard clue task completed with status: {}", success ? "Success" : "Failure");
    }
}
