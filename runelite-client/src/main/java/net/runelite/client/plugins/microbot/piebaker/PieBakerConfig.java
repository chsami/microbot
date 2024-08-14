package net.runelite.client.plugins.microbot.piebaker;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("piebaker")
public interface PieBakerConfig extends Config {
    @ConfigItem(
            keyName = "instructions",
            name = "Instructions",
            description = "",
            position = 0
    )
    default String instructions() {
        return "1. Ensure you have the required runes in your inventory.\n" +
               "2. Start the bot at any bank, preferably a populated one like the Grand Exchange.";
    }

    @ConfigItem(
            keyName = "selectedPie",
            name = "Select Pie",
            description = "Choose the pie you want to bake",
            position = 1
    )
    default String selectedPie() {
        return "Uncooked Berry Pie";
    }

    @ConfigItem(
            keyName = "tickPerfect",
            name = "Tick Perfect",
            description = "Enable tick perfect spell casting",
            position = 2
    )
    default boolean tickPerfect() {
        return false;
    }
}
