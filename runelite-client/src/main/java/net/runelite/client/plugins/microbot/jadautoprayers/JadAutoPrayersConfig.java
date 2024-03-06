package net.runelite.client.plugins.microbot.jadautoprayers;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("JadAutoPrayers")
public interface JadAutoPrayersConfig extends Config {
    @ConfigItem(
            keyName = "oneTickFlick",
            name = "One Tick Flick",
            description = ""
    )
    default boolean oneTickFlick() {
        return true;
    }

    @ConfigItem(
            keyName = "rigourUnlocked",
            name = "Rigour Unlocked",
            description = "Do you have rigour?"
    )
    default boolean rigourUnlocked() {
        return true;
    }
}
