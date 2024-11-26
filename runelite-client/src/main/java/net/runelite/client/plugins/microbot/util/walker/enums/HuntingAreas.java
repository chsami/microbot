package net.runelite.client.plugins.microbot.util.walker.enums;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

@Getter
public enum HuntingAreas {
    HUNTER_GUILD("Hunter Guild", new WorldPoint(1555, 3420, 0)),
    BARB_TAILED_KEBBIT("Barb-tailed Kebbit (Feldip Hunter Area)", new WorldPoint(2557, 2912, 0)),
    BLACK_CHINCHOMPA("Black Chinchompa (Wilderness)", new WorldPoint(3142, 3771, 0)),
    BLACK_SALAMANDER("Black Salamander (Boneyard Hunter Area)", new WorldPoint(3294, 3673, 0)),
    BLACK_WARLOCK("Black Warlock (Feldip Hunter Area)", new WorldPoint(2557, 2912, 0)),
    CARNIVOROUS_CHINCHOMPA("Carnivorous Chinchompa (Feldip Hunter Area)", new WorldPoint(2557, 2912, 0)),
    CARNIVOROUS_CHINCHOMPA_2("Carnivorous Chinchompa (Gwenith Hunter Area Outside)", new WorldPoint(2269, 3408, 0)),
    CARNIVOROUS_CHINCHOMPA_3("Carnivorous Chinchompa (Gwenith Hunter Area Inside)", new WorldPoint(3293, 6160, 0)),
    CHINCHOMPA("Chinchompa (Isle of Souls North West)", new WorldPoint(2127, 2950, 0)),
    CHINCHOMPA_2("Chinchompa (Piscatoris Hunter Area)", new WorldPoint(2335, 3584, 0)),
    COPPER_LONGTAIL("Copper Longtail (Aldarin North)", new WorldPoint(1357, 2977, 0)),
    COPPER_LONGTAIL_2("Copper Longtail (Isle of Souls North)", new WorldPoint(2207, 2964, 0)),
    CRIMSON_SWIFT("Crimson Swift (Feldip Hunter Area)", new WorldPoint(2557, 2912, 0)),
    CRIMSON_SWIFT_2("Crimson Swift (Isle of Souls South West)", new WorldPoint(2158, 2822, 0)),
    DARK_KEBBIT("Dark Kebbit (Falconry)", new WorldPoint(2379, 3599, 0)),
    DASHING_KEBBIT("Dashing Kebbit (Falconry)", new WorldPoint(2379, 3599, 0)),
    EMBERTAILED_JERBOA("Embertailed Jerboa (Hunter Guild West)", new WorldPoint(1515, 3047, 0)),
    FELDIP_WEASEL("Feldip Weasel (Feldip Hunter Area)", new WorldPoint(2557, 2912, 0)),
    FISH_SHOAL("Fish Shoal (Fossil Island Underwater)", new WorldPoint(3743, 10295, 0)),
    HERBIBOAR("Herbiboar (Fossil Island 1)", new WorldPoint(3693, 3800, 0)),
    HORNED_GRAAHK("Horned Graahk (Karamja)", new WorldPoint(2786, 3001, 0)),
    MOONLIGHT_ANTELOPE("Moonlight Antelope (Hunter Guild Caverns)", new WorldPoint(1559, 9420, 0)),
    ORANGE_SALAMANDER("Orange Salamander (Necropolis)", new WorldPoint(3285, 2739, 0)),
    ORANGE_SALAMANDER_2("Orange Salamander (Uzer Hunter Area)", new WorldPoint(3401, 3104, 0)),
    PYRE_FOX("Pyre Fox (Avium Savannah)", new WorldPoint(1616, 2999, 0)),
    RED_SALAMANDER("Red Salamander (Ourania Hunter Area East)", new WorldPoint(2447, 3219, 0)),
    RED_SALAMANDER_2("Red Salamander (Ourania Hunter Area South)", new WorldPoint(2475, 3240, 0)),
    RUBY_HARVEST("Ruby Harvest (Aldarin West)", new WorldPoint(1342, 2934, 0)),
    SANDWORMS("Sandworms (Port Piscarilius Beach)", new WorldPoint(1840, 3802, 0)),
    SPINED_LARUPIA("Spined Larupia (Feldip Hunter Area)", new WorldPoint(2557, 2912, 0)),
    SPOTTED_KEBBIT("Spotted Kebbit (Falconry)", new WorldPoint(2379, 3599, 0)),
    SUNLIGHT_ANTELOPE("Sunlight Antelope (Avium Savannah East)", new WorldPoint(1745, 3008, 0)),
    SUNLIGHT_MOTH("Sunlight Moth (Hunter Guild North)", new WorldPoint(1556, 3091, 0)),
    SUNLIGHT_MOTH_2("Sunlight Moth (Hunter Guild Southeast)", new WorldPoint(1575, 3020, 0)),
    TECU_SALAMANDER("Tecu Salamander (Ralos Rise)", new WorldPoint(1475, 3096, 0)),
    TROPICAL_WAGTAIL("Tropical Wagtail (Feldip Hunter Area)", new WorldPoint(2557, 2912, 0));

    private final String name;
    private final WorldPoint worldPoint;

    HuntingAreas(String name, WorldPoint worldPoint) {
        this.name = name;
        this.worldPoint = worldPoint;
    }

    @Override
    public String toString() {
        return name;
    }
}
