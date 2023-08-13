package net.runelite.client.plugins.microbot.farming.tithefarm.farming;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.microbot.farming.tithefarm.farming.enums.FarmingMaterial;
import net.runelite.client.plugins.microbot.fletching.enums.FletchingMode;

@ConfigGroup("farming")
public interface FarmingConfig extends Config {
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
            description = "Choose your tree",
            position = 0,
            section = generalSection
    )
    default FarmingMaterial farmingMaterial()
    {
        return FarmingMaterial.OAK_TREE;
    }
}
