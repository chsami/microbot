package net.runelite.client.plugins.microbot.autobanker;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("autobanker")
public interface AutoBankerConfig extends Config {
    @ConfigItem(
            position = 1,
            keyName = "ItemsToBank",
            name = "Items to bank",
            description = "Items to bank"
    )
    default String itemsToBank() {
        return "item1,item2,item3";
    }
}
