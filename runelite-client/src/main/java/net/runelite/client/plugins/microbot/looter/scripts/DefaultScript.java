package net.runelite.client.plugins.microbot.looter.scripts;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.looter.AutoLooterConfig;
import net.runelite.client.plugins.microbot.looter.enums.DefaultLooterStyle;
import net.runelite.client.plugins.microbot.looter.enums.LooterState;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.grounditem.LootingParameters;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class DefaultScript extends Script {

    LooterState state = LooterState.LOOTING;

    public boolean run(AutoLooterConfig config) {
        Microbot.enableAutoRunOn = false;
        initialPlayerLocation = null;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!super.run()) return;
                if (!Microbot.isLoggedIn() || Rs2Inventory.isFull() || Rs2Combat.inCombat()) return;
                long startTime = System.currentTimeMillis();

                if (initialPlayerLocation == null) {
                    initialPlayerLocation = Rs2Player.getWorldLocation();
                }

                switch (state) {
                    case LOOTING:
                        if (config.looterStyle() == DefaultLooterStyle.ITEM_LIST) {
                            LootingParameters itemLootParams = new LootingParameters(
                                    config.distanceToStray(),
                                    1,
                                    1,
                                    config.minFreeSlots(),
                                    config.toggleDelayedLooting(),
                                    config.toggleLootMyItemsOnly(),
                                    config.listOfItemsToLoot()
                            );
                            if (Rs2GroundItem.lootItemsBasedOnNames(itemLootParams)) {
                                Microbot.pauseAllScripts = false;
                            }
                        } else if (config.looterStyle() == DefaultLooterStyle.GE_PRICE_RANGE) {
                            LootingParameters valueParams = new LootingParameters(
                                    config.minPriceOfItem(),
                                    config.maxPriceOfItem(),
                                    config.distanceToStray(),
                                    1,
                                    config.minFreeSlots(),
                                    config.toggleDelayedLooting(),
                                    config.toggleLootMyItemsOnly()
                            );
                            if (Rs2GroundItem.lootItemBasedOnValue(valueParams)) {
                                Microbot.pauseAllScripts = false;
                            }
                        }
                        if (Rs2Inventory.getEmptySlots() <= config.minFreeSlots()) {
                            state = LooterState.BANKING;
                            return;
                        }
                        break;
                    case BANKING:
                        if (!Rs2Bank.bankItemsAndWalkBackToOriginalPosition(Rs2Inventory.all().stream().map(Rs2Item::getName).collect(Collectors.toList()), initialPlayerLocation))
                            return;
                        state = LooterState.LOOTING;
                        break;
                }

                long endTime = System.currentTimeMillis();
                long totalTime = endTime - startTime;
                System.out.println("Total time for loop " + totalTime);

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 200, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }
}
