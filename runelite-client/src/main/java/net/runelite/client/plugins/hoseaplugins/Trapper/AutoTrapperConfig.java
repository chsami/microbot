package net.runelite.client.plugins.hoseaplugins.Trapper;


import net.runelite.client.plugins.hoseaplugins.Trapper.data.Salamander;
import net.runelite.client.config.*;

@ConfigGroup("AutoTrapperConfig")
public interface AutoTrapperConfig extends Config {
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
            keyName = "salamanderType",
            name = "Salamander",
            description = "",
            position = 1
    )
    default Salamander salamander() {
        return Salamander.RED_SALAMANDER;
    }

    @ConfigItem(
            keyName = "maxArea",
            name = "Max dist",
            description = "Max distance from start tile to set traps",
            position = 2
    )
    default int maxDist() {
        return 10;
    }

    @ConfigSection(
            name = "Tick Delay",
            description = "",
            position = 1

    )
    String tickDelaySection = "Tick Delay";

    @ConfigItem(
            name = "Tick Delay",
            keyName = "tickDelay",
            description = "Slow down plugin",
            position = 1,
            section = tickDelaySection
    )
    default int tickDelay() {
        return 0;
    }
}

