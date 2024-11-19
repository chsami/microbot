package net.runelite.client.plugins.microbot.nateplugins.moneymaking.natehumidifier;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.microbot.nateplugins.moneymaking.natehumidifier.enums.Items;


@ConfigGroup("General")
public interface HumidifierConfig extends Config {
    @ConfigSection(
            name = "General",
            description = "General",
            position = 0,
            closedByDefault = false
    )
    String generalSection = "general";

    @ConfigItem(
            keyName = "Items",
            name = "Items",
            description = "Choose the Item to Humidify",
            position = 0,
            section = generalSection
    )
    default Items ITEM()
    {
        return Items.CLAY;
    }
}
