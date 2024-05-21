package net.runelite.client.plugins.microbot.construction;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.plugins.microbot.farming.enums.FarmingMaterial;

@ConfigGroup(ConstructionConfig.GROUP)
public interface ConstructionConfig extends Config {

    String GROUP = "Construction";

    @ConfigItem(
            keyName = "Guide",
            name = "How to use",
            description = "How to use the script",
            position = 0
    )
    default String GUIDE()
    {
        return "This script only supports oak larder with a demon butler.";
    }
}
