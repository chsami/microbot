package net.runelite.client.plugins.microbot.accountselector;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("AutoLoginConfig")
public interface AutoLoginConfig extends Config {
    @ConfigSection(
            name = "General",
            description = "General",
            position = 0,
            closedByDefault = false
    )
    String generalSection = "general";

    @ConfigItem(
            keyName = "World",
            name = "World",
            description = "World",
            position = 0,
            section = generalSection
    )
    default int world() { return 360; }

    @ConfigItem(
            keyName = "Is Member",
            name = "Is Member",
            description = "use Member worlds",
            position = 0,
            section = generalSection
    )
    default boolean isMember() { return false; }

    @ConfigItem(
            keyName = "RandomWorld",
            name = "RandomWorld",
            description = "use random worlds",
            position = 0,
            section = generalSection
    )
    default boolean useRandomWorld() { return true; }
}
