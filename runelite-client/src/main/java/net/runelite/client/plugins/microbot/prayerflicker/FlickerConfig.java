package net.runelite.client.plugins.microbot.prayerflicker;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("Prayer Flicker")

public interface FlickerConfig extends Config {
    @ConfigItem(
            keyName = "Guide",
            name = "How to use",
            description = "How to use the script",
            position = 0
    )
    default String GUIDE()
    {
        return "Setup ur quick prayer and then turn this on";
    }

    @ConfigItem(
            keyName = "InCombat",
            name = "In Combat Only",
            description = "Enable this if you only want to flick while in combat",
            position = 1
    )
    default boolean inCombatOnly()
    {
        return true;
    }
}
