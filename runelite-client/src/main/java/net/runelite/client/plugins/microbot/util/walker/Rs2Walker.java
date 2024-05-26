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
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.menu.NewMenuEntry;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.ui.overlay.worldmap.WorldMapPoint;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static net.runelite.client.plugins.microbot.util.Global.*;



public class Rs2Walker {
    static int stuckCount = 0;
    static WorldPoint lastPosition;
    @Setter
    public static ShortestPathConfig config;
    static int idle = 0;

    static WorldPoint currentTarget;

    private static ExecutorService pathfindingExecutor = Executors.newSingleThreadExecutor();

    public static boolean walkTo(WorldPoint target) {
        return walkTo(target, 6);
    }

    public static boolean walkTo(WorldPoint target, int distance) {
        if (Rs2Player.getWorldLocation().distanceTo(target) < distance) {
            return true;
        }
        if (currentTarget != null && currentTarget.equals(target) && ShortestPathPlugin.getMarker() != null && !Microbot.getClientThread().scheduledFuture.isDone())
            return false;
        setTarget(target);
        stuckCount = 0;
        idle = 0;
        Microbot.getClientThread().runOnSeperateThread(() -> {
            while (true) {
                if (!Microbot.isLoggedIn()) {
                    setTarget(null);
                    break;
                }
                if (ShortestPathPlugin.getPathfinder() == null) {
                    if (ShortestPathPlugin.getMarker() == null)
                        break;
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

                if (stuckCount > 10) {
                    setTarget(null);
                }

                List<WorldPoint> path = ShortestPathPlugin.getPathfinder().getPath();
                int indexOfStartPoint = getClosestTileIndex(path);
                lastPosition = Rs2Player.getWorldLocation();

                if (Rs2Player.getWorldLocation().distanceTo(target) == 0)
                    break;

                /**
                 * MAIN WALK LOOP
                 */
                for (int i = indexOfStartPoint; i < ShortestPathPlugin.getPathfinder().getPath().size() - 1; i++) {
                    WorldPoint currentWorldPoint = ShortestPathPlugin.getPathfinder().getPath().get(i);
                    indexOfStartPoint = getClosestTileIndex(path);

                    /**
                     * CHECK DOORS
                     */
                    Microbot.status = "Checking for doors...";
                    long startTime = System.currentTimeMillis();

                    boolean doorOrTransportResult = handleDoors(currentWorldPoint, ShortestPathPlugin.getPathfinder().getPath().get(i + 1));
                    if (doorOrTransportResult) {
                        break;
                    }
                    long endTime = System.currentTimeMillis();
                    long totalTime = endTime - startTime;
                    System.out.println("Handling doors took " + totalTime + "ms");

                    if (!Microbot.getClient().isInInstancedRegion()) {
                        Microbot.status = "Checking for transports...";
                        startTime = System.currentTimeMillis();
                        doorOrTransportResult = handleTransports(path, indexOfStartPoint);
                        endTime = System.currentTimeMillis();
                        totalTime = endTime - startTime;
                        System.out.println("Handling transports took " + totalTime + "ms");
                    }

                    if (doorOrTransportResult)
                        break;

                    System.out.println(currentWorldPoint.distanceTo2D(Rs2Player.getWorldLocation()));
                    if (currentWorldPoint.distanceTo2D(Rs2Player.getWorldLocation()) > config.recalculateDistance()
                            || Rs2Player.getWorldLocation().distanceTo(target) < 12 && currentWorldPoint.distanceTo2D(Rs2Player.getWorldLocation()) > distance) {
                        // InstancedRegions require localPoint instead of worldpoint to navigate
                        if (Microbot.getClient().isInInstancedRegion()) {
                            Rs2Walker.walkFastCanvas(currentWorldPoint);
                            sleep(600, 1000);
                        } else {
                            Rs2Walker.walkMiniMap(currentWorldPoint);
                            int randomInt = Random.random(3, 5);
                            sleepUntilTrue(() -> currentWorldPoint.distanceTo2D(Rs2Player.getWorldLocation()) < randomInt, 100, 2000);
                            break;
                        }
                        //avoid tree attacking you in draynor
                        checkIfStuck();
                    }
                }

                if (Rs2Player.getWorldLocation().distanceTo(target) < 10) {
                    System.out.println("walk minimap");
                    Rs2Walker.walkMiniMap(target);
                    sleep(600, 1200);
                    System.out.println("sleep walk minimap");
                }
            }
            return Rs2Player.getWorldLocation().distanceTo(target) < distance;
        });
        return false;
    }

    public static boolean walkMiniMap(WorldPoint worldPoint, int zoomDistance) {
        if (Microbot.getClient().getMinimapZoom() != zoomDistance)
            Microbot.getClient().setMinimapZoom(zoomDistance);

        Point point = Rs2MiniMap.worldToMinimap(worldPoint);

        if (point == null) return false;

        Microbot.getMouse().click(point);

        return true;
    }


    public static boolean walkMiniMap(WorldPoint worldPoint) {
        return walkMiniMap(worldPoint, 5);
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
            canv = Perspective.localToCanvas(Microbot.getClient(), LocalPoint.fromScene(worldPoint.getX() - Microbot.getClient().getBaseX(), worldPoint.getY() - Microbot.getClient().getBaseY(), Microbot.getClient().getTopLevelWorldView().getScene()), Microbot.getClient().getPlane());
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

    // takes an avg 200-300 ms
    public static boolean canReach(WorldPoint worldPoint, int sizeX, int sizeY) {
        Pathfinder pathfinder = new Pathfinder(ShortestPathPlugin.getPathfinderConfig(), Rs2Player.getWorldLocation(), worldPoint);
        pathfindingExecutor.submit(pathfinder);
        sleepUntil(pathfinder::isDone);
        boolean result = new WorldArea(pathfinder.getPath().get(pathfinder.getPath().size() - 1), 2, 2)
                .intersectsWith(new WorldArea(worldPoint, sizeX + 2, sizeY + 2));
        return result;
    }

    public static boolean canReach(WorldPoint worldPoint) {
        return canReach(worldPoint, 1, 1);
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
     * @param currentWorldPoint
     * @param nextWorldPoint
     * @return
     */
    private static boolean handleDoors(WorldPoint currentWorldPoint, WorldPoint nextWorldPoint) {

        if (ShortestPathPlugin.getPathfinder() == null) return false;

        if (nextWorldPoint == null) return false;

        boolean hasDoor = isDoorPresent(currentWorldPoint, nextWorldPoint);

        if (!hasDoor) return false;

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

        if (wallObject != null && Rs2Camera.isTileOnScreen(wallObject)) {
            Rs2GameObject.interact(wallObject);
            Rs2Player.waitForWalking();
            return true;
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

        currentTarget = target;

        if (target == null) {
            synchronized (ShortestPathPlugin.getPathfinderMutex()) {
                if (ShortestPathPlugin.getPathfinder() != null) {
                    ShortestPathPlugin.getPathfinder().cancel();
                }
                ShortestPathPlugin.setPathfinder(null);
                System.out.println("reset path finder!");
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
            if (Microbot.getClientThread().scheduledFuture != null)
                Microbot.getClientThread().scheduledFuture.cancel(true);
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

        System.out.println("checking door: " + a + " b " + b);
        if (wallObject != null) {
            ObjectComposition objectComposition = Rs2GameObject.getObjectComposition(wallObject.getId());
            if (objectComposition == null) {
                return false;
            }
            boolean found = false;
            for (String action : objectComposition.getActions()) {
                if (action != null && (action.equals("Open") || action.contains("pay-toll"))) {
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
        ObjectComposition objectCompositionb = Rs2GameObject.getObjectComposition(wallObjectb.getId());
        if (objectCompositionb == null) {
            return false;
        }
        boolean foundb = false;
        for (String action : objectCompositionb.getActions()) {
            if (action != null && (action.equals("Open") || action.contains("pay-toll"))) {
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
        for (WorldPoint a : ShortestPathPlugin.getTransports().keySet()
                .stream()
                .filter(x -> x.distanceTo(Rs2Player.getWorldLocation()) <= 12)
                .sorted(Comparator.comparingInt(worldPoint -> worldPoint.distanceTo(Rs2Player.getWorldLocation())))
                .collect(Collectors.toList())) {

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

                            if (b.getDestination().distanceTo2D(Rs2Player.getWorldLocation()) > 20) {
                                handleTrapdoor(b);
                            }

                            GameObject gameObject = Rs2GameObject.getGameObjects(b.getObjectId(), b.getOrigin()).stream().findFirst().orElse(null);

                            if (gameObject != null && gameObject.getId() == b.getObjectId() && Rs2Camera.isTileOnScreen(gameObject)) {
                                if (Rs2GameObject.hasLineOfSight(gameObject)) {
                                    Rs2GameObject.interact(gameObject, b.getAction());
                                    sleep(1200, 1600);
                                    return true;
                                } else {
                                    Rs2Walker.walkFastCanvas(path.get(i));
                                    sleep(1200, 1600);
                                }
                            } else {
                                GroundObject groundObject = Rs2GameObject.getGroundObjects(b.getObjectId(), b.getOrigin()).stream().filter(x -> !x.getWorldLocation().equals(Rs2Player.getWorldLocation())).findFirst().orElse(null);
                                if (groundObject != null && groundObject.getId() == b.getObjectId() && Rs2Camera.isTileOnScreen(groundObject)) {
                                    if (Rs2GameObject.hasLineOfSight(groundObject)) {
                                        Rs2GameObject.interact(groundObject, b.getAction());
                                        if (b.isAgilityShortcut()) {
                                            Rs2Player.waitForAnimation();
                                        }
                                        sleep(1200, 1600);
                                        return true;
                                    } else {
                                        Rs2Walker.walkFastCanvas(path.get(i));
                                        sleep(1200, 1600);
                                    }
                                }
                            }
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

    public static boolean isNear() {
        WorldPoint playerLocation = Rs2Player.getWorldLocation();
        int index = IntStream.range(0, ShortestPathPlugin.getPathfinder().getPath().size())
                .filter(f -> ShortestPathPlugin.getPathfinder().getPath().get(f).distanceTo2D(playerLocation) < 3)
                .findFirst()
                .orElse(-1);
        return index >= ShortestPathPlugin.getPathfinder().getPath().size() - 10;
    }

    /**
     * @param target
     * @return
     */
    public static boolean isNear(WorldPoint target) {
        return Rs2Player.getWorldLocation().equals(target);
    }

    private static void checkIfStuck() {
        if (Rs2Player.getWorldLocation().equals(lastPosition)) {
            stuckCount++;
        } else {
            stuckCount = 0;
        }
    }
}
