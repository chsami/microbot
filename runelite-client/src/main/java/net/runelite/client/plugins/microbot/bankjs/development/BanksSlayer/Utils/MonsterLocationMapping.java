package net.runelite.client.plugins.microbot.bankjs.development.BanksSlayer.Utils;

import net.runelite.api.coords.WorldPoint;

import java.util.HashMap;
import java.util.Map;

public class MonsterLocationMapping {
    // Define monster locations
    public static WorldPoint Aberrant_Spectres = new WorldPoint(2456, 9791, 0);
    public static WorldPoint Banshees = new WorldPoint(3437, 3562, 0);
    public static WorldPoint Bats = new WorldPoint(3429, 3523, 0);
    public static WorldPoint Bears = new WorldPoint(2699, 3331, 0);
    public static WorldPoint Birds = new WorldPoint(3230, 3296, 0);
    public static WorldPoint Black_Demons = new WorldPoint(1724, 10092, 0);
    public static WorldPoint Black_Dragons = new WorldPoint(2454, 4362, 0);
    public static WorldPoint Bloodveld = new WorldPoint(2451, 9822, 0);
    public static WorldPoint Brine_Rats = new WorldPoint(2707, 10132, 0);
    public static WorldPoint Cave_Bugs = new WorldPoint(3150, 9575, 0);
    public static WorldPoint Cave_Crawlers = new WorldPoint(2791, 9996, 0);
    public static WorldPoint Cave_Slimes = new WorldPoint(3155, 9555, 0);
    public static WorldPoint Cows = new WorldPoint(3260, 3277, 0);
    public static WorldPoint Crawling_Hands = new WorldPoint(3418, 3546, 0);
    public static WorldPoint Dagannoth = new WorldPoint(2524, 10020, 0);
    public static WorldPoint Dwarves = new WorldPoint(3022, 9847, 0);
    public static WorldPoint Elves = new WorldPoint(2199, 3252, 0);
    public static WorldPoint Fire_Giants = new WorldPoint(2388, 9788, 0);
    public static WorldPoint Goblins = new WorldPoint(3250, 3234, 0);
    public static WorldPoint Greater_Demons = new WorldPoint(1702, 10100, 0);
    public static WorldPoint Icefiends = new WorldPoint(3007, 3479, 0);
    public static WorldPoint Iron_Dragons = new WorldPoint(1664, 10079, 0);
    public static WorldPoint Kalphite = new WorldPoint(3323, 9502, 0);
    public static WorldPoint Kurask = new WorldPoint(2701, 10000, 0);
    public static WorldPoint Lizards = new WorldPoint(3470, 3052, 0);
    public static WorldPoint Minotaurs = new WorldPoint(1872, 5238, 0);
    public static WorldPoint Monkeys = new WorldPoint(2876, 3150, 0);
    public static WorldPoint Rats = new WorldPoint(3237, 9866, 0);
    public static WorldPoint Skeletal_Wyverns = new WorldPoint(3064, 9549, 0);
    public static WorldPoint Skeletons = new WorldPoint(3275, 9911, 0);
    public static WorldPoint Spiritual_Creatures = new WorldPoint(2914, 5343, 2);
    public static WorldPoint Trolls = new WorldPoint(2872, 3584, 0);
    public static WorldPoint Turoth = new WorldPoint(2722, 10008, 0);
    public static WorldPoint Tzhaar = new WorldPoint(2462, 5165, 0);
    public static WorldPoint Wolves = new WorldPoint(2840, 3499, 0);
    public static WorldPoint Wyrms = new WorldPoint(1273, 10186, 0);


    // Map to store the mapping between task names and monster locations
    private static final Map<String, WorldPoint> MONSTER_LOCATIONS = new HashMap<>();

    static {
        MONSTER_LOCATIONS.put("Aberrant Spectres", Aberrant_Spectres);
        MONSTER_LOCATIONS.put("Banshees", Banshees);
        MONSTER_LOCATIONS.put("Bats", Bats);
        MONSTER_LOCATIONS.put("Bears", Bears);
        MONSTER_LOCATIONS.put("Birds", Birds);
        MONSTER_LOCATIONS.put("Black Demons", Black_Demons);
        MONSTER_LOCATIONS.put("Black Dragons", Black_Dragons);
        MONSTER_LOCATIONS.put("Bloodveld", Bloodveld);
        MONSTER_LOCATIONS.put("Brine Rats", Brine_Rats);
        MONSTER_LOCATIONS.put("Cave Bugs", Cave_Bugs);
        MONSTER_LOCATIONS.put("Cave Crawlers", Cave_Crawlers);
        MONSTER_LOCATIONS.put("Cave Slimes", Cave_Slimes);
        MONSTER_LOCATIONS.put("Cows", Cows);
        MONSTER_LOCATIONS.put("Crawling Hands", Crawling_Hands);
        MONSTER_LOCATIONS.put("Dagannoth", Dagannoth);
        MONSTER_LOCATIONS.put("Dwarves", Dwarves);
        MONSTER_LOCATIONS.put("Elves", Elves);
        MONSTER_LOCATIONS.put("Fire Giants", Fire_Giants);
        MONSTER_LOCATIONS.put("Goblins", Goblins);
        MONSTER_LOCATIONS.put("Greater Demons", Greater_Demons);
        MONSTER_LOCATIONS.put("Icefiends", Icefiends);
        MONSTER_LOCATIONS.put("Iron Dragons", Iron_Dragons);
        MONSTER_LOCATIONS.put("Kalphite", Kalphite);
        MONSTER_LOCATIONS.put("Kurask", Kurask);
        MONSTER_LOCATIONS.put("Lizards", Lizards);
        MONSTER_LOCATIONS.put("Minotaurs", Minotaurs);
        MONSTER_LOCATIONS.put("Monkeys", Monkeys);
        MONSTER_LOCATIONS.put("Rats", Rats);
        MONSTER_LOCATIONS.put("Skeletal Wyverns", Skeletal_Wyverns);
        MONSTER_LOCATIONS.put("Skeletons", Skeletons);
        MONSTER_LOCATIONS.put("Spiritual Creatures", Spiritual_Creatures);
        MONSTER_LOCATIONS.put("Trolls", Trolls);
        MONSTER_LOCATIONS.put("Turoth", Turoth);
        MONSTER_LOCATIONS.put("TzHaar", Tzhaar);
        MONSTER_LOCATIONS.put("Wolves", Wolves);
        MONSTER_LOCATIONS.put("Wyrms", Wyrms);
        // Add more mappings as needed
    }

    // Method to get the monster location for a given task name
    public static WorldPoint getMonsterLocation(String taskName) {
        return MONSTER_LOCATIONS.get(taskName);
    }
}
