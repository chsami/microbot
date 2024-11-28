package net.runelite.client.plugins.microbot.util.walker.enums;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

@Getter
public enum Chinchompas {

    BLACK_CHINCHOMPA("Black Chinchompa (Wilderness)", new WorldPoint(3142, 3771, 0)),
    CARNIVOROUS_CHINCHOMPA("Carnivorous Chinchompa (Feldip Hunter Area)", new WorldPoint(2557, 2912, 0)),
    CARNIVOROUS_CHINCHOMPA_2("Carnivorous Chinchompa (Gwenith Hunter Area Outside)", new WorldPoint(2269, 3408, 0)),
    CARNIVOROUS_CHINCHOMPA_3("Carnivorous Chinchompa (Gwenith Hunter Area Inside)", new WorldPoint(3293, 6160, 0)),
    CHINCHOMPA("Chinchompa (Isle of Souls North West)", new WorldPoint(2127, 2950, 0)),
    CHINCHOMPA_2("Chinchompa (Piscatoris Hunter Area)", new WorldPoint(2335, 3584, 0));
    
    private final String name;
    private WorldPoint worldPoint;

    Chinchompas(String name, WorldPoint worldPoint) {
        this.name = name;
        this.worldPoint = worldPoint;
    }

    Chinchompas(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
