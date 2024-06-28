package net.runelite.client.plugins.microbot.shortestpath;

import net.runelite.api.World;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

public class WorldPointUtil {
    public static int packWorldPoint(WorldPoint point) {
        return packWorldPoint(point.getX(), point.getY(), point.getPlane());
    }

    // Packs a world point into a single int
    // First 15 bits are x, next 15 are y, last 2 bits are the plane
    public static int packWorldPoint(int x, int y, int plane) {
        return (x & 0x7FFF) | ((y & 0x7FFF) << 15) | ((plane & 0x3) << 30);
    }

    public static WorldPoint unpackWorldPoint(int packedPoint) {
        final int x = unpackWorldX(packedPoint);
        final int y = unpackWorldY(packedPoint);
        final int plane = unpackWorldPlane(packedPoint);
        return new WorldPoint(x, y, plane);
    }

    public static int unpackWorldX(int packedPoint) {
        return packedPoint & 0x7FFF;
    }

    public static int unpackWorldY(int packedPoint) {
        return (packedPoint >> 15) & 0x7FFF;
    }

    public static int unpackWorldPlane(int packedPoint) {
        return (packedPoint >> 30) & 0x3;
    }

    public static int distanceBetween(int previousPacked, int currentPacked) {
        return distanceBetween(previousPacked, currentPacked, 1);
    }

    public static int distanceBetween(int previousPacked, int currentPacked, int diagonal) {
        final int previousX = WorldPointUtil.unpackWorldX(previousPacked);
        final int previousY = WorldPointUtil.unpackWorldY(previousPacked);
        final int previousZ = WorldPointUtil.unpackWorldPlane(previousPacked);
        final int currentX = WorldPointUtil.unpackWorldX(currentPacked);
        final int currentY = WorldPointUtil.unpackWorldY(currentPacked);
        final int currentZ = WorldPointUtil.unpackWorldPlane(currentPacked);
        final int dx = Math.abs(previousX - currentX);
        final int dy = Math.abs(previousY - currentY);
        final int dz = previousZ != currentZ ? 1000 : 0;

        if (diagonal == 1) {
            return Math.max(dx, dy) + dz;
        } else if (diagonal == 2) {
            return dx + dy + dz;
        }

        return Integer.MAX_VALUE;
    }

    public static int distanceBetween(WorldPoint previous, WorldPoint current) {
        return distanceBetween(previous, current, 1);
    }

    public static int distanceBetween(WorldPoint previous, WorldPoint current, int diagonal) {
        final int dx = Math.abs(previous.getX() - current.getX());
        final int dy = Math.abs(previous.getY() - current.getY());

        if (diagonal == 1) {
            return Math.max(dx, dy);
        } else if (diagonal == 2) {
            return dx + dy;
        }

        return Integer.MAX_VALUE;
    }

    // Matches WorldArea.distanceTo
    public static int distanceToArea(int packedPoint, WorldArea area) {
        final int plane = unpackWorldPlane(packedPoint);
        if (area.getPlane() != plane) {
            return Integer.MAX_VALUE;
        }

        final int y = unpackWorldY(packedPoint);
        final int x = unpackWorldX(packedPoint);
        final int areaMaxX = area.getX() + area.getWidth() - 1;
        final int areaMaxY = area.getY() + area.getHeight() - 1;
        final int dx = Math.max(Math.max(area.getX() - x, 0), x - areaMaxX);
        final int dy = Math.max(Math.max(area.getY() - y, 0), y - areaMaxY);

        return Math.max(dx, dy);
    }

    public static boolean isPointInPolygon(WorldPoint point, WorldPoint[] polygon) {
        int n = polygon.length;
        int j = n - 1;
        boolean inside = false;

        for (int i = 0; i < n; i++) {
            if (polygon[i].getY() < point.getY() && polygon[j].getY() >= point.getY() ||
                    polygon[j].getY() < point.getY() && polygon[i].getY() >= point.getY()) {
                if (polygon[i].getX() + (point.getY() - polygon[i].getY()) / (double)(polygon[j].getY() - polygon[i].getY()) * (polygon[j].getX() - polygon[i].getX()) < point.getX()) {
                    inside = !inside;
                }
            }
            j = i;
        }

        return inside;
    }
}
