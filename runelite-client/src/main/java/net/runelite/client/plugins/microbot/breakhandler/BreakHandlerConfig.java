package net.runelite.client.plugins.microbot.breakhandler;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.microbot.util.antiban.enums.PlaySchedule;

@ConfigGroup("Breakhandler")
public interface BreakHandlerConfig extends Config {

    // Play Schedule section
    @ConfigSection(
            name = "Play Schedule",
            description = "Options related to using a play schedule",
            position = 5
    )
    String usePlaySchedule = "usePlaySchedule";

    @ConfigItem(
            keyName = "TimeUntilBreakStart",
            name = "Time until break start",
            description = "Time until break start in minutes",
            position = 0
    )
    default int timeUntilBreakStart() {
        return 60;
    }

    @ConfigItem(
            keyName = "TimeUntilBreakEnd",
            name = "Time until break end",
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
            description = "Logout when taking a break",
            position = 4
    )
    default boolean logoutAfterBreak() {
        return true;
    }

    @ConfigItem(
            keyName = "UsePlaySchedule",
            name = "Use Play Schedule",
            description = "Enable or disable the use of a play schedule",
            position = 5,
            section = "UsePlaySchedule"
    )
    default boolean usePlaySchedule() {
        return false;
    }

    @ConfigItem(
            keyName = "PlaySchedule",
            name = "Play Schedule",
            description = "Select the play schedule",
            position = 6,
            section = "UsePlaySchedule"
    )
    default PlaySchedule playSchedule() {
        return PlaySchedule.MEDIUM_DAY;
    }
}
