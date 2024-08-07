package net.runelite.client.plugins.microbot.roguesden.steps;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.roguesden.model.BotApi;

public class EnterCourseStep extends Step {
    private final InteractWallObjectStep interactWallObjectStep;
    private final DepositBankStep depositBankStep;
    private final MoveToLocationStep moveToLocationStep;

    public EnterCourseStep(final BotApi botApi, final String name) {
        super(botApi, name);
        interactWallObjectStep = new InteractWallObjectStep(botApi, name, new WorldPoint(3056, 4991, 1), "Open");
        depositBankStep = new DepositBankStep(botApi, "Deposit items to bank");
        moveToLocationStep = new MoveToLocationStep(botApi, "Walk to entrance", new WorldPoint(3056, 4991, 1));
    }

    @Override
    public void execute() {
        if (!botApi.isInventoryEmpty())
        {
            depositBankStep.execute();
            return;
        }

        moveToLocationStep.execute();
        interactWallObjectStep.execute();
    }
}
