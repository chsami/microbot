package net.runelite.client.plugins.microbot.util.walker.enums;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

@Getter
public enum Kebbits {

    BARB_TAILED_KEBBIT("Barb-tailed Kebbit (Feldip Hunter Area)", new WorldPoint(2557, 2912, 0)),
    DARK_KEBBIT("Dark Kebbit (Falconry)", new WorldPoint(2379, 3599, 0)),
    DASHING_KEBBIT("Dashing Kebbit (Falconry)", new WorldPoint(2379, 3599, 0)),
    SPOTTED_KEBBIT("Spotted Kebbit (Falconry)", new WorldPoint(2379, 3599, 0));

    private final String name;
    private WorldPoint worldPoint;

    Kebbits(String name, WorldPoint worldPoint) {
        this.name = name;
        this.worldPoint = worldPoint;
    }

    Kebbits(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
