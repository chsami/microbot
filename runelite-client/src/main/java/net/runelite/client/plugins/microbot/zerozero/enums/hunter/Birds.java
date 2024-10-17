package net.runelite.client.plugins.microbot.zerozero.enums.hunter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum Birds {
    CRIMSON(1, "Crimson swift", 1),
    GOLDEN(2, "Golden warbler", 5),
    COPPER(3, "Copper longtail", 9),
    CERULEAN(4, "Cerulean twitch", 11),
    TROPICAL(5, "Tropical wagtail", 15);

    private final int id;
    private final String name;
    private final int hunterLevel;


}
