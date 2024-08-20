package net.runelite.client.plugins.microbot.mining.amethyst;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.microbot.mining.amethyst.enums.AmethystCraftingOption;

@ConfigGroup("AmethystMining")
public interface AmethystMiningConfig extends Config {

    @ConfigSection(
            name = "Chiseling Options",
            description = "Options related to chiseling amethysts",
            position = 2
    )
    String chiselingOptionsSection = "chiselingOptionsSection";

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

    @ConfigItem(
            keyName = "chiselAmethysts",
            name = "Chisel Amethysts",
            description = "If enabled, the player will chisel the amethysts after mining.",
            position = 0,
            section = chiselingOptionsSection
    )
    default boolean chiselAmethysts() {
        return false;
    }

    @ConfigItem(
            keyName = "amethystCraftingOption",
            name = "Item",
            description = "Choose what to craft from amethysts.",
            position = 1,
            section = chiselingOptionsSection
    )
    default AmethystCraftingOption amethystCraftingOption() {
        return AmethystCraftingOption.BOLT_TIPS;
    }
}
