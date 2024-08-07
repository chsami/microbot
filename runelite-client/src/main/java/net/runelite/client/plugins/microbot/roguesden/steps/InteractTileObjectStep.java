package net.runelite.client.plugins.microbot.roguesden.steps;

import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.roguesden.model.BotApi;

import java.util.function.Function;

public class InteractTileObjectStep extends Step {
    private WorldPoint worldPoint;
    private String action;
    private Function<WorldPoint, ? extends TileObject> findTileObject;

    public InteractTileObjectStep(final BotApi botApi, final String name,
                                  final WorldPoint worldPoint, final String action,
                                  final Function<WorldPoint, ? extends TileObject> findTileObject) {
        super(botApi, name);
        this.worldPoint = worldPoint;
        this.action = action;
        this.findTileObject = findTileObject;
    }

    @Override
    public void execute() {
        final TileObject tileObject = findTileObject.apply(worldPoint);

        if (tileObject == null)
        {
            System.out.println("Could not find tileObject for " + this.getName());
            return;
        }

        if (botApi.interact(tileObject, action))
        {
            botApi.sleepUntilPlayerIdle();
        }
    }
}
