package net.runelite.client.plugins.microbot.shortestpath;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.shortestpath.pathfinder.Pathfinder;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.ui.overlay.worldmap.WorldMapPoint;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 *
 */
public class WalkerScript extends Script {

    public static String version = "1.0";

    /**
     *
     * @param target
     * @return
     */
    private boolean isNear(WorldPoint target) {
        return Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(target) <= 1;
    }

    /**
     *
     * @param target
     * @return
     */
    public boolean walkTo(WorldPoint target) {
        if (ShortestPathPlugin.getPathfinder() != null) return false;
        if (mainScheduledFuture != null && !mainScheduledFuture.isDone()) {
            shutdown();
        }
        setTarget(target);
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {
                if (!Microbot.isLoggedIn()) return;
                if (ShortestPathPlugin.getPathfinder() == null) {
                    shutdown();
                    return;
                }
                List<WorldPoint> path = ShortestPathPlugin.getPathfinder().getPath();
                int indexOfStartPoint = getClosestTileIndex(path);
                /**
                 * CHECK DOORS
                 */
                boolean foundDoor = handleDoors(path, indexOfStartPoint);
                if (foundDoor)
                    return;
                boolean foundTransport = handleTransports(path, indexOfStartPoint);
                if (foundTransport)
                    return;
                /**
                 * MAIN WALK LOOP
                 */
                for (int i = indexOfStartPoint; i < ShortestPathPlugin.getPathfinder().getPath().size(); i++) {
                    WorldPoint currentWorldPoint = ShortestPathPlugin.getPathfinder().getPath().get(i);

                    if (isNear(target)) {
                        shutdown();
                        break;
                    }
                    if (Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo2D(target) < 12) {
                        Microbot.getWalker().walkFastCanvas(target);
                        break;
                    }
                    if (currentWorldPoint.distanceTo2D(Microbot.getClient().getLocalPlayer().getWorldLocation()) > Random.random(8, 12)) {
                        Microbot.getWalker().walkMiniMap(currentWorldPoint);
                        System.out.println("Click minimap!");
                        sleepUntil(() -> currentWorldPoint.distanceTo2D(Microbot.getClient().getLocalPlayer().getWorldLocation()) < Random.random(3, 6));
                    }
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 2000, TimeUnit.MILLISECONDS);
        return true;
    }

    /**
     *
     * @param path
     * @param indexOfStartPoint
     * @return
     */
    private boolean handleDoors(List<WorldPoint> path, int indexOfStartPoint) {
        for (int i = indexOfStartPoint; i < path.size(); i++) {
            WorldPoint currentWorldPoint = path.get(i);
            WorldPoint nextWorldPoint = null;

            if (currentWorldPoint.distanceTo(Microbot.getClient().getLocalPlayer().getWorldLocation()) > 12) continue;

            if (i + 1 < path.size()) {
                nextWorldPoint = ShortestPathPlugin.getPathfinder().getPath().get(i + 1);
            }
            if (nextWorldPoint == null) continue;

            boolean hasDoor = isDoorPresent(currentWorldPoint, nextWorldPoint);
            if (hasDoor) {
                WallObject wallObject = null;
                Tile currentTile = getTile(currentWorldPoint);
                if (currentTile != null) {
                    wallObject = currentTile.getWallObject();
                }
                if (wallObject == null) {
                    Tile nextTile = getTile(nextWorldPoint);
                    if (nextTile != null) {
                        wallObject = nextTile.getWallObject();
                    }
                }
                if (wallObject != null) {
                    Rs2GameObject.interact(wallObject);
                    Rs2Player.waitForWalking();
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    /**
     *
     * @param path
     * @return
     */
    public int getClosestTileIndex(List<WorldPoint> path) {
        WorldPoint startPoint = path.stream()
                .min(Comparator.comparingInt(a -> Math.abs(a.distanceTo(Microbot.getClient().getLocalPlayer().getWorldLocation()))))
                .orElse(null);

        return IntStream.range(0, path.size())
                .filter(i -> path.get(i).equals(startPoint))
                .findFirst()
                .orElse(0);
    }

    /**
     *
     * @param start
     */
    public void setStart(WorldPoint start) {
        if (ShortestPathPlugin.getPathfinder() == null) {
            return;
        }
        ShortestPathPlugin.setStartPointSet(true);
        restartPathfinding(start, ShortestPathPlugin.getPathfinder().getTarget());
    }

    /**
     *
     * @param target
     */
    public void setTarget(WorldPoint target) {
        Player localPlayer = Microbot.getClient().getLocalPlayer();
        if (!ShortestPathPlugin.isStartPointSet() && localPlayer == null) {
            return;
        }

        if (target == null) {
            synchronized (ShortestPathPlugin.getPathfinderMutex()) {
                if (ShortestPathPlugin.getPathfinder() != null) {
                    ShortestPathPlugin.getPathfinder().cancel();
                }
                ShortestPathPlugin.setPathfinder(null);
            }

            Microbot.getWorldMapPointManager().remove(ShortestPathPlugin.getMarker());
            ShortestPathPlugin.setMarker(null);
            ShortestPathPlugin.setStartPointSet(false);
        } else {
            Microbot.getWorldMapPointManager().removeIf(x -> x == ShortestPathPlugin.getMarker());
            ShortestPathPlugin.setMarker(new WorldMapPoint(target, ShortestPathPlugin.MARKER_IMAGE));
            ShortestPathPlugin.getMarker().setName("Target");
            ShortestPathPlugin.getMarker().setTarget(ShortestPathPlugin.getMarker().getWorldPoint());
            ShortestPathPlugin.getMarker().setJumpOnClick(true);
            Microbot.getWorldMapPointManager().add(ShortestPathPlugin.getMarker());

            WorldPoint start = Microbot.getClient().isInInstancedRegion() ?
                    WorldPoint.fromLocalInstance(Microbot.getClient(), localPlayer.getLocalLocation()) : localPlayer.getWorldLocation();
            ShortestPathPlugin.setLastLocation(start);
            if (ShortestPathPlugin.isStartPointSet() && ShortestPathPlugin.getPathfinder() != null) {
                start = ShortestPathPlugin.getPathfinder().getStart();
            }
            restartPathfinding(start, target);
        }
    }

    /**
     *
     * @param start
     * @param end
     */
    public void restartPathfinding(WorldPoint start, WorldPoint end) {
        synchronized (ShortestPathPlugin.getPathfinderMutex()) {
            if (ShortestPathPlugin.getPathfinder() != null) {
                ShortestPathPlugin.getPathfinder().cancel();
                ShortestPathPlugin.getPathfinderFuture().cancel(true);
            }

            if (ShortestPathPlugin.getPathfindingExecutor() == null) {
                ThreadFactory shortestPathNaming = new ThreadFactoryBuilder().setNameFormat("shortest-path-%d").build();
                ShortestPathPlugin.setPathfindingExecutor(Executors.newSingleThreadExecutor(shortestPathNaming));
            }
        }

        Microbot.getClientThread().invokeLater(() -> {
            ShortestPathPlugin.getPathfinderConfig().refresh();
            synchronized (ShortestPathPlugin.getPathfinderMutex()) {
                ShortestPathPlugin.setPathfinder(new Pathfinder(ShortestPathPlugin.getPathfinderConfig(), start, end));
                ShortestPathPlugin.setPathfinderFuture(ShortestPathPlugin.getPathfindingExecutor().submit(ShortestPathPlugin.getPathfinder()));
            }
        });
    }

    /**
     * TODO: REFACTOR DUPLICATE CODE
     * @param a
     * @param b
     * @return
     */
    public static boolean isDoorPresent(WorldPoint a, WorldPoint b) {
        Tile currentTile = getTile(a);
        WallObject wallObject;
        if (currentTile != null) {
            wallObject = currentTile.getWallObject();
        } else {
            wallObject = null;
        }
        if (wallObject == null)
            return false;

        ObjectComposition objectComposition = Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getObjectDefinition(wallObject.getId()));
        if (objectComposition == null) {
            return false;
        }
        boolean found = false;
        for (String action : objectComposition.getActions()) {
            if (action != null && action.equals("Open")) {
                found = true;
                break;
            }
        }
        if (!found) {
            return false;
        }
        int orientation = wallObject.getOrientationA();
        if (orientation == 1) {
            //blocks west
            if (a.dx(-1).equals(b)) {
                return true;
            }
        }
        if (orientation == 4) {
            //blocks east
            if (a.dx(+1).equals(b)) {
                return true;
            }
        }
        if (orientation == 2) {
            //blocks north
            if (a.dy(1).equals(b)) {
                return true;
            }
        }
        if (orientation == 8) {
            //blocks south
            return a.dy(-1).equals(b);
        }
        Tile nextTile = getTile(b);
        WallObject wallObjectb;
        if (nextTile != null) {
            wallObjectb = currentTile.getWallObject();
        } else {
            wallObjectb = null;
        }
        if (wallObjectb == null) {
            return false;
        }
        ObjectComposition objectCompositionb = Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getObjectDefinition(wallObjectb.getId()));
        if (objectCompositionb == null) {
            return false;
        }
        boolean foundb = false;
        for (String action : objectCompositionb.getActions()) {
            if (action != null && action.equals("Open")) {
                foundb = true;
                break;
            }
        }
        if (!foundb) {
            return false;
        }
        int orientationb = wallObjectb.getOrientationA();
        if (orientationb == 1) {
            //blocks east
            if (b.dx(-1).equals(a)) {
                return true;
            }
        }
        if (orientationb == 4) {
            //blocks south
            if (b.dx(+1).equals(a)) {
                return true;
            }
        }
        if (orientationb == 2) {
            //blocks south
            if (b.dy(+1).equals(a)) {
                return true;
            }
        }
        if (orientationb == 8) {
            //blocks north
            return b.dy(-1).equals(a);
        }
        return false;
    }

    /**
     *
     * @param point
     * @return
     */
    public static Tile getTile(WorldPoint point) {
        LocalPoint a = LocalPoint.fromWorld(Microbot.getClient(), point);
        if (a == null) {
            return null;
        }
        return Microbot.getClient().getScene().getTiles()[point.getPlane()][a.getSceneX()][a.getSceneY()];
    }

    /**
     *
     * @param path
     * @param indexOfStartPoint
     * @return
     */
    public boolean handleTransports(List<WorldPoint> path, int indexOfStartPoint) {
        for (WorldPoint a : ShortestPathPlugin.getTransports().keySet()) {

            if (a.distanceTo(Microbot.getClient().getLocalPlayer().getWorldLocation()) > 12) {
                continue;
            }

            for (Transport b : ShortestPathPlugin.getTransports().getOrDefault(a, new ArrayList<>())) {
                for (WorldPoint origin : WorldPoint.toLocalInstance(Microbot.getClient(), b.getOrigin())) {

                    if (Microbot.getClient().getLocalPlayer().getWorldLocation().getPlane() != b.getOrigin().getPlane()) {
                        continue;
                    }

                    for (int i = indexOfStartPoint; i < path.size(); i++) {
                        if (origin.getPlane() != Microbot.getClient().getLocalPlayer().getWorldLocation().getPlane()) continue;
                        if (path.stream().noneMatch(x -> x.equals(b.getDestination()))) continue;

                        int indexOfOrigin = IntStream.range(0, path.size())
                                .filter(f -> path.get(f).equals(b.getOrigin()))
                                .findFirst()
                                .orElse(0);
                        int indexOfDestination = IntStream.range(0, path.size())
                                .filter(f -> path.get(f).equals(b.getDestination()))
                                .findFirst()
                                .orElse(0);
                        if (indexOfDestination < indexOfOrigin) continue;

                        if (path.get(i).equals(origin)
                                || path.get(i).dx(-1).equals(origin)
                                || path.get(i).dx(+1).equals(origin)
                                || path.get(i).dy(-1).equals(origin)
                                || path.get(i).dy(+1).equals(origin)) {
                            Rs2GameObject.interact(b.getObjectId());
                            Rs2Player.waitForWalking();
                            Rs2GameObject.interact(b.getObjectId(), b.getAction());
                            return true;
                        }

                    }
                }
            }
        }
        return false;
    }

    private boolean doesExistOnSCreen(WorldPoint b) {
        if (b.getPlane() != Microbot.getClient().getPlane()) {
            return false;
        }

        LocalPoint lp = LocalPoint.fromWorld(Microbot.getClient(), b);
        if (lp == null) {
            return false;
        }

        Polygon poly = Perspective.getCanvasTilePoly(Microbot.getClient(), lp);
        if (poly == null) {
            return false;
        }

        return true;
    }

}
