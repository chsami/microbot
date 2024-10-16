package net.runelite.client.plugins.microbot.tutorialisland;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup(TutorialIslandConfig.configGroup)
public interface TutorialIslandConfig extends Config {
    
    String configGroup = "MicroTutIsland";
    String toggleMusic = "toggleMusic";
    String toggleRoofs = "toggleRoofs";
    String toggleLevelUp = "toggleLevelUp";
    String toggleShiftDrop = "toggleShiftDrop";
    String toggleDevOverlay = "toggleDevOverlay";

    @ConfigSection(
            name = "QOL Settings",
            description = "Configure in-game settings",
            position = 0
    )
    String qolSection = "qol";

    @ConfigItem(
            keyName = toggleMusic,
            name = "Toggle Music",
            description = "Turns off in-game music",
            position = 0,
            section = qolSection
    )
    default boolean toggleMusic() {
        return true;
    }

    @ConfigItem(
            keyName = toggleRoofs,
            name = "Toggle Roofs",
            description = "Turns on 'hide roofs' in-game",
            position = 1,
            section = qolSection
    )
    default boolean toggleRoofs() {
        return true;
    }

    @ConfigItem(
            keyName = toggleLevelUp,
            name = "Toggle Disable Level-up Notifications",
            description = "Turns on 'disable level-up notifications'",
            position = 2,
            section = qolSection
    )
    default boolean toggleDisableLevelUp() {
        return true;
    }

    @ConfigItem(
            keyName = toggleShiftDrop,
            name = "Toggle Shift Dropping",
            description = "Turns on 'shift dropping'",
            position = 3,
            section = qolSection
    )
    default boolean toggleShiftDrop() {
        return true;
    }

    @ConfigSection(
            name = "Overlay Settings",
            description = "Configure overlay settings",
            position = 1,
            closedByDefault = true
    )
    String overlaySection = "overlay";

    @ConfigItem(
            keyName = toggleDevOverlay,
            name = "Toggle developer overlay",
            description = "Turns on developer info in overlay",
            position = 0,
            section = overlaySection
    )
    default boolean toggleDevOverlay() {
        return false;
    }
}
