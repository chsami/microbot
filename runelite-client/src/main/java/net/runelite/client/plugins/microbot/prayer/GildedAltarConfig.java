package net.runelite.client.plugins.microbot.prayer;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("GildedAltar")
public interface GildedAltarConfig extends Config {

    @ConfigItem(
            keyName = "Guide",
            name = "How to use",
            description = "How to use the script",
            position = 0
    )
    default String GUIDE() {
        return "This only supports house advertisements. Use this script in w330";
    }
}
