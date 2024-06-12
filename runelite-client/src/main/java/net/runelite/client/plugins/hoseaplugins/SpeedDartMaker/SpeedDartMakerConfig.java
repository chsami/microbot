package net.runelite.client.plugins.hoseaplugins.SpeedDartMaker;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;
import net.runelite.client.config.Range;

@ConfigGroup("SpeedDartMaker")
public interface SpeedDartMakerConfig extends Config {
    @ConfigItem(
            keyName = "Toggle",
            name = "Toggle",
            description = "",
            position = 0
    )
    default Keybind toggle() {
        return Keybind.NOT_SET;
    }

    @Range(
            min = 1,
            max = 10
    )
    @ConfigItem(
            keyName = "perTick",
            name = "Darts per tick",
            description = "",
            position = 1
    )
    default int perTick() {
        return 10;
    }

    @ConfigItem(
            keyName = "broadBolts",
            name = "Broad Bolts?",
            description = "",
            position = 2
    )
    default boolean broadBolts() {
        return false;
    }
}
