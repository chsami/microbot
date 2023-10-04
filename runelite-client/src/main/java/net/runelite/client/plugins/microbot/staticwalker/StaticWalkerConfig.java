package net.runelite.client.plugins.microbot.staticwalker;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup(StaticWalkerPlugin.CONFIG_GROUP)
public interface StaticWalkerConfig extends Config {
    @ConfigSection(
            name = "General",
            description = "General",
            position = 0,
            closedByDefault = false
    )
    String generalSection = "general";
    @ConfigSection(
            name = "Custom",
            description = "Custom",
            position = 1,
            closedByDefault = false
    )
    String customSection = "custom";

    @ConfigItem(
            keyName = "Locations",
            name = "Locations",
            description = "Choose your location",
            position = 0,
            section = generalSection
    )
    default WorldDestinations locations() {
        return WorldDestinations.VARROCK_GRAND_EXCHANGE;
    }

    @ConfigItem(
            keyName = "x",
            name = "X Coordinate",
            description = "X World Coordinate",
            position = 0,
            section = customSection
    )
    default String xCoordinate() {
        return "";
    }

    @ConfigItem(
            keyName = "y",
            name = "Y Coordinate",
            description = "Y World Coordinate",
            position = 1,
            section = customSection
    )
    default String yCoordinate() {
        return "";
    }

    @ConfigItem(
            keyName = "z",
            name = "Z Coordinate",
            description = "Z World Coordinate",
            position = 2,
            section = customSection
    )
    default String zCoordinate() {
        return "0";
    }
}
