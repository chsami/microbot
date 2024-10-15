package net.runelite.client.plugins.microbot.zerozeroQOL;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("zerozeroQOL")
public interface zerozeroQOLConfig extends Config {

    @ConfigItem(
            keyName = "walkToBank",
            name = "Walk to closest bank",
            description = "Toggle to walk to the nearest bank"
    )
    default boolean walkToBank() {
        return false;
    }

    @ConfigItem(
            keyName = "walkToVarrockTree",
            name = "Walk to Varrock Tree",
            description = "Toggle to walk to the tree in Varrock"
    )
    default boolean walkToVarrockTree() {
        return false; // Default off
    }
}
