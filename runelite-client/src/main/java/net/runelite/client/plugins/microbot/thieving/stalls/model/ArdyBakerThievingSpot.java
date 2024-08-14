package net.runelite.client.plugins.microbot.thieving.stalls.model;

import lombok.AllArgsConstructor;
import net.runelite.api.GameObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.thieving.stalls.constants.StallLoot;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;

import javax.inject.Inject;

@AllArgsConstructor(onConstructor_ = @Inject)
public class ArdyBakerThievingSpot implements IStallThievingSpot {

    private static WorldPoint SAFESPOT = new WorldPoint(2669, 3310, 0);
    private static WorldPoint BANK = new WorldPoint(2656, 3283, 0);
    private static final int STALL_ID = 11730;
    private static final int BANK_BOOTH = 10356;

    private BotApi botApi;

    @Override
    public void thieve() {
        if (!botApi.walkTo(SAFESPOT))
        {
            return;
        }

        final GameObject stall = botApi.getGameObject(STALL_ID, SAFESPOT.dx(-2));
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
