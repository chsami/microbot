package net.runelite.client.plugins.microbot.barrows;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("Barrows")
public interface AutoBarrowsConfig extends Config {
    @ConfigItem(
            keyName = "guide",
            name = "How to use",
            description = "How to use this plugin",
            position = 0
    )
    default String GUIDE() {
        return "TODO: Write this XD";
    }

    @ConfigSection(
            name = "Teleport",
            description = "Teleport",
            position = 2,
            closedByDefault = false
    )
    String teleportSections = "teleport";

    @ConfigSection(
            name = "Potions",
            description = "Potions",
            position = 1,
            closedByDefault = false
    )
    String potionSection = "potions";


}
