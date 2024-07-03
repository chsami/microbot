package net.runelite.client.plugins.microbot.mining.amethyst;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("AmethystMining")
public interface AmethystMiningConfig extends Config {

    @ConfigItem(
            keyName = "guide",
            name = "How to use",
            description = "How to use this plugin",
            position = 0
    )
    default String GUIDE() {
        return "Start near the bank chest in the amethyst mine at the mining guild.";
    }

    @ConfigItem(
            keyName = "PickAxeInInventory",
            name = "Pick Axe In Inventory?",
            description = "Pick Axe in inventory?",
            position = 1
    )
    default boolean pickAxeInInventory() {
        return false;
    }
}