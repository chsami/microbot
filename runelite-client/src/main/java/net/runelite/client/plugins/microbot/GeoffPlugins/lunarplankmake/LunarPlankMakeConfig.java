package net.runelite.client.plugins.microbot.GeoffPlugins.lunarplankmake;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.microbot.GeoffPlugins.lunarplankmake.enums.Logs;

@ConfigGroup("plankMake")
public interface LunarPlankMakeConfig extends Config {
    String GROUP = "Plank Make";

    @ConfigItem(
            keyName = "guide",
            name = "How to use",
            description = "How to use this plugin",
            position = 0,
            section = generalSection
    )
    default String GUIDE() {
        return "To initiate the process, please begin at a bank with Astral, Earth, nature " +
                "runes and coins in your inventory. If you are using the Mud staff " +
                "equip that and ensure that you have previously pre-cast the " +
                "Plank Make spell on the desired log and acknowledge the " +
                "prompt to avoid any further notifications. With these steps complete, " +
                "you should be ready to proceed.";
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
            description = "Type of plank to make",
            position = 1,
            section = generalSection
    )
    default Logs ITEM() {
        return Logs.LOGS;
    }

    @ConfigItem(
            keyName = "useSetDelay",
            name = "Use Set Delay",
            description = "Enable to use a set delay between actions",
            position = 2,
            section = generalSection
    )
    default boolean useSetDelay() {
        return false;
    }

    @ConfigItem(
            keyName = "setDelay",
            name = "Set Delay (ms)",
            description = "The fixed delay in milliseconds between actions",
            position = 3,
            section = generalSection
    )
    default int setDelay() {
        return 500; // Default to 500 milliseconds
    }

    @ConfigItem(
            keyName = "useRandomDelay",
            name = "Use Random Delay",
            description = "Enable to use a random delay between actions",
            position = 4,
            section = generalSection
    )
    default boolean useRandomDelay() {
        return false;
    }

    @ConfigItem(
            keyName = "maxRandomDelay",
            name = "Maximum Random Delay (ms)",
            description = "The maximum random delay in milliseconds between actions",
            position = 5,
            section = generalSection
    )
    default int maxRandomDelay() {
        return 1000; // Default to 1000 milliseconds
    }
}
