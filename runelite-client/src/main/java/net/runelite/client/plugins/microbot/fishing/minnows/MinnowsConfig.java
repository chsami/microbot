package net.runelite.client.plugins.microbot.fishing.minnows;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

import static net.runelite.client.plugins.microbot.fishing.minnows.MinnowsConfig.GROUP;

@ConfigGroup(GROUP)
public interface MinnowsConfig extends Config {
    String GROUP = "Minnows";

    @ConfigSection(
            name = "General",
            description = "General",
            position = 0,
            closedByDefault = false
    )
    String generalSection = "general";

    @ConfigItem(
            keyName = "GUIDE",
            name = "GUIDE",
            description = "GUIDE",
            position = 0,
            section = generalSection
    )
    default String GUIDE() {
        return "This plugin allows for fully automated minnow fishing at the fishing guild. \n\n" +
                "To use this plugin, simply start the script at the minnows fishing platform with a small fishing net in your inventory.";
    }
}
