package net.runelite.client.plugins.microbot.cluesolverv2.tasks;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.plugins.microbot.cluescrolls.clues.AnagramClue;
import net.runelite.client.plugins.microbot.cluesolverv2.taskinterface.ClueTask;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import javax.inject.Inject;

import static net.runelite.client.plugins.microbot.util.Global.sleep;

@Slf4j
public class AnagramClueTask implements ClueTask {

    @Inject
    private EventBus eventBus;

    private AnagramClue clue;

    private State state;

    // Define the states for the task
    private enum State {
        NAVIGATING_TO_LOCATION,
        INTERACTING_WITH_NPC,
        ANSWERING_QUESTIONS,
        COMPLETED,
        FAILED
    }

    /**
     * Constructor for the task.
     */
    //TODO: Implement better overall logic for the task
    public AnagramClueTask() {
        this.state = State.NAVIGATING_TO_LOCATION;
    }

    /**
     * Sets the clue and initializes the task.
     *
     * @param clue The AnagramClue instance to solve.
     */
    public void setClue(AnagramClue clue) {
        this.clue = clue;
        log.info("AnagramClueTask initialized for clue: {}", clue.getText());
    }

    @Override
    public void start() {
        if (clue == null) {
            log.error("AnagramClue is null. Cannot start task.");
            state = State.FAILED;
            return;
        }
        log.info("Starting AnagramClueTask for clue: {}", clue.getText());
        eventBus.register(this);
    }

    @Override
    public boolean execute() {
        log.debug("Executing AnagramClueTask in state: {}", state);
        switch (state) {
            case NAVIGATING_TO_LOCATION:
                return navigateToLocation();

            case INTERACTING_WITH_NPC:
                return interactWithNPC();

            case ANSWERING_QUESTIONS:
                return answerQuestions();

            case COMPLETED:
                log.info("AnagramClueTask completed.");
                eventBus.unregister(this);
                return true;

            case FAILED:
                log.error("AnagramClueTask failed.");
                eventBus.unregister(this);
                return true;

            default:
                log.error("Unknown state encountered: {}", state);
                state = State.FAILED;
                return true;
        }
    }

    @Override
    public void stop() {
        log.info("Stopping AnagramClueTask.");
        state = State.FAILED;
        eventBus.unregister(this);
    }

    @Override
    public String getTaskDescription() {
        return "Solving Anagram Clue: " + clue.getText();
    }

    /**
     * Step 1: Navigate to the clue location.
     *
     * @return true if navigation is complete; false otherwise.
     */
    private boolean navigateToLocation() {
        WorldPoint location = clue.getLocation(null);
        if (location == null) {
            log.error("Clue location is null. Cannot navigate.");
            state = State.FAILED;
            return true;
        }

        if (Rs2Player.distanceTo(location) > 5) {
            log.info("Navigating to clue location: {}", location);
            boolean success = Rs2Walker.walkTo(location, 5);
            if (!success) {
                log.warn("Failed to navigate to location. Retrying.");
                return false;
            }
            return false; // Wait for navigation to complete
        }

        log.info("Player has arrived at the clue location.");
        state = State.INTERACTING_WITH_NPC;
        return false;
    }

    /**
     * Step 2: Interact with the NPC specified in the clue.
     *
     * @return true if interaction is successful; false otherwise.
     */
    private boolean interactWithNPC() {
        log.info("Interacting with the NPC to complete the clue.");
        NPC target = Rs2Npc.getNpc(clue.getNpc());

        if (target == null) {
            log.warn("NPC {} not found in the vicinity.", clue.getNpc());
            return false;
        }

        boolean interactionInitiated = Rs2Npc.interact(target, "Talk-to");
        if (interactionInitiated) {
            log.info("NPC interaction initiated.");
            sleep(1000);
            state = State.ANSWERING_QUESTIONS;
            return false; // Proceed to answer questions if any
        } else {
            log.warn("Failed to interact with the NPC.");
            return false;
        }
    }

    /**
     * Step 3: Answer any questions posed by the NPC.
     *
     * @return true if questions are answered successfully; false otherwise.
     */
    private boolean answerQuestions() {
        if (clue.getQuestion() != null && clue.getAnswer() != null) {
            log.info("Answering NPC question: {}", clue.getQuestion());

            while (Rs2Dialogue.isInDialogue()) {
                if (Rs2Dialogue.hasContinue()) {
                    Rs2Dialogue.clickContinue();
                    sleep(1000);
                } else {
                    break; // Exit loop if no "continue" option is available
                }
            }

            if (!Rs2Dialogue.isInDialogue()) {
                log.info("Dialogue ended. Answering question...");
                Rs2Keyboard.typeString(clue.getAnswer());
                sleep(2000); // Wait to ensure the input is processed
                Rs2Keyboard.enter();
                sleep(1000); // Wait after submitting the answer

                // Verify if dialogue has ended or further action is required
                if (!Rs2Dialogue.isInDialogue()) {
                    log.info("Answered the question successfully. Marking task as completed.");
                    state = State.COMPLETED;
                    return true;
                } else {
                    log.warn("Dialogue still active. Possible incorrect answer or additional steps required.");
                    if (Rs2Dialogue.hasContinue()) {
                        Rs2Dialogue.clickContinue();
                    }
                    return false;
                }
            } else {
                log.warn("Still in dialogue. Unable to answer question at this stage.");
                return false;
            }
        } else {
            log.info("No questions to answer. Marking task as completed.");
            state = State.COMPLETED;
            return true;
        }
    }

}
