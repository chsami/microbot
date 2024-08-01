package net.runelite.client.plugins.microbot.roguesden.steps;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.roguesden.model.BotApi;

public class PickupItemStep extends Step {
    private final int itemId;
    private final WorldPoint worldPoint;

    public PickupItemStep(final BotApi botApi, final String name, final WorldPoint worldPoint, final int itemId) {
        super(botApi, name);
        this.worldPoint = worldPoint;
        this.itemId = itemId;
    }

    @Override
    public void execute() {
        botApi.pickupItem(worldPoint, itemId);
    }
}
