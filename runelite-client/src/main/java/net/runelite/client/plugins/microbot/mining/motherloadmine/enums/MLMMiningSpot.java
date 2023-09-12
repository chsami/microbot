package net.runelite.client.plugins.microbot.mining.motherloadmine.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.coords.WorldPoint;

import java.util.Arrays;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum MLMMiningSpot {
    IDLE(null),
    WEST_LOWER(Arrays.asList(new WorldPoint(3731, 5661, 0), new WorldPoint(3730, 5668, 0))),
    WEST_MID(null),
    WEST_UPPER(null),
    SOUTH(Arrays.asList(new WorldPoint(3745, 5647, 0), new WorldPoint(3756, 5653, 0))),
    NORTH_UPPER(null);

    private final List<WorldPoint> worldPoint;

}
