package net.runelite.client.plugins.hoseaplugins.AutoCombat;


import net.runelite.client.config.*;

@ConfigGroup("AutoCombatConfig")
public interface AutoCombatConfig extends Config {
    @ConfigItem(
            keyName = "Toggle",
            name = "Toggle",
            description = "",
            position = -100
    )
    default Keybind toggle() {
        return Keybind.NOT_SET;
    }


    @ConfigSection(
            name = "Auto Combat Configuration",
            description = "Configure how to handles game tick delays, 1 game tick equates to roughly 600ms",
            position = 1,
            closedByDefault = false
    )
    String autoCombatConfig = "autoCombatConfig";

    @ConfigItem(
            keyName = "targetName",
            name = "Target names",
            description = "",
            position = -99,
            section = autoCombatConfig
    )
    default String targetNames() {
        return "Chicken,Goblin";
    }

    @ConfigItem(
            keyName = "useCombatPotion",
            name = "Combat potions?",
            description = "Uses regular or super combat potions",
            position = -10,
            section = autoCombatConfig
    )
    default boolean useCombatPotion() {
        return true;
    }

    @Range(
            min = 1,
            max = 99
    )
    @ConfigItem(
            keyName = "useCombatAt",
            name = "Use at",
            description = "What level to use combat potions at",
            position = -9,
            section = autoCombatConfig
    )

    default int useCombatPotAt() {
        return 80;
    }

    @ConfigItem(
            keyName = "useRangingPotion",
            name = "Ranging potions?",
            description = "Uses ranging potions",
            position = -8,
            section = autoCombatConfig
    )
    default boolean useRangingPotion() {
        return false;
    }

    @Range(
            min = 1,
            max = 99
    )
    @ConfigItem(
            keyName = "useRangingPotAt",
            name = "Use at",
            description = "What level to use ranging potions at",
            position = -7,
            section = autoCombatConfig
    )

    default int useRangingPotAt() {
        return 80;
    }

    @ConfigItem(
            keyName = "usePrayerPotion",
            name = "Prayer potions?",
            description = "Uses prayer potions",
            position = 4,
            section = autoCombatConfig
    )
    default boolean usePrayerPotion() {
        return true;
    }

    @Range(
            min = 1,
            max = 99
    )
    @ConfigItem(
            keyName = "usePrayerAt",
            name = "Use at",
            description = "What level to use prayer potions at, prayer or super restore",
            position = 5,
            section = autoCombatConfig
    )

    default int usePrayerPotAt() {
        return 20;
    }

    @Range(
            min = 2,
            max = 90
    )
    @ConfigItem(keyName = "eatAt",
            name = "Eat at",
            description = "What HP to eat at",
            position = 6,
            section = autoCombatConfig)
    default int eatAt() {
        return 50;
    }

    @ConfigItem(keyName = "shutdownOnTaskDone",
            name = "Stop when task done?",
            description = "Teleports away and stops. Untick if you have no task or want to stay",
            position = 8,
            section = autoCombatConfig)
    default boolean shutdownOnTaskDone() {
        return false;
    }

    @ConfigItem(keyName = "breakTab",
            name = "Break Tab on Task Done?",
            description = "Break any teleport tablet when task completes",
            position = 10,
            section = autoCombatConfig)
    default boolean breakTab() {
        return false;
    }

    @ConfigItem(keyName = "buryBones",
            name = "Bury bones/ashes",
            description = "Will bury ANY bone/ash in your inventory",
            position = 10,
            section = autoCombatConfig)
    default boolean buryBones() {
        return false;
    }

    @ConfigSection(
            name = "Looting Configuration",
            description = "Configure how to handle looting",
            position = 2,
            closedByDefault = false
    )
    String lootingConfig = "lootingConfig";

    @ConfigItem(
            keyName = "lootEnabled",
            name = "Loot?",
            description = "Loots items",
            position = 1,
            section = lootingConfig
    )
    default boolean lootEnabled() {
        return true;
    }

    @ConfigItem(
            keyName = "lootNames",
            name = "Loot names",
            description = "",
            position = 3,
            section = lootingConfig
    )
    default String lootNames() {
        return "Feather";
    }
}

