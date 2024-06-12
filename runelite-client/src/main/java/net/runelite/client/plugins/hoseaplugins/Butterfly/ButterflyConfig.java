package net.runelite.client.plugins.hoseaplugins.Butterfly;


import net.runelite.client.config.*;

@ConfigGroup("ButterflyConfig")
public interface ButterflyConfig extends Config {
    @ConfigItem(
            keyName = "Toggle",
            name = "Toggle",
            description = "",
            position = 0
    )
    default Keybind toggle() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(
            keyName = "butterflyName",
            name = "Butterfly",
            description = "",
            position = 1
    )
    default ButterflyType butterfly() {
        return ButterflyType.RUBY_HARVEST;
    }

    @ConfigItem(
            keyName = "tickDelay",
            name = "Tick delay",
            description = "Will slow the plugin by this many ticks",
            position = 2
    )
    default int tickDelay() {
        return 0;
    }
}

