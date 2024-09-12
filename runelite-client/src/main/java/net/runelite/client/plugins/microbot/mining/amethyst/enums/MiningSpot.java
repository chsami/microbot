package net.runelite.client.plugins.microbot.mining.amethyst.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.coords.WorldPoint;

import java.util.Random;

@Getter
@RequiredArgsConstructor
public enum MiningSpot {
    RANDOM_POINT_1(new WorldPoint(3019, 9706, 0)),
    RANDOM_POINT_2(new WorldPoint(3020, 9706, 0)),
    RANDOM_POINT_3(new WorldPoint(3018, 9703, 0)),
    RANDOM_POINT_4(new WorldPoint(3019, 9703, 0)),
    RANDOM_POINT_5(new WorldPoint(3020, 9700, 0)),
    NULL(null);
    private static final Random RANDOM = new Random();
    private final WorldPoint worldPoint;

    public static MiningSpot getRandomMiningSpot() {
        MiningSpot[] spots = values();
        return spots[RANDOM.nextInt(spots.length)];
    }
}
