package net.runelite.client.plugins.microbot.sticktothescript.gefiremaker;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.microbot.sticktothescript.common.enums.GEWorkLocation;
import net.runelite.client.plugins.microbot.sticktothescript.common.enums.LogType;


@ConfigGroup("GEFiremaker")
public interface GEFiremakerConfig extends Config {


    @ConfigSection(
            name = "General",
            description = "General Information & Settings",
            position = 0
    )
    String generalSection = "General";

    @ConfigSection(
            name = "Firemaking",
            description = "Firemaking Settings",
            position = 1
    )
    String firemakingSection = "Firemaking";

    @ConfigItem(
            keyName = "logType",
            name = "Log Type",
            description = "The type of logs to use to make the fire",
            position = 0,
            section = firemakingSection
    )
    default LogType sLogType()
    {
        return LogType.NORMAL_LOGS;
    }

    @ConfigItem(
            keyName = "location",
            name = "Desired Fire Location",
            description = "The desired location to build a fire if a fire does not exist",
            position = 1,
            section = firemakingSection
    )
    default GEWorkLocation sLocation()
    {
        return GEWorkLocation.NORTH_EAST;
    }

    @ConfigItem(
            keyName = "debug",
            name = "Debug",
            description = "Enable debug information",
            position = 2,
            section = firemakingSection
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
        return "This plugin adds logs to campfires at the Grand Exchange. If the fire does not already exist, it will create it in the desired location that you select. If you have any desired features, please contact me through Discord.";
    }
}
