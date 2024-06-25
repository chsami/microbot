package net.runelite.client.plugins.microbot.looter.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.coords.WorldPoint;

@Getter
@RequiredArgsConstructor
public enum FlaxLocations {
    LANDS_END(new WorldPoint(1496, 3434, 0)),
    TREE_GNOME_STRONGHOLD(new WorldPoint(2481, 3397, 0)),
    SEERS_VILLAGE(new WorldPoint(2742, 3447, 0)),
    RELLEKKA(new WorldPoint(2621, 3640, 0)),
    HOSIDIUS(new WorldPoint(1762, 3657, 0));

    private final WorldPoint worldPoint;
}
