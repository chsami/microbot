package net.runelite.client.plugins.microbot.agility;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.worldmap.AgilityCourseLocation;

@ConfigGroup("MicroAgility")
public interface MicroAgilityConfig extends Config {
    @ConfigSection(
            name = "General",
            description = "General",
            position = 0,
            closedByDefault = false
    )
    String generalSection = "general";

    @ConfigItem(
            keyName = "Course",
            name = "Course",
            description = "Choose your agility course",
            position = 1,
            section = generalSection
    )
    default AgilityCourseLocation agilityCourse()
    {
        return AgilityCourseLocation.CANIFIS_ROOFTOP_COURSE;
    }

    @ConfigItem(
            keyName = "Hitpoints",
            name = "Hitpoints treshhold",
            description = "Use food at certain hitpoint treshhold. If there's no food in the inventory, the script stops. Set to 0 in order to disable.",
            position = 2,
            section = generalSection
    )
    default int hitpoints()
    {
        return 20;
    }

    @ConfigItem(
            keyName = "BreakTime",
            name = "Pause Randomly",
            description = "Pauses randomly before clicking the next obstacle.",
            position = 3,
            section = generalSection
    )
    default boolean pauseRandomly()
    {
        return true;
    }

    @ConfigItem(
            keyName = "pauseMinTime",
            name = "Pause Min Time",
            description = "Minimum amount of time to randomly pause for.",
            position = 4,
            section = generalSection
    )
    default int pauseMinTime()
    {
        return 10;
    }

    @ConfigItem(
            keyName = "pauseMaxTime",
            name = "Pause Max Time",
            description = "Maximum amount of time to randomly pause for.",
            position = 5,
            section = generalSection
    )
    default int pauseMaxTime()
    {
        return 5000;
    }

    @ConfigItem(
            keyName = "Alchemy",
            name = "Alch",
            description = "Use Low/High Alchemy while doing agility",
            position = 5,
            section = generalSection
    )
    default boolean alchemy()
    {
        return false;
    }

    @ConfigItem(
            keyName = "item",
            name = "Item To Alch",
            description = "Item to alch",
            position = 5,
            section = generalSection
    )
    default String item()
    {
        return "";
    }
}