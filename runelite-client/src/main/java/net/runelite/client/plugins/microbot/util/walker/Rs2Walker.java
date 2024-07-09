package net.runelite.client.plugins.microbot.util.walker;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Setter;
import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.devtools.MovementFlag;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.shortestpath.ShortestPathConfig;
import net.runelite.client.plugins.microbot.shortestpath.ShortestPathPlugin;
import net.runelite.client.plugins.microbot.shortestpath.Transport;
import net.runelite.client.plugins.microbot.shortestpath.pathfinder.Pathfinder;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.menu.NewMenuEntry;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.tile.Rs2Tile;
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
    private static final ExecutorService pathfindingExecutor = Executors.newSingleThreadExecutor();
    @Setter
    public static ShortestPathConfig config;
    static int stuckCount = 0;
    static WorldPoint lastPosition;
    static int idle = 0;
    static WorldPoint currentTarget;

    public static boolean walkTo(WorldArea area, int distanceThreshold) {
        if (area.distanceTo(Rs2Player.getWorldLocation()) > distanceThreshold){
            var points = area.toWorldPointList();
            var index = new java.util.Random().nextInt(points.size());
            return Rs2Walker.walkTo(points.get(index));
        }
        return true;
    }
    
    public static boolean walkTo(int x, int y, int plane) {
        return walkTo(x, y, plane, 6);
    }

    public static boolean walkTo(int x, int y, int plane, int distance) {
        return walkTo(new WorldPoint(x, y, plane), distance);
    }


    public static boolean walkTo(WorldPoint target) {
        return walkTo(target, config.reachedDistance());
    }

    public static boolean walkTo(WorldPoint target, int distance) {
        if (Rs2Tile.getReachableTilesFromTile(Rs2Player.getWorldLocation(), distance).containsKey(target)
                || !Rs2Tile.isWalkable(LocalPoint.fromWorld(Microbot.getClient().getTopLevelWorldView(), target)) && Rs2Player.getWorldLocation().distanceTo(target) <= distance) {
            return true;
        }
        if (currentTarget != null && currentTarget.equals(target) && ShortestPathPlugin.getMarker() != null && !Microbot.getClientThread().scheduledFuture.isDone())
            return false;
        setTarget(target);
        ShortestPathPlugin.setReachedDistance(distance);
        stuckCount = 0;
        idle = 0;
        Microbot.getClientThread().runOnSeperateThread(() -> {
            try {
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

                        if (!Rs2Tile.isTileReachable(currentWorldPoint) && !Microbot.getClient().isInInstancedRegion()) {
                            continue;
                        }

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
                            doorOrTransportResult = handleTransports(path, i);
                            endTime = System.currentTimeMillis();
                            totalTime = endTime - startTime;
                            System.out.println("Handling transports took " + totalTime + "ms");
                        }

                        if (doorOrTransportResult)
                            break;

                        if (currentWorldPoint.distanceTo2D(Rs2Player.getWorldLocation()) > config.recalculateDistance()
                                || Rs2Player.getWorldLocation().distanceTo(target) < 12 && currentWorldPoint.distanceTo2D(Rs2Player.getWorldLocation()) > distance) {
                            // InstancedRegions require localPoint instead of worldpoint to navigate
                            if (Microbot.getClient().isInInstancedRegion()) {
                                Rs2Walker.walkFastCanvas(currentWorldPoint);
                                sleep(600, 1000);
                            } else {
                                long movingStart = System.currentTimeMillis();
                                Rs2Walker.walkMiniMap(getPointWithWallDistance(currentWorldPoint));
                                int randomInt = Random.random(3, 5);
                                sleepUntilTrue(() -> currentWorldPoint.distanceTo2D(Rs2Player.getWorldLocation()) < randomInt, 100, 2000);
                                if (System.currentTimeMillis() - movingStart < 120) {
                                    sleep(600, 1000);
                                }
                                break;
                            }
                            //avoid tree attacking you in draynor
                            checkIfStuck();
                        }
                    }

                if (Rs2Tile.getReachableTilesFromTile(Rs2Player.getWorldLocation(), 12).containsKey(path.get(path.size() - 1))) {
                        System.out.println("walk minimap");

                        if (Microbot.getClient().isInInstancedRegion())
                            Rs2Walker.walkFastCanvas(target);
                        else
                            Rs2Walker.walkMiniMap(target);

                        sleep(600, 1200);
                        System.out.println("sleep walk minimap");
                    }
                }
                return Rs2Player.getWorldLocation().distanceTo(target) < distance;
            } catch(Exception ex) {
                Microbot.log("Microbot Walker Exception " + ex.getMessage());
            }
            return false;
        });
        return false;
    }

    public static WorldPoint getPointWithWallDistance(WorldPoint target){
        var tiles = Rs2Tile.getReachableTilesFromTile(target, 1);

        var localPoint = LocalPoint.fromWorld(Microbot.getClient().getTopLevelWorldView(), target);
        if (Microbot.getClient().getCollisionMaps() != null && localPoint != null) {
            int[][] flags = Microbot.getClient().getCollisionMaps()[Microbot.getClient().getPlane()].getFlags();

            if (hasMinimapRelevantMovementFlag(localPoint, flags)){
                for (var tile : tiles.keySet()){
                    var localTilePoint = LocalPoint.fromWorld(Microbot.getClient().getTopLevelWorldView(), tile);
                    if (localTilePoint == null)
                        continue;

                    if (!hasMinimapRelevantMovementFlag(localTilePoint, flags))
                        return tile;
                }
            }

            int data = flags[localPoint.getSceneX()][localPoint.getSceneY()];

            Set<MovementFlag> movementFlags = MovementFlag.getSetFlags(data);

            if (movementFlags.contains(MovementFlag.BLOCK_MOVEMENT_EAST)
                    || movementFlags.contains(MovementFlag.BLOCK_MOVEMENT_WEST)
                    || movementFlags.contains(MovementFlag.BLOCK_MOVEMENT_NORTH)
                    || movementFlags.contains(MovementFlag.BLOCK_MOVEMENT_SOUTH)){
                for (var tile : tiles.keySet()){
                    var localTilePoint = LocalPoint.fromWorld(Microbot.getClient().getTopLevelWorldView(), tile);
                    if (localTilePoint == null)
                        continue;

                    int tileData = flags[localTilePoint.getSceneX()][localTilePoint.getSceneY()];
                    Set<MovementFlag> tileFlags = MovementFlag.getSetFlags(data);

                    if (tileFlags.isEmpty())
                        return tile;
                }
            }
        }

        return target;
    }

    static boolean hasMinimapRelevantMovementFlag(LocalPoint point, int[][] flagMap){
        int data = flagMap[point.getSceneX()][point.getSceneY()];
        Set<MovementFlag> movementFlags = MovementFlag.getSetFlags(data);

        if (movementFlags.contains(MovementFlag.BLOCK_MOVEMENT_EAST)
            && Rs2Tile.isWalkable(point.dx(1)))
            return true;

        if (movementFlags.contains(MovementFlag.BLOCK_MOVEMENT_WEST)
                && Rs2Tile.isWalkable(point.dx(-1)))
            return true;

        if (movementFlags.contains(MovementFlag.BLOCK_MOVEMENT_NORTH)
                && Rs2Tile.isWalkable(point.dy(1)))
            return true;

        return movementFlags.contains(MovementFlag.BLOCK_MOVEMENT_SOUTH)
                && Rs2Tile.isWalkable(point.dy(-1));
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

    public static boolean walkMiniMap(WorldArea area) {
        var points = area.toWorldPointList();
        var index = new java.util.Random().nextInt(points.size());
        return Rs2Walker.walkMiniMap(points.get(index));
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

    public static void walkFastCanvas(WorldPoint worldPoint){
        walkFastCanvas(worldPoint, true);
    }

    public static void walkFastCanvas(WorldPoint worldPoint, boolean toogleRun) {
        Rs2Player.toggleRunEnergy(toogleRun);
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
    // Used mainly for agility, might have to tweak this for other stuff
    public static boolean canReach(WorldPoint worldPoint, int sizeX, int sizeY) {
        Pathfinder pathfinder = new Pathfinder(ShortestPathPlugin.getPathfinderConfig(), Rs2Player.getWorldLocation(), worldPoint);
        pathfindingExecutor.submit(pathfinder);
        sleepUntil(pathfinder::isDone);
        WorldArea pathArea = new WorldArea(pathfinder.getPath().get(pathfinder.getPath().size() - 1), 3, 3);
        WorldArea objectArea = new WorldArea(worldPoint, sizeX + 2, sizeY + 2);
        boolean result = pathArea
                .intersectsWith2D(objectArea);
        return result;
    }

    public static boolean canReach(WorldPoint worldPoint) {
        Pathfinder pathfinder = new Pathfinder(ShortestPathPlugin.getPathfinderConfig(), Rs2Player.getWorldLocation(), worldPoint);
        pathfindingExecutor.submit(pathfinder);
        sleepUntil(pathfinder::isDone);
        if (pathfinder.getPath().get(pathfinder.getPath().size() - 1).getPlane() != worldPoint.getPlane()) return false;
        WorldArea pathArea = new WorldArea(pathfinder.getPath().get(pathfinder.getPath().size() - 1), 2, 2);
        WorldArea objectArea = new WorldArea(worldPoint, 2, 2);
        boolean result = pathArea
                .intersectsWith2D(objectArea);
        return result;
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

        if (!hasDoor) {
            //temp fix: some doors are objects instead of walls
            TileObject door = Rs2GameObject.findObject(ObjectID.DOOR_1535, nextWorldPoint);
            if (door == null) return false;

            Rs2GameObject.interact(door, "open");
            Rs2Player.waitForWalking();
            return false;
        }


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
            ObjectComposition objectComposition = Rs2GameObject.getObjectComposition(wallObject.getId());

            for (var action : objectComposition.getActions()) {
                if (action != null && (action.contains("Pay-toll") || action.contains("Pick-lock") || action.contains("Walk-through"))) {
                    Rs2GameObject.interact(wallObject, action);
                    Rs2Player.waitForWalking();
                    return true;
                } else if (action != null && action.contains("Walk-through")) {
                    Rs2GameObject.interact(wallObject, action);
                    Rs2Player.waitForWalking();
                    return true;
                }
                if (action != null && action.contains("Pick-lock")) {
                    Rs2GameObject.interact(wallObject, action);
                    Rs2Player.waitForWalking();
                    return true;
                }
            }

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

        var tiles = Rs2Tile.getReachableTilesFromTile(Rs2Player.getWorldLocation(), 20);

        startPoint = path.stream()
                .min(Comparator.comparingInt(a -> {
                    if (tiles.containsKey(a))
                        return tiles.get(a);

                    return Integer.MAX_VALUE;
                }))
                .orElse(null);

        return IntStream.range(0, path.size())
                .filter(i -> path.get(i).equals(startPoint))
                .findFirst()
                .orElse(0);
    }

    /**
     * @param target
     */
    public static void setTarget(WorldPoint target) {
        if (!Microbot.isLoggedIn()) return;
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

        if (wallObject != null) {
            ObjectComposition objectComposition = Rs2GameObject.getObjectComposition(wallObject.getId());
            if (objectComposition == null) {
                return false;
            }
            boolean found = false;
            for (String action : objectComposition.getActions()) {
                if (action != null && (action.equals("Open") || action.contains("Pay-toll") || action.contains("Pick-lock") || action.contains("Walk-through"))) {
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
                if (a.dx(-1).getX() == b.getX()) {
                    return true;
                }
            }
            if (orientation == 4) {
                //blocks east
                if (a.dx(+1).getX() == b.getX()) {
                    return true;
                }
            }
            if (orientation == 2) {
                //blocks north
                if (a.dy(1).getY() == b.getY()) {
                    return true;
                }
            }
            if (orientation == 8) {
                //blocks south
                if (a.dy(-1).getY() == b.getY())
                    return true;
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
            if (action != null && (action.equals("Open") || action.contains("Pay-toll") || action.contains("Walk-through"))) {
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
            if (b.dx(-1).getX() == a.getX()) {
                return true;
            }
        }
        if (orientationb == 4) {
            //blocks west
            if (b.dx(+1).getX() == a.getX()) {
                return true;
            }
        }
        if (orientationb == 2) {
            //blocks south
            if (b.dy(+1).getY() == a.getY()) {
                return true;
            }
        }
        if (orientationb == 8) {
            //blocks north
            return b.dy(-1).getY() == a.getY();
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
        for (Transport b : ShortestPathPlugin.getTransports().getOrDefault(path.get(indexOfStartPoint), new ArrayList<>())) {
            for (WorldPoint origin : WorldPoint.toLocalInstance(Microbot.getClient(), b.getOrigin())) {

                if (b.getOrigin() != null && Rs2Player.getWorldLocation().getPlane() != b.getOrigin().getPlane()) {
                    continue;
                }

                for (int i = indexOfStartPoint; i < path.size(); i++) {
                    if (origin != null && origin.getPlane() != Rs2Player.getWorldLocation().getPlane())
                        continue;
                    if (path.stream().noneMatch(x -> x.equals(b.getDestination()))) continue;

                    int indexOfOrigin = IntStream.range(0, path.size())
                            .filter(f -> b.getOrigin() == null || path.get(f).equals(b.getOrigin()))
                            .findFirst()
                            .orElse(-1);
                    int indexOfDestination = IntStream.range(0, path.size())
                            .filter(f -> path.get(f).equals(b.getDestination()))
                            .findFirst()
                            .orElse(-1);
                    if (indexOfDestination == -1) continue;
                    if (indexOfOrigin == -1) continue;
                    if (indexOfDestination < indexOfOrigin) continue;

                    if (origin != null && !Rs2Tile.isTileReachable(path.get(i))) {
                        continue;
                    }

                    if (origin == null || path.get(i).equals(origin)) {
                        boolean handled = false;
                        if (b.isShip()) {
                            var npc = Rs2Npc.getNpc(b.getNpcName());
                            if (Rs2Npc.canWalkTo(npc, 20)) {
                                Rs2Npc.interact(npc, b.getAction());
                                Rs2Player.waitForWalking();
                                handled = true;
                            } else {
                                Rs2Walker.walkFastCanvas(path.get(i));
                                sleep(1200, 1600);
                            }
                        }

                        if (origin != null && b.getDestination().distanceTo2D(Rs2Player.getWorldLocation()) > 20) {
                            handled |= handleTrapdoor(b);
                        }

                        if (b.isSpiritTree()) {
                            handled |= b.handleSpiritTree();
                        }


                        if (b.isGnomeGlider()) {
                            handled |= b.handleGlider();
                        }

                        if (b.isFairyRing()) {
                            handled |= b.handleFairyRing();
                        }

                        if (b.isPlayerItem()){
                            handled |= b.handleItemTeleport();
                        }

                        if (handled)
                            return true;

                        GameObject gameObject = Rs2GameObject.getGameObjects(b.getObjectId(), b.getOrigin()).stream().findFirst().orElse(null);

                        //check game objects
                        if (gameObject != null && gameObject.getId() == b.getObjectId()) {
                            boolean interact = Rs2GameObject.interact(gameObject, b.getAction(), true);
                            if (!interact) {
                                Rs2Walker.walkMiniMap(path.get(i));
                                sleep(1600, 2000);
                                return false;
                            }
                            Rs2Player.waitForWalking();
                            return true;
                        }


                        //check wall objects (tunnels)
                        WallObject wallObject = Rs2GameObject.getWallObjects(b.getObjectId(), b.getOrigin()).stream().findFirst().orElse(null);
                        if (wallObject != null && wallObject.getId() == b.getObjectId()) {
                            boolean interact = Rs2GameObject.interact(wallObject, b.getAction(), true);
                            if (!interact) {
                                Rs2Walker.walkMiniMap(path.get(i));
                                sleep(1600, 2000);
                                return false;
                            }
                            Rs2Player.waitForWalking();
                            return true;
                        }

                        //check ground objects
                        GroundObject groundObject = Rs2GameObject.getGroundObjects(b.getObjectId(), b.getOrigin()).stream().filter(x -> !x.getWorldLocation().equals(Rs2Player.getWorldLocation())).findFirst().orElse(null);
                        if (groundObject != null && groundObject.getId() == b.getObjectId()) {
                            boolean interact = Rs2GameObject.interact(groundObject, b.getAction(), true);
                            if (!interact) {
                                Rs2Walker.walkMiniMap(path.get(i));
                                sleep(1600, 2000);
                                return false;
                            }
                            if (b.isAgilityShortcut()) {
                                Rs2Player.waitForAnimation();
                            } else {
                                Rs2Player.waitForWalking();
                            }
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

    /**
     * Checks if the player's current location is within the specified area defined by the given world points.
     *
     * @param worldPoints an array of two world points of the NW and SE corners of the area
     * @return true if the player's current location is within the specified area, false otherwise
     */
    public static boolean isInArea(WorldPoint... worldPoints) {
        WorldPoint playerLocation = Rs2Player.getWorldLocation();
        return playerLocation.getX() <= worldPoints[0].getX() &&   // NW corner x
                playerLocation.getY() >= worldPoints[0].getY() &&   // NW corner y
                playerLocation.getX() >= worldPoints[1].getX() &&   // SE corner x
                playerLocation.getY() <= worldPoints[1].getY();     // SE corner Y
        // draws box from 2 points to check against all variations of player X,Y from said points.
    }

    /**
     * Checks if the player's current location is within the specified range from the given center point.
     *
     * @param centerOfArea a WorldPoint which is the center of the desired area,
     * @param range        an int of range to which the boundaries will be drawn in a square,
     * @return true if the player's current location is within the specified area, false otherwise
     */
    public static boolean isInArea(WorldPoint centerOfArea, int range) {
        WorldPoint nwCorner = new WorldPoint(centerOfArea.getX() + range + range, centerOfArea.getY() - range, centerOfArea.getPlane());
        WorldPoint seCorner = new WorldPoint(centerOfArea.getX() - range - range, centerOfArea.getY() + range, centerOfArea.getPlane());
        return isInArea(nwCorner, seCorner); // call to our sibling
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
}
