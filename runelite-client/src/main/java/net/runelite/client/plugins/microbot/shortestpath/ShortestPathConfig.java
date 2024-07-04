package net.runelite.client.plugins.microbot.shortestpath;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup(ShortestPathPlugin.CONFIG_GROUP)
public interface ShortestPathConfig extends Config {
    @ConfigSection(
        name = "Settings",
        description = "Options for the pathfinding",
        position = 0
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
        description = "Whether to include agility shortcuts in the path.<br>You must also have the required agility level",
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
        description = "Whether to include small boats in the path<br>(e.g. the boat to Fishing Platform)",
        position = 4,
        section = sectionSettings
    )
    default boolean useBoats() {
        return true;
    }

    @ConfigItem(
        keyName = "useCanoes",
        name = "Use canoes",
        description = "Whether to include canoes in the path",
        position = 5,
        section = sectionSettings
    )
    default boolean useCanoes() {
        return false;
    }

    @ConfigItem(
        keyName = "useCharterShips",
        name = "Use charter ships",
        description = "Whether to include charter ships in the path",
        position = 6,
        section = sectionSettings
    )
    default boolean useCharterShips() {
        return false;
    }

    @ConfigItem(
        keyName = "useShips",
        name = "Use ships",
        description = "Whether to include passenger ships in the path<br>(e.g. the customs ships to Karamja)",
        position = 7,
        section = sectionSettings
    )
    default boolean useShips() {
        return true;
    }

    @ConfigItem(
        keyName = "useFairyRings",
        name = "Use fairy rings",
        description = "Whether to include fairy rings in the path.<br>" +
            "You must also have completed the required quests or miniquests",
        position = 8,
        section = sectionSettings
    )
    default boolean useFairyRings() {
        return true;
    }

    @ConfigItem(
        keyName = "useGnomeGliders",
        name = "Use gnome gliders",
        description = "Whether to include gnome gliders in the path",
        position = 9,
        section = sectionSettings
    )
    default boolean useGnomeGliders() {
        return true;
    }

    @ConfigItem(
        keyName = "useSpiritTrees",
        name = "Use spirit trees",
        description = "Whether to include spirit trees in the path",
        position = 10,
        section = sectionSettings
    )
    default boolean useSpiritTrees() {
        return true;
    }

    @ConfigItem(
        keyName = "useTeleportationLevers",
        name = "Use teleportation levers",
        description = "Whether to include teleportation levers in the path<br>(e.g. the lever from Edgeville to Wilderness)",
        position = 11,
        section = sectionSettings
    )
    default boolean useTeleportationLevers() {
        return true;
    }

    @ConfigItem(
        keyName = "useTeleportationPortals",
        name = "Use teleportation portals",
        description = "Whether to include teleportation portals in the path<br>(e.g. the portal from Ferox Enclave to Castle Wars)",
        position = 12,
        section = sectionSettings
    )
    default boolean useTeleportationPortals() {
        return true;
    }

    @ConfigItem(
        keyName = "usePlayerItems",
        name = "Use items",
        description = "Whether to include items from the player's inventory and equipment.<br>Options labelled (perm) only use permanent items.",
        position = 13,
        section = sectionSettings
    )
    default PlayerItemTransportSetting playerItemTransportSetting() {
        return PlayerItemTransportSetting.InventoryNonConsumable;
    }

    @ConfigItem(
            keyName = "playerItemConsumableDistance",
            name = "Consumable item distance",
            description = "The distance consumable items add to the path. This sets a minimum distance for consumable items when pathing.",
            position = 14,
            section = sectionSettings
    )
    default int playerItemConsumableDistance() {
        return 200;
    }

    @ConfigItem(
        keyName = "cancelInstead",
        name = "Cancel instead of recalculating",
        description = "Whether the path should be cancelled rather than recalculated when the recalculate distance limit is exceeded",
        position = 15,
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
        position = 16,
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
        position = 17,
        section = sectionSettings
    )
    default int reachedDistance() {
        return 10;
    }

    @ConfigItem(
        keyName = "showTileCounter",
        name = "Show tile counter",
        description = "Whether to display the number of tiles travelled, number of tiles remaining or disable counting",
        position = 18,
        section = sectionSettings
    )
    default TileCounter showTileCounter() {
        return TileCounter.DISABLED;
    }

    @ConfigItem(
        keyName = "tileCounterStep",
        name = "Tile counter step",
        description = "The number of tiles between the displayed tile counter numbers",
        position = 19,
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
        position = 20,
        section = sectionSettings
    )
    default int calculationCutoff()
    {
        return 5;
    }

    @ConfigItem(
        keyName = "showTransportInfo",
        name = "Show transport info",
        description = "Whether to display transport destination hint info, e.g. which chat option and text to click",
        position = 21,
        section = sectionSettings
    )
    default boolean showTransportInfo() {
        return true;
    }

    @ConfigSection(
        name = "Display",
        description = "Options for displaying the path on the world map, minimap and scene tiles",
        position = 22
    )
    String sectionDisplay = "sectionDisplay";

    @ConfigItem(
        keyName = "drawMap",
        name = "Draw path on world map",
        description = "Whether the path should be drawn on the world map",
        position = 23,
        section = sectionDisplay
    )
    default boolean drawMap() {
        return true;
    }

    @ConfigItem(
        keyName = "drawMinimap",
        name = "Draw path on minimap",
        description = "Whether the path should be drawn on the minimap",
        position = 24,
        section = sectionDisplay
    )
    default boolean drawMinimap() {
        return true;
    }

    @ConfigItem(
        keyName = "drawTiles",
        name = "Draw path on tiles",
        description = "Whether the path should be drawn on the game tiles",
        position = 25,
        section = sectionDisplay
    )
    default boolean drawTiles() {
        return true;
    }

    @ConfigItem(
        keyName = "pathStyle",
        name = "Path style",
        description = "Whether to display the path as tiles or a segmented line",
        position = 26,
        section = sectionDisplay
    )
    default TileStyle pathStyle() {
        return TileStyle.TILES;
    }

    @ConfigSection(
        name = "Colours",
        description = "Colours for the path map, minimap and scene tiles",
        position = 27
    )
    String sectionColours = "sectionColours";

    @Alpha
    @ConfigItem(
        keyName = "colourPath",
        name = "Path",
        description = "Colour of the path tiles on the world map, minimap and in the game scene",
        position = 28,
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
        position = 29,
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
        position = 30,
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
        position = 31,
        section = sectionColours
    )
    default Color colourCollisionMap() {
        return new Color(0, 128, 255, 128);
    }

    @Alpha
    @ConfigItem(
        keyName = "colourText",
        name = "Text",
        description = "Colour of the text of the tile counter and fairy ring codes",
        position = 32,
        section = sectionColours
    )
    default Color colourText() {
        return Color.WHITE;
    }

    @ConfigSection(
        name = "Debug Options",
        description = "Various options for debugging",
        position = 33,
        closedByDefault = true
    )
    String sectionDebug = "sectionDebug";

    @ConfigItem(
        keyName = "drawTransports",
        name = "Draw transports",
        description = "Whether transports should be drawn",
        position = 34,
        section = sectionDebug
    )
    default boolean drawTransports() {
        return false;
    }

    @ConfigItem(
        keyName = "drawCollisionMap",
        name = "Draw collision map",
        description = "Whether the collision map should be drawn",
        position = 35,
        section = sectionDebug
    )
    default boolean drawCollisionMap() {
        return false;
    }

    @ConfigItem(
        keyName = "drawDebugPanel",
        name = "Show debug panel",
        description = "Toggles displaying the pathfinding debug stats panel",
        position = 36,
        section = sectionDebug
    )
    default boolean drawDebugPanel() {
        return false;
    }
}
