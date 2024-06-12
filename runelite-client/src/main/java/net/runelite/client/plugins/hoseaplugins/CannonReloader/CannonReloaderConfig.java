package net.runelite.client.plugins.hoseaplugins.CannonReloader;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("CannonReloader")
public interface CannonReloaderConfig extends Config {
    @ConfigItem(
            keyName = "cannonInstructions",
            name = "",
            description = "Cannon Instructions.",
            position = 40,
            section = "cannonConfig"
    )
    default String cannonConfigInstructions() {
        return "Right click the cannon and the safe spot to set your desired locations.";
    }

    @ConfigItem(
            keyName = "Toggle",
            name = "Toggle",
            description = "",
            position = 0
    )
    default Keybind toggle() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(
            keyName = "UseSafespot",
            name = "Use Safespot?",
            description = "Should safespot be used?",
            position = 43,
            section = "enemyNPCConfig"
    )
    default boolean useSafespot() {
        return false;
    }

    @Range(
            min = 1,
            max = 30
    )
    @ConfigItem(
            keyName = "CannonLowAmount",
            name = "Reload cannon at:",
            position = 104,
            section = "cannonConfig",
            description = "Will reload cannon at set amount."
    )
    default int cannonLowAmount()
    {
        return 5;
    }

    @Range(
            min = 1,
            max = 30
    )
    @ConfigItem(
            keyName = "CannonLowRandomAmount",
            name = "Reload random #:",
            position = 105,
            section = "cannonConfig",
            description = "Random for cannon low amount."
    )
    default int cannonLowRandom()
    {
        return 5;
    }

    @ConfigSection(
            name = "Tile Configuration",
            description = "ging stuff for tiles section.",
            closedByDefault = true,
            position = 166
    )
    String tileConfig = "tileConfig";

    @ConfigItem(
            keyName = "safespotTile",
            name = "safespot tile colour",
            position = 169,
            description = "",
            section = "tileConfig"
    )
    default Color safespotTile()
    {
        return Color.GREEN;
    }

    @Alpha
    @ConfigItem(
            keyName = "safespotTileFill",
            name = "safespotTile fill colour",
            position = 170,
            description = "",
            section = "tileConfig"
    )
    default Color safespotTileFill()
    {
        return new Color(0, 0, 0, 50);
    }

    @ConfigItem(
            keyName = "cannonspotFillColour",
            name = "cannonSpotTile tile colour",
            position = 173,
            description = "",
            section = "tileConfig"
    )
    default Color cannonSpotTile()
    {
        return Color.YELLOW;
    }
    @Alpha
    @ConfigItem(
            keyName = "cannonSpotTileFill",
            name = "cannonSpotTileFill fill colour",
            position = 174,
            description = "",
            section = "tileConfig"
    )
    default Color cannonSpotTileFill()
    {
        return new Color(0, 0, 0, 50);
    }
}
