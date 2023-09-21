package net.runelite.client.plugins.microbot.fletching;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.microbot.fletching.enums.FletchingItem;
import net.runelite.client.plugins.microbot.fletching.enums.FletchingMaterial;
import net.runelite.client.plugins.microbot.fletching.enums.FletchingMode;

@ConfigGroup(FletchingConfig.GROUP)
public interface FletchingConfig extends Config {

    String GROUP = "Fletching";

    @ConfigItem(
            keyName = "guide",
            name = "How to use",
            description = "How to use this plugin",
            position = 1
    )
    default String GUIDE() {
        return "Start the script at any bank (grand exchange preferably)\n" +
                "Make sure to have a bank and all the logs in your bank";
    }

    @ConfigSection(
            name = "General",
            description = "General",
            position = 0,
            closedByDefault = false
    )
    String generalSection = "general";

    @ConfigItem(
            keyName = "Mode",
            name = "Mode",
            description = "Choose your mode of fletching",
            position = 0,
            section = generalSection
    )
    default FletchingMode fletchingMode()
    {
        return FletchingMode.UNSTRUNG;
    }
    @ConfigItem(
            keyName = "Material",
            name = "Material",
            description = "Choose your material",
            position = 1,
            section = generalSection
    )
    default FletchingMaterial fletchingMaterial()
    {
        return FletchingMaterial.LOG;
    }
    @ConfigItem(
            keyName = "Item",
            name = "Item",
            description = "Choose your item",
            position = 2,
            section = generalSection
    )
    default FletchingItem fletchingItem()
    {
        return FletchingItem.SHORT;
    }
    @ConfigSection(
            name = "Antiban",
            description = "Configure antiban measures",
            position = 1,
            closedByDefault = false
    )
    String antibanSection = "antiban";
    @ConfigItem(
            keyName = "Afk",
            name = "Afk randomly",
            description = "Randomy afks between 3 and 60 seconds",
            position = 0,
            section = antibanSection
    )
    default boolean Afk()
    {
        return false;
    }
}
