package net.runelite.client.plugins.envisionplugins.breakhandler;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("Break Handling")
public interface BreakHandlerConfig extends Config {
    @ConfigSection(
            name = "Default Break Durations",
            description = "Default Break Durations",
            position = 0,
            closedByDefault = false
    )
    String breakSection = "Default Break Durations";

    @ConfigSection(
            name = "Default Run Time Durations",
            description = "Default Run Time Durations",
            position = 1,
            closedByDefault = false
    )
    String runTimeSection = "Default Run Time Durations";

    @ConfigItem(
            keyName = "Minimum Duration",
            name = "Minimum Duration",
            description = "In seconds, the minimum time the script should run for. 3600 seconds = 1 hour",
            position = 0,
            section = runTimeSection
    )
    default int MINIMUM_RUN_TIME_DURATION() { return 3600; }

    @ConfigItem(
            keyName = "Maximum Duration",
            name = "Maximum Duration",
            description = "In seconds, the maximum time the script should run for. 3600 seconds = 1 hour",
            position = 1,
            section = runTimeSection
    )
    default int MAXIMUM_RUN_TIME_DURATION() { return 19800; }

    @ConfigItem(
            keyName = "Minimum Break Duration",
            name = "Minimum Break Duration",
            description = "In seconds, the minimum time the break should last. 3600 seconds = 1 hour",
            position = 2,
            section = breakSection
    )
    default int MINIMUM_BREAK_DURATION() { return 300; }

    @ConfigItem(
            keyName = "Maximum Break Duration",
            name = "Maximum Break Duration",
            description = "In seconds, the maximum time the break should last. 3600 seconds = 1 hour",
            position = 3,
            section = breakSection
    )
    default int MAXIMUM_BREAK_DURATION() { return 900; }
}
