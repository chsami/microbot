package net.runelite.client.plugins.microbot.cluesolver.cluetask;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.cluescrolls.ClueScrollPlugin;
import net.runelite.client.plugins.cluescrolls.clues.ThreeStepCrypticClue;
import net.runelite.client.plugins.microbot.cluesolver.ClueSolverPlugin;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import java.util.concurrent.ExecutorService;

@Slf4j
public class ThreeStepCrypticClueTask extends ClueTask {
    private final ThreeStepCrypticClue clue;
    private final EventBus eventBus;
    private final ExecutorService backgroundExecutor;

    private enum State {
        STEP_ONE,
        STEP_TWO,
        STEP_THREE,
        COMPLETED
    }

    private State state = State.STEP_ONE;

    public ThreeStepCrypticClueTask(Client client, ThreeStepCrypticClue clue, ClueScrollPlugin clueScrollPlugin,
                                    ClueSolverPlugin clueSolverPlugin, EventBus eventBus, ExecutorService backgroundExecutor) {
        super(client, clueScrollPlugin, clueSolverPlugin);
        this.clue = clue;
        this.eventBus = eventBus;
        this.backgroundExecutor = backgroundExecutor;
    }

    @Override
    protected boolean executeTask() throws Exception {
        eventBus.register(this);
        log.info("Executing ThreeStepCrypticClueTask");
        navigateToStepLocation();
        return true;
    }

    private void navigateToStepLocation() {
        WorldPoint location = getLocationForCurrentStep();
        if (location == null) {
            log.error("Location for current step is null.");
            completeTask(false);
            return;
        }

        log.info("Navigating to location for {}: {}", state, location);
        backgroundExecutor.submit(() -> {
            boolean startedWalking = Rs2Walker.walkTo(location, 1);
            if (!startedWalking) {
                log.error("Failed to initiate walking to location: {}", location);
                completeTask(false);
            }
        });
    }

    private WorldPoint getLocationForCurrentStep() {
        switch (state) {
            case STEP_ONE:
                return clue.getClueSteps().get(0).getKey().getLocation(clueScrollPlugin);
            case STEP_TWO:
                return clue.getClueSteps().get(1).getKey().getLocation(clueScrollPlugin);
            case STEP_THREE:
                return clue.getClueSteps().get(2).getKey().getLocation(clueScrollPlugin);
            default:
                return null;
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        Player player = client.getLocalPlayer();
        if (player == null) return;

        WorldPoint playerLocation = player.getWorldLocation();
        WorldPoint targetLocation = getLocationForCurrentStep();

        if (targetLocation != null && playerLocation.distanceTo(targetLocation) < 3) {
            log.info("Player arrived at the location for {}.", state);
            processStep();
        }
    }

    private void processStep() {
        switch (state) {
            case STEP_ONE:
                log.info("Processing step one of the cryptic clue.");
                state = State.STEP_TWO;
                navigateToStepLocation();
                break;

            case STEP_TWO:
                log.info("Processing step two of the cryptic clue.");
                state = State.STEP_THREE;
                navigateToStepLocation();
                break;

            case STEP_THREE:
                log.info("Processing step three of the cryptic clue.");
                state = State.COMPLETED;
                completeTask(true);
                break;

            case COMPLETED:
                log.info("Three-step cryptic clue task completed.");
                completeTask(true);
                break;
        }
    }

    @Override
    protected void completeTask(boolean success) {
        super.completeTask(success);
        eventBus.unregister(this);
        log.info("Three-step cryptic clue task completed with status: {}", success ? "Success" : "Failure");
    }
}
