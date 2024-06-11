package net.runelite.client.plugins.microbot.breakhandler;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("example")
public interface BreakHandlerConfig extends Config {
    @ConfigItem(
            keyName = "TimeUntilBreak",
            name = "Time until break (minutes)",
            description = "Time until break",
            position = 0
    )
    default int TimeUntilBreak() {
        return 120;
    }
}

