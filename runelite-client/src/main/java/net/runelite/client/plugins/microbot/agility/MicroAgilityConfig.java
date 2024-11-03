package net.runelite.client.plugins.microbot.agility;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.microbot.agility.enums.AgilityCourseName;
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
            keyName = "guide",
            name = "How to use",
            description = "How to use this plugin",
            position = 0,
            section = generalSection
    )
    default String GUIDE() {
        return "Enable the plugin near the start of your selected agility course. \n\n" +
                "Course requirements: \n" +
                "Ape Atoll - Kruk or Ninja greegree equipped. Stamina pots recommended. \n" +
                "Shayzien Advanced - Crossbow and Mith Grapple equipped.";
    }
    @ConfigItem(
            keyName = "Course",
            name = "Course",
            description = "Choose your agility course",
            position = 1,
            section = generalSection
    )
    default AgilityCourseName agilityCourse()
    {
        return AgilityCourseName.CANIFIS_ROOFTOP_COURSE;
    }

    @ConfigItem(
            keyName = "Hitpoints",
            name = "Eat at %)",
            description = "Use food below certain hitpoint percent. If there's no food in the inventory, the script stops. Set to 0 in order to disable.",
            position = 2,
            section = generalSection
    )
    default int hitpoints()
    {
        return 20;
    }


    @ConfigItem(
            keyName = "Alchemy",
            name = "Alch",
            description = "Use Low/High Alchemy while doing agility",
            position = 3,
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
            position = 3,
            section = generalSection
    )
    default String item()
    {
        return "";
    }
}