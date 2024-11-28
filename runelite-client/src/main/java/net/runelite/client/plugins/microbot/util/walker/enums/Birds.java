package net.runelite.client.plugins.microbot.util.walker.enums;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

@Getter
public enum Birds {

    COPPER_LONGTAIL("Copper Longtail (Aldarin North)", new WorldPoint(1357, 2977, 0)),
    COPPER_LONGTAIL_2("Copper Longtail (Isle of Souls North)", new WorldPoint(2207, 2964, 0)),
    CRIMSON_SWIFT("Crimson Swift (Feldip Hunter Area)", new WorldPoint(2557, 2912, 0)),
    CRIMSON_SWIFT_2("Crimson Swift (Isle of Souls South West)", new WorldPoint(2158, 2822, 0)),
    TROPICAL_WAGTAIL("Tropical Wagtail (Feldip Hunter Area)", new WorldPoint(2557, 2912, 0));
    
    private final String name;
    private WorldPoint worldPoint;

    Birds(String name, WorldPoint worldPoint) {
        this.name = name;
        this.worldPoint = worldPoint;
    }

    Birds(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
