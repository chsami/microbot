package net.runelite.client.plugins.microbot.playerassistfork.loot;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.playerassistfork.PlayerAssistConfig;
import net.runelite.client.plugins.microbot.playerassistfork.PlayerAssistPlugin;
import net.runelite.client.plugins.microbot.playerassistfork.PlayerAssistState;
import net.runelite.client.plugins.microbot.util.grounditem.LootingParameters;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Slf4j
public class LootScript extends Script {

    public void run(PlayerAssistConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!super.run() || PlayerAssistPlugin.fulfillConditionsToRun()) return;

                if (!config.toggleLootItems()) return;

                // Go to bank if inventory is full
                if (Rs2Inventory.isFull() || Rs2Inventory.getEmptySlots() <= config.minFreeSlots()) {
                    if (PlayerAssistPlugin.playerState != PlayerAssistState.BANKING)
                        PlayerAssistPlugin.playerState = PlayerAssistState.BANKING;
                    return;
                }

                if (config.toggleLootItemsByValue()) {
                    lootItemsByValue(config);
                }

                if (config.toggleLootSelectedCategories()) {
                    lootBones(config);
                    lootAshes(config);
                    lootRunes(config);
                    lootCoins(config);
                    lootUntradeableItems(config);
                    lootArrows(config);
                }

                if (config.toggleLootItemsByName()) {
                    lootItemsByName(config);
                }

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 200, TimeUnit.MILLISECONDS);
    }

    private void lootArrows(PlayerAssistConfig config) {
        if (config.toggleLootArrows()) {
            LootingParameters arrowParams = new LootingParameters(
                    config.attackRadius(),
                    1,
                    10,
                    config.minFreeSlots(),
                    config.toggleDelayedLooting(),
                    config.toggleOnlyLootMyItems(),
                    "arrow"
            );
            if (Rs2GroundItem.lootItemsBasedOnNames(arrowParams)) {
                Microbot.pauseAllScripts = false;
            }
        }
    }

    private void lootBones(PlayerAssistConfig config) {
        if (config.toggleBuryBones()) {
            LootingParameters bonesParams = new LootingParameters(
                    config.attackRadius(),
                    1,
                    1,
                    config.minFreeSlots(),
                    config.toggleDelayedLooting(),
                    config.toggleOnlyLootMyItems(),
                    "bones"
            );
            if (Rs2GroundItem.lootItemsBasedOnNames(bonesParams)) {
                Microbot.pauseAllScripts = false;
            }
        }
    }

    private void lootAshes(PlayerAssistConfig config) {
        if (config.toggleScatter()) {
            LootingParameters ashesParams = new LootingParameters(
                    config.attackRadius(),
                    1,
                    1,
                    config.minFreeSlots(),
                    config.toggleDelayedLooting(),
                    config.toggleOnlyLootMyItems(),
                    " ashes"
            );
            if (Rs2GroundItem.lootItemsBasedOnNames(ashesParams)) {
                Microbot.pauseAllScripts = false;
            }
        }
    }

    // loot runes
    private void lootRunes(PlayerAssistConfig config) {
        if (config.toggleLootRunes()) {
            LootingParameters runesParams = new LootingParameters(
                    config.attackRadius(),
                    1,
                    1,
                    config.minFreeSlots(),
                    config.toggleDelayedLooting(),
                    config.toggleOnlyLootMyItems(),
                    " rune"
            );
            if (Rs2GroundItem.lootItemsBasedOnNames(runesParams)) {
                Microbot.pauseAllScripts = false;
            }
        }
    }

    // loot coins
    private void lootCoins(PlayerAssistConfig config) {
        if (config.toggleLootCoins()) {
            LootingParameters coinsParams = new LootingParameters(
                    config.attackRadius(),
                    1,
                    1,
                    config.minFreeSlots(),
                    config.toggleDelayedLooting(),
                    config.toggleOnlyLootMyItems(),
                    "coins"
            );
            if (Rs2GroundItem.lootCoins(coinsParams)) {
                Microbot.pauseAllScripts = false;
            }
        }
    }

    // loot untreadable items
    private void lootUntradeableItems(PlayerAssistConfig config) {
        if (config.toggleLootUntradables()) {
            LootingParameters untradeableItemsParams = new LootingParameters(
                    config.attackRadius(),
                    1,
                    1,
                    config.minFreeSlots(),
                    config.toggleDelayedLooting(),
                    config.toggleOnlyLootMyItems(),
                    "untradeable"
            );
            if (Rs2GroundItem.lootUntradables(untradeableItemsParams)) {
                Microbot.pauseAllScripts = false;
            }
        }
    }

    private void lootItemsByValue(PlayerAssistConfig config) {
        LootingParameters valueParams = new LootingParameters(
                config.minPriceOfItemsToLoot(),
                config.maxPriceOfItemsToLoot(),
                config.attackRadius(),
                1,
                config.minFreeSlots(),
                config.toggleDelayedLooting(),
                config.toggleOnlyLootMyItems()
        );
        if (Rs2GroundItem.lootItemBasedOnValue(valueParams)) {
            Microbot.pauseAllScripts = false;
        }
    }

    private void lootItemsByName(PlayerAssistConfig config) {
        LootingParameters params = new LootingParameters(
                config.attackRadius(),
                1,
                1,
                config.minFreeSlots(),
                config.toggleDelayedLooting(),
                config.toggleOnlyLootMyItems(),
                Arrays.stream(config.lootItemsByName().split(","))
                        .map(String::trim).toArray(String[]::new)
        );

        Microbot.pauseAllScripts = true;
        sleepUntil(() -> Rs2GroundItem.lootItemsBasedOnNames(params));
        PlayerAssistPlugin.playerState = PlayerAssistState.COMBAT;
        Microbot.pauseAllScripts = false;
    }

    public void shutdown() {
        super.shutdown();
    }
}
