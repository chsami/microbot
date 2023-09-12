package net.runelite.client.plugins.ogPlugins.ogfiremaking;
import net.runelite.client.plugins.ogPlugins.ogfiremaking.enums.Logs;
import net.runelite.client.plugins.ogPlugins.ogfiremaking.enums.locations;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("ogfiremaking")
public interface ogFiremakingConfig extends Config {
    @ConfigSection(
            name = "General",
            description = "General",
            position = 0,
            closedByDefault = false
    )
    String generalSection = "general";

    @ConfigItem(
            keyName = "Logs",
            name = "Logs",
            description = "Choose the logs you would like to start with?",
            position = 1,
            section = generalSection
    )
    default Logs selectedLogs() {return Logs.YEW;}
    @ConfigItem(
            keyName = "Location",
            name = "Location",
            description = "Please select your location.",
            position = 2,
            section = generalSection
    )
    default locations selectedLocation() {return locations.GRAND_EXCHANGE;}
    @ConfigItem(
            keyName = "Progression Mode?",
            name = "Progression Mode?",
            description = "Would you like to automatically start with the next log once you have the level?",
            position = 3,
            section = generalSection
    )
    default boolean getProgressionMode() {return true;}

}
