package net.runelite.client.plugins.microbot.roguesden;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("rogues den")
public interface RoguesDenConfig extends Config {
    @ConfigItem(
            keyName = "guide",
            name = "How to use",
            description = "How to use this plugin",
            position = 0
    )
    default String GUIDE() {
        return "Start with an empty inventory and nothing equiped, at the rogues' den entrance";
    }
}
