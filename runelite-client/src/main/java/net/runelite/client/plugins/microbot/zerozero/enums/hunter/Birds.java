package net.runelite.client.plugins.microbot.zerozero.enums.hunter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.coords.WorldArea;

@Getter
@RequiredArgsConstructor
public enum Birds {
    CRIMSON(1, "Crimson swift", 1, new WorldArea(2609, 2930, 5, 5, 0)),
    GOLDEN(2, "Golden warbler", 5, new WorldArea(3000, 3000, 7, 7, 0)),  // Example location
    COPPER(3, "Copper longtail", 9, new WorldArea(3050, 3100, 5, 5, 0)),  // Example location
    CERULEAN(4, "Cerulean twitch", 11, new WorldArea(3200, 3300, 6, 6, 0)),  // Example location
    TROPICAL(5, "Tropical wagtail", 15, new WorldArea(3400, 3400, 5, 5, 0));  // Example location

    private final int id;
    private final String name;
    private final int hunterLevel;
    private final WorldArea area;  // Each bird now has its own WorldArea
}
