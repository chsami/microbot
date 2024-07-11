package net.runelite.client.plugins.microbot.shortestpath.enums;

import lombok.Getter;

@Getter
public enum Farming {
    NONE("None"),
    ALLOTMENTS("Allotments"),
    BUSHES("Bushes"),
    FRUIT_TREES("Fruit Trees"),
    HERBS("Herbs"),
    HOPS("Hops"),
    TREES("Trees");

    private final String name;

    Farming(String name) {
        this.name = name;
    }

}
