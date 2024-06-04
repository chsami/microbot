package net.runelite.client.plugins.microbot.scurrius;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("Scurrius")
public interface ScurriusConfig extends Config {
    @ConfigItem(
            keyName = "Prioritize",
            name = "Prioritize",
            description = "Prioritize Giant rats",
            position = 0
    )
    default boolean prioritizeRats() {
        return false;
    }
}
