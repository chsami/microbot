package net.runelite.client.plugins.microbot.util.walker.enums;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

@Getter
public enum Allotments {
    NONE("None"),
    ARDOUGNE("Ardougne", new WorldPoint(2668, 3375, 0)),
    CATHERBY("Catherby", new WorldPoint(2811, 3465, 0)),
    CIVITAS_ILLA_FORTIS("Civitas illa Fortis", new WorldPoint(1587, 3099, 0)),
    FALADOR("Falador", new WorldPoint(3056, 3310, 0)),
    FARMING_GUILD("Farming Guild", new WorldPoint(1265, 3730, 0)),
    KOUREND("Kourend", new WorldPoint(1736, 3553, 0)),
    MORYTANIA("Morytania", new WorldPoint(3600, 3523, 0)),
    PRIFDDINAS("Prifddinas", new WorldPoint(3290, 6100, 0));

    private final String name;
    private WorldPoint worldPoint;

    Allotments(String name, WorldPoint worldPoint) {
        this.name = name;
        this.worldPoint = worldPoint;
    }

    Allotments(String name) {
        this.name = name;
    }
}