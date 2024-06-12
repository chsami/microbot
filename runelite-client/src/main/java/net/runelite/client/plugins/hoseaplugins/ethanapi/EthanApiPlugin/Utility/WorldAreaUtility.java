package net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Utility;

import net.runelite.client.plugins.hoseaplugins.ethanapi.PacketUtils.PacketReflection;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

import java.util.ArrayList;
import java.util.List;


public class WorldAreaUtility {
    public static List<WorldPoint> objectInteractableTiles(TileObject e) {
        Point p;
        int width = 1;
        int height = 1;
        if (e instanceof GameObject) {
            GameObject gameObject = (GameObject) e;
            width = gameObject.sizeX();
            height = gameObject.sizeY();
            p = gameObject.getSceneMinLocation();
        } else {
            p = new Point(e.getLocalLocation().getSceneX(), e.getLocalLocation().getSceneY());
        }
        LocalPoint objectMinLocation = new LocalPoint(p.getX(), p.getY());
        WorldPoint objectMinWorldPoint = WorldPoint.fromScene(PacketReflection.getClient(), objectMinLocation.getX(), objectMinLocation.getY(), e.getPlane());
        int xSW = objectMinWorldPoint.getX();
        int ySW = objectMinWorldPoint.getY();
        int xNE = xSW + width - 1;
        int yNE = ySW + height - 1;
        List<WorldPoint> surroundingTiles = new ArrayList<>();
        for (int x = xSW - 1; x <= xNE + 1; x++) {
            for (int y = ySW - 1; y <= yNE + 1; y++) {
                // Check if the tile is a corner
                boolean isCorner = (x == xSW - 1 && y == ySW - 1) || (x == xNE + 1 && y == ySW - 1) ||
                        (x == xSW - 1 && y == yNE + 1) || (x == xNE + 1 && y == yNE + 1);

                // Exclude tiles that are within the object or are corner tiles
                if ((x >= xSW && x <= xNE && y >= ySW && y <= yNE) || isCorner) {
                    continue;
                }
                surroundingTiles.add(new WorldPoint(x, y, objectMinWorldPoint.getPlane()));
            }
        }
        CollisionData[] collisionData = PacketReflection.getClient().getCollisionMaps();
        if (collisionData == null) {
            return surroundingTiles;
        }

        int[][] planeData = collisionData[PacketReflection.getClient().getPlane()].getFlags();

        var iter = surroundingTiles.iterator();

        while (iter.hasNext()) {
            WorldPoint testPoint = iter.next();
            LocalPoint localScenePoint = LocalPoint.fromWorld(PacketReflection.getClient(), testPoint);
            if (localScenePoint == null) {
                continue;
            }

            int flags = planeData[localScenePoint.getSceneX()][localScenePoint.getSceneY()];
            if ((flags & CollisionDataFlag.BLOCK_MOVEMENT_FULL) != 0) {
                iter.remove();
            }
        }
        return surroundingTiles;
    }

    public static List<WorldPoint> objectInteractableTiles(Actor e) {
        int x = 1;
        int y = 1;
        if (e instanceof NPC) {
            NPC gameObject = (NPC) e;
            x = gameObject.getComposition().getSize();
            y = gameObject.getComposition().getSize();
        }
        WorldPoint areaMinCornerWorldPoint = new WorldPoint(e.getWorldLocation().getX() - 1, e.getWorldLocation().getY() - 1, e.getWorldLocation().getPlane());
        List<WorldPoint> objectArea = e.getWorldArea().toWorldPointList();
        int sx = x + 2;
        int sy = y + 2;
        ArrayList<WorldPoint> grownArea = new ArrayList<>(new WorldArea(areaMinCornerWorldPoint, sx, sy).toWorldPointList());
        grownArea.remove(grownArea.size() - 1);
        grownArea.remove((sx * sy) - sx);
        grownArea.remove(sx - 1);
        grownArea.remove(0);
        grownArea.removeAll(objectArea);

        CollisionData[] collisionData = PacketReflection.getClient().getCollisionMaps();
        if (collisionData == null) {
            return grownArea;
        }

        int[][] planeData = collisionData[PacketReflection.getClient().getPlane()].getFlags();

        for (int i = grownArea.size() - 1; i >= 0; i--) {
            WorldPoint testPoint = grownArea.get(i);
            LocalPoint localScenePoint = LocalPoint.fromWorld(PacketReflection.getClient(), testPoint);
            if (localScenePoint == null) {
                continue;
            }

            int flags = planeData[localScenePoint.getSceneX()][localScenePoint.getSceneY()];
            if ((flags & CollisionDataFlag.BLOCK_MOVEMENT_FULL) != 0) {
                grownArea.remove(testPoint);
            }
        }

        return grownArea;
    }
}