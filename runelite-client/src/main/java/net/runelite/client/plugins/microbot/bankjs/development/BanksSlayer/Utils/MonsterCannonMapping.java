package net.runelite.client.plugins.microbot.bankjs.development.BanksSlayer.Utils;

import net.runelite.api.coords.WorldPoint;

import java.util.HashMap;
import java.util.Map;

public class MonsterCannonMapping {

    //Define Cannon Spot locations for each monster

    public static WorldPoint Kalphite_Cannon = new WorldPoint(3324,9503,0);
    public static WorldPoint Aberrant_Spectres_Cannon = new WorldPoint(2456,9791,0);
    // Add more as needed

    private static final Map<String, WorldPoint> CANNON_LOCATIONS = new HashMap<>();

    static {
        CANNON_LOCATIONS.put("Kalphite", Kalphite_Cannon);
        CANNON_LOCATIONS.put("Aberrant Spectres", Aberrant_Spectres_Cannon);
    }

    public static WorldPoint getCannonLocation(String taskName) {
        return CANNON_LOCATIONS.get(taskName);
    }
}
