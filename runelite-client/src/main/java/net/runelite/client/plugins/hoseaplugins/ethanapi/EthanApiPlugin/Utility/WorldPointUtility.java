package net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Utility;

import net.runelite.api.coords.WorldPoint;

public class WorldPointUtility {
    public static boolean arePointsAdjacent(WorldPoint reference, WorldPoint target) {
        int x1 = reference.getX();
        int x2 = target.getX();

        int y1 = reference.getY();
        int y2 = target.getY();

        if (x1 != x2 && y1 != y2) {
            return false;
        }

        if (x1 == x2 && Math.abs(y1 - y2) == 1) {
            return true;
        }

        return y1 == y2 && Math.abs(x1 - x2) == 1;
    }
}
