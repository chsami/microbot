package net.runelite.client.plugins.microbot.util.math;

import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;

import java.awt.*;


/**
 * Game world and projection calculations.
 */
public class Calculations {
    /**
     * Returns the angle to a given tile in degrees anti-clockwise from the
     * positive x axis (where the x-axis is from west to east).
     *
     * @param t The target tile
     * @return The angle in degrees
     */
    public static int angleToTile(Actor t) {
        int angle = (int) Math.toDegrees(Math.atan2(t.getWorldLocation().getY() - Microbot.getClient().getLocalPlayer().getWorldLocation().getY(),
                t.getWorldLocation().getX() - Microbot.getClient().getLocalPlayer().getWorldLocation().getX()));
        return angle >= 0 ? angle : 360 + angle;
    }

    public static int angleToTile(TileObject t) {
        int angle = (int) Math.toDegrees(Math.atan2(t.getWorldLocation().getY() - Microbot.getClient().getLocalPlayer().getWorldLocation().getY(),
                t.getWorldLocation().getX() - Microbot.getClient().getLocalPlayer().getWorldLocation().getX()));
        return angle >= 0 ? angle : 360 + angle;
    }

    public static int angleToTile(LocalPoint localPoint) {
        int angle = (int) Math.toDegrees(Math.atan2(localPoint.getY() - Microbot.getClient().getLocalPlayer().getWorldLocation().getY(),
                localPoint.getX() - Microbot.getClient().getLocalPlayer().getWorldLocation().getX()));
        return angle >= 0 ? angle : 360 + angle;
    }

    public static boolean pointOnScreen(Point check) {
        int x = check.getX(), y = check.getY();
        return x > Microbot.getClient().getViewportXOffset() && x < Microbot.getClient().getViewportWidth()
                && y > Microbot.getClient().getViewportYOffset() && y < Microbot.getClient().getViewportHeight();
    }

    public static Point tileToScreen(final Tile tile, final double dX, final double dY, final int height) {
        return Perspective.localToCanvas(Microbot.getClient(), new LocalPoint(tile.getLocalLocation().getX(), tile.getLocalLocation().getY()), Microbot.getClient().getPlane(), height);
    }

    public static Point tileToScreenPoint(final Point point, final double dX, final double dY, final int height) {
        return Perspective.localToCanvas(Microbot.getClient(), new LocalPoint(point.getX(), point.getY()), Microbot.getClient().getPlane(), height);
    }

    public static boolean tileOnScreen(Actor actor) {
        Point p = new Point(actor.getLocalLocation().getX(), actor.getLocalLocation().getY());
        Point tileToScreenPoint = tileToScreenPoint(p, 0.5, 0.5, 0);
        return (tileToScreenPoint != null) && pointOnScreen(tileToScreenPoint);
    }

    public static boolean tileOnScreen(TileObject tileObject) {
        Point p = new Point(tileObject.getLocalLocation().getX(), tileObject.getLocalLocation().getY());
        Point tileToScreenPoint = tileToScreenPoint(p, 0.5, 0.5, 0);
        return (tileToScreenPoint != null) && pointOnScreen(tileToScreenPoint);
    }

    public static boolean tileOnScreen(LocalPoint localPoint) {
        if (localPoint == null) return false;
        Point p = new Point((int) localPoint.getX(), (int) localPoint.getY());
        Point tileToScreenPoint = tileToScreenPoint(p, 0.5, 0.5, 0);
        return (tileToScreenPoint != null) && pointOnScreen(tileToScreenPoint);
    }

    public static Point tileToScreenHalfWay(final Tile tile, final double dX, final double dY, final int height) {
        return Perspective.localToCanvas(Microbot.getClient(), new LocalPoint((tile.getWorldLocation().getX() +
                Microbot.getClient().getLocalPlayer().getWorldLocation().getX()) / 2, (tile.getWorldLocation().getY() +
                Microbot.getClient().getLocalPlayer().getWorldLocation().getY()) / 2), Microbot.getClient().getPlane(), height);
    }

    public static boolean tileOnScreen(Tile t) {
        Point point = tileToScreen(t, 0.5, 0.5, 0);
        return (point != null) && pointOnScreen(point);
    }

    public static boolean tileOnScreenHalfWay(Tile t) {
        Point point = tileToScreenHalfWay(t, 0.5, 0.5, 0);
        return (point != null) && pointOnScreen(point);
    }

    public static Tile getTileOnScreen(Tile tile) {
        try {
            if (tileOnScreen(tile)) {
                return tile;
            } else {
                if (tileOnScreenHalfWay(tile)) {
                    return tile;
                } else {
                    return getTileOnScreen(tile);
                }
            }
        } catch (StackOverflowError soe) {
            return null;
        }
    }

    /**
     * checks whether or not a given RSTile is reachable.
     *
     * @param dest     The <code>RSTile</code> to check.
     * @param isObject True if an instance of <code>RSObject</code>.
     * @return <code>true</code> if player can reach specified Object; otherwise
     * <code>false</code>.
     */
    public static boolean canReach(Tile dest, boolean isObject) {
        return pathLengthTo(dest, isObject) != -1;
    }

    public static boolean canReach(WorldPoint dest, boolean isObject) {
        return pathLengthTo(dest, isObject) != -1;
    }

    public static boolean tileOnMap(WorldPoint w) {
        return tileToMinimap(w) != null;
    }

    public static Point tileToMinimap(WorldPoint w) {
        return worldToMinimap(w.getX(), w.getY());
    }

    public static Point worldToMinimap(double x, double y) {
        LocalPoint test = LocalPoint.fromWorld(Microbot.getClient(), (int) x, (int) y);
        if (test != null) {
            return Microbot.getClientThread().runOnClientThread(() -> Perspective.localToMinimap(Microbot.getClient(), test, 2500 * (int) Microbot.getClient().getMinimapZoom()));
        }
        return null;
    }

    public static Point worldToCanvas(double x, double y) {
        LocalPoint test = LocalPoint.fromWorld(Microbot.getClient(), (int) x, (int) y);
        if (test != null) {
            return Microbot.getClientThread().runOnClientThread(() -> Perspective.localToCanvas(Microbot.getClient(), test, Microbot.getClient().getPlane()));
        }
        return null;
    }


    /**
     * Returns the length of the path generated to a given RSTile.
     *
     * @param dest     The destination tile.
     * @param isObject <code>true</code> if reaching any tile adjacent to the destination
     *                 should be accepted.
     * @return <code>true</code> if reaching any tile adjacent to the destination
     * should be accepted.
     */
    public static int pathLengthTo(Tile dest, boolean isObject) {
        WorldPoint curPos = Microbot.getClient().getLocalPlayer().getWorldLocation();
        return pathLengthBetween(curPos, dest, isObject);
    }

    public static int pathLengthTo(WorldPoint dest, boolean isObject) {
        WorldPoint curPos = Microbot.getClient().getLocalPlayer().getWorldLocation();
        return pathLengthBetween(curPos, dest, isObject);
    }

    public static int pathLengthBetween(WorldPoint start, Tile dest, boolean isObject) {
        return dijkstraDist(start.getX() - Microbot.getClient().getBaseX(), // startX
                start.getY() - Microbot.getClient().getBaseY(), // startY
                dest.getWorldLocation().getX() - Microbot.getClient().getBaseX(), // destX
                dest.getWorldLocation().getY() - Microbot.getClient().getBaseY(), // destY
                isObject); // if it's an object, accept any adjacent tile
    }

    public static int pathLengthBetween(WorldPoint start, WorldPoint dest, boolean isObject) {
        return dijkstraDist(start.getX() - Microbot.getClient().getBaseX(), // startX
                start.getY() - Microbot.getClient().getBaseY(), // startY
                dest.getX() - Microbot.getClient().getBaseX(), // destX
                dest.getY() - Microbot.getClient().getBaseY(), // destY
                isObject); // if it's an object, accept any adjacent tile
    }

    /**
     * @param startX   the startX (0 < startX < 104)
     * @param startY   the startY (0 < startY < 104)
     * @param destX    the destX (0 < destX < 104)
     * @param destY    the destY (0 < destY < 104)
     * @param isObject if it's an object, it will find path which touches it.
     * @return The distance of the shortest path to the destination; or -1 if no
     * valid path to the destination was found.
     */
    private static int dijkstraDist(final int startX, final int startY, final int destX, final int destY,
                                    final boolean isObject) {
        final int[][] prev = new int[104][104];
        final int[][] dist = new int[104][104];
        final int[] path_x = new int[4000];
        final int[] path_y = new int[4000];
        for (int xx = 0; xx < 104; xx++) {
            for (int yy = 0; yy < 104; yy++) {
                prev[xx][yy] = 0;
                dist[xx][yy] = 99999999;
            }
        }
        int curr_x = startX;
        int curr_y = startY;
        prev[startX][startY] = 99;
        dist[startX][startY] = 0;
        int path_ptr = 0;
        int step_ptr = 0;
        path_x[path_ptr] = startX;
        path_y[path_ptr++] = startY;
        final byte blocks[][] = Microbot.getClient().getTileSettings()[Microbot.getClient().getPlane()];
        final int pathLength = path_x.length;
        boolean foundPath = false;
        while (step_ptr != path_ptr) {
            curr_x = path_x[step_ptr];
            curr_y = path_y[step_ptr];
            if (Math.abs(curr_x - destX) + Math.abs(curr_y - destY) == (isObject ? 1 : 0)) {
                foundPath = true;
                break;
            }
            step_ptr = (step_ptr + 1) % pathLength;
            final int cost = dist[curr_x][curr_y] + 1;
            // south
            if ((curr_y > 0) && (prev[curr_x][curr_y - 1] == 0) && ((blocks[curr_x + 1][curr_y] & 0x1280102) == 0)) {
                path_x[path_ptr] = curr_x;
                path_y[path_ptr] = curr_y - 1;
                path_ptr = (path_ptr + 1) % pathLength;
                prev[curr_x][curr_y - 1] = 1;
                dist[curr_x][curr_y - 1] = cost;
            }
            // west
            if ((curr_x > 0) && (prev[curr_x - 1][curr_y] == 0) && ((blocks[curr_x][curr_y + 1] & 0x1280108) == 0)) {
                path_x[path_ptr] = curr_x - 1;
                path_y[path_ptr] = curr_y;
                path_ptr = (path_ptr + 1) % pathLength;
                prev[curr_x - 1][curr_y] = 2;
                dist[curr_x - 1][curr_y] = cost;
            }
            // north
            if ((curr_y < 104 - 1) && (prev[curr_x][curr_y + 1] == 0) && ((blocks[curr_x + 1][curr_y + 2] &
                    0x1280120) == 0)) {
                path_x[path_ptr] = curr_x;
                path_y[path_ptr] = curr_y + 1;
                path_ptr = (path_ptr + 1) % pathLength;
                prev[curr_x][curr_y + 1] = 4;
                dist[curr_x][curr_y + 1] = cost;
            }
            // east
            if ((curr_x < 104 - 1) && (prev[curr_x + 1][curr_y] == 0) && ((blocks[curr_x + 2][curr_y + 1] &
                    0x1280180) == 0)) {
                path_x[path_ptr] = curr_x + 1;
                path_y[path_ptr] = curr_y;
                path_ptr = (path_ptr + 1) % pathLength;
                prev[curr_x + 1][curr_y] = 8;
                dist[curr_x + 1][curr_y] = cost;
            }
            // south west
            if ((curr_x > 0) && (curr_y > 0) && (prev[curr_x - 1][curr_y - 1] == 0) && ((blocks[curr_x][curr_y] &
                    0x128010e) == 0) && ((blocks[curr_x][curr_y + 1] & 0x1280108) == 0) && ((blocks[curr_x +
                    1][curr_y] & 0x1280102) == 0)) {
                path_x[path_ptr] = curr_x - 1;
                path_y[path_ptr] = curr_y - 1;
                path_ptr = (path_ptr + 1) % pathLength;
                prev[curr_x - 1][curr_y - 1] = 3;
                dist[curr_x - 1][curr_y - 1] = cost;
            }
            // north west
            if ((curr_x > 0) && (curr_y < 104 - 1) && (prev[curr_x - 1][curr_y + 1] == 0) && (
                    (blocks[curr_x][curr_y + 2] & 0x1280138) == 0) && ((blocks[curr_x][curr_y + 1] & 0x1280108) ==
                    0) && ((blocks[curr_x + 1][curr_y + 2] & 0x1280120) == 0)) {
                path_x[path_ptr] = curr_x - 1;
                path_y[path_ptr] = curr_y + 1;
                path_ptr = (path_ptr + 1) % pathLength;
                prev[curr_x - 1][curr_y + 1] = 6;
                dist[curr_x - 1][curr_y + 1] = cost;
            }
            // south east
            if ((curr_x < 104 - 1) && (curr_y > 0) && (prev[curr_x + 1][curr_y - 1] == 0) && ((blocks[curr_x +
                    2][curr_y] & 0x1280183) == 0) && ((blocks[curr_x + 2][curr_y + 1] & 0x1280180) == 0) && (
                    (blocks[curr_x + 1][curr_y] & 0x1280102) == 0)) {
                path_x[path_ptr] = curr_x + 1;
                path_y[path_ptr] = curr_y - 1;
                path_ptr = (path_ptr + 1) % pathLength;
                prev[curr_x + 1][curr_y - 1] = 9;
                dist[curr_x + 1][curr_y - 1] = cost;
            }
            // north east
            if ((curr_x < 104 - 1) && (curr_y < 104 - 1) && (prev[curr_x + 1][curr_y + 1] == 0) && ((blocks[curr_x
                    + 2][curr_y + 2] & 0x12801e0) == 0) && ((blocks[curr_x + 2][curr_y + 1] & 0x1280180) == 0) && (
                    (blocks[curr_x + 1][curr_y + 2] & 0x1280120) == 0)) {
                path_x[path_ptr] = curr_x + 1;
                path_y[path_ptr] = curr_y + 1;
                path_ptr = (path_ptr + 1) % pathLength;
                prev[curr_x + 1][curr_y + 1] = 12;
                dist[curr_x + 1][curr_y + 1] = cost;
            }
        }
        return foundPath ? dist[curr_x][curr_y] : -1;
    }

    public static void renderValidMovement() {
        Microbot.getClientThread().runOnClientThread(() -> {

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx == 0 && dy == 0) {
                        continue;
                    }
                    canWalk(dx, dy);
                }
            }
            return true;
        });
    }

    public static boolean canWalk(int dx, int dy) {
        return Microbot.getClientThread().runOnClientThread(() -> {
            WorldArea area = Microbot.getClient().getLocalPlayer().getWorldArea();
            if (area == null) {
                return false;
            }
            boolean canWalk = area.canTravelInDirection(Microbot.getClient(), dx, dy);
            if (!canWalk) return false;
            LocalPoint lp = Microbot.getClient().getLocalPlayer().getLocalLocation();
            if (lp == null) {
                return false;
            }

            lp = new LocalPoint(
                    lp.getX() + dx * Perspective.LOCAL_TILE_SIZE + dx * Perspective.LOCAL_TILE_SIZE * (area.getWidth() - 1) / 2,
                    lp.getY() + dy * Perspective.LOCAL_TILE_SIZE + dy * Perspective.LOCAL_TILE_SIZE * (area.getHeight() - 1) / 2);

            Polygon poly = Perspective.getCanvasTilePoly(Microbot.getClient(), lp);
            if (poly == null) {
                return false;
            }
            return true;
        });
    }
}
