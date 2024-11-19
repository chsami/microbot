package net.runelite.client.plugins.microbot.cluesolverv2.tasks;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.plugins.microbot.cluescrolls.clues.CipherClue;
import net.runelite.client.plugins.microbot.cluesolverv2.taskinterface.ClueTask;

import javax.inject.Inject;

@Slf4j
public class CipherClueTask implements ClueTask {

    private enum State {
        DECODING_CIPHER,
        NAVIGATING_TO_NPC,
        INTERACTING_WITH_NPC,
        COMPLETED,
        FAILED
    }

    @Inject
    private EventBus eventBus;

    private CipherClue clue;
    private State state;

    public void setClue(CipherClue clue) {
        this.clue = clue;
        this.state = State.DECODING_CIPHER;
        log.info("CipherClueTask initialized for clue: {}", clue.getText());
    }

    @Override
    public void start() {
        if (clue == null) {
            log.error("CipherClue is null. Cannot start task.");
            state = State.FAILED;
            return;
        }
        log.info("Starting CipherClueTask for clue: {}", clue.getText());
        eventBus.register(this);
    }

    @Override
    public boolean execute() {
        log.debug("Executing CipherClueTask in state: {}", state);
        switch (state) {
            case DECODING_CIPHER:
                return decodeCipher();
            case NAVIGATING_TO_NPC:
                return navigateToNpc();
            case INTERACTING_WITH_NPC:
                return interactWithNpc();
            case COMPLETED:
                log.info("CipherClueTask completed.");
                eventBus.unregister(this);
                return true;
            case FAILED:
                log.error("CipherClueTask failed.");
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
        log.info("Stopping CipherClueTask.");
        state = State.FAILED;
        eventBus.unregister(this);
    }

    @Override
    public String getTaskDescription() {
        return "Solving Cipher Clue: " + clue.getText();
    }

    private boolean decodeCipher() {
        log.info("Decoding cipher...");
        // Implement decoding logic or use a predefined mapping
        int npcName = clue.getNpcId();
        if (npcName == -1) {
            log.error("Failed to decode cipher.");
            state = State.FAILED;
            return true;
        }
        log.info("Cipher decoded to NPC: {}", npcName);
        state = State.NAVIGATING_TO_NPC;
        return false;
    }

    private boolean navigateToNpc() {
        log.info("Navigating to NPC...");
        // Implement navigation logic to NPC's location
        // Update state upon arrival
        state = State.INTERACTING_WITH_NPC;
        return false;
    }

    private boolean interactWithNpc() {
        log.info("Interacting with NPC...");
        // Implement NPC interaction
        state = State.COMPLETED;
        return true;
    }
}
