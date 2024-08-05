package net.runelite.client.plugins.microbot.thieving.stalls.model;

import lombok.AllArgsConstructor;
import net.runelite.api.GameObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.thieving.stalls.constants.StallLoot;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;

import javax.inject.Inject;

@AllArgsConstructor(onConstructor_ = @Inject)
public class HosidiusFruitThievingSpot implements IStallThievingSpot {
    private static WorldPoint SAFESPOT = new WorldPoint(1800, 3607, 0);
    private static final int STALL_ID = 28823;

    private BotApi botApi;

    @Override
    public void thieve() {
        if (!botApi.walkTo(SAFESPOT))
        {
            return;
        }

        final GameObject stall = botApi.getGameObject(STALL_ID, SAFESPOT.dx(1));
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
        return StallLoot.FRUIT.getItemIds();
    }
}
