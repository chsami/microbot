package net.runelite.client.plugins.hoseaplugins.AutoAerial;


import net.runelite.client.config.*;

@ConfigGroup("AutoAerial")
public interface AutoAerialConfig extends Config {
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
