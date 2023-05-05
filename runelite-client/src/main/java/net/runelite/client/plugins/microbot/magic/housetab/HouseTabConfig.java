package net.runelite.client.plugins.microbot.magic.housetab;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.plugins.microbot.fletching.enums.FletchingMode;
import net.runelite.client.plugins.microbot.magic.housetab.enums.HOUSETABS_CONFIG;

@ConfigGroup(HouseTabConfig.GROUP)
public interface HouseTabConfig extends Config {

    String GROUP = "HouseTab";

    @ConfigItem(
            keyName = "House Config",
            name = "House Config",
            description = "Choose your house config",
            position = 0
    )
    default HOUSETABS_CONFIG HouseConfig()
    {
        return HOUSETABS_CONFIG.OWN_HOUSE;
    }

    @ConfigItem(
            keyName = "Player Name",
            name = "Player Name",
            description = "Choose the player name's house",
            position = 1
    )
    default String housePlayerName()
    {
        return "valeron xiii";
    }
}
