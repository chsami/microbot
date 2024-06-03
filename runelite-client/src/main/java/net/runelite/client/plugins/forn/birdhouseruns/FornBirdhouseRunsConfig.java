package net.runelite.client.plugins.forn.birdhouseruns;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("FornBirdhouseRuns")
public interface FornBirdhouseRunsConfig extends Config {
    @ConfigSection(
        name = "Guide",
        description = "Guide",
        position = 1
    )
    String guideSection = "Guide";

    @ConfigItem(
        keyName = "guide",
        name = "How to use",
        description = "How to use this plugin",
        position = 1,
        section = guideSection
    )
    default String GUIDE() {
        return "Start next to a bank\n" +
            "Have the following in your bank:\n" +
            "1. Digsite pendant\n" +
            "2. Runes for Varrock tele if teleporting \n\n" +
            "Equip graceful option will bank equipped \n" +
            "Otherwise will use current equipped gear except amulet \n" +
            "Have to turn off manually after run";
    }

    @ConfigSection(
        name = "Settings",
        description = "Settings",
        position = 2
    )
    String settingsSection = "Settings";

//    For debugging
//    @ConfigItem(
//        keyName = "step",
//        name = "Step to start on",
//        description = "Step to start on",
//        position = 1,
//        section = settingsSection
//    )
//    default states STEP() {
//        return states.GEARING;
//    }

    @ConfigItem(
        keyName = "seed",
        name = "Seeds to use",
        description = "What seed to use in birdhouse",
        position = 2,
        section = settingsSection
    )
    default FornBirdhouseRunsInfo.seedTypes SEED() {
        return FornBirdhouseRunsInfo.seedTypes.POTATO_SEED;
    }

    @ConfigItem(
        keyName = "logs",
        name = "Logs to use",
        description = "What logs to use for birdhouse",
        position = 3,
        section = settingsSection
    )
    default FornBirdhouseRunsInfo.logTypes LOG() {
        return FornBirdhouseRunsInfo.logTypes.MAHOGANY_LOGS;
    }

    @ConfigItem(
        keyName = "graceful",
        name = "Equip graceful?",
        description = "Should graceful be equipped from bank?",
        position = 4,
        section = settingsSection
    )
    default boolean GRACEFUL() {
        return false;
    }

    @ConfigItem(
        keyName = "teleport",
        name = "Teleport at end?",
        description = "Should it teleport at the end of the run?",
        position = 5,
        section = settingsSection
    )
    default boolean TELEPORT() {
        return false;
    }

}
