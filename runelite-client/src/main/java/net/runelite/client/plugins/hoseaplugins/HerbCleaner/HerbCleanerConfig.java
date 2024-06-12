package net.runelite.client.plugins.hoseaplugins.HerbCleaner;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;
import net.runelite.client.config.Range;

@ConfigGroup("HerbCleaner")
public interface HerbCleanerConfig extends Config {
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
            keyName = "herbType",
            name = "Herb Type",
            description = "",
            position = 1
    )
    default HerbType herbType() {
        return HerbType.GUAM;
    }

    @Range(max = 10)
    @ConfigItem(
            keyName = "herbAmount",
            name = "Herbs per tick",
            description = "Number of herbs you clean per tick",
            position = 2
    )
    default int herbAmount() {
        return 10;
    }
}
