package net.runelite.client.plugins.hoseaplugins.Chompy;


import net.runelite.client.config.*;

@ConfigGroup("AutoChompyConfig")
public interface AutoChompyConfig extends Config {
    @ConfigItem(
            keyName = "Toggle",
            name = "Toggle",
            description = "",
            position = 0
    )
    default Keybind toggle() {
        return Keybind.NOT_SET;
    }

}

