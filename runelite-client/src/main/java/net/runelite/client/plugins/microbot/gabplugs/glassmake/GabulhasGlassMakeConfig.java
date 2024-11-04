package net.runelite.client.plugins.microbot.gabplugs.glassmake;

import net.runelite.client.config.*;

@ConfigGroup("GabulhasGlassMake")
@ConfigInformation(
        "<ol><li>Configure the fairy rings to DKP at least once</li><li>Make sure to have karambwan vessel and raw karambwanji</li><li>Start the script next to the karambwan fishing spot</li></ol>"
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
            keyName = "Items",
            name = "Items",
            description = "Choose the type of seaweed",
            position = 0,
            section = generalSection
    )
    default GabulhasGlassMakeInfo.items ITEM()
    {
        return GabulhasGlassMakeInfo.items.GiantSeaweed;
    }
}


