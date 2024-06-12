package net.runelite.client.plugins.hoseaplugins.AutoRifts;


import net.runelite.client.config.*;

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

    @ConfigSection(
            name = "Auto Rifts Configuration",
            description = "Configure your settings for the AutoRifts plugin",
            position = 1,
            closedByDefault = true
    )
    String autoRiftsConfig = "autoRiftsConfig";

    @ConfigItem(
            keyName = "prioritizeBloodDeath",
            name = "Always use blood/death altars",
            description = "Will ignore point balance for these altars and always use them if available.",
            position = 2,
            section = autoRiftsConfig
    )
    default boolean prioritizeBloodDeath() {
        return true;
    }

    @ConfigItem(
            keyName = "dropRunes",
            name = "Drop Runes",
            description = "Drop Runes instead of depositing (kek uim)",
            position = 3,
            section = autoRiftsConfig
    )
    default boolean dropRunes() {
        return false;
    }

    @ConfigItem(
            keyName = "dropRunesFilter",
            name = "Drop Runes Filter",
            description = "If Drop Runes is not enabled and this has runes entered, the type of rune entered here will still get dropped, others will get deposited (ex: air, Mind, Body). Add runes with full name, air rune, mind rune , cosmic rune, etc... and split with comma ','",
            position = 5,
            section = autoRiftsConfig
    )
    default String dropRunesFilter() {
        return "";
    }

    @ConfigItem(
            keyName = "usePouches",
            name = "Use Essence Pouches?",
            description = "Requires NPC Contact runes in Rune Pouch or Redwood lit Lantern",
            position = 6,
            section = autoRiftsConfig
    )
    default boolean usePouches() {
        return false;
    }

    @ConfigItem(
            keyName = "hasBook",
            name = "Abyssal Book in bank? (IMPORTANT FOR NPC CONTACT)",
            description = "IMPORTANT TO USE NPC CONTACT",
            position = 7,
            section = autoRiftsConfig
    )
    default boolean hasBook() {
        return true;
    }

    @ConfigItem(
            keyName = "startFrags",
            name = "Starting Fragments (0 to wait for first portal)",
            description = "How many fragments you should get before leaving the starting zone",
            position = 8,
            section = autoRiftsConfig
    )
    default int startingFrags() {
        return 0;
    }

    @ConfigSection(
            name = "Altar Ignore Configuration",
            description = "",
            position = 60,
            closedByDefault = true
    )
    String ignoreSection = "ignoreSection";

    @ConfigItem(
            keyName = "ignoreAir",
            name = "Ignore Air Altar",
            description = "",
            position = 61,
            section = ignoreSection
    )
    default boolean ignoreAir() {
        return false;
    }

    @ConfigItem(
            keyName = "ignoreMind",
            name = "Ignore Mind Altar",
            description = "",
            position = 62,
            section = ignoreSection
    )
    default boolean ignoreMind() {
        return false;
    }

    @ConfigItem(
            keyName = "ignoreWater",
            name = "Ignore Water Altar",
            description = "",
            position = 63,
            section = ignoreSection
    )
    default boolean ignoreWater() {
        return false;
    }

    @ConfigItem(
            keyName = "ignoreEarth",
            name = "Ignore Earth Altar",
            description = "",
            position = 64,
            section = ignoreSection
    )
    default boolean ignoreEarth() {
        return false;
    }

    @ConfigItem(
            keyName = "ignoreFire",
            name = "Ignore Fire Altar",
            description = "",
            position = 65,
            section = ignoreSection
    )
    default boolean ignoreFire() {
        return false;
    }

    @ConfigItem(
            keyName = "ignoreBody",
            name = "Ignore Body Altar",
            description = "",
            position = 66,
            section = ignoreSection
    )
    default boolean ignoreBody() {
        return false;
    }

    @ConfigItem(
            keyName = "ignoreCosmic",
            name = "Ignore Cosmic Altar",
            description = "",
            position = 67,
            section = ignoreSection
    )
    default boolean ignoreCosmic() {
        return false;
    }

    @ConfigItem(
            keyName = "ignoreChaos",
            name = "Ignore Chaos Altar",
            description = "",
            position = 68,
            section = ignoreSection
    )
    default boolean ignoreChaos() {
        return false;
    }

    @ConfigItem(
            keyName = "ignoreNature",
            name = "Ignore Nature Altar",
            description = "",
            position = 69,
            section = ignoreSection
    )
    default boolean ignoreNature() {
        return false;
    }

    @ConfigItem(
            keyName = "ignoreLaw",
            name = "Ignore Law Altar",
            description = "",
            position = 70,
            section = ignoreSection
    )
    default boolean ignoreLaw() {
        return false;
    }

    @ConfigItem(
            keyName = "ignoreDeath",
            name = "Ignore Death Altar",
            description = "",
            position = 71,
            section = ignoreSection
    )
    default boolean ignoreDeath() {
        return false;
    }

    @ConfigItem(
            keyName = "ignoreBlood",
            name = "Ignore Blood Altar",
            description = "",
            position = 72,
            section = ignoreSection
    )
    default boolean ignoreBlood() {
        return false;
    }

    @ConfigSection(
            name = "Game Tick Configuration",
            description = "Configure how to handles game tick delays, 1 game tick equates to roughly 600ms",
            position = 100,
            closedByDefault = true
    )
    String delayTickConfig = "delayTickConfig";

    @Range(
            max = 10
    )
    @ConfigItem(
            keyName = "tickDelayMin",
            name = "Game Tick Min",
            description = "",
            position = 101,
            section = delayTickConfig
    )
    default int tickDelayMin() {
        return 1;
    }

    @Range(
            max = 10
    )
    @ConfigItem(
            keyName = "tickDelayMax",
            name = "Game Tick Max",
            description = "",
            position = 102,
            section = delayTickConfig
    )
    default int tickDelayMax() {
        return 3;
    }

    @ConfigItem(
            keyName = "tickDelayEnabled",
            name = "Tick delay",
            description = "enables some tick delays",
            position = 103,
            section = delayTickConfig
    )
    default boolean tickDelay() {
        return false;
    }
}

