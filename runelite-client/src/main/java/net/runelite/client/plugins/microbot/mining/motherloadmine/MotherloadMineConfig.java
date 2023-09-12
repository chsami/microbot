package net.runelite.client.plugins.microbot.mining.motherloadmine;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("MotherloadMine")
public interface MotherloadMineConfig extends Config {

    @ConfigItem(
            keyName = "guide",
            name = "How to use",
            description = "How to use this plugin",
            position = 0
    )
    default String GUIDE() {
        return "1. Have a hammer in your inventory \n2. Start near the bank chest in motherload mine";
    }
}
