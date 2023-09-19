package net.runelite.client.plugins.microbot.crafting;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.microbot.crafting.enums.Activities;
import net.runelite.client.plugins.microbot.crafting.enums.Gems;
import net.runelite.client.plugins.microbot.crafting.enums.Glass;

@ConfigGroup(CraftingConfig.GROUP)
public interface CraftingConfig extends Config {

    String GROUP = "Crafting";

    @ConfigSection(
            name = "General",
            description = "General",
            position = 0
    )
    String generalSection = "general";

    @ConfigItem(
            keyName = "Activity",
            name = "Activity",
            description = "Choose the type of crafting activity to perform",
            position = 0,
            section = generalSection
    )
    default Activities activityType()
    {
        return Activities.NONE;
    }

    @ConfigItem(
            keyName = "Afk",
            name = "Afk randomly",
            description = "Randomy afks between 3 and 60 seconds",
            position = 1,
            section = generalSection
    )
    default boolean Afk()
    {
        return false;
    }

    @ConfigSection(
            name = "Gems",
            description = "Config for gem cutting",
            position = 1
    )
    String gemSection = "gem";

    @ConfigItem(
            keyName = "Gem",
            name = "Gem",
            description = "Choose the type of gem to cut",
            position = 0,
            section = gemSection
    )
    default Gems gemType()
    {
        return Gems.NONE;
    }

    @ConfigSection(
            name = "Glass",
            description = "Config for glass blowing",
            position = 2
    )
    String glassSection = "glass";

    @ConfigItem(
            keyName = "Glass",
            name = "Glass",
            description = "Choose the type of glass item to blow",
            position = 0,
            section = glassSection
    )
    default Glass glassType()
    {
        return Glass.PROGRESSIVE;
    }
}
