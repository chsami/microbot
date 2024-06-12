package net.runelite.client.plugins.hoseaplugins.ethanapi.PathingTesting;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("PathingTesting")
public interface PathingTestingConfig extends Config {
    @ConfigItem(
            keyName = "x",
            name = "x",
            description = ""
    )
    default int x() {
        return -1;
    }

    @ConfigItem(
            keyName = "y",
            name = "y",
            description = ""
    )
    default int y() {
        return -1;
    }

    @ConfigItem(
            keyName = "run",
            name = "run",
            description = ""
    )
    default boolean run() {
        return false;
    }
    @ConfigItem(
            keyName = "stop",
            name = "stop",
            description = ""
    )
    default boolean stop() {
        return false;
    }
    @ConfigItem(
            keyName = "drawTiles",
            name = "drawTiles",
            description = ""
    )
    default boolean drawTiles() {
        return false;
    }

}
