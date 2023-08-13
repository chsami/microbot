package net.runelite.client.plugins.microbot.woodcutting;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.microbot.woodcutting.enums.WoodcuttingTree;

@ConfigGroup("Woodcutting")
public interface WoodcuttingConfig extends Config {
    @ConfigSection(
            name = "General",
            description = "General",
            position = 0,
            closedByDefault = false
    )
    String generalSection = "general";

    @ConfigItem(
            keyName = "Tree",
            name = "Tree",
            description = "Choose the tree",
            position = 0,
            section = generalSection
    )
    default WoodcuttingTree TREE()
    {
        return WoodcuttingTree.TREE;
    }

    @ConfigItem(
            keyName = "Axe inventory?",
            name = "Axe Inventory?",
            description = "Enable this if you have an axe in your inventory",
            position = 1,
            section = generalSection
    )
    default boolean hasAxeInventory()
    {
        return false;
    }
}
