package net.runelite.client.plugins.hoseaplugins.Firemaking;

import net.runelite.client.config.*;

@ConfigGroup("AutoFiremaking")
public interface FiremakingConfig extends Config {
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
            keyName = "location",
            name = "Location",
            description = "",
            position = 1
    )
    default FiremakingLocation getLocation() {
        return FiremakingLocation.GE;
    }

    @Range(
            min = 1,
            max = 10
    )
    @ConfigItem(
            keyName = "logName",
            name = "Logs",
            description = "",
            position = 2
    )
    default String getLogs() {
        return "Mahogany logs";
    }

}
