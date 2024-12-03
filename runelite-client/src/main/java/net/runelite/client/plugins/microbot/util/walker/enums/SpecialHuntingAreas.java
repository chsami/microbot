package net.runelite.client.plugins.microbot.util.walker.enums;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

@Getter
public enum SpecialHuntingAreas {

    EMBERTAILED_JERBOA("Embertailed Jerboa (Hunter Guild West)", new WorldPoint(1515, 3047, 0)),
    FELDIP_WEASEL("Feldip Weasel (Feldip Hunter Area)", new WorldPoint(2557, 2912, 0)),
    FISH_SHOAL("Fish Shoal (Fossil Island Underwater)", new WorldPoint(3743, 10295, 0)),
    HERBIBOAR("Herbiboar (Fossil Island 1)", new WorldPoint(3693, 3800, 0)),
    HORNED_GRAAHK("Horned Graahk (Karamja)", new WorldPoint(2786, 3001, 0)),
    MOONLIGHT_ANTELOPE("Moonlight Antelope (Hunter Guild Caverns)", new WorldPoint(1559, 9420, 0)),
    PYRE_FOX("Pyre Fox (Avium Savannah)", new WorldPoint(1616, 2999, 0)),
    SPINED_LARUPIA("Spined Larupia (Feldip Hunter Area)", new WorldPoint(2557, 2912, 0)),
    SUNLIGHT_ANTELOPE("Sunlight Antelope (Avium Savannah East)", new WorldPoint(1745, 3008, 0));

    private final String name;
    private WorldPoint worldPoint;

    SpecialHuntingAreas(String name, WorldPoint worldPoint) {
        this.name = name;
        this.worldPoint = worldPoint;
    }

    SpecialHuntingAreas(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}