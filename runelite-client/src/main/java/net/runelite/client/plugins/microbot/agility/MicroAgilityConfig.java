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
            description = "Use food at certain hitpoint treshhold",
            position = 2,
            section = generalSection
    )
    default int hitpoints()
    {
        return 20;
    }
}
