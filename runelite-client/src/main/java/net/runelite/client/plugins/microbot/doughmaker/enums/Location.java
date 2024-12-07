package net.runelite.client.plugins.microbot.doughmaker.enums;

import lombok.Getter;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

@Getter
public enum Location {
    INSIDE_WHEAT_FIELD(new WorldArea(3138, 3458, 7, 7, 0), new WorldPoint(3141, 3461, 0)),
    OUTSIDE_WHEAT_FIELD(new WorldArea(3145, 3455, 4, 3, 0), new WorldPoint(3141, 3456, 0)),
    NEAR_COOKING_GUILD_DOOR(new WorldArea(3141, 3439, 5, 3, 0), new WorldPoint(3143, 3441, 0)),
    COOKING_GUILD_FIRST_FLOOR(new WorldArea(3137, 3444, 12, 10, 0), new WorldPoint(3143, 3446, 0)),
    COOKING_GUILD_SECOND_FLOOR(new WorldArea(3138, 3446, 11, 8, 1), new WorldPoint(3143, 3450, 1)),
    COOKING_GUILD_THIRD_FLOOR(new WorldArea(3138, 3446, 9, 8, 2), new WorldPoint(3142, 3450, 2)),
    NEAREST_BANK(new WorldArea(3180, 3433, 11, 15, 0), new WorldPoint(3182, 3442, 0)),
    OUTSIDE_POINT(new WorldArea(0, 0, 0, 0, 0), new WorldPoint(0, 0, 0));

    private final WorldArea area;
    private final WorldPoint point;

    Location(WorldArea area, WorldPoint point) {
        this.area = area;
        this.point = point;
    }

    public static Stream<Location> stream() {
        return Stream.of(Location.values());
    }
}
