package net.runelite.client.plugins.microbot.cluesolver.cluetask;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.cluescrolls.ClueScrollPlugin;
import net.runelite.client.plugins.cluescrolls.clues.SkillChallengeClue;
import net.runelite.client.plugins.microbot.cluesolver.ClueSolverPlugin;

import java.util.concurrent.ExecutorService;

@Slf4j
public class SkillChallengeClueTask extends ClueTask {
    private final SkillChallengeClue clue;
    private final EventBus eventBus;
    private final ExecutorService backgroundExecutor;

    private enum State {
        NAVIGATING_TO_LOCATION,
        PERFORMING_SKILL_TASK,
        CONFIRMING_COMPLETION,
        COMPLETED
    }

    private State state = State.NAVIGATING_TO_LOCATION;

    public SkillChallengeClueTask(Client client, SkillChallengeClue clue, ClueScrollPlugin clueScrollPlugin,
                                  ClueSolverPlugin clueSolverPlugin, EventBus eventBus, ExecutorService backgroundExecutor) {
        super(client, clueScrollPlugin, clueSolverPlugin);
        this.clue = clue;
        this.eventBus = eventBus;
        this.backgroundExecutor = backgroundExecutor;
    }

    @Override
    protected boolean executeTask() throws Exception {
        eventBus.register(this);
        log.info("Executing SkillChallengeClueTask - placeholder implementation");
        //navigateToLocation();
        return true;
    }

//    private void navigateToLocation() {
//        if (clue.getLocation(clueScrollPlugin) == null) {
//            log.error("Skill challenge clue location is null.");
//            completeTask(false);
//            return;
//        }
//
//        log.info("Navigating to skill challenge location: {}", clue.getLocation(clueScrollPlugin));
//        backgroundExecutor.submit(() -> {
//            boolean startedWalking = Rs2Walker.walkTo(clue.getLocation(clueScrollPlugin), 1);
//            if (!startedWalking) {
//                log.error("Failed to start walking to location: {}", clue.getLocation(clueScrollPlugin));
//                completeTask(false);
//            }
//        });
//    }

    @Subscribe
    public void onGameTick(GameTick event) {
        Player player = client.getLocalPlayer();
        if (player == null) return;

        switch (state) {
            case NAVIGATING_TO_LOCATION:
                if (isPlayerAtLocation()) {
                    log.info("Player has arrived at the skill challenge location.");
                    state = State.PERFORMING_SKILL_TASK;
                    performSkillTask();
                }
                break;

            case PERFORMING_SKILL_TASK:
                if (isSkillTaskInProgress()) {
                    log.info("Skill task is in progress.");
                } else {
                    state = State.CONFIRMING_COMPLETION;
                }
                break;

            case CONFIRMING_COMPLETION:
                if (confirmSkillTaskCompletion()) {
                    state = State.COMPLETED;
                    completeTask(true);
                } else {
                    log.warn("Failed to confirm skill task completion.");
                    completeTask(false);
                }
                break;

            case COMPLETED:
                log.info("Skill challenge clue task completed.");
                completeTask(true);
                break;
        }
    }

    private boolean isPlayerAtLocation() {
        return false;
    }

    private void performSkillTask() {
        log.info("Placeholder: Performing skill-related action for the skill challenge.");
        // Placeholder for actual skill performance logic
    }

    private boolean isSkillTaskInProgress() {
        // Placeholder for logic to detect if the player is currently performing the skill task
        return false;
    }

    private boolean confirmSkillTaskCompletion() {
        log.info("Placeholder: Confirming skill task completion.");
        // Placeholder for logic to confirm skill task completion
        return true;
    }
}
