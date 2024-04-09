package net.runelite.client.plugins.microbot.util.walker;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.shortestpath.ShortestPathPlugin;

public class Rs2Walker {
    public static boolean walkTo(WorldPoint target) {
        if (Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(target) <= 1) {
            return true;
        }
        ShortestPathPlugin.walkerScript.walkTo(target);
        return false;
    }
}
