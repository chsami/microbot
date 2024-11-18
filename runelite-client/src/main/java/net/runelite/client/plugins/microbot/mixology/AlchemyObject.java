package net.runelite.client.plugins.microbot.mixology;

import net.runelite.api.coords.WorldPoint;

public enum AlchemyObject {
    MOX_LEVER(54868, new WorldPoint(1395, 9324, 0)),
    AGA_LEVER(54867, new WorldPoint(1394, 9324, 0)),
    LYE_LEVER(54869, new WorldPoint(1393, 9324, 0)),
    MIXING_VESSEL(55395, new WorldPoint(1394, 9326, 0)),
    ALEMBIC(55391, new WorldPoint(1391, 9325, 0)),
    AGITATOR(55390, new WorldPoint(1393, 9329, 0)),
    RETORT(55389, new WorldPoint(1397, 9325, 0)),
    CONVEYOR_BELT(54917, new WorldPoint(1394, 9331, 0)),
    HOPPER(54903, new WorldPoint(1394, 9322, 0)),
    DIGWEED_NORTH_EAST(55396, new WorldPoint(1399, 9331, 0)),
    DIGWEED_SOUTH_EAST(55397, new WorldPoint(1399, 9322, 0)),
    DIGWEED_SOUTH_WEST(55398, new WorldPoint(1389, 9322, 0)),
    DIGWEED_NORTH_WEST(55399, new WorldPoint(1389, 9331, 0));

    private final int objectId;
    private final WorldPoint coordinate;

    AlchemyObject(int objectId, WorldPoint coordinate) {
        this.objectId = objectId;
        this.coordinate = coordinate;
    }

    public int objectId() {
        return this.objectId;
    }

    public WorldPoint coordinate() {
        return this.coordinate;
    }
}
