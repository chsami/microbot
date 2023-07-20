package net.runelite.client.plugins.nateplugins.natefishing.natefishing;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.nateplugins.natefishing.natefishing.enums.Fishs;

@ConfigGroup("Mining")
public interface FishingConfig extends Config {
    @ConfigSection(
            name = "General",
            description = "General",
            position = 0,
            closedByDefault = false
    )
    String generalSection = "general";

    @ConfigItem(
            keyName = "Fish",
            name = "Fish",
            description = "Choose the fish",
            position = 0,
            section = generalSection
    )
    default Fishs Fish()
    {
        return Fishs.SHRIMP;
    }

}
