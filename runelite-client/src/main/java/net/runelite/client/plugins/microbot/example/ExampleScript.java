package net.runelite.client.plugins.microbot.example;

import net.runelite.client.plugins.loottracker.LootTrackerItem;
import net.runelite.client.plugins.loottracker.LootTrackerPlugin;
import net.runelite.client.plugins.loottracker.LootTrackerRecord;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.grandexchange.Rs2GrandExchange;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;

import java.util.concurrent.TimeUnit;


public class ExampleScript extends Script {
    public static double version = 1.0;

    public static net.runelite.api.NPC npc = null;

    public String test() {
        return "hello world from test";
    }

    public boolean run(ExampleConfig config) {
        Microbot.enableAutoRunOn = false;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;
                long startTime = System.currentTimeMillis();

                boolean isBankOpen = Rs2Bank.walkToBankAndUseBank();

                if (isBankOpen) {
                    boolean hasWithdrawAsNote = Rs2Bank.setWithdrawAsNote();
                    if (!hasWithdrawAsNote) return;
                    for (LootTrackerRecord lootTrackerRecord: LootTrackerPlugin.panel.aggregateRecords) {
                        for (LootTrackerItem lootTrackerItem:  lootTrackerRecord.getItems()) {
                            if (mainScheduledFuture.isCancelled()) break;
                            if (!Rs2Inventory.isTradeable(lootTrackerItem.getId())) continue;
                            Rs2Bank.withdrawAll(lootTrackerItem.getId());
                        }
                    }
                    Rs2Bank.closeBank();
                }




                for (Rs2Item item:
                        Rs2Inventory.items()) {

                    if (!item.isTradeable()) continue;

                    if (Rs2GrandExchange.getAvailableSlot().getKey() == null && Rs2GrandExchange.hasSoldOffer()) {
                        Rs2GrandExchange.collectToBank();
                        sleep(600);
                    }

                    Rs2GrandExchange.sellItemUnder5Percent(item.name);
                }

//                LootTrackerBox.getItems()




         /*       if (npc == null) {
                    System.out.println("NPc is null");
                    return;
                }

                System.out.println(Rs2Npc.getHealth(npc));*/

                long endTime = System.currentTimeMillis();
                long totalTime = endTime - startTime;
                System.out.println("Total time for loop " + totalTime);

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 300, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }
}
