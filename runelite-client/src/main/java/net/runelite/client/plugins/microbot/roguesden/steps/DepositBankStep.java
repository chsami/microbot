package net.runelite.client.plugins.microbot.roguesden.steps;

import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.roguesden.model.BotApi;

public class DepositBankStep extends Step {
    public DepositBankStep(final BotApi botApi, final String name) {
        super(botApi, name);
    }

    @Override
    public void execute() {
        botApi.walkTo(new WorldPoint(3042, 4972, 1), 10);

        final NPC npc = botApi.getNpc(3194);
        botApi.depositInventory(npc);
    }
}
