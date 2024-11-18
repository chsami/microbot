package net.runelite.client.plugins.microbot.cluesolverv2;

import com.google.inject.Inject;
import com.google.inject.Provider;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.cluescrolls.ClueScrollPlugin;
import net.runelite.client.plugins.microbot.cluescrolls.clues.*;
import net.runelite.client.plugins.microbot.cluesolverv2.taskinterface.ClueTask;
import net.runelite.client.plugins.microbot.cluesolverv2.tasks.*;
import net.runelite.client.plugins.microbot.util.npc.Rs2NpcManager;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class ClueSolverScriptV2 extends Script {

    private ClueSolverConfig config;
    private ClueTask currentTask;
    private boolean isRunning = false;
    private final ReentrantLock taskLock = new ReentrantLock();

    @Inject
    private ClueScrollPlugin clueScrollPlugin;


    @Inject
    private EventBus eventBus;

    @Inject
    private Provider<EmoteClueTask> emoteClueTaskProvider;

    @Inject
    private Provider<CoordinateClueTask> coordinateClueTaskProvider;

    @Inject
    private Provider<AnagramClueTask> anagramClueTaskProvider;

    @Inject
    private Provider<CrypticClueTask> crypticClueTaskProvider;

    @Inject
    private Provider<MapClueTask> mapClueTaskProvider;


    /**
     * Starts the Clue Solver Script V2.
     *
     * @param config Configuration settings for the clue solver.
     * @return True if the script starts successfully; false otherwise.
     */
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
                log.debug("Executing Clue Solver Script V2...");
                processClueTask();
            } catch (Exception e) {
                log.error("Error in ClueSolverScriptV2 execution", e);
            }
        }, 200, config.taskInterval(), TimeUnit.MILLISECONDS);

        log.info("Clue Solver Script V2 initialized with task interval: {} ms", config.taskInterval());
        return true;
    }

    /**
     * Shuts down the Clue Solver Script V2 gracefully.
     */
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

        // Safely stop the current task
        taskLock.lock();
        try {
            if (currentTask != null) {
                currentTask.stop();
                currentTask = null;
            }
        } finally {
            taskLock.unlock();
        }

        eventBus.unregister(this);
        isRunning = false;
        log.info("Clue Solver Script V2 successfully stopped.");
    }

    /**
     * Processes the current clue task.
     * Ensures that only one task is active at a time.
     */
    private void processClueTask() {
        if (taskLock.tryLock()) {
            try {
                if (currentTask == null) {
                    ClueScroll activeClue = clueScrollPlugin.getClue();
                    if (activeClue != null && config.toggleAll()) {
                        log.info("New clue detected: {}", activeClue);
                        currentTask = createTask(activeClue);
                        if (currentTask != null) {
                            currentTask.start();
                            log.info("Started task: {}", currentTask.getTaskDescription());
                        }
                    }
                } else {
                    log.debug("Executing current task: {}", currentTask.getTaskDescription());
                    boolean taskCompleted = currentTask.execute();
                    if (taskCompleted) {
                        log.info("Current task completed: {}", currentTask.getTaskDescription());
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
            } finally {
                taskLock.unlock();
            }
        } else {
            log.debug("A task is already being processed. Skipping this cycle.");
        }
    }

    /**
     * Creates a task based on the type of clue.
     *
     * @param clue Clue to create task for.
     * @return Task for the clue.
     */
    private ClueTask createTask(ClueScroll clue) {
        log.info("Creating task for clue type: {}", clue.getClass().getSimpleName());

        if (clue instanceof EmoteClue) {
            EmoteClueTask task = emoteClueTaskProvider.get();
            task.setClue((EmoteClue) clue);
            return task;
        } else if (clue instanceof CoordinateClue) {
            CoordinateClueTask task = coordinateClueTaskProvider.get();
            task.setClue((CoordinateClue) clue);
            return task;
        } else if (clue instanceof AnagramClue) {
            AnagramClueTask task = anagramClueTaskProvider.get();
            task.setClue((AnagramClue) clue);
            return task;
        } else if (clue instanceof CrypticClue) {
            CrypticClueTask task = crypticClueTaskProvider.get();
            task.setClue((CrypticClue) clue);
            return task;
        } else if (clue instanceof MapClue) {
            MapClueTask task = mapClueTaskProvider.get();
            task.setClue((MapClue) clue);
            return task;
        }

        log.warn("No task found for clue type: {}", clue.getClass().getSimpleName());
        return null;
    }


    /**
     * Loads NPC data required for clue solving.
     */
    private void loadNpcData() {
        try {
            Rs2NpcManager.loadJson();
            log.info("NPC data loaded successfully.");
        } catch (Exception e) {
            log.error("Failed to load NPC data", e);
            throw new RuntimeException("Failed to load NPC data", e);
        }
    }

    /**
     * Updates the configuration of the Clue Solver Script V2.
     *
     * @param config New configuration settings.
     */
    public void updateConfig(ClueSolverConfig config) {
        this.config = config;
        log.info("Updated ClueSolverScriptV2 configuration.");
    }
}
