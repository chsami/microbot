package net.runelite.client.plugins.microbot.gabplugs.glassmake;

import net.runelite.client.config.*;

@ConfigGroup("GabulhasGlassMake")
@ConfigInformation(
        "Just make sure to have Seaweed and Buckets of sand in your bank."
)
public interface GabulhasGlassMakeConfig extends Config {

    @ConfigSection(
            name = "General",
            description = "General",
            position = 0,
            closedByDefault = false
    )
    String generalSection = "general";

    @ConfigItem(
            keyName = "Seaweed type",
            name = "seaweed type",
            description = "Choose the type of seaweed",
            position = 0,
            section = generalSection
    )
    default GabulhasGlassMakeInfo.items ITEM()
    {
        return GabulhasGlassMakeInfo.items.GiantSeaweed;
    }
}


