package net.runelite.client.plugins.microbot.roguesden.steps;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.roguesden.model.BotApi;

public class InteractDecorationStep extends InteractTileObjectStep {

    public InteractDecorationStep(final BotApi botApi, final String name, final WorldPoint worldPoint, final String action) {
        super(botApi, name, worldPoint, action, botApi::findDecorativeObject);
    }
}
