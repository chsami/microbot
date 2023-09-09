package net.runelite.client.plugins.jrPlugins.AutoRifts;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;


@ConfigGroup("AutoRifts")
public interface AutoRiftsConfig extends Config {
    @ConfigItem(
            keyName = "Toggle",
            name = "Toggle",
            description = "",
            position = 0
    )
    default Keybind toggle() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(
            keyName = "startFrags",
            name = "Starting Fragments",
            description = "How many fragments you should get before leaving the starting zone",
            position = 1
    )
    default int startingFrags() {
        return 60;
    }

    @ConfigItem(
            keyName = "minFrags",
            name = "Minimum Fragments",
            description = "When you should mine more fragments",
            position = 2
    )
    default int minFrags() {
        return 24;
    }

    @ConfigItem(
            keyName = "ignorePortal",
            name = "Ignore Portal Ess",
            description = "How much essence you should have to ignore portal",
            position = 3
    )
    default int ignorePortal() {
        return 20;
    }

    @ConfigItem(
            keyName = "dropRunes",
            name = "Drop Runes",
            description = "Drop Runes instead of depositing (kek uim)",
            position = 4
    )
    default boolean dropRunes() {
        return false;
    }

    @ConfigItem(
            keyName = "usePouches",
            name = "Use Essence Pouches?",
            description = "Requires NPC Contact runes in Rune Pouch or Redwood lit Lantern",
            position = 6
    )
    default boolean usePouches() {
        return false;
    }

    @ConfigItem(
            keyName = "hasBook",
            name = "Abyssal Book in bank? (IMPORTANT FOR NPC CONTACT)",
            description = "IMPORTANT TO USE NPC CONTACT",
            position = 7
    )
    default boolean hasBook() {
        return true;
    }

    @ConfigItem(
            keyName = "prioritizeCatalytic",
            name = "Prioritizes Catalytic Energy",
            description = "Will try to balance points if not ticked",
            position = 8
    )
    default boolean prioritizeCatalytic() {
        return true;
    }

    @ConfigItem(
            keyName = "prioritizeHigher",
            name = "Prioritize Higher Tier Runes(BETA)",
            description = "Prioritizes Nature/Law/Death/Blood even if points arent balanced - Expect some bugs",
            position = 9
    )
    default boolean prioritizeHighTier() {
        return true;
    }
}
