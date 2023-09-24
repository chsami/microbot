package net.runelite.client.plugins.ogPlugins.ogPrayer;

import net.runelite.client.config.*;
import net.runelite.client.plugins.ogPlugins.ogPrayer.enums.RestockMethod;
import net.runelite.client.plugins.ogPlugins.ogPrayer.enums.Locations;
import net.runelite.client.plugins.ogPlugins.ogPrayer.enums.Bones;

@ConfigGroup("ogChaosAlter")
public interface ogPrayerConfig extends Config {

    @ConfigSection(
            name = "Delays",
            description = "Delays",
            position = 0,
            closedByDefault = true
    )
    String delaySection = "delays";
    @ConfigSection(
            name = "AFK",
            description = "AFK",
            position = 1,
            closedByDefault = true
    )
    String afkSection = "afk";
    @ConfigSection(
            name = "Altar",
            description = "Altar",
            position = 2,
            closedByDefault = false
    )
    String alterSection = "altar";
    @ConfigSection(
            name = "Settings",
            description = "Settings",
            position = 3,
            closedByDefault = false
    )
    String settingSection = "setting";



    //Delays
    @ConfigItem(
            keyName = "Delay Min",
            name = "Delay Min",
            description = "Sets minimal delay",
            position = 0,
            section = delaySection
    )
    @Range(
            min = 40,
            max = 70
    )
    @Units(Units.MILLISECONDS)
    default int delayMin() {return 40;}

    @ConfigItem(
            keyName = "Delay Max",
            name = "Delay Max",
            description = "Sets maximum delay",
            position = 1,
            section = delaySection
    )
    @Range(
            min = 80,
            max = 130
    )
    @Units(Units.MILLISECONDS)
    default int delayMax() {return 80;}
    @ConfigItem(
            keyName = "Delay Deviation Chance",
            name = "Delay Deviation Chance",
            description = "The chance for it to go out of your provided ranges",
            position = 2,
            section = delaySection
    )
    @Range(
            min = 30,
            max = 50
    )
    default int delayChance() {return 30;}




    //AFK
    @ConfigItem(
            keyName = "AFK Min",
            name = "AFK Min",
            description = "Sets minimal afk",
            position = 0,
            section = afkSection
    )
    @Range(
            min = 0,
            max = 2
    )
    @Units(Units.MINUTES)
    default int afkMin() {return 0;}

    @ConfigItem(
            keyName = "AFK Max",
            name = "AFK Max",
            description = "Sets maximum afk",
            position = 1,
            section = afkSection
    )
    @Range(
            min = 3,
            max = 5
    )
    @Units(Units.MINUTES)
    default int afkMax() {return 3;}
    @ConfigItem(
            keyName = "AFK Chance",
            name = "AFK Chance",
            description = "How often do you want AFKs to happen? 1000 = Almost No AFK",
            position = 2,
            section = afkSection
    )
    @Range(
            min = 250,
            max = 1000
    )
    default int afkChance() {return 500;}




    //Altar
    @ConfigItem(
            keyName = "Instructions",
            name = "Instructions",
            description = "Instructions",
            position = 0,
            section = alterSection
    )
    default String basicInstructions() {
        return "Currently Only Un-Note is supported" +
                "\nPersonal House not supported atm" +
                "\nBank pin not supported atm" +
                "\nIf using Others POH start in Rimmington" +
                "\nAdd your preffered POHs and look out for luring POHs, add those to ignore if found\n" +
                "\nIf using Chaos Alter start there" +
                "\nAdd a few worlds you would like to hop between" +
                "\nAt least 3 are required. Recommend 7-10\n" +
                "\nHave coins and desired noted bones in Rs2Inventory" +
                "\nIf you would like something added please lmk"
                ;}
    @ConfigItem(
            keyName = "Location",
            name = "Location",
            description = "Choose the location you would like to use.",
            position = 1,
            section = alterSection
    )
    default Locations selectedLocation() {return Locations.CHAOS_ALTAR;}
    @ConfigItem(
            keyName = "Restock",
            name = "Restock",
            description = "Choose where you would like to restock?",
            position = 2,
            section = alterSection
    )
    default RestockMethod selectedRestockMethod() {return RestockMethod.ELDER_CHAOS_DRUID;}
    @ConfigItem(
            keyName = "Bones",
            name = "Bones",
            description = "Choose the bones you would like to sacrifice?",
            position = 3,
            section = alterSection
    )
    default Bones selectedBones() {return Bones.Superior_dragon_bones;}
    @ConfigItem(
            keyName = "One Tick",
            name = "One Tick",
            description = "Would you like to 1 tick?",
            position = 4,
            section = alterSection
    )
    default boolean selectedTickOption() {return true;}
    @ConfigItem(
            keyName = "Use Personal POH?",
            name = "Use Personal POH?",
            description = "Would you like to use your POH alter?",
            position = 5,
            section = alterSection
    )
    default boolean usePersonalPOH() {return false;}
    @ConfigItem(
            keyName = "Preferred POH?",
            name = "Preferred POH?",
            description = "If you have a preferred POH enter it here, else leave blank",
            position = 6,
            section = alterSection
    )
    default String preferredPOH() {return "";}
    @ConfigItem(
            keyName = "POHs to Ignore?",
            name = "POHs to Ignore?",
            description = "Houses to stay away from. Separate names with a ','",
            position = 7,
            section = alterSection
    )
    default String bannedPOHs() {return "";}



    //Settings
    //TODO add hidden string functionality
    @ConfigItem(
            keyName = "Bank Pin",
            name = "Bank Pin",
            description = "Enter your bank pin if you want the script to use it",
            position = 0,
            section = settingSection
    )
    default String getBankPin() {return "";}
    @ConfigItem(
            keyName = "Worlds?",
            name = "Worlds?",
            description = "Enter a list of worlds you want to hop to.",
            position = 1,
            section = settingSection
    )
    default String worldHopList() {return "";}
    @ConfigItem(
            keyName = "Verbose Logging?",
            name = "Verbose Logging?",
            description = "Verbose Logging?",
            position = 2,
            section = settingSection
    )
    default boolean verboseLogging() {return false;}
}
