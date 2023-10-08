package net.runelite.client.plugins.griffinplugins.griffintrainer;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup(GriffinTrainerPlugin.CONFIG_GROUP)
public interface GriffinTrainerConfig extends Config {
    @ConfigSection(
            name = "General",
            description = "General",
            position = 0,
            closedByDefault = false
    )
    String generalSection = "general";

    @ConfigItem(
            keyName = "worldNumber",
            name = "World Number",
            description = "World Number",
            position = 0,
            section = generalSection
    )
    default int worldNumber() {
        return 316;
    }

    @ConfigItem(
            keyName = "minTotalTime",
            name = "Min Total Time (m)",
            description = "Min Total Time (m)",
            position = 1,
            section = generalSection
    )
    default int minTotalTime() {
        return 60;
    }

    @ConfigItem(
            keyName = "maxTotalTime",
            name = "Max Total Time (m)",
            description = "Max Total Time (m)",
            position = 2,
            section = generalSection
    )
    default int maxTotalTime() {
        return 90;
    }

    @ConfigItem(
            keyName = "minTaskTime",
            name = "Min Task Time (m)",
            description = "Min Task Time (m)",
            position = 3,
            section = generalSection
    )
    default int minTaskTime() {
        return 20;
    }

    @ConfigItem(
            keyName = "maxTaskTime",
            name = "Max Task Time (m)",
            description = "Max Task Time (m)",
            position = 4,
            section = generalSection
    )
    default int maxTaskTime() {
        return 30;
    }

    @ConfigItem(
            keyName = "equipGear",
            name = "Equip Initial Gear",
            description = "Equip Initial Gear",
            position = 5,
            section = generalSection
    )
    default boolean equipGear() {
        return true;
    }

    @ConfigSection(
            name = "Task Settings",
            description = "Task Settings",
            position = 1,
            closedByDefault = false
    )
    String tasksSection = "tasks";

    @ConfigItem(
            keyName = "trainCombat",
            name = "Train Combat",
            description = "Train Combat",
            position = 0,
            section = tasksSection
    )
    default boolean trainCombat() {
        return true;
    }

    @ConfigItem(
            keyName = "trainMining",
            name = "Train Mining",
            description = "Train Mining",
            position = 1,
            section = tasksSection
    )
    default boolean trainMining() {
        return true;
    }

    @ConfigItem(
            keyName = "trainFishing",
            name = "Train Fishing",
            description = "Train Fishing",
            position = 2,
            section = tasksSection
    )
    default boolean trainFishing() {
        return true;
    }

    @ConfigItem(
            keyName = "trainWoodcutting",
            name = "Train Woodcutting",
            description = "Train Woodcutting",
            position = 3,
            section = tasksSection
    )
    default boolean trainWoodcutting() {
        return true;
    }

    @ConfigSection(
            name = "Skill Levels",
            description = "Skill Levels",
            position = 2,
            closedByDefault = false
    )
    String skillsSection = "skills";

    @ConfigItem(
            keyName = "attackLevel",
            name = "Attack",
            description = " Attack Level",
            position = 0,
            section = skillsSection
    )
    default int attackLevel() {
        return 0;
    }

    @ConfigItem(
            keyName = "strengthLevel",
            name = "Strength",
            description = " Strength Level",
            position = 1,
            section = skillsSection
    )
    default int strengthLevel() {
        return 0;
    }

    @ConfigItem(
            keyName = "defenceLevel",
            name = "Defence",
            description = " Defence Level",
            position = 2,
            section = skillsSection
    )
    default int defenceLevel() {
        return 0;
    }

    @ConfigItem(
            keyName = "prayerLevel",
            name = "Prayer",
            description = " Prayer Level",
            position = 3,
            section = skillsSection
    )
    default int prayerLevel() {
        return 0;
    }

    @ConfigItem(
            keyName = "miningLevel",
            name = "Mining",
            description = " Mining Level",
            position = 4,
            section = skillsSection
    )
    default int miningLevel() {
        return 0;
    }

    @ConfigItem(
            keyName = "fishingLevel",
            name = "Fishing",
            description = " Fishing Level",
            position = 5,
            section = skillsSection
    )
    default int fishingLevel() {
        return 0;
    }

    @ConfigItem(
            keyName = "woodcuttingLevel",
            name = "Woodcutting",
            description = " Woodcutting Level",
            position = 6,
            section = skillsSection
    )
    default int woodcuttingLevel() {
        return 0;
    }

    @ConfigSection(
            name = "Mining Settings",
            description = "Mining Settings",
            position = 3,
            closedByDefault = false
    )
    String miningSettingsSection = "miningSettingsSection";

    @ConfigItem(
            keyName = "keepOre",
            name = "Keep Ore",
            description = "Keep Ore",
            position = 1,
            section = miningSettingsSection
    )
    default boolean keepOre() {
        return true;
    }

//    @ConfigItem(
//            keyName = "hopWorlds",
//            name = "Hop Worlds",
//            description = "Hop Worlds",
//            position = 2,
//            section = miningSettingsSection
//    )
//    default boolean hopWorlds() {
//        return true;
//    }
//
//    @ConfigItem(
//            keyName = "maxPlayers",
//            name = "Max Players In Mining Area",
//            description = "Max Players In Mining Area",
//            position = 3,
//            section = miningSettingsSection
//    )
//    default int maxPlayers() {
//        return 2;
//    }
//
//    @ConfigItem(
//            keyName = "maxWorldsToTry",
//            name = "Max Worlds To Try",
//            description = "Max Worlds To Try",
//            position = 4,
//            section = miningSettingsSection
//    )
//    default int maxWorldsToTry() {
//        return 10;
//    }

    @ConfigSection(
            name = "Combat Settings",
            description = "Combat Settings",
            position = 4,
            closedByDefault = false
    )
    String combatSettingsSection = "combatSettingsSection";

    @ConfigItem(
            keyName = "collectItems",
            name = "Collect Items",
            description = "Collect Items",
            position = 0,
            section = combatSettingsSection
    )
    default boolean collectItems() {
        return true;
    }

    @ConfigSection(
            name = "Fishing Settings",
            description = "Fishing Settings",
            position = 5,
            closedByDefault = false
    )
    String fishingSettingsSection = "fishingSettings";

    @ConfigItem(
            keyName = "keepFish",
            name = "Keep Fish",
            description = "Keep Fish",
            position = 0,
            section = fishingSettingsSection
    )
    default boolean keepFish() {
        return true;
    }

    @ConfigSection(
            name = "Woodcutting Settings",
            description = "Woodcutting Settings",
            position = 6,
            closedByDefault = false
    )
    String woodcuttingSettingsSection = "woodcuttingSettings";

    @ConfigItem(
            keyName = "keepLogs",
            name = "Keep Logs",
            description = "Keep Logs",
            position = 0,
            section = woodcuttingSettingsSection
    )
    default boolean keepLogs() {
        return true;
    }

}
