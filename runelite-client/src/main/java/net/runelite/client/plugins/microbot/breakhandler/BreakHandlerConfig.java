package net.runelite.client.plugins.microbot.breakhandler;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("Breakhandler")
public interface BreakHandlerConfig extends Config {
    @ConfigItem(
            keyName = "TimeUntilBreakStart",
            name = "Time break start",
            description = "Time until break start in minutes",
            position = 0
    )
    default int timeUntilBreakStart() {
        return 60;
    }
    @ConfigItem(
            keyName = "TimeUntilBreakEnd",
            name = "Time break end",
            description = "Time until break ends in minutes",
            position = 1
    )
    default int timeUntilBreakEnd() {
        return 120;
    }

    @ConfigItem(
            keyName = "BreakDurationStart",
            name = "Break duration start",
            description = "Break duration start in minutes",
            position = 2
    )
    default int breakDurationStart() {
        return 10;
    }
    @ConfigItem(
            keyName = "BreakDurationEnd",
            name = "Break duration end",
            description = "Break duration end in minutes",
            position = 3
    )
    default int breakDurationEnd() {
        return 15;
    }
    @ConfigItem(
            keyName = "Logout",
            name = "Logout",
            description = "logout when taking a break",
            position = 4
    )
    default boolean logoutAfterBreak() {
        return true;
    }
}

