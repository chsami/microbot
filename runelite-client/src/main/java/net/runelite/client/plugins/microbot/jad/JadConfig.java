package net.runelite.client.plugins.microbot.jad;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("Jad")
public interface JadConfig extends Config {
    @ConfigItem(
            keyName = "GUIDE",
            name = "GUIDE",
            description = "GUIDE",
            position = 0
    )
    default String GUIDE() {
        return "This plugin will pray switch jad attacks and attack healers. Supports up to 3 jads";
    }
}
