package net.runelite.client.plugins.hoseaplugins.ItemCombiner;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Keybind;

@ConfigGroup("ItemCombiner")
public interface ItemCombinerConfig extends Config {
    @ConfigItem(
            keyName = "Toggle",
            name = "Toggle",
            description = "",
            position = 0
    )
    default Keybind toggle() {
        return Keybind.NOT_SET;
    }

    @ConfigSection(
            name = "Configuration",
            description = "Config for item combiner",
            position = 1
    )
    String configuration = "Configuration";

    @ConfigItem(
            keyName = "itemOneName",
            name = "Item One (Tool/Vial)",
            description = "Name of the first item",
            position = 2
    )
    default String itemOneName() {
        return "";
    }

    @ConfigItem(
            keyName = "itemOneAmt",
            name = "Item One Amount",
            description = "Amount of the first item",
            position = 3
    )
    default int itemOneAmt() {
        return 14;
    }

    @ConfigItem(
            keyName = "itemTwoName",
            name = "Item Two (Herb/Second/Gem/Etc.)",
            description = "Name of the second item",
            position = 4
    )
    default String itemTwoName() {
        return "";
    }

    @ConfigItem(
            keyName = "itemTwoAmt",
            name = "Item Two Amount",
            description = "Amount of the second item",
            position = 4
    )
    default int itemTwoAmt() {
        return 14;
    }
}
