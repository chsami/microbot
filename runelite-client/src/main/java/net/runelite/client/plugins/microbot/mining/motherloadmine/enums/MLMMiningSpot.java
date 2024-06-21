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
    WEST_UPPER(Arrays.asList(new WorldPoint(3752, 5683, 0), new WorldPoint(3752, 5680, 0))),
    SOUTH(Arrays.asList(new WorldPoint(3745, 5647, 0), new WorldPoint(3756, 5653, 0))),
    NORTH(null);
    //EAST_UPPER(Arrays.asList(new WorldPoint(3760, 5671, 0), new WorldPoint(3758, 5673, 0)));

    private final List<WorldPoint> worldPoint;

}
