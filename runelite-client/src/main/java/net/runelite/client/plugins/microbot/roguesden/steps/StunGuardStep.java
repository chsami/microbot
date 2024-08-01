package net.runelite.client.plugins.microbot.roguesden.steps;

import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.roguesden.model.BotApi;

public class StunGuardStep extends Step {

    private final PickupItemStep pickupItemStep;
    private final MoveToLocationStep moveToLocationStep;
    private static final int STUN_POWDER = 5559;
    private static final int GUARD = 3191;
    private static final int THROW_ANIMATION = 929;

    public StunGuardStep(final BotApi botApi) {
        super(botApi, "Stun guard");
        pickupItemStep = new PickupItemStep(botApi, "Pick up flash powder", new WorldPoint(3009, 5063, 1), STUN_POWDER);
        moveToLocationStep = new MoveToLocationStep(botApi, "Run past guard", new WorldPoint(3028, 5055, 1), 20);
    }

    @Override
    public void execute() {
        if (!botApi.inventoryContains(STUN_POWDER))
        {
            pickupItemStep.execute();
            return;
        }

        NPC guard = botApi.getNpc(GUARD);
        if (guard != null && botApi.useItemOnNpc(STUN_POWDER, guard))
        {
            botApi.sleepUntil(() -> botApi.getAnimation() == THROW_ANIMATION, 10000);
            moveToLocationStep.execute();
        }
    }
}
