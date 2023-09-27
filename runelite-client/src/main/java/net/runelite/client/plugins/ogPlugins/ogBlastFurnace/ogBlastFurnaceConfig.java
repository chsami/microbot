package net.runelite.client.plugins.ogPlugins.ogBlastFurnace;

import net.runelite.client.config.*;
import net.runelite.client.plugins.ogPlugins.ogBlastFurnace.enums.Bars;

@ConfigGroup("ogBlastFurnace")
public interface ogBlastFurnaceConfig extends Config {

    @ConfigSection(
            name ="Delays",
            description = "Delays when doing actions",
            position = 0,
            closedByDefault = false

    )
    String delaySection = "delays";
    @ConfigSection(
            name ="AFK",
            description = "AFK Settings",
            position = 1,
            closedByDefault = false

    )
    String afkSection = "afk";
    @ConfigSection(
            name = "Stamina Potion",
            description = "Stamina Potion Settings",
            position = 2,
            closedByDefault = false
    )
    String staminaPotionSection = "stamina";

    @ConfigSection(
            name = "Blast Furnace Settings",
            description = "Blast Furnace Settings",
            position = 3,
            closedByDefault = false
    )
    String bFSettingsSection = "bFSettings";



    //Delays
    @ConfigItem(
            keyName = "Delay Min",
            name = "Delay Min",
            description = "Sets minimal delay",
            position = 0,
            section = delaySection
    )
    @Range(
            min = 30,
            max = 90
    )
    @Units(Units.MILLISECONDS)
    default int delayMin() {return 50;}

    @ConfigItem(
            keyName = "Delay Max",
            name = "Delay Max",
            description = "Sets maximum delay",
            position = 1,
            section = delaySection
    )
    @Range(
            min = 100,
            max = 200
    )
    @Units(Units.MILLISECONDS)
    default int delayMax() {return 150;}
    @ConfigItem(
            keyName = "Delay Target",
            name = "Delay Target",
            description = "Preferred delay time",
            position = 2,
            section = delaySection
    )
    @Range(
            min = 30,
            max = 200
    )
    @Units(Units.MILLISECONDS)
    default int delayTarget() {return 90;}

    //AFK
    @ConfigItem(
            keyName = "AFK Directions",
            name = "AFK Directions",
            description = "Directions",
            position = 0,
            section = afkSection
    )
    default String directions() {return "Please set the Runelite Logout Timer plugin to 25 minutes.\nOr at-least greater than your AFK Max";}
        @ConfigItem(
            keyName = "AFK Max",
            name = "AFK Max",
            description = "Sets Maximum AFK",
            position = 1,
            section = afkSection
    )
    @Range(
            min = 0,
            max = 24
    )
    @Units(Units.MINUTES)
    default int afkMax() {return 4;}
    @ConfigItem(
            keyName = "AFK Target",
            name = "AFK Target",
            description = "Preferred AFK time",
            position = 2,
            section = afkSection
    )
    @Range(
            min = 0,
            max = 24
    )
    @Units(Units.MINUTES)
    default int afkTarget() {return 3;}

    //Stamina
    @ConfigItem(
            keyName = "Stamina Potion",
            name = "Use Stamina Potion?",
            description = "Use Stamina Potion?",
            position = 0,
            section = staminaPotionSection
    )
    default boolean useStamina() {return true;}
    @ConfigItem(
            keyName = "Keep Stamina Potion Active?",
            name = "Keep Stamina Potion Active?",
            description = "Keep Stamina Potion Active?",
            position = 1,
            section = staminaPotionSection
    )
    default boolean keepStaminaActive() {return true;}
    @ConfigItem(
            keyName = "Min Run Energy",
            name = "Min Run Energy",
            description = "Min Run Energy",
            position = 2,
            section = staminaPotionSection
    )
    @Range(
            min = 0,
            max = 98
    )
    default int staminaMin() {return 50;}

    @ConfigItem(
            keyName = "Max Run Energy",
            name = "Max Run Energy",
            description = "Max Run Energy",
            position = 3,
            section = staminaPotionSection
    )
    @Range(
            min = 1,
            max = 99
    )
    default int staminaMax() {return 150;}

    //Blast Furnace Settings
    @ConfigItem(
            keyName = "BF Directions",
            name = "BF Directions",
            description = "Directions",
            position = 0,
            section = bFSettingsSection
    )
    default String directionsBF() {return "Requires Ice Gloves\nRequires Goldsmith Gauntlets if doing Gold Bars ";}
    @ConfigItem(
            keyName = "Bars",
            name = "Bars",
            description = "Bars",
            position = 1,
            section = bFSettingsSection
    )
    default Bars getBars() {return Bars.GOLD_BAR;}
    @ConfigItem(
            keyName = "Talk to Foreman?",
            name = "Talk to Foreman?",
            description = "Talk to Foreman?",
            position = 2,
            section = bFSettingsSection
    )
    default boolean talkToForeman() {return false;}
    @ConfigItem(
            keyName = "Use Coal Bag?",
            name = "Use Coal Bag?",
            description = "Use Coal Bag?",
            position = 3,
            section = bFSettingsSection
    )
    default boolean useCoalBag() {return true;}
    @ConfigItem(
            keyName = "Refill Coffer?",
            name = "Refill Coffer?",
            description = "Refill Coffer?",
            position = 4,
            section = bFSettingsSection
    )
    default boolean getRefill() {return true;}
    @ConfigItem(
            keyName = "Coffer Refill Amount",
            name = "Coffer Refill Amount",
            description = "Coffer Refill Amount",
            position = 5,
            section = bFSettingsSection
    )
    @Range(min = 100000, max = 2147483647)
    default int getRefillAmount() {return 1000000;}


}
