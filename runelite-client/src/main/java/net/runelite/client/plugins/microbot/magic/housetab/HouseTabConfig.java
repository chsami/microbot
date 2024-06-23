package net.runelite.client.plugins.microbot.magic.housetab;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(HouseTabConfig.GROUP)
public interface HouseTabConfig extends Config {

    String GROUP = "HouseTab";

    @ConfigItem(
            keyName = "OwnHouse",
            name = "Own house",
            description = "Use your own house",
            position = 0
    )
    default boolean ownHouse()
    {
        return false;
    }

    @ConfigItem(
            keyName = "Player Name",
            name = "Player Name",
            description = "Choose the player name's house",
            position = 1
    )
    default String housePlayerName()
    {
        return "";
    }
}
