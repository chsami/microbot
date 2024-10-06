package net.runelite.client.plugins.microbot.sticktothescript.common;

import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

import java.util.List;

public class Functions {
    // Check if the player is close to a specific location (withing 10 tiles)
    public static boolean closeToLocation(WorldPoint location) {
        return Rs2Player.getWorldLocation().distanceTo(location) <= 10;
    }

    public static WorldPoint isGameObjectOnTile(List<WorldPoint> locations, List<Integer> ids) {
        // Iterate through the different locations and return the first one that has the desired object ID on it
        for (WorldPoint loc : locations) {
            TileObject tile = Rs2GameObject.findGameObjectByLocation(loc);
            if (tile != null && ids.contains(tile.getId())) {
                return loc;
            }
        }
        return null;
    }
    public static boolean isGameObjectOnTile(WorldPoint location, int id) {
        // Return true if the specified tile contains the desired ID.
        TileObject tile = Rs2GameObject.findGameObjectByLocation(location);
        if (tile != null && id == tile.getId()) {
            return true;
        }
        return false;
    }
}
