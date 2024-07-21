package net.runelite.client.plugins.microbot.plankmake;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.microbot.plankmake.enums.Logs;

@ConfigGroup("plankMake")
public interface PlankMakeConfig extends Config {
    String GROUP = "Plank Make";

    @ConfigItem(
            keyName = "guide",
            name = "How to use",
            description = "How to use this plugin",
            position = 0,
            section = generalSection
    )
    default String GUIDE() {
        return "Start at any bank with runes and coins in your inventory and make sure you have\n" +
                "already pre-cast plank make on the log you want to use and click don't ask again \n" +
                "on the pop up and everything should be good to go.";
    }

    @ConfigSection(
            name = "General",
            description = "General",
            position = 0,
            closedByDefault = false
    )
    String generalSection = "general";

    @ConfigItem(
            keyName = "logType",
            name = "Log Type",
            description = "Type of plank to make",
            position = 1,
            section = generalSection
    )
    default Logs ITEM() {
        return Logs.LOGS;
    }

    @ConfigItem(
            keyName = "lazyMode",
            name = "Enable Lazy Mode",
            description = "Enable lazy mode to add random delays and make the script less tick perfect",
            position = 2,
            section = generalSection
    )
    default boolean lazyMode() {
        return false;
    }

    @ConfigItem(
            keyName = "maxLazyDelay",
            name = "Maximum Lazy Delay",
            description = "The maximum additional delay in milliseconds to add in lazy mode",
            position = 3,
            section = generalSection
    )
    default int maxLazyDelay() {
        return 500; // Default to 500 milliseconds
    }
}
