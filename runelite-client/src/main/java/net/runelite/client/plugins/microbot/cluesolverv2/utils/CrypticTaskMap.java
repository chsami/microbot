package net.runelite.client.plugins.microbot.cluesolverv2.utils;

import net.runelite.api.coords.WorldPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrypticTaskMap {
    public static final List<Map<String, Object>> crypticTaskMap = new ArrayList<>();

    static {
        addToMap("Aberrant spectre", null);
        addToMap("Deviant spectre", null);
        addToMap("Waterfiend", null);
        addToMap("Basilisk", null);
        addToMap("Ankou", null);
        addToMap("Bloodveld", null);
        addToMap("Rock crab", null);
        addToMap("Green dragon", null);
        addToMap("Crocodile", null);
        addToMap("Hellhound", null);
        addToMap("Aviansie", null);
        addToMap("Frost nagua", null);
        addToMap("Guard", new WorldPoint(2653, 3320, 0)); // Doric's Hut
        addToMap("Guard (Port Sarim Jail)", new WorldPoint(3012, 3222, 0)); // Jailhouse
        addToMap("Guard dog", null);
        addToMap("Pirate", new WorldPoint(2797, 3163, 0)); // Brimhaven
        addToMap("Hill Giant", null);
        addToMap("Man", null);
        addToMap("Monk", new WorldPoint(2607, 3209, 0)); //Ardy
        addToMap("Wizard", new WorldPoint(3096, 9572, 0)); // Wizard's Tower basement
        addToMap("Chicken", null);
        addToMap("Gunthor the Brave", new WorldPoint(3139, 3365, 0)); // Barbarian Camp
        addToMap("Green dragon", new WorldPoint(3147, 3651, 0)); // Wilderness Slayer Cave
        addToMap("King Black Dragon", new WorldPoint(2273, 4680, 0)); // KBD Lair
        addToMap("Penda", new WorldPoint(2930, 3536, 0)); // Burthorpe
    }

    private static void addToMap(String npc, WorldPoint location) {
        Map<String, Object> entry = new HashMap<>();
        entry.put("npc", npc);
        entry.put("location", location);
        crypticTaskMap.add(entry);
    }
}
