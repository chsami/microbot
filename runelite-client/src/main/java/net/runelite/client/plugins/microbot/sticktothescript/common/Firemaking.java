package net.runelite.client.plugins.microbot.sticktothescript.common;

import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;

import java.util.List;

public class Firemaking {

    public static WorldPoint isGameObjectOnTile(List<WorldPoint> locations, List<Integer> ids) {
        // Iterate through the different fire spots and return the first one that has a fire on it
        for (WorldPoint loc : locations) {
            TileObject tile = Rs2GameObject.findGameObjectByLocation(loc);
            if (tile != null && ids.contains(tile.getId())) {
                return loc;
            }
        }
        return null;
    }
}
