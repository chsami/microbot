package net.runelite.client.plugins.microbot.quest;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("quest")
public interface QuestConfig extends Config {

    @ConfigSection(
            name = "Static Walker",
            description = "Options for Static Walker",
            position = 0,
            closedByDefault = false
    )
    String staticWalkerSection = "staticWalkerSection";
    @ConfigItem(
            keyName = "HybridWalking",
            name = "Enable Hybrid Walking",
            description = "Combines Dynamic Walker and Static Walker",
            section = staticWalkerSection,
            position = 0
    )
    default boolean enableHybridWalking() {
        return false;
    }

    @ConfigItem(
            keyName = "UseNearest",
            name = "Allow Partial Paths",
            description = "Static Walker will find the full path or nearest partial path it can find",
            section = staticWalkerSection,
            position = 1
    )
    default boolean useNearest() {
        return false;
    }
}
