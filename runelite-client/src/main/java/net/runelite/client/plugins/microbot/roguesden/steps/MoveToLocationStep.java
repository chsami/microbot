package net.runelite.client.plugins.microbot.roguesden.steps;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.roguesden.model.BotApi;

public class MoveToLocationStep extends Step {
    private WorldPoint worldPoint;
    private final int minimumRunRequired;

    public MoveToLocationStep(final BotApi botApi, final String name, final WorldPoint worldPoint) {
        super(botApi, name);
        this.worldPoint = worldPoint;
        this.minimumRunRequired = 0;
    }

    public MoveToLocationStep(final BotApi botApi, final String name, final WorldPoint worldPoint, final int minimumRunRequired) {
        super(botApi, name);
        this.worldPoint = worldPoint;
        this.minimumRunRequired = minimumRunRequired;
    }


    @Override
    public void execute() {
        if (botApi.getRunEnergy() < minimumRunRequired)
        {
            botApi.sleepUntil(() -> botApi.getRunEnergy() >= minimumRunRequired);
            return;
        }

        if (needToRun())
        {
            botApi.setRunState(true);
            botApi.sleepUntil(botApi::getRunState);
            botApi.sleepUntilNextTick();
        }

        if (shouldSaveRun())
        {
            botApi.setRunState(false);
        }

        if (botApi.walkTo(worldPoint))
        {
            botApi.sleepUntilPlayerIdle();
        }

        if (botApi.getCurrentPlayerLocation() == worldPoint)
        {
            botApi.sleepUntilNextTick();
        }
    }

    private boolean needToRun()
    {
        return minimumRunRequired > 0;
    }

    private boolean shouldSaveRun()
    {
        return minimumRunRequired == 0 && botApi.getRunEnergy() < 50;
    }
}
