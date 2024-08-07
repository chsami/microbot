package net.runelite.client.plugins.microbot.roguesden.steps;

import net.runelite.api.ItemID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.roguesden.model.BotApi;
import net.runelite.client.plugins.microbot.util.Global;

public class OpenTileDoorStep extends Step {

    private final PickupItemStep pickupItemStep;
    private final InteractWallObjectStep interactWallObjectStep;

    public OpenTileDoorStep(final BotApi botApi, final String name) {
        super(botApi, name);
        this.pickupItemStep = new PickupItemStep(botApi, "Pick up Tile", new WorldPoint(3018, 5080, 1), ItemID.TILE_5568);
        this.interactWallObjectStep = new InteractWallObjectStep(botApi, "Open door", new WorldPoint(3023, 5082, 1), "Open");
    }

    @Override
    public void execute() {
        if (!botApi.inventoryContains(ItemID.TILE_5568))
        {
            pickupItemStep.execute();
            botApi.sleepUntil(() -> botApi.inventoryContains(ItemID.TILE_5568));
            return;
        }

        final Widget tile = botApi.getWidget(45088773);

        if (tile == null)
        {
            interactWallObjectStep.execute();
            botApi.sleepUntil(() -> botApi.getWidget(45088773) != null);
            return;
        }

        botApi.clickWidget(tile);
        Global.sleep(3000);
    }
}
