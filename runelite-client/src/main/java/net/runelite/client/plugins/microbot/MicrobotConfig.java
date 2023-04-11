package net.runelite.client.plugins.microbot;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("Microbot")
public interface MicrobotConfig extends Config {
    @ConfigSection(
            name = "General",
            description = "The general settings for the bot",
            position = 0
    )
    String general = "General";
    @ConfigSection(
            name = "Combat",
            description = "The combat settings for the bot",
            position = 1
    )
    String combat = "Combat";
    @ConfigSection(
            name = "Minigames",
            description = "The minigames settings for the bot",
            position = 2
    )
    String minigames = "Minigames";
    @ConfigSection(
            name = "Skills",
            description = "The skills settings for the bot",
            position = 3
    )
    String skills = "Skills";
    @ConfigSection(
            name = "Streaming",
            description = "The streaming settings for the bot",
            position = 4
    )
    String streaming = "Streaming";




    //END SECTIONS


    @ConfigItem(
            keyName = "Cannon",
            name = "Cannon",
            description = "Automatically reloads cannon",
            position = 0,
            section = combat
    )
    default boolean toggleCannon()
    {
        return false;
    }
    @ConfigItem(
            keyName = "Combat",
            name = "Combat",
            description = "Attacks npc",
            position = 1,
            section = combat
    )
    default boolean toggleCombat()
    {
        return false;
    }
    @ConfigItem(
            keyName = "Attackable Npcs",
            name = "Attackable Npcs",
            description = "List of attackable npcs",
            position = 2,
            section = combat
    )
    default String combatNpcList()
    {
        return "";
    }
    @ConfigItem(
            keyName = "Food",
            name = "Food",
            description = "Automatically eats food",
            position = 3,
            section = combat
    )
    default boolean toggleFood()
    {
        return false;
    }
    @ConfigItem(
            keyName = "Prayer",
            name = "Prayer",
            description = "Automatically drinks prayer potions",
            position = 4,
            section = combat
    )
    default boolean togglePrayer()
    {
        return false;
    }
    @ConfigItem(
            keyName = "Combat potion",
            name = "Combat potion",
            description = "Automatically drinks combat potions",
            position = 5,
            section = combat
    )
    default boolean toggleCombatPotion()
    {
        return false;
    }
    @ConfigItem(
            keyName = "Mouse",
            name = "Use hardware mouse",
            description = "Enable this to use the hardware mouse (make sure to turn on/off the microbot plugin)",
            position = 1,
            section = general
    )
    default boolean toggleHardwareMouse()
    {
        return false;
    }
    @ConfigItem(
            keyName = "Loot items",
            name = "Loot items",
            description = "Enable/disable loot items",
            position = 2,
            section = general
    )
    default boolean isLootItems()
    {
        return true;
    }
    @ConfigItem(
            keyName = "Items to loot",
            name = "Items to loot",
            description = "Items to loot comma seperated",
            position = 3,
            section = general
    )
    default String itemsToLoot()
    {
        return "";
    }
    @ConfigItem(
            keyName = "HouseTabs",
            name = "HouseTab Bot",
            description = "Creates house tablet teleports. This script requires 40 magic and 30 atk",
            position = 1
    )
    default boolean HouseTabBotToggle()
    {
        return false;
    }

    @ConfigItem(
            keyName = "Bolt enchanting",
            name = "Bolt Enchanter Bot",
            description = "Enchants any bolts for magic experience",
            position = 2
    )
    default boolean BoltEnchanterBotToggle()
    {
        return false;
    }
    @ConfigItem(
            keyName = "Highalching",
            name = "High alcher",
            description = "High alch items",
            position = 3
    )
    default boolean HighAlcherBotToggle()
    {
        return false;
    }
    @ConfigItem(
            keyName = "Fletcher",
            name = "Fletcher",
            description = "Traing fletching",
            position = 4
    )
    default boolean toggleFletcher()
    {
        return false;
    }
    @ConfigItem(
            keyName = "Bowstrings",
            name = "Bowstrings",
            description = "Traing Bowstrings",
            position = 4,
            section = skills
    )
    default boolean toggleBowstrings()
    {
        return false;
    }
    @ConfigItem(
            keyName = "Primary fletching item",
            name = "Primary fletching item",
            description = "Primary item eg: knife or bowstring",
            position = 5,
            section = skills
    )
    default String PrimaryFletchItem()
    {
        return "knife";
    }
    @ConfigItem(
            keyName = "Logs to fletch",
            name = "Logs to fletch",
            description = "Logs to fletch",
            position = 6,
            section = skills
    )
    default String logsToFletch()
    {
        return "Yew logs";
    }
    @ConfigItem(
            keyName = "Construction",
            name = "Construction",
            description = "Train construction",
            position = 0,
            section = skills
    )
    default boolean toggleConstruction()
    {
        return false;
    }
    @ConfigItem(
            keyName = "Jad",
            name = "Jad",
            description = "Automatically pray flicks Jad",
            position = 0,
            section = minigames
    )
    default boolean toggleJad()
    {
        return false;
    }
    @ConfigItem(
            keyName = "Hide username",
            name = "Hide username",
            description = "Hides your ingame username",
            position = 0,
            section = streaming
    )
    default boolean toggleHideUserName()
    {
        return false;
    }
}
