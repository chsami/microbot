package net.runelite.client.plugins.microbot.cluesolver.cluetask;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.cluescrolls.ClueScrollPlugin;
import net.runelite.client.plugins.cluescrolls.clues.FairyRingClue;
import net.runelite.client.plugins.microbot.cluesolver.ClueSolverPlugin;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import java.util.concurrent.ExecutorService;

@Slf4j
public class FairyRingClueTask extends ClueTask {
    private final FairyRingClue clue;
    private final EventBus eventBus;
    private final ExecutorService backgroundExecutor;

    private enum State {
        NAVIGATING_TO_FAIRY_RING,
        USING_FAIRY_RING,
        COMPLETED
    }

    private State state = State.NAVIGATING_TO_FAIRY_RING;

    public FairyRingClueTask(Client client, FairyRingClue clue, ClueScrollPlugin clueScrollPlugin,
                             ClueSolverPlugin clueSolverPlugin, EventBus eventBus, ExecutorService backgroundExecutor) {
        super(client, clueScrollPlugin, clueSolverPlugin);
        this.clue = clue;
        this.eventBus = eventBus;
        this.backgroundExecutor = backgroundExecutor;
    }

    @Override
    protected boolean executeTask() {
        log.info("Executing Fairy Ring clue task.");
        navigateToFairyRing();
        return true;
    }

    private void navigateToFairyRing() {
        WorldPoint location = clue.getLocation(clueScrollPlugin);
        if (location == null) {
            log.error("Location for Fairy Ring clue is null.");
            completeTask(false);
            return;
        }

        log.info("Navigating to Fairy Ring location: {}", location);
        backgroundExecutor.submit(() -> {
            boolean startedWalking = Rs2Walker.walkTo(location, 1);
            if (!startedWalking) {
                log.error("Failed to initiate walking to location: {}", location);
                completeTask(false);
            }
        });
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        Player player = client.getLocalPlayer();
        if (player == null) return;

        switch (state) {
            case NAVIGATING_TO_FAIRY_RING:
                if (isPlayerAtLocation()) {
                    log.info("Player has arrived at the Fairy Ring location.");
                    state = State.USING_FAIRY_RING;
                    useFairyRing();
                }
                break;

            case USING_FAIRY_RING:
                if (useFairyRing()) {
                    state = State.COMPLETED;
                    completeTask(true);
                } else {
                    log.warn("Failed to use Fairy Ring.");
                    completeTask(false);
                }
                break;

            case COMPLETED:
                log.info("Fairy Ring clue task completed.");
                completeTask(true);
                break;

            default:
                break;
        }
    }

    private boolean isPlayerAtLocation() {
        return clue.getLocation(clueScrollPlugin).distanceTo(client.getLocalPlayer().getWorldLocation()) < 3;
    }

    private boolean useFairyRing() {
        // Placeholder for logic to interact with Fairy Ring, e.g., selecting codes to teleport.
        log.info("Using the Fairy Ring with designated codes.");
        // Here you would use the actual method to input codes and confirm interaction with the Fairy Ring
        return true;
    }

    @Override
    protected void completeTask(boolean success) {
        super.completeTask(success);
        eventBus.unregister(this);
        log.info("Fairy Ring clue task completed with status: {}", success ? "Success" : "Failure");
    }
}
