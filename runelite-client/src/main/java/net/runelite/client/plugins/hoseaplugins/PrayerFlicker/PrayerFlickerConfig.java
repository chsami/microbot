package net.runelite.client.plugins.hoseaplugins.PrayerFlicker;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;

@ConfigGroup("PrayerFlicker")
public interface PrayerFlickerConfig extends Config {
    @ConfigItem(
            keyName = "Toggle",
            name = "Toggle",
            description = ""
    )
    default Keybind toggle() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(
            keyName = "minimapToggle",
            name = "Toggle with Minimap Orb",
            description = "Toggle using the Minimap Orb"
    )
    default boolean minimapToggle() {
        return false;
    }
}