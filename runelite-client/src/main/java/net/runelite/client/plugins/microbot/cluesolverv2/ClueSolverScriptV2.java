package net.runelite.client.plugins.microbot.cluesolverv2;

import com.google.inject.Inject;
import com.google.inject.Provider;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.plugins.cluescrolls.ClueScrollPlugin;
import net.runelite.client.plugins.cluescrolls.clues.ClueScroll;
import net.runelite.client.plugins.cluescrolls.clues.EmoteClue;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.cluesolverv2.taskinterface.ClueTask;
import net.runelite.client.plugins.microbot.cluesolverv2.tasks.EmoteClueTask;
import net.runelite.client.plugins.microbot.cluesolverv2.util.ClueHelperV2;
import net.runelite.client.plugins.microbot.util.npc.Rs2NpcManager;

import java.util.concurrent.TimeUnit;

@Slf4j
public class ClueSolverScriptV2 extends Script {

    private ClueSolverConfig config;
    private ClueTask currentTask;
    private boolean isRunning = false;

    @Inject
    private Client client;

    @Inject
    private ClueScrollPlugin clueScrollPlugin;

    @Inject
    private ClueHelperV2 clueHelper;

    @Inject
    private EventBus eventBus;

    @Inject
    private Provider<EmoteClueTask> emoteClueTaskProvider;

    public boolean run(ClueSolverConfig config) {
        // Only start if not already running
        if (isRunning) {
            log.warn("Clue Solver Script V2 is already running.");
            return false;
        }

        log.info("Starting Clue Solver Script V2...");
        this.config = config;
        isRunning = true;
        loadNpcData();
        eventBus.register(this);

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn() || !super.run()) return;
                log.info("Executing Clue Solver Script V2...");
                processClueTask();
            } catch (Exception e) {
                log.error("Error in ClueSolverScriptV2 execution", e);
            }
        }, 200, config.taskInterval(), TimeUnit.MILLISECONDS);

        log.info("Clue Solver Script V2 initialized with task interval: {} ms", config.taskInterval());
        return true;
    }

    public void shutdown() {
        // Only stop if the script is running
        if (!isRunning) {
            log.warn("Clue Solver Script V2 is already stopped.");
            return;
        }

        log.info("Stopping Clue Solver Script V2...");
        super.shutdown();

        // Cancel the scheduled task if it’s running
        if (mainScheduledFuture != null && !mainScheduledFuture.isCancelled()) {
            mainScheduledFuture.cancel(true);
        }

        if (currentTask != null) {
            currentTask.stop();
            currentTask = null;
        }

        eventBus.unregister(this);
        isRunning = false;
        log.info("Clue Solver Script V2 successfully stopped.");
    }

    private void processClueTask() {
        if (currentTask == null) {
            ClueScroll activeClue = clueScrollPlugin.getClue();
            if (activeClue != null && config.toggleAll()) {
                log.info("New clue detected: {}", activeClue);
                currentTask = createTask(activeClue);
                if (currentTask != null) {
                    currentTask.start();
                }
            }
        } else {
            log.info("Executing current task: {}", currentTask.getTaskDescription());
            if (currentTask.execute()) {
                log.info("Current task completed.");
                currentTask.stop();
                currentTask = null;
                // Apply cooldown if specified
                if (config.cooldownBetweenTasks() > 0) {
                    log.info("Applying cooldown of {} ms between tasks", config.cooldownBetweenTasks());
                    try {
                        Thread.sleep(config.cooldownBetweenTasks());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        log.error("Interrupted during cooldown", e);
                    }
                }
            }
        }
    }

    private ClueTask createTask(ClueScroll clue) {
        log.info("Creating task for clue type: {}", clue.getClass().getSimpleName());

        if (clue instanceof EmoteClue) {
            EmoteClueTask task = emoteClueTaskProvider.get();
            task.setClue((EmoteClue) clue);
            return task;
        }

        log.warn("No task found for clue type: {}", clue.getClass().getSimpleName());
        return null;
    }

    private void loadNpcData() {
        try {
            Rs2NpcManager.loadJson();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load NPC data", e);
        }
    }

    public void updateConfig(ClueSolverConfig config) {
        this.config = config;
        log.info("Updated ClueSolverScriptV2 configuration.");
    }
}
