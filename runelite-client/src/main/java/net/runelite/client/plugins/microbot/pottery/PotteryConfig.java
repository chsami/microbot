package net.runelite.client.plugins.microbot.pottery;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.microbot.pottery.enums.HumidifyAction;
import net.runelite.client.plugins.microbot.pottery.enums.HumidifyItems;
import net.runelite.client.plugins.microbot.pottery.enums.PotteryItems;
import net.runelite.client.plugins.microbot.pottery.enums.PotteryLocations;

@ConfigGroup("pottery")
public interface PotteryConfig extends Config {
    @ConfigSection(
            name = "General",
            description = "General",
            position = 0
    )
    String generalSection = "general";

    @ConfigItem(
            keyName = "Guide",
            name = "How to use",
            description = "How to use this plugin",
            position = 0,
            section = generalSection
    )
    default String GUIDE() {
        return "Start anywhere, script will check inventory & will goto bank if needed\n" +
                "Make sure you have clay (or soft clay) in your bank!\n" +
                "Make sure you have empty humidifying items in bank!";
    }

    @ConfigItem(
            keyName = "location",
            name = "Location",
            description = "Choose the location where to train",
            position = 1,
            section = generalSection
    )
    default PotteryLocations location()
    {
        return PotteryLocations.EAST_ARDOUGNE;
    }


    @ConfigItem(
            keyName = "potteryItem",
            name = "Pottery Item",
            description = "Choose pottery item to create",
            position = 2,
            section = generalSection
    )
    default PotteryItems potteryItem()
    {
        return PotteryItems.CUP;
    }

    @ConfigSection(
            name = "Humidify",
            description = "Humidify Options",
            position = 1
    )
    String humidifySection = "humidify";
    
    // Hiding this for now until I can test with Lunar spellbook & rune pouch
    @ConfigItem(
            keyName = "humidifyAction",
            name = "Humidify Action",
            description = "Choose action used to Humidify clay to soft clay",
            position = 0,
            section = humidifySection,
            hidden = true
    )
    default HumidifyAction humidifyAction() { return HumidifyAction.ITEM; }

    @ConfigItem(
            keyName = "humidifyItem",
            name = "Humidify Item",
            description = "Choose item used to humidify clay (if using item for Humidify Action)",
            position = 1,
            section = humidifySection
    )
    default HumidifyItems humidifyItem()
    {
        return HumidifyItems.JUG;
    }

    @ConfigItem(
            keyName = "forceRefill",
            name = "Force Refill",
            description = "Enable this option to fill all humidify items before making soft clay",
            position = 2,
            section = humidifySection
    )
    default boolean forceRefill()
    {
        return false;
    }
}
