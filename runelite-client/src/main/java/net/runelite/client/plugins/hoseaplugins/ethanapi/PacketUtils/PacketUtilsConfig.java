package net.runelite.client.plugins.hoseaplugins.ethanapi.PacketUtils;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("PacketUtils")
public interface PacketUtilsConfig extends Config {
    @ConfigItem(
            keyName = "debug",
            name = "debug",
            description = "enable menuaction debug output"
    )
    default boolean debug() {
        return false;
    }

    @ConfigItem(
            keyName = "alwaysOn",
            name = "Always enabled.",
            description = "Makes this plugin always enabled on startup if the revision matches."
    )
    default boolean alwaysOn() {
        return false;
    }

}