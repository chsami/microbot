package net.runelite.client.plugins.microbot.util.coords;

import net.runelite.api.Point;
import net.runelite.api.WorldView;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.util.tile.Rs2Tile;

import java.util.List;
import java.util.stream.Collectors;

public class Rs2WorldArea extends WorldArea {
    public Rs2WorldArea(int x, int y, int width, int height, int plane) {
        super(x, y, width, height, plane);
    }

    public Rs2WorldArea(WorldPoint location, int width, int height) {
        super(location, width, height);
    }

    public Rs2WorldArea(WorldArea area) {
        super(area.getX(), area.getY(), area.getWidth(), area.getHeight(), area.getPlane());
    }

    public Rs2WorldArea(WorldPoint swLocation, WorldPoint neLocation)
    {
        super(swLocation, neLocation.getX() - swLocation.getX() + 1, neLocation.getY() - swLocation.getY() + 1);
    }

    public Rs2WorldArea offset(int size) {
        return new Rs2WorldArea(getX() - size, getY() - size, getWidth() + (size * 2), getHeight() + (size * 2), getPlane());
    }

    /**
     * Checks whether this area is within melee distance of another without blocking in-between.
     *
     * @param wv the worldview to test in
     * @param other the other area
     * @return true if in melee distance without blocking, false otherwise
     */
    public boolean canMelee(WorldView wv, Rs2WorldArea other) {
        if (!isInMeleeDistance(other)) {
            return false;
        }
        Point p1 = this.getComparisonPoint(other);
        Point p2 = other.getComparisonPoint(this);
        int dx = p2.getX() - p1.getX();
        int dy = p2.getY() - p1.getY();
        return this.canTravelInDirection(wv, dx, dy);
    }

    /**
     * Gets the point within this area that is closest to another.
     *
     * @param other the other area
     * @return the closest point to the passed area
     */
    private Point getComparisonPoint(WorldArea other) {
        int x = Math.min(Math.max(other.getX(),this.getX()), this.getX() + this.getWidth() - 1);
        int y = Math.min(Math.max(other.getY(),this.getY()), this.getY() + this.getHeight() - 1);
        return new Point(x, y);
    }

    public List<WorldPoint> getInteractable()
    {

        List<WorldPoint> surrounding = this.offset(1).toWorldPointList();

        surrounding.removeIf(this::contains);


        return surrounding.stream()
                .filter(Rs2Tile::isWalkable)
                .collect(Collectors.toList());
    }
}
