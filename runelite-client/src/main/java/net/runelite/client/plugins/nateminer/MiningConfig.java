package net.runelite.client.plugins.nateminer;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.nateminer.enums.Rocks;

@ConfigGroup("Mining")
public interface MiningConfig extends Config {
    @ConfigSection(
            name = "General",
            description = "General",
            position = 0,
            closedByDefault = false
    )
    String generalSection = "general";

    @ConfigItem(
            keyName = "Ore",
            name = "Ore",
            description = "Choose the ore",
            position = 0,
            section = generalSection
    )
    default Rocks ORE()
    {
        return Rocks.TIN;
    }

    @ConfigItem(
            keyName = "Pickaxe inventory?",
            name = "Pickaxe in Inventory?",
            description = "Enable this if you have a pickaxe in your inventory make sure its on the first slot",
            position = 1,
            section = generalSection
    )
    default boolean hasPickaxeInventory()
    {
        return false;
    }
}
