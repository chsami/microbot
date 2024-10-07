package net.runelite.client.plugins.microbot.sticktothescript.varrockanvil;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.microbot.smelting.enums.AnvilItem;
import net.runelite.client.plugins.microbot.smelting.enums.Bars;


@ConfigGroup("VarrockAnvil")
public interface VarrockAnvilConfig extends Config {


    @ConfigSection(
            name = "General",
            description = "General Information & Settings",
            position = 0
    )
    String generalSection = "General";

    @ConfigSection(
            name = "Smithing",
            description = "Smithing Settings",
            position = 1
    )
    String smithingSection = "Smithing";

    @ConfigItem(
            keyName = "barType",
            name = "Bar Type",
            description = "The type of bar to use on the anvil",
            position = 0,
            section = smithingSection
    )
    default Bars sBarType()
    {
        return Bars.BRONZE;
    }

    @ConfigItem(
            keyName = "smithObject",
            name = "Smith Object",
            description = "The desired object to smith at the anvil",
            position = 1,
            section = smithingSection
    )
    default AnvilItem sAnvilItem()
    {
        return AnvilItem.SCIMITAR;
    }

    @ConfigItem(
            keyName = "debug",
            name = "Debug",
            description = "Enable debug information",
            position = 2,
            section = smithingSection
    )
    default boolean sDebug()
    {
        return false;
    }

    @ConfigItem(
            keyName = "about",
            name = "About This Script",
            position = 0,
            description = "",
            section = generalSection
    )
    default String about() {
        return "This plugin smiths bars at the Varrock anvil.\n\nIf you have any desired features, please contact me through Discord.";
    }
}
