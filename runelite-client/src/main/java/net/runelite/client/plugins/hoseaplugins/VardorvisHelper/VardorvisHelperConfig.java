package net.runelite.client.plugins.hoseaplugins.VardorvisHelper;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("VardorvisHelper")
public interface VardorvisHelperConfig extends Config {

    @ConfigItem(
            keyName = "autoPray",
            name = "Auto Prayers",
            description = ""
    )
    default boolean autoPray() {
        return false;
    }

    @ConfigItem(
            keyName = "awakened",
            name = "Awakened Vardorvis",
            description = "",
            position = 0
    )
    default boolean awakened() {
        return false;
    }
}
