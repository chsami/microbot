package net.runelite.client.plugins.microbot.util.walker.enums;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

@Getter
public enum HuntingAreas {
    NONE("None"),
    BIRDS("Birds"),
    CHINCHOMPAS("Chinchompas"),
    INSECTS("Insects"),
    KEBBITS("Kebbits"),
    SALAMANDERS("Salamanders"),
    SPECIAL("Special");

    private final String name;

    HuntingAreas(String name) {
        this.name = name;
    }
}
