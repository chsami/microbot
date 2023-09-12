package net.runelite.client.plugins.ogPlugins.ogrunecrafting.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.coords.WorldPoint;

@Getter
@RequiredArgsConstructor
public enum Alter {
    FIRE_ALTER("Fire Alter",
            34764,
            new WorldPoint(2584,4840,0),
            34817,
            new WorldPoint(3315,3257,0),
            new WorldPoint(3315,3257,0)
    );

    private final String name;
    private final int alterID;
    private final WorldPoint alterLocation;
    private final int ruinAlterID;
    private final WorldPoint ruinLocation;
    private final WorldPoint nextToAlter;

    public String getName() {
        return name;
    }
    public int getAlterID() {
        return alterID;
    }
    public WorldPoint getAlterLocation() {
        return alterLocation;
    }

    public int getRuinAlterID() {
        return ruinAlterID;
    }

    public WorldPoint getRuinLocation() {
        return ruinLocation;
    }

    public WorldPoint getNextToAlter() {
        return nextToAlter;
    }
}