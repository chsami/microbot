package net.runelite.client.plugins.microbot.util.walker;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Setter;
import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.shortestpath.ShortestPathConfig;
import net.runelite.client.plugins.microbot.shortestpath.ShortestPathPlugin;
import net.runelite.client.plugins.microbot.shortestpath.Transport;
import net.runelite.client.plugins.microbot.shortestpath.pathfinder.Pathfinder;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.menu.NewMenuEntry;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.ui.overlay.worldmap.WorldMapPoint;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static net.runelite.client.plugins.microbot.util.Global.sleep;
import static net.runelite.client.plugins.microbot.util.Global.sleepUntil;

public class Rs2Walker {
    static int stuckCount = 0;
    static WorldPoint lastPosition;
    @Setter
    public static ShortestPathConfig config;

    public static boolean walkTo(WorldPoint target) {
        if (Rs2Player.getWorldLocation().distanceTo(target) <= 1) {
            return true;
        }
        if (ShortestPathPlugin.getMarker() != null) return false;
        setTarget(target);
        Microbot.getClientThread().runOnSeperateThread(() -> {
            while (true) {
                if (!Microbot.isLoggedIn()) {
                    setTarget(null);
                    break;
                }
                if (ShortestPathPlugin.getPathfinder() == null) {
                    Microbot.status = "Waiting for pathfinder...";
                    continue;
                }
                if (!ShortestPathPlugin.getPathfinder().isDone()) {
                    Microbot.status = "Waiting for path calculation...";
                    continue;
                }

                if (isNear(ShortestPathPlugin.getPathfinder().getPath().get(ShortestPathPlugin.getPathfinder().getPath().size() - 1))) {
                    setTarget(null);
                    break;
                }

                List<WorldPoint> path = ShortestPathPlugin.getPathfinder().getPath();
                int indexOfStartPoint = getClosestTileIndex(path);

                //sleep(50, 150);

                //TODO: investigate why this happens
                if (Rs2Player.getWorldLocation().distanceTo(target) == 0)
                    break;

                /**
                 * MAIN WALK LOOP
                 */
                for (int i = indexOfStartPoint; i < ShortestPathPlugin.getPathfinder().getPath().size(); i++) {
                    WorldPoint currentWorldPoint = ShortestPathPlugin.getPathfinder().getPath().get(i);

                    /**
                     * CHECK DOORS
                     */
                    Microbot.status = "Checking for doors...";
                    handleDoors(path, indexOfStartPoint);

                    if (!Microbot.getClient().isInInstancedRegion()) {
                        Microbot.status = "Checking for transports...";
                        handleTransports(path, indexOfStartPoint);
                    }

                    if (Rs2Player.getWorldLocation().distanceTo2D(target) <= config.recalculateDistance()) {
                        Rs2Walker.walkFastCanvas(target);
                        break;
                    }
                    System.out.println(currentWorldPoint.distanceTo2D(Rs2Player.getWorldLocation()));
                    if (currentWorldPoint.distanceTo2D(Rs2Player.getWorldLocation()) > config.recalculateDistance()) {
                        // InstancedRegions require localPoint instead of worldpoint to navigate
                        if (Microbot.getClient().isInInstancedRegion()) {
                            Rs2Walker.walkFastCanvas(currentWorldPoint);
                            sleep(600, 1000);
                        } else {
                            Rs2Walker.walkMiniMap(currentWorldPoint);
                            sleepUntil(() -> currentWorldPoint.distanceTo2D(Rs2Player.getWorldLocation()) < Random.random(3, 6));
                        }
                        //avoid tree attacking you in draynor
                        if (stuckCount > 5) {
                            Rs2Walker.walkFastCanvas(currentWorldPoint);
                            stuckCount = 0;
                        }
                        if (Rs2Player.getWorldLocation().equals(lastPosition)) {
                            stuckCount++;
                        } else {
                            lastPosition = Rs2Player.getWorldLocation();
                        }
                    }
                }
            }
            return true;
        });
        return false;
    }

    /**
     * @param target
     * @return
     */
    private static boolean isNear(WorldPoint target) {
        return Rs2Player.getWorldLocation().equals(target);
    }


    public static boolean walkMiniMap(WorldPoint worldPoint) {
        if (Microbot.getClient().getMinimapZoom() > 2)
            Microbot.getClient().setMinimapZoom(2);

        Point point = Rs2MiniMap.worldToMinimap(worldPoint);

        if (point == null) return false;

        Microbot.getMouse().click(point);

        return true;
    }

    /**
     * Used in instances like vorkath, jad
     *
     * @param localPoint
     */
    public static void walkFastLocal(LocalPoint localPoint) {
        Point canv = Perspective.localToCanvas(Microbot.getClient(), localPoint, Microbot.getClient().getPlane());
        int canvasX = canv != null ? canv.getX() : -1;
        int canvasY = canv != null ? canv.getY() : -1;

        Microbot.doInvoke(new NewMenuEntry(canvasX, canvasY, MenuAction.WALK.getId(), 0, -1, "Walk here"), new Rectangle(1, 1, Microbot.getClient().getCanvasWidth(), Microbot.getClient().getCanvasHeight()));
        //Rs2Reflection.invokeMenu(canvasX, canvasY, MenuAction.WALK.getId(), 0, -1, "Walk here", "", -1, -1);
    }

    public static void walkFastCanvas(WorldPoint worldPoint) {
        Rs2Player.toggleRunEnergy(true);
        Point canv;
        if (Microbot.getClient().isInInstancedRegion()) {
            worldPoint = WorldPoint.toLocalInstance(Microbot.getClient(), worldPoint).stream().findFirst().get();
            LocalPoint localPoint = LocalPoint.fromWorld(Microbot.getClient(), worldPoint);
            canv = Perspective.localToCanvas(Microbot.getClient(), localPoint, Microbot.getClient().getPlane());
        } else {
            canv = Perspective.localToCanvas(Microbot.getClient(), LocalPoint.fromScene(worldPoint.getX() - Microbot.getClient().getBaseX(), worldPoint.getY() - Microbot.getClient().getBaseY()), Microbot.getClient().getPlane());

        }

        int canvasX = canv != null ? canv.getX() : -1;
        int canvasY = canv != null ? canv.getY() : -1;

        Microbot.doInvoke(new NewMenuEntry(canvasX, canvasY, MenuAction.WALK.getId(), 0, -1, "Walk here"), new Rectangle(1, 1, Microbot.getClient().getCanvasWidth(), Microbot.getClient().getCanvasHeight()));
        //Rs2Reflection.invokeMenu(canvasX, canvasY, MenuAction.WALK.getId(), 0, -1, "Walk here", "", -1, -1);
    }

    public static WorldPoint walkCanvas(WorldPoint worldPoint) {
        Point point = Perspective.localToCanvas(Microbot.getClient(), LocalPoint.fromWorld(Microbot.getClient(), worldPoint), Microbot.getClient().getPlane());

        if (point == null) return null;

        Microbot.getMouse().click(point);

        return worldPoint;
    }

    public static boolean canReach(WorldPoint target) {
        return new WorldArea(
                target,
                1,
                1)
                .hasLineOfSightTo(Microbot.getClient(), Microbot.getClient().getLocalPlayer().getWorldLocation().toWorldArea());
    }

    public static boolean isCloseToRegion(int distance, int regionX, int regionY) {
        WorldPoint worldPoint = WorldPoint.fromRegion(Microbot.getClient().getLocalPlayer().getWorldLocation().getRegionID(),
                regionX,
                regionY,
                Microbot.getClient().getPlane());

        return worldPoint.distanceTo(Microbot.getClient().getLocalPlayer().getWorldLocation()) < distance;
    }

    public static int distanceToRegion(int regionX, int regionY) {
        WorldPoint worldPoint = WorldPoint.fromRegion(Microbot.getClient().getLocalPlayer().getWorldLocation().getRegionID(),
                regionX,
                regionY,
                Microbot.getClient().getPlane());

        return worldPoint.distanceTo(Microbot.getClient().getLocalPlayer().getWorldLocation());
    }

    /**
     * @param path
     * @param indexOfStartPoint
     * @return
     */
    private static boolean handleDoors(List<WorldPoint> path, int indexOfStartPoint) {
        for (int i = indexOfStartPoint; i < path.size(); i++) {
            WorldPoint currentWorldPoint = path.get(i);
            //TODO: Make this more efficient, something like only calculate tile on screen?

//            if (currentWorldPoint.distanceTo(Rs2Player.getWorldLocation()) > config.recalculateDistance())
//                continue;

            WorldPoint nextWorldPoint = null;

            if (ShortestPathPlugin.getPathfinder() == null) continue;

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
    public static int getClosestTileIndex(List<WorldPoint> path) {
        WorldPoint startPoint;

        startPoint = path.stream()
                .min(Comparator.comparingInt(a -> Math.abs(a.distanceTo(Rs2Player.getWorldLocation()))))
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
    public static void setTarget(WorldPoint target) {
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
     * @param start
     * @param end
     */
    public static void restartPathfinding(WorldPoint start, WorldPoint end) {
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
        LocalPoint a;
        if (Microbot.getClient().isInInstancedRegion()) {
            WorldPoint instancedWorldPoint = WorldPoint.toLocalInstance(Microbot.getClient(), point).stream().findFirst().get();
            a = LocalPoint.fromWorld(Microbot.getClient(), instancedWorldPoint);
        } else {
            a = LocalPoint.fromWorld(Microbot.getClient(), point);
        }
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
    public static boolean handleTransports(List<WorldPoint> path, int indexOfStartPoint) {
        for (WorldPoint a : ShortestPathPlugin.getTransports().keySet()) {

            //TODO: Make this more efficient, something like only calculate tile on screen?
//            if (a.distanceTo(Rs2Player.getWorldLocation()) > config.recalculateDistance()) {
//                continue;
//            }

            for (Transport b : ShortestPathPlugin.getTransports().getOrDefault(a, new ArrayList<>())) {
                for (WorldPoint origin : WorldPoint.toLocalInstance(Microbot.getClient(), b.getOrigin())) {

                    if (Rs2Player.getWorldLocation().getPlane() != b.getOrigin().getPlane()) {
                        continue;
                    }

                    for (int i = indexOfStartPoint; i < path.size(); i++) {
                        if (origin.getPlane() != Rs2Player.getWorldLocation().getPlane())
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

                            if (b.getDestination().distanceTo(Rs2Player.getWorldLocation()) > 20) {
                                handleTrapdoor(b);
                            }

//                            GameObject gameObject = Rs2GameObject.getGameObject(path.get(i));
//                            if (gameObject == null || gameObject.getId() != b.getObjectId())
//                                continue;

                            Rs2GameObject.interact(b.getObjectId(), b.getAction());
                            return true;
                        }

                    }
                }
            }
        }
        return false;
    }

    private static boolean handleTrapdoor(Transport b) {
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
}
