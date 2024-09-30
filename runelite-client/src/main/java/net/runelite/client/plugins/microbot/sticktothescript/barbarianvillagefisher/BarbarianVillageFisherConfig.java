package net.runelite.client.plugins.microbot.sticktothescript.barbarianvillagefisher;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.microbot.sticktothescript.barbarianvillagefisher.enums.BarbarianFishingFunctions;
import net.runelite.client.plugins.microbot.sticktothescript.barbarianvillagefisher.enums.BarbarianFishingType;

@ConfigGroup("BarbarianVillageFisher")
public interface BarbarianVillageFisherConfig extends Config {

    @ConfigSection(
            name = "General",
            description = "General Information & Settings",
            position = 0
    )
    String generalSection = "General";

    @ConfigSection(
            name = "Fishing",
            description = "Fishing Settings",
            position = 1
    )
    String fishingSection = "Fishing";

    @ConfigItem(
            keyName = "fishType",
            name = "Fish Type",
            description = "What type of fishing are we going to do?",
            position = 0,
            section = fishingSection
    )
    default BarbarianFishingType sFishingType()
    {
        return BarbarianFishingType.FLY_FISHING;
    }

    @ConfigItem(
            keyName = "function",
            name = "Function",
            description = "Bank or drop items",
            position = 1,
            section = fishingSection
    )
    default BarbarianFishingFunctions sFunction()
    {
        return BarbarianFishingFunctions.DROP_RAW;
    }
    
    @ConfigItem(
            keyName = "debug",
            name = "Debug",
            description = "Enable debug information",
            position = 2,
            section = fishingSection
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
        return "This plugin fishes and cooks at the fishing spot in Barbarian Village. You can also bank the raw or cooked food in Edgeville.\n\nIf you have any desired features, please contact me through Discord.";
    }
}
