package net.runelite.client.plugins.microbot.plankmake;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.microbot.plankmake.enums.Logs;

@ConfigGroup("plankMake")
public interface PlankMakeConfig extends Config {
        String GROUP = "Plank Make";

        @ConfigItem(
                keyName = "guide",
                name = "How to use",
                description = "How to use this plugin",
                position = 0,
                section = generalSection
        )
        default String GUIDE() {
            return "Start at any bank with runes and coins in your inventory and make sure you have\n" +
                    "already pre-cast plank make on the log you want to use and click don't ask again \n" +
                    "on the pop up and everything should be good to go.";
        }

        @ConfigSection(
                name = "General",
                description = "General",
                position = 0,
                closedByDefault = false
        )
        String generalSection = "general";



    @ConfigItem(
        keyName = "logType",
        name = "Log Type",
        description = "Type of plank to make"
    )
    default Logs ITEM() {
        return Logs.LOGS;
    }
}