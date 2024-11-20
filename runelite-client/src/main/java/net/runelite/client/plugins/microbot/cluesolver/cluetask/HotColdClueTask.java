package net.runelite.client.plugins.microbot.cluesolver.cluetask;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.cluescrolls.ClueScrollPlugin;
import net.runelite.client.plugins.cluescrolls.clues.HotColdClue;
import net.runelite.client.plugins.microbot.cluesolver.ClueSolverPlugin;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import java.util.concurrent.ExecutorService;

@Slf4j
public class HotColdClueTask extends ClueTask {
    private final HotColdClue clue;
    private final EventBus eventBus;
    private final ExecutorService backgroundExecutor;

    private enum State {
        NAVIGATING_TO_LOCATION,
        SEARCHING_AREA,
        DIGGING,
        COMPLETED
    }

    private State state = State.NAVIGATING_TO_LOCATION;

    public HotColdClueTask(Client client, HotColdClue clue, ClueScrollPlugin clueScrollPlugin,
                           ClueSolverPlugin clueSolverPlugin, EventBus eventBus, ExecutorService backgroundExecutor) {
        super(client, clueScrollPlugin, clueSolverPlugin);
        this.clue = clue;
        this.eventBus = eventBus;
        this.backgroundExecutor = backgroundExecutor;
    }

    @Override
    public void run() {
        eventBus.register(this);
        log.info("Starting HotColdClueTask");
        backgroundExecutor.submit(this);
    }

    @Override
    protected boolean executeTask() throws Exception {
        log.info("Executing Hot Cold clue task.");
        navigateToGeneralLocation();
        return true;
    }

    private void navigateToGeneralLocation() {
        WorldPoint startingLocation = clue.getLocation(clueScrollPlugin);
        if (startingLocation == null) {
            log.error("Starting location for Hot Cold clue is null.");
            completeTask(false);
            return;
        }

        log.info("Navigating to general area for Hot Cold clue: {}", startingLocation);
        backgroundExecutor.submit(() -> {
            boolean startedWalking = Rs2Walker.walkTo(startingLocation, 5);
            if (!startedWalking) {
                log.error("Failed to initiate walking to starting location: {}", startingLocation);
                completeTask(false);
            }
        });
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        Player player = client.getLocalPlayer();
        if (player == null) return;

        switch (state) {
            case NAVIGATING_TO_LOCATION:
                if (isPlayerNearStartingLocation()) {
                    log.info("Player is near the starting location.");
                    state = State.SEARCHING_AREA;
                    searchAreaForClue();
                }
                break;

            case SEARCHING_AREA:
                interpretHotColdFeedback();
                break;

            case DIGGING:
                performDig();
                break;

            case COMPLETED:
                log.info("Hot Cold clue task completed.");
                completeTask(true);
                break;
        }
    }

    private boolean isPlayerNearStartingLocation() {
        return clue.getLocation().distanceTo(client.getLocalPlayer().getWorldLocation()) < 5;
    }

    private void searchAreaForClue() {
        log.info("Searching area for Hot Cold clue.");
        // Logic for initial direction based on Hot/Cold feedback.
    }

    private void interpretHotColdFeedback() {
        // Interpret Hot/Cold feedback from the clue and adjust position accordingly.
        log.info("Interpreting Hot/Cold feedback...");
        // Placeholder for actual Hot/Cold clue feedback handling.
        if (isClueFound()) {
            state = State.DIGGING;
        }
    }

    private boolean isClueFound() {
        // Logic to confirm if player has reached the final location (based on "Boiling hot" feedback or similar).
        return true;  // Placeholder for actual final location check.
    }

    private void performDig() {
        log.info("Digging for Hot Cold clue.");
        // Use dig action to complete the clue.
        if (true) {  // Placeholder for successful dig logic.
            state = State.COMPLETED;
            completeTask(true);
        } else {
            log.warn("Failed to dig at location.");
            completeTask(false);
        }
    }

    @Override
    protected void completeTask(boolean success) {
        super.completeTask(success);
        eventBus.unregister(this);
        log.info("Hot Cold clue task completed with status: {}", success ? "Success" : "Failure");
    }
}
