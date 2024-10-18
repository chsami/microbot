package net.runelite.client.plugins.microbot.zerozero.enums.hunter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.coords.WorldArea;

@Getter
@RequiredArgsConstructor
public enum Birds {
    CRIMSON(1, "Crimson swift", 1, new WorldArea(2604, 2924, 10, 10, 0)),
    GOLDEN(2, "Golden warbler", 5, new WorldArea(3404, 3144, 10, 10, 0)),
    COPPER(3, "Copper longtail", 9, new WorldArea(2341, 3599, 10, 10, 0)),
    CERULEAN(4, "Cerulean twitch", 11, new WorldArea(2727, 3772, 10, 10, 0)),
    TROPICAL(5, "Tropical wagtail", 15, new WorldArea(2504, 2888, 10, 10, 0));

    private final int id;
    private final String name;
    private final int hunterLevel;
    private final WorldArea area;

}
