package net.runelite.client.plugins.microbot.crafting;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.microbot.crafting.enums.Gems;

@ConfigGroup(CraftingConfig.GROUP)
public interface CraftingConfig extends Config {

    String GROUP = "Crafting";

    @ConfigSection(
            name = "General",
            description = "General",
            position = 0,
            closedByDefault = false
    )
    String generalSection = "general";

    @ConfigItem(
            keyName = "Gem",
            name = "Gem",
            description = "Choose the type of gem to cut",
            position = 0,
            section = generalSection
    )
    default Gems gemType()
    {
        return Gems.OPAL;
    }
}
