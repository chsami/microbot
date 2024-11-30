package net.runelite.client.plugins.microbot.util.walker.enums;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

@Getter
public enum Insects {

    BLACK_WARLOCK("Black Warlock (Feldip Hunter Area)", new WorldPoint(2557, 2912, 0)),
    RUBY_HARVEST("Ruby Harvest (Aldarin West)", new WorldPoint(1342, 2934, 0)),
    SANDWORMS("Sandworms (Port Piscarilius Beach)", new WorldPoint(1840, 3802, 0)),
    SUNLIGHT_MOTH("Sunlight Moth (Hunter Guild North)", new WorldPoint(1556, 3091, 0)),
    SUNLIGHT_MOTH_2("Sunlight Moth (Hunter Guild Southeast)", new WorldPoint(1575, 3020, 0));

    private final String name;
    private WorldPoint worldPoint;

    Insects(String name, WorldPoint worldPoint) {
        this.name = name;
        this.worldPoint = worldPoint;
    }

    Insects(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
