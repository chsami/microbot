package net.runelite.client.plugins.microbot.smelting;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.microbot.smelting.enums.Bars;

@ConfigGroup("Smithing")
public interface AutoSmeltingConfig extends Config {
    @ConfigItem(
            keyName = "guide",
            name = "How to use",
            description = "How to use this plugin",
            position = 0,
            section = generalSection
    )
    default String GUIDE() {
        return "Select the type of bar you want to smelt and stand near any furnace.\n" +
                "You need enough ore in your bank to fill your inventory, e.g. 14 tin and 14 copper or 9 iron and 18 coal.\n";
    }

    @ConfigSection(
            name = "General",
            description = "General",
            position = 0
    )
    String generalSection = "general";

    @ConfigItem(
            keyName = "Bar",
            name = "Bar",
            description = "Choose the bar",
            position = 0,
            section = generalSection
    )
    default Bars SELECTED_BAR_TYPE()
    {
        return Bars.BRONZE;
    }
}