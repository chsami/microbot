package net.runelite.client.plugins.microbot.util.walker.enums;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

@Getter
public enum CompostBins {
    NONE("None"),
    ARDOUGNE("Ardougne", new WorldPoint(2663, 3375, 0)),
    CATHERBY("Catherby", new WorldPoint(2806, 3464, 0)),
    CIVITAS_ILLA_FORTIS("Civitas illa Fortis", new WorldPoint(1586, 3103, 0)),
    FALADOR("Falador", new WorldPoint(3056, 3310, 0)),
    FARMING_GUILD("Farming Guild", new WorldPoint(1270, 3730, 0)),
    KOUREND("Kourend", new WorldPoint(1730, 3556, 0)),
    MORYTANIA("Morytania", new WorldPoint(3608, 3522, 0)),
    PRIFDDINAS("Prifddinas", new WorldPoint(3292, 6117, 0));

    private final String name;
    private WorldPoint worldPoint;

    CompostBins(String name, WorldPoint worldPoint) {
        this.name = name;
        this.worldPoint = worldPoint;
    }

    CompostBins(String name) {
        this.name = name;
    }
}
