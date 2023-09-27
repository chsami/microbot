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

    @ConfigSection(
            name = "Discord Break Notifications",
            description = "Discord Break Notifications",
            position = 1,
            closedByDefault = false
    )
    String discordSection = "Discord Webhooks";

    @ConfigSection(
            name = "Debug",
            description = "Debug",
            position = 2,
            closedByDefault = false
    )
    String debugSection = "Debug";

    @ConfigItem(
            keyName = "Minimum Duration",
            name = "Minimum Duration",
            description = "In seconds, the minimum time the script should run for. 3600 seconds = 1 hour",
            position = 0,
            section = runTimeSection
    )
    default int MINIMUM_RUN_TIME_DURATION() { return 60; }

    @ConfigItem(
            keyName = "Maximum Duration",
            name = "Maximum Duration",
            description = "In seconds, the maximum time the script should run for. 3600 seconds = 1 hour",
            position = 1,
            section = runTimeSection
    )
    default int MAXIMUM_RUN_TIME_DURATION() { return 120; }

    @ConfigItem(
            keyName = "Minimum Break Duration",
            name = "Minimum Break Duration",
            description = "In seconds, the minimum time the break should last. 3600 seconds = 1 hour",
            position = 2,
            section = breakSection
    )
    default int MINIMUM_BREAK_DURATION() { return 20; }

    @ConfigItem(
            keyName = "Maximum Break Duration",
            name = "Maximum Break Duration",
            description = "In seconds, the maximum time the break should last. 3600 seconds = 1 hour",
            position = 3,
            section = breakSection
    )
    default int MAXIMUM_BREAK_DURATION() { return 30; }

    @ConfigItem(
            keyName = "Discord Webhook for notifications",
            name = "Discord Webhook for notifications",
            description = "Discord webhook to send break start and end notifications to.",
            position = 4,
            section = discordSection
    )
    default String DISCORD_WEBHOOK() { return ""; }

    @ConfigItem(
            keyName = "Enable Discord Webhook for notifications",
            name = "Enable Discord Webhook for notifications",
            description = "Enable Discord webhook?",
            position = 5,
            section = discordSection
    )
    default boolean ENABLE_DISCORD_WEBHOOK() { return false; }

    @ConfigItem(
            keyName = "Enable verbose debug System logs?",
            name = "Enable verbose debug System logs?",
            description = "Enable verbose debug System logs?",
            position = 6,
            section = debugSection
    )
    default boolean VERBOSE_LOGGING() { return false; }
}
