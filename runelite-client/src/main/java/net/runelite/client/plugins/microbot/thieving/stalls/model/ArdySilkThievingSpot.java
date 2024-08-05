package net.runelite.client.plugins.microbot.thieving.stalls.model;

import lombok.AllArgsConstructor;
import net.runelite.api.GameObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.thieving.stalls.constants.StallLoot;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;

import javax.inject.Inject;

@AllArgsConstructor(onConstructor_ = @Inject)
public class ArdySilkThievingSpot implements IStallThievingSpot {

    private static WorldPoint SAFESPOT = new WorldPoint(2662, 3316, 0);
    private static final int STALL_ID = 11729;

    private BotApi botApi;

    @Override
    public void thieve() {
        if (!botApi.walkTo(SAFESPOT))
        {
            return;
        }

        final GameObject stall = botApi.getGameObject(STALL_ID, SAFESPOT.dy(-2));
        if (stall == null)
        {
            return;
        }

        botApi.steal(stall);
        botApi.sleepUntilNextTick();
    }

    @Override
    public void bank() {
        Rs2Bank.walkToBankAndUseBank();
        Rs2Bank.depositAll();
        Rs2Bank.closeBank();
    }

    @Override
    public Integer[] getItemIdsToDrop() {
        return StallLoot.BAKER.getItemIds();
    }
}
