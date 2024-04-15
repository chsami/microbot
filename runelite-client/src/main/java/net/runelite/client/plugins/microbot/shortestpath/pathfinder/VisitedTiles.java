package net.runelite.client.plugins.microbot.shortestpath.pathfinder;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.shortestpath.WorldPointUtil;

import static net.runelite.api.Constants.REGION_SIZE;

public class VisitedTiles {
    private final SplitFlagMap.RegionExtent regionExtents;
    private final int widthInclusive;

    private final VisitedRegion[] visitedRegions;
    private final byte[] visitedRegionPlanes;

    public VisitedTiles(CollisionMap map) {
        regionExtents = SplitFlagMap.getRegionExtents();
        widthInclusive = regionExtents.getWidth() + 1;
        final int heightInclusive = regionExtents.getHeight() + 1;

        visitedRegions = new VisitedRegion[widthInclusive * heightInclusive];
        visitedRegionPlanes = map.getPlanes();
    }

    public boolean get(WorldPoint point) {
        return get(point.getX(), point.getY(), point.getPlane());
    }

    public boolean get(int packedPoint) {
        final int x = WorldPointUtil.unpackWorldX(packedPoint);
        final int y = WorldPointUtil.unpackWorldY(packedPoint);
        final int plane = WorldPointUtil.unpackWorldPlane(packedPoint);
        return get(x, y, plane);
    }

    public boolean get(int x, int y, int plane) {
        final int regionIndex = getRegionIndex(x / REGION_SIZE, y / REGION_SIZE);
        if (regionIndex < 0 || regionIndex >= visitedRegions.length) {
            return true; // Region is out of bounds; report that it's been visited to avoid exploring it further
        }

        final VisitedRegion region = visitedRegions[regionIndex];
        if (region == null) {
            return false;
        }

        return region.get(x % REGION_SIZE, y % REGION_SIZE, plane);
    }

    public boolean set(int packedPoint) {
        final int x = WorldPointUtil.unpackWorldX(packedPoint);
        final int y = WorldPointUtil.unpackWorldY(packedPoint);
        final int plane = WorldPointUtil.unpackWorldPlane(packedPoint);
        return set(x, y, plane);
    }

    public boolean set(int x, int y, int plane) {
        final int regionIndex = getRegionIndex(x / REGION_SIZE, y / REGION_SIZE);
        if (regionIndex < 0 || regionIndex >= visitedRegions.length) {
            return false; // Region is out of bounds; report that it's been visited to avoid exploring it further
        }

        VisitedRegion region = visitedRegions[regionIndex];
        if (region == null) {
            region = new VisitedRegion(visitedRegionPlanes[regionIndex]);
            visitedRegions[regionIndex] = region;
        }

        return region.set(x % REGION_SIZE, y % REGION_SIZE, plane);
    }

    public void clear() {
        for (int i = 0; i < visitedRegions.length; ++i) {
            if (visitedRegions[i] != null) {
                visitedRegions[i] = null;
            }
        }
    }

    private int getRegionIndex(int regionX, int regionY) {
        return (regionX - regionExtents.minX) + (regionY - regionExtents.minY) * widthInclusive;
    }

    private static class VisitedRegion {
        // This assumes a row is at most 64 tiles and fits in a long
        private final long[] planes;
        private final byte planeCount;

        VisitedRegion(byte planeCount) {
            this.planeCount = planeCount;
            this.planes = new long[planeCount * REGION_SIZE];
        }

        // Sets a tile as visited in the tile bitset
        // Returns true if the tile is unique and hasn't been seen before or false if it was seen before
        public boolean set(int x, int y, int plane) {
            if (plane >= planeCount) {
                // Plane is out of bounds; report that it has been visited to avoid further exploration
                return false;
            }
            final int index = y + plane * REGION_SIZE;
            boolean unique = (planes[index] & (1L << x)) == 0;
            planes[index] |= 1L << x;
            return unique;
        }

        public boolean get(int x, int y, int plane) {
            if (plane >= planeCount) {
                // This check is necessary since we check visited tiles before checking the collision map, e.g. the node
                // at (2816, 3455, 1) will check its neighbour to the north which is in a new region with no plane = 1
                return true;
            }
            return (planes[y + plane * REGION_SIZE] & (1L << x)) != 0;
        }
    }
}
