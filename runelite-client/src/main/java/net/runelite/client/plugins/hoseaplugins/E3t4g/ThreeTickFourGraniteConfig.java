package net.runelite.client.plugins.hoseaplugins.E3t4g;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;

@ConfigGroup("e3t4g")
public interface ThreeTickFourGraniteConfig extends Config {
    @ConfigItem(keyName = "e3t4gToggle", name = "Toggle", description = "")
    default Keybind e3t4gToggle() {
        return Keybind.NOT_SET;
    }
}
