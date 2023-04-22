package net.runelite.client.plugins.microbot;

import net.runelite.client.config.*;
import net.runelite.client.plugins.microbot.util.walker.TileCounter;
import net.runelite.client.plugins.microbot.util.walker.TileStyle;

import java.awt.*;

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
            name = "Bosses",
            description = "The bosses settings for the bot",
            position = 4
    )
    String bosses = "Bosses";
    @ConfigSection(
            name = "Streaming",
            description = "The streaming settings for the bot",
            position = 5
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
    @ConfigItem(
            keyName = "Crafting",
            name = "Crafting",
            description = "Train crafting",
            position = 1,
            section = skills
    )
    default boolean toggleCrafting()
    {
        return false;
    }
    @ConfigItem(
            keyName = "Giants foundry",
            name = "Giants foundry",
            description = "Giants foundry minigame for smithing experience",
            position = 1,
            section = minigames
    )
    default boolean toggleGiantsFoundry()
    {
        return false;
    }
    @ConfigItem(
            keyName = "Tithe Farm",
            name = "Tithe Farm",
            description = "Tithe Farm minigame for farming experience",
            position = 2,
            section = minigames
    )
    default boolean toggleTitheFarming()
    {
        return false;
    }


    //SHORTEST PATH CONFIGS
    @ConfigSection(
            name = "Settings",
            description = "Options for the pathfinding",
            position = 100,
            closedByDefault = true
    )
    String sectionSettings = "sectionSettings";

    @ConfigItem(
            keyName = "avoidWilderness",
            name = "Avoid wilderness",
            description = "Whether the wilderness should be avoided if possible<br>" +
                    "(otherwise, will e.g. suggest using wilderness lever to travel from Edgeville to Ardougne)",
            position = 1,
            section = sectionSettings
    )
    default boolean avoidWilderness() {
        return true;
    }

    @ConfigItem(
            keyName = "useAgilityShortcuts",
            name = "Use agility shortcuts",
            description = "Whether to include agility shortcuts in the path. You must also have the required agility level",
            position = 2,
            section = sectionSettings
    )
    default boolean useAgilityShortcuts() {
        return true;
    }

    @ConfigItem(
            keyName = "useGrappleShortcuts",
            name = "Use grapple shortcuts",
            description = "Whether to include crossbow grapple agility shortcuts in the path.<br>" +
                    "You must also have the required agility, ranged and strength levels",
            position = 3,
            section = sectionSettings
    )
    default boolean useGrappleShortcuts() {
        return false;
    }

    @ConfigItem(
            keyName = "useBoats",
            name = "Use boats",
            description = "Whether to include boats, canoes and charter ships in the path",
            position = 4,
            section = sectionSettings
    )
    default boolean useBoats() {
        return true;
    }

    @ConfigItem(
            keyName = "useFairyRings",
            name = "Use fairy rings",
            description = "Whether to include fairy rings in the path.<br>" +
                    "You must also have completed the required quest or miniquest",
            position = 5,
            section = sectionSettings
    )
    default boolean useFairyRings() {
        return false;
    }

    @ConfigItem(
            keyName = "useTeleports",
            name = "Use teleports",
            description = "Whether to include teleportation portals and levers in the path",
            position = 6,
            section = sectionSettings
    )
    default boolean useTeleports() {
        return false;
    }

    @ConfigItem(
            keyName = "cancelInstead",
            name = "Cancel instead of recalculating",
            description = "Whether the path should be cancelled rather than recalculated when the recalculate distance limit is exceeded",
            position = 7,
            section = sectionSettings
    )
    default boolean cancelInstead() {
        return false;
    }

    @Range(
            min = -1,
            max = 20000
    )
    @ConfigItem(
            keyName = "recalculateDistance",
            name = "Recalculate distance",
            description = "Distance from the path the player should be for it to be recalculated (-1 for never)",
            position = 8,
            section = sectionSettings
    )
    default int recalculateDistance() {
        return 10;
    }

    @Range(
            min = -1,
            max = 50
    )
    @ConfigItem(
            keyName = "finishDistance",
            name = "Finish distance",
            description = "Distance from the target tile at which the path should be ended (-1 for never)",
            position = 9,
            section = sectionSettings
    )
    default int reachedDistance() {
        return 5;
    }

    @ConfigItem(
            keyName = "showTileCounter",
            name = "Show tile counter",
            description = "Whether to display the number of tiles travelled, number of tiles remaining or disable counting",
            position = 10,
            section = sectionSettings
    )
    default TileCounter showTileCounter() {
        return TileCounter.DISABLED;
    }

    @ConfigItem(
            keyName = "tileCounterStep",
            name = "Tile counter step",
            description = "The number of tiles between the displayed tile counter numbers",
            position = 11,
            section = sectionSettings
    )
    default int tileCounterStep()
    {
        return 1;
    }

    @Units(
            value = Units.TICKS
    )
    @Range(
            min = 1,
            max = 30
    )
    @ConfigItem(
            keyName = "calculationCutoff",
            name = "Calculation cutoff",
            description = "The cutoff threshold in number of ticks (0.6 seconds) of no progress being<br>" +
                    "made towards the path target before the calculation will be stopped",
            position = 12,
            section = sectionSettings
    )
    default int calculationCutoff()
    {
        return 5;
    }

    @ConfigSection(
            name = "Display",
            description = "Options for displaying the path on the world map, minimap and scene tiles",
            position = 13,
            closedByDefault = true
    )
    String sectionDisplay = "sectionDisplay";

    @ConfigItem(
            keyName = "drawMap",
            name = "Draw path on world map",
            description = "Whether the path should be drawn on the world map",
            position = 14,
            section = sectionDisplay
    )
    default boolean drawMap() {
        return true;
    }

    @ConfigItem(
            keyName = "drawMinimap",
            name = "Draw path on minimap",
            description = "Whether the path should be drawn on the minimap",
            position = 15,
            section = sectionDisplay
    )
    default boolean drawMinimap() {
        return true;
    }

    @ConfigItem(
            keyName = "drawTiles",
            name = "Draw path on tiles",
            description = "Whether the path should be drawn on the game tiles",
            position = 16,
            section = sectionDisplay
    )
    default boolean drawTiles() {
        return true;
    }

    @ConfigItem(
            keyName = "drawTransports",
            name = "Draw transports",
            description = "Whether transports should be drawn",
            position = 17,
            section = sectionDisplay
    )
    default boolean drawTransports() {
        return false;
    }

    @ConfigItem(
            keyName = "drawCollisionMap",
            name = "Draw collision map",
            description = "Whether the collision map should be drawn",
            position = 18,
            section = sectionDisplay
    )
    default boolean drawCollisionMap() {
        return false;
    }

    @ConfigItem(
            keyName = "pathStyle",
            name = "Path style",
            description = "Whether to display the path as tiles or a segmented line",
            position = 19,
            section = sectionDisplay
    )
    default TileStyle pathStyle() {
        return TileStyle.TILES;
    }

    @ConfigSection(
            name = "Colours",
            description = "Colours for the path map, minimap and scene tiles",
            position = 20,
            closedByDefault = true
    )
    String sectionColours = "sectionColours";

    @Alpha
    @ConfigItem(
            keyName = "colourPath",
            name = "Path",
            description = "Colour of the path tiles on the world map, minimap and in the game scene",
            position = 21,
            section = sectionColours
    )
    default Color colourPath() {
        return new Color(255, 0, 0);
    }

    @Alpha
    @ConfigItem(
            keyName = "colourPathCalculating",
            name = "Calculating",
            description = "Colour of the path tiles while the pathfinding calculation is in progress",
            position = 22,
            section = sectionColours
    )
    default Color colourPathCalculating() {
        return new Color(0, 0, 255);
    }

    @Alpha
    @ConfigItem(
            keyName = "colourTransports",
            name = "Transports",
            description = "Colour of the transport tiles",
            position = 23,
            section = sectionColours
    )
    default Color colourTransports() {
        return new Color(0, 255, 0, 128);
    }

    @Alpha
    @ConfigItem(
            keyName = "colourCollisionMap",
            name = "Collision map",
            description = "Colour of the collision map tiles",
            position = 24,
            section = sectionColours
    )
    default Color colourCollisionMap() {
        return new Color(0, 128, 255, 128);
    }
    @ConfigItem(
            keyName = "Zulrah",
            name = "Zulrah",
            description = "Kills zulrah",
            position = 0,
            section = bosses
    )
    default boolean toggleZulrah() {
        return false;
    }
}
