package net.runelite.client.plugins.microbot.roguesden.steps;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.roguesden.model.BotApi;

public class InteractGameObjectStep extends InteractTileObjectStep {
    public InteractGameObjectStep(final BotApi botApi, final String name, final WorldPoint worldPoint) {
        super(botApi, name, worldPoint, null, botApi::findGameObject);
    }

    public InteractGameObjectStep(final BotApi botApi, final String name, final WorldPoint worldPoint, final String action) {
        super(botApi, name, worldPoint, action, botApi::findGameObject);
    }
}
