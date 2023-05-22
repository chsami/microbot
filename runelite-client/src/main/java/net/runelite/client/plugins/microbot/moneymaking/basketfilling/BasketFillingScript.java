package net.runelite.client.plugins.microbot.moneymaking.basketfilling;

import net.runelite.api.MenuEntry;
import net.runelite.api.NpcID;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class BasketFillingScript extends Script {

    public static double version = 1.0;

    public boolean run(BasketFillingConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {
                if (!Inventory.isInventoryFull() || !Inventory.hasItem("basket")) {
                    Rs2Bank.openBank();
                    if (Rs2Bank.isBankOpen()) {
                        Rs2Bank.depositAll();
                        Rs2Bank.withdrawItemX(true, "tomato", 24);
                        Rs2Bank.withdrawItemX(true, "basket", 24);
                        Rs2Bank.closeBank();
                        sleepUntil(() -> !Rs2Bank.isBankOpen());
                    }
                } else {
                    for (int i = 24; i < 28; i++) {
                        Inventory.useItemSlot(i);
                    }
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }
}
