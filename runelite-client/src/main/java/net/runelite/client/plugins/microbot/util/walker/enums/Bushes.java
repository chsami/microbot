package net.runelite.client.plugins.microbot.util.walker.enums;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

@Getter
public enum Bushes {
    NONE("None"),
    ARDOUGNE("Ardougne", new WorldPoint(2616, 3227, 0)),
    CHAMPIONS_GUILD("Champions Guild", new WorldPoint(3182, 3360, 0)),
    ETCETERIA("Etceteria", new WorldPoint(2592, 3865, 0)),
    FARMING_GUILD("Farming Guild", new WorldPoint(1260, 3731, 0)),
    RIMMINGTON("Rimmington", new WorldPoint(2942, 3223, 0));

    private final String name;
    private WorldPoint worldPoint;

    Bushes(String name, WorldPoint worldPoint) {
        this.name = name;
        this.worldPoint = worldPoint;
    }

    Bushes(String name) {
        this.name = name;
    }
}