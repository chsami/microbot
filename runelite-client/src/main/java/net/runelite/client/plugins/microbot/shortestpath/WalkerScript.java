package net.runelite.client.plugins.microbot.shortestpath;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.shortestpath.pathfinder.Pathfinder;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.ui.overlay.worldmap.WorldMapPoint;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class handles all the complex code for navigating and path finding in osrs
 */
public class WalkerScript extends Script {

    public static String version = "1.2";

    int stuckCount = 0;

    WorldPoint lastPosition;

    ShortestPathConfig config;

    public WalkerScript(ShortestPathConfig config) {
        this.config = config;
    }

    /**
     * @param target
     * @return
     */
    private boolean isNear(WorldPoint target) {
        return Microbot.getClient().getLocalPlayer().getWorldLocation().equals(target);
    }

    /**
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
                if (!ShortestPathPlugin.getPathfinder().isDone())
                    return;

                List<WorldPoint> path = ShortestPathPlugin.getPathfinder().getPath();
                int indexOfStartPoint = getClosestTileIndex(path);

                /**
                 * MAIN WALK LOOP
                 */
                for (int i = indexOfStartPoint; i < ShortestPathPlugin.getPathfinder().getPath().size(); i++) {
                    WorldPoint currentWorldPoint = ShortestPathPlugin.getPathfinder().getPath().get(i);

                    if (isNear(target)) {
                        shutdown();
                        break;
                    }

                    /**
                     * CHECK DOORS
                     */
                    Microbot.status = "Checking for doors...";
                    boolean foundDoor = handleDoors(path, indexOfStartPoint);
                    if (foundDoor)
                        return;
                    Microbot.status = "Checking for transports...";
                    boolean foundTransport = handleTransports(path, indexOfStartPoint);
                    if (foundTransport)
                        return;

                    if (Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo2D(target) <= config.reachedDistance()) {
                        Rs2Walker.walkFastCanvas(target);
                        break;
                    }
                    if (currentWorldPoint.distanceTo2D(Microbot.getClient().getLocalPlayer().getWorldLocation()) > config.reachedDistance()) {
                        Rs2Walker.walkMiniMap(currentWorldPoint);
                        sleepUntil(() -> currentWorldPoint.distanceTo2D(Microbot.getClient().getLocalPlayer().getWorldLocation()) < Random.random(3, 6));
                        //avoid tree attacking you in draynor
                        if (stuckCount > 5) {
                            Rs2Walker.walkFastCanvas(currentWorldPoint);
                            stuckCount = 0;
                        }
                        if (Microbot.getClient().getLocalPlayer().getWorldLocation().equals(lastPosition)) {
                            stuckCount++;
                        } else {
                            lastPosition = Microbot.getClient().getLocalPlayer().getWorldLocation();
                        }
                    }
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 2000, TimeUnit.MILLISECONDS);
        return true;
    }

    /**
     * @param path
     * @param indexOfStartPoint
     * @return
     */
    private boolean handleDoors(List<WorldPoint> path, int indexOfStartPoint) {
        for (int i = indexOfStartPoint; i < path.size(); i++) {
            WorldPoint currentWorldPoint = path.get(i);
            if (path.get(i).distanceTo(Microbot.getClient().getLocalPlayer().getWorldLocation()) > config.reachedDistance())
                continue;
            WorldPoint nextWorldPoint = null;

            if (ShortestPathPlugin.getPathfinder() == null) continue;

            if (currentWorldPoint.distanceTo(Microbot.getClient().getLocalPlayer().getWorldLocation()) > config.recalculateDistance())
                continue;

            if (i + 1 < path.size()) {
                nextWorldPoint = ShortestPathPlugin.getPathfinder().getPath().get(i + 1);
            }
            //TODO: figure out how to handle doors being on the next world location instead of the current one
            //So a could be null because pos a had no object and b could be wallobject because the wallobject comes a tile further
           /* if (i > 0) {
                previousWorldPoint = ShortestPathPlugin.getPathfinder().getPath().get(i -1);
            }*/
            if (nextWorldPoint == null) continue;

            boolean hasDoor = isDoorPresent(currentWorldPoint, nextWorldPoint);

         /*   if (!hasDoor)
                hasDoor = isDoorPresent(currentWorldPoint, previousWorldPoint);*/

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
            shutdown();
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
     *
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
//        if (wallObject == null)
//            return false;

        if (wallObject != null) {
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
        }

        Tile nextTile = getTile(b);
        WallObject wallObjectb;
        if (nextTile != null) {
            wallObjectb = nextTile.getWallObject();
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
     * @param path
     * @param indexOfStartPoint
     * @return
     */
    public boolean handleTransports(List<WorldPoint> path, int indexOfStartPoint) {
        for (WorldPoint a : ShortestPathPlugin.getTransports().keySet()) {

            if (a.distanceTo(Microbot.getClient().getLocalPlayer().getWorldLocation()) > config.reachedDistance()) {
                continue;
            }

            for (Transport b : ShortestPathPlugin.getTransports().getOrDefault(a, new ArrayList<>())) {
                for (WorldPoint origin : WorldPoint.toLocalInstance(Microbot.getClient(), b.getOrigin())) {

                    if (Microbot.getClient().getLocalPlayer().getWorldLocation().getPlane() != b.getOrigin().getPlane()) {
                        continue;
                    }

                    for (int i = indexOfStartPoint; i < path.size(); i++) {
                        if (origin.getPlane() != Microbot.getClient().getLocalPlayer().getWorldLocation().getPlane())
                            continue;
                        if (path.stream().noneMatch(x -> x.equals(b.getDestination()))) continue;

                        int indexOfOrigin = IntStream.range(0, path.size())
                                .filter(f -> path.get(f).equals(b.getOrigin()))
                                .findFirst()
                                .orElse(-1);
                        int indexOfDestination = IntStream.range(0, path.size())
                                .filter(f -> path.get(f).equals(b.getDestination()))
                                .findFirst()
                                .orElse(-1);
                        if (indexOfDestination == -1) continue;
                        if (indexOfOrigin == -1) continue;
                        if (indexOfDestination < indexOfOrigin) continue;

                        if (path.get(i).equals(origin)) {

                            if (b.getDestination().distanceTo(Microbot.getClient().getLocalPlayer().getWorldLocation()) > 20) {
                                handleTrapdoor(b);
                            }

//                            GameObject gameObject = Rs2GameObject.getGameObject(path.get(i));
//                            if (gameObject == null || gameObject.getId() != b.getObjectId())
//                                continue;

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

    private boolean handleTrapdoor(Transport b) {
        List<GroundObject> gameObjects = Rs2GameObject.getGroundObjects(25);
        gameObjects = gameObjects.stream().filter(g -> Rs2GameObject.getObjectIdsByName("trapdoor").stream().anyMatch(x -> g.getId() == x)).collect(Collectors.toList());
        GroundObject trapdoor = gameObjects
                .stream()
                .map(x -> {
                    ObjectComposition objectComposition = Rs2GameObject.convertGameObjectToObjectComposition(x.getId());
                    if (objectComposition == null) {
                        return null;
                    }
                    boolean isTrapdoorNearOrigin = x.getWorldLocation().distanceTo(b.getOrigin()) <= 5;
                    if (!isTrapdoorNearOrigin) {
                        return null;
                    }
                    boolean hasOpenAction = !Arrays.stream(objectComposition.getActions()).filter(Objects::nonNull).filter(o -> o.toLowerCase().contains("open")).findFirst().orElse("").isEmpty();
                    if (!hasOpenAction) {
                        return null;
                    }
                    return x;
                }).filter(Objects::nonNull)
                .findFirst().orElse(null);
        if (trapdoor != null) {
            Rs2GameObject.interact(trapdoor, "open");
            Rs2Player.waitForAnimation();
            return true;
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
