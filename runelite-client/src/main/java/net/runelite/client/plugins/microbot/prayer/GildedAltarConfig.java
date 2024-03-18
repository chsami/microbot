package net.runelite.client.plugins.microbot.prayer;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.plugins.microbot.magic.housetab.enums.HOUSETABS_CONFIG;

@ConfigGroup("GildedAltar")
public interface GildedAltarConfig extends Config {
    @ConfigItem(
            keyName = "House Config",
            name = "House Config",
            description = "Choose your house config",
            position = 0,
            hidden = true
    )
    default HOUSETABS_CONFIG HouseConfig()
    {
        return HOUSETABS_CONFIG.HOUSE_ADVERTISEMENT;
    }

    @ConfigItem(
            keyName = "Player Name",
            name = "Player Name",
            description = "Choose the player name's house",
            position = 1
    )
    default String housePlayerName()
    {
        return "xgrace,workless";
    }
}
