package net.runelite.client.plugins.ogPlugins.ogConstruction;

import net.runelite.client.config.*;
import net.runelite.client.plugins.ogPlugins.ogConstruction.enums.Butler;
import net.runelite.client.plugins.ogPlugins.ogConstruction.enums.Furniture;

@ConfigGroup("ogConst")
public interface ogConstConfig extends Config {

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
            name = "Instructions",
            description = "Instructions",
            position = 2,
            closedByDefault = false
    )
    String instructionSection = "instruction";

    @ConfigSection(
            name = "Furniture",
            description = "Furniture",
            position = 3,
            closedByDefault = false
    )
    String furnitureSection = "furniture";

    @ConfigSection(
            name = "Servant",
            description = "Servant",
            position = 4,
            closedByDefault = false
    )
    String servantSection = "servant";

    @ConfigSection(
            name = "Settings",
            description = "Settings",
            position = 5,
            closedByDefault = false
    )
    String settingSection = "settings";


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

    //Instructions
    @ConfigItem(
            keyName = "Instructions",
            name = "Instructions",
            description = "Instructions",
            position = 0,
            section = instructionSection
    )
    default String directions() {return "Please have in inventory:\n-Saw\n-Hammer\n-Coins\n-Noted planks needed\n-Mythic Cape(if doing mythic mount)\nRecommend you place the room\\dungeon you are working in next to the butler's moneybag room";}


    //Furniture Section
    @ConfigItem(
            keyName = "Furniture",
            name = "Furniture",
            description = "Choose the furniture you would like to make?",
            position = 1,
            section = furnitureSection
    )
    default Furniture selectedFurniture() {return Furniture.OAK_LARDER;}



    //Butler Section
    @ConfigItem(
            keyName = "Servant",
            name = "Servant",
            description = "Choose the servant you would like to use?",
            position = 1,
            section = servantSection
    )
    default Butler selectedButler() {return Butler.DEMON_BUTLER;}
    @ConfigItem(
            keyName = "Servant's Moneybag",
            name = "Servant's Moneybag",
            description = "Use Servant's Moneybag?",
            position = 2,
            section = servantSection
    )
    default boolean useMoneyBag() {return true;}
    @ConfigItem(
            keyName = "Moneybag Refill Threshold",
            name = "Moneybag Refill Threshold",
            description = "How low would you like the moneybag to go before refilling?",
            position = 3,
            section = servantSection
    )
    @Range(
            min = 100000,
            max = 2500000
    )
    default int getMinMoneybagAmount() {return 1500000;}

    //Settings
    @ConfigItem(
            keyName = "Verbose Logging?",
            name = "Verbose Logging?",
            description = "Verbose Logging?",
            position = 1,
            section = settingSection
    )
    default boolean verboseLogging() {return false;}



}
