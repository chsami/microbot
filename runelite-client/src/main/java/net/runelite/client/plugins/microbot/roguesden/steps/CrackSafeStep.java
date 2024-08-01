package net.runelite.client.plugins.microbot.roguesden.steps;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.roguesden.model.BotApi;

public class CrackSafeStep extends InteractDecorationStep {

    public CrackSafeStep(BotApi botApi) {
        super(botApi, "Crack safe", new WorldPoint(3018, 5047, 1), "Crack");
    }

    @Override
    public void execute()
    {
        super.execute();
        botApi.sleepUntilPlayerIdle();
        botApi.sleepUntil(() -> false, 3000);
    }
}
