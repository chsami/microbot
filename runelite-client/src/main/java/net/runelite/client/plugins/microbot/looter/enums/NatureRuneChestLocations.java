package net.runelite.client.plugins.microbot.looter.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

@Getter
@RequiredArgsConstructor
public enum NatureRuneChestLocations {

    EAST_ARDOUGNE(new WorldPoint(2673, 3301, 1), ObjectID.CHEST_11736),
    RELLEKKA(new WorldPoint(2667, 3693, 1), ObjectID.CHEST_11736);

    private final WorldPoint worldPoint;
    private final int objectID;
}
