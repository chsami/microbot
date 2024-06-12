package net.runelite.client.plugins.hoseaplugins.RooftopAgility;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Keybind;
import net.runelite.client.config.Range;

@ConfigGroup("RooftopAgility")
public interface RooftopAgilityConfig extends Config {

    @ConfigItem(
            keyName = "agilityToggle",
            name = "Toggle",
            description = "",
            position = 0
    )
    default Keybind agilityToggle() {
        return Keybind.NOT_SET;
    }

    @ConfigSection(
            name = "Game Tick Configuration",
            description = "Configure how the bot handles game tick delays, 1 game tick equates to roughly 600ms",
            position = 1
    )
    String delayTickConfig = "delayTickConfig";

    @Range(
            max = 10
    )
    @ConfigItem(
            keyName = "tickDelayMin",
            name = "Game Tick Min",
            description = "",
            position = 2,
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
            position = 3,
            section = delayTickConfig
    )
    default int tickDelayMax() {
        return 3;
    }

    @ConfigItem(
            keyName = "tickDelayEnabled",
            name = "Tick delay",
            description = "enables some tick delays",
            position = 4,
            section = delayTickConfig
    )
    default boolean tickDelay() {
        return false;
    }

    @ConfigSection(
            name = "Agility Configuration",
            description = "",
            position = 5
    )
    String agilitySection = "agilityTitle";

    @ConfigItem(
            keyName = "Course",
            name = "Course",
            description = "Supported agility courses",
            position = 6,
            section = agilitySection
    )
    default Course course() {
        return Course.GNOME;
    }

    @ConfigItem(
            keyName = "foodName",
            name = "Food Name",
            description = "Name of food to eat (i.e Cake)",
            position = 7,
            section = agilitySection
    )
    default String foodName() {
        return "";
    }

    @ConfigItem(
            keyName = "camelotTeleport",
            name = "Use Camelot Teleport",
            description = "Use Camelot Teleport if you have hard diaries completed. Requires Air Runes or (Air Staff equipped) and Law Runes in inventory",
            position = 9,
            section = agilitySection
    )
    default boolean camelotTeleport() {
        return false;
    }

    @ConfigItem(
            keyName = "mogPickup",
            name = "Pick up Mark of Grace",
            description = "Enable to pick up Marks of Grace",
            position = 10,
            section = agilitySection
    )
    default boolean mogPickup() {
        return true;
    }

    @ConfigItem(
            keyName = "mogStack",
            name = "Ardougne marks stack",
            description = "The number of marks of grace to be stacked before it is picked up at Ardougne.",
            position = 11,
            section = agilitySection
    )
    default int mogStack() {
        return 0;
    }

    @ConfigItem(
            keyName = "lowHP",
            name = "Stop/Eat at HP",
            description = "Stop/Eat if HP goes below given threshold",
            position = 12,
            section = agilitySection
    )
    default int lowHP() {
        return 5;
    }

    @ConfigItem(
            keyName = "boostWithPie",
            name = "Enable Summer Pies",
            description = "Enable using Summer Pies",
            position = 13,
            section = agilitySection
    )
    default boolean boostWithPie() {
        return false;
    }

    @ConfigItem(
            keyName = "pieLevel",
            name = "Min boost level",
            description = "A Summer Pie will be used whenever your Agility drops below this level",
            position = 14,
            section = agilitySection
    )
    default int pieLevel() {
        return 80;
    }

    @ConfigItem(
            keyName = "keepGoing",
            name = "Keep Running",
            description = "Keep running after out of Pies/Food in bank",
            position = 15,
            section = agilitySection
    )
    default boolean keepGoing() {
        return false;
    }

    @Range(
            min = 10,
            max = 100
    )

    @ConfigItem(
            keyName = "enableRun",
            name = "Enable Run",
            description = "Amount of run energy restored to run back on",
            position = 15,
            section = agilitySection
    )
    default int enableRun() {
        return 50;
    }

    @ConfigItem(
            keyName = "highAlch",
            name = "High Alch",
            description = "Items you want to high alch, supports wildcards '*'",
            position = 16,
            section = agilitySection
    )
    default String highAlch() {
        return "Rune *";
    }
}
