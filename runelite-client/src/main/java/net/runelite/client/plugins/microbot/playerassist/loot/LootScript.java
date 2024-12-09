package net.runelite.client.plugins.microbot.playerassist.loot;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.playerassist.PlayerAssistConfig;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.grounditem.LootingParameters;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class LootScript extends Script {

    public LootScript() {
    }

    public boolean run(PlayerAssistConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            if (!Microbot.isLoggedIn()) return;
            if (Rs2Inventory.isFull() || Rs2Inventory.getEmptySlots() <= config.minFreeSlots() || (Rs2Combat.inCombat() && !config.toggleForceLoot()))
                return;

            if (!config.toggleLootItems()) return;
            lootItemsByValue(config);
            lootBones(config);
            lootAshes(config);
            lootRunes(config);
            lootCoins(config);
            lootUntradeableItems(config);
            lootArrows(config);
            lootCustomItems(config);

        }, 0, 200, TimeUnit.MILLISECONDS);
        return true;
    }

    // parse csv string accounting for quotes and spaces and standardize to proper case
    private List<String> parseCSVString(String csv) {
        List<String> items = new ArrayList<>();
        if (csv == null || csv.trim().isEmpty()) {
            return items;
        }

        // split by comma but keep quoted strings intact
        Pattern pattern = Pattern.compile("\"([^\"]*)\"|[^,]+");
        Matcher matcher = pattern.matcher(csv);

        while (matcher.find()) {
            String item = matcher.group();
            // remove quotes if present and trim whitespace
            if (item.startsWith("\"") && item.endsWith("\"")) {
                item = item.substring(1, item.length() - 1);
            }
            // trim whitespace and standardize case
            item = item.trim();
            if (!item.isEmpty()) {
                // convert to proper case (first letter capital, rest lowercase)
                item = item.substring(0, 1).toUpperCase() + item.substring(1).toLowerCase();
                items.add(item);
            }
        }
        return items;
    }

    private void lootCustomItems(PlayerAssistConfig config) {
        if (config.customItemsToLoot() == null || config.customItemsToLoot().trim().isEmpty()) {
            return;
        }

        List<String> itemsToLoot = parseCSVString(config.customItemsToLoot());
        for (String itemName : itemsToLoot) {
            if (Rs2GroundItem.loot(itemName, 1, config.attackRadius())) {
                Microbot.pauseAllScripts = false;
            }
        }
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

    public void shutdown() {
        super.shutdown();
    }
}
