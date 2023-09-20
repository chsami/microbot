package net.runelite.client.plugins.ogPlugins.ogRunecrafting;

import net.runelite.client.config.*;
import net.runelite.client.plugins.ogPlugins.ogRunecrafting.enums.Alter;
import net.runelite.client.plugins.ogPlugins.ogRunecrafting.enums.Banks;
import net.runelite.client.plugins.ogPlugins.ogRunecrafting.enums.Runes;

@ConfigGroup("ogrunecrafting")
public interface ogRunecraftingConfig extends Config {
    @ConfigSection(
            name = "Method",
            description = "Method",
            position = 0,
            closedByDefault = false
    )
    String runeSelectionSection = "runeSelection";
    @ConfigSection(
            name = "Method Settings",
            description = "Method Settings",
            position = 1,
            closedByDefault = false
    )
    String methodSettingsSection = "methodSettings";
    @ConfigSection(
            name = "Item Settings",
            description = "Item Settings",
            position = 2,
            closedByDefault = false
    )
    String itemSettings = "itemSettings";
    @ConfigSection(
            name = "Debug Settings",
            description = "Debug Settings",
            position = 3,
            closedByDefault = false
    )
    String debugSettings = "debugSettings";



    //Methods
    @ConfigItem(
            position = 0,
            keyName = "Rune",
            name = "Rune",
            description = "Please select the rune you wish to make",
            section = runeSelectionSection
    ) default Runes selectRuneToMake() {return Runes.LAVA_RUNE;}




    //Method Settings
    @ConfigItem(
            position = 0,
            keyName = "Alter",
            name = "Alter",
            description = "Select the alter you would like to use",
            section = methodSettingsSection
    ) default Alter selectAlter() {return Alter.FIRE_ALTER;}

    @ConfigItem(
            position = 1,
            keyName = "Bank",
            name = "Bank",
            description = "Select bank you would like to use",
            section = methodSettingsSection
    ) default Banks selectBank() {return Banks.CRAFTING_GUILD;}



    //Item Settings
    @ConfigItem(
            position = 0,
            keyName = "Use Stamina Potions?",
            name = "Use Stamina Potions?",
            description = "Use Stamina Potions?",
            section = itemSettings
    ) default boolean useStaminas() {return true;}

    @Range(
            min = 0,
            max = 99
    )
    @Units(Units.PERCENT)
    @ConfigItem(
            position = 1,
            keyName = "Min Stamina Energy?",
            name = "Min Stamina Energy?",
            description = "Min Stamina Energy?",
            section = itemSettings
    ) default int getMinStaminaEnergy() {return 60;}

    @ConfigItem(
            position = 2,
            keyName = "Keep Stamina Active?",
            name = "Keep Stamina Active?",
            description = "Keep Stamina Active?",
            section = itemSettings
    ) default boolean keepStaminaActive() {return true;}

    @ConfigItem(
            position = 3,
            keyName = "Small pouch",
            name = "Small pouch",
            description = "Small pouch",
            section = itemSettings
    ) default boolean useSmallPouch() {return true;}

    @ConfigItem(
            position = 4,
            keyName = "Medium pouch",
            name = "Medium pouch",
            description = "Medium pouch",
            section = itemSettings
    ) default boolean useMediumPouch() {return true;}

    @ConfigItem(
            position = 5,
            keyName = "Large pouch",
            name = "Large pouch",
            description = "Large pouch",
            section = itemSettings
    ) default boolean useLargePouch() {return true;}

    @ConfigItem(
            position = 6,
            keyName = "Giant pouch",
            name = "Giant pouch",
            description = "Giant pouch",
            section = itemSettings
    ) default boolean useGiantPouch() {return true;}

    @ConfigItem(
            position = 7,
            keyName = "Colossal pouch",
            name = "Colossal pouch",
            description = "Colossal pouch",
            section = itemSettings
    ) default boolean useColossalPouch() {return false;}

    @ConfigItem(
            position = 0,
            keyName = "Verbose Logging",
            name = "Verbose Logging",
            description = "Verbose Logging",
            section = debugSettings
    ) default boolean getVerboseLogging() {return true;}





}
