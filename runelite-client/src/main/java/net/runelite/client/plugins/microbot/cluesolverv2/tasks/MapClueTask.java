package net.runelite.client.plugins.microbot.cluesolverv2.tasks;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.plugins.microbot.cluescrolls.clues.MapClue;
import net.runelite.client.plugins.microbot.cluesolverv2.taskinterface.ClueTask;

import javax.inject.Inject;

@Slf4j
public class MapClueTask implements ClueTask {

    private enum State {
        CHECKING_ITEMS,
        NAVIGATING_TO_LOCATION,
        DIGGING,
        COMPLETED,
        FAILED
    }

    @Inject
    private EventBus eventBus;

    private MapClue clue;
    private State state;

    public void setClue(MapClue clue) {
        this.clue = clue;
        this.state = State.CHECKING_ITEMS;
        log.info("Map clue task set to clue: " + clue);
    }

    @Override
    public void start() {

    }

    @Override
    public boolean execute() {
        return false;
    }

    @Override
    public void stop() {

    }

    @Override
    public String getTaskDescription() {
        return "";
    }
}
