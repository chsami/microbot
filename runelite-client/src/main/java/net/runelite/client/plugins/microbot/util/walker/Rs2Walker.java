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
import net.runelite.client.plugins.microbot.shortestpath.TransportType;
import net.runelite.client.plugins.microbot.shortestpath.pathfinder.Pathfinder;
import net.runelite.client.plugins.microbot.util.equipment.JewelleryLocationEnum;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.menu.NewMenuEntry;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.tile.Rs2Tile;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
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
import static net.runelite.client.plugins.microbot.util.walker.Rs2MiniMap.worldToMinimap;


public class Rs2Walker {
    private static final ExecutorService pathfindingExecutor = Executors.newSingleThreadExecutor();
    @Setter
    public static ShortestPathConfig config;
    static int stuckCount = 0;
    static WorldPoint lastPosition;
    static int idle = 0;
    static WorldPoint currentTarget;
    static int nextWalkingDistance = 10;

    public static boolean walkTo(WorldArea area, int distanceThreshold) {
        if (area.distanceTo(Rs2Player.getWorldLocation()) > distanceThreshold) {
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
                while (!Thread.currentThread().isInterrupted()) {
                    if (!Microbot.isLoggedIn()) {
                        setTarget(null);
                        break;
                    }
                    if (ShortestPathPlugin.getPathfinder() == null) {
                        if (ShortestPathPlugin.getMarker() == null) {
                            break;
                        }
                        continue;
                    }
                    if (!ShortestPathPlugin.getPathfinder().isDone()) {
                        continue;
                    }

                    if (ShortestPathPlugin.getPathfinder().getPath().size() > 0 && isNear(ShortestPathPlugin.getPathfinder().getPath().get(ShortestPathPlugin.getPathfinder().getPath().size() - 1))) {
                        setTarget(null);
                        break;
                    }

                    if (Rs2Npc.getNpcsAttackingPlayer(Microbot.getClient().getLocalPlayer()) != null
                            && Rs2Npc.getNpcsAttackingPlayer(Microbot.getClient().getLocalPlayer()).stream().anyMatch(x -> x.getId() == 4417)) { //dead tree in draynor
                        var moveableTiles = Rs2Tile.getReachableTilesFromTile(Rs2Player.getWorldLocation(), 5).keySet().toArray(new WorldPoint[0]);
                        walkMiniMap(moveableTiles[Random.random(0, moveableTiles.length)]);
                        sleepGaussian(1000, 300);
                    }

                    //avoid tree attacking you in draynor
                    checkIfStuck();
                    if (stuckCount > 10) {
                        var moveableTiles = Rs2Tile.getReachableTilesFromTile(Rs2Player.getWorldLocation(), 5).keySet().toArray(new WorldPoint[0]);
                        walkMiniMap(moveableTiles[Random.random(0, moveableTiles.length)]);
                        sleepGaussian(1000, 300);
                    }

                    if (ShortestPathPlugin.getPathfinder() == null) break;

                    List<WorldPoint> path = ShortestPathPlugin.getPathfinder().getPath();
                    int indexOfStartPoint = getClosestTileIndex(path);
                    lastPosition = Rs2Player.getWorldLocation();

                    if (Rs2Player.getWorldLocation().distanceTo(target) == 0) {
                        break;
                    }


                    /**
                     * MAIN WALK LOOP
                     */
                    boolean doorOrTransportResult = false;
                    for (int i = indexOfStartPoint; i < path.size(); i++) {
                        WorldPoint currentWorldPoint = path.get(i);

                        if (i > 0 && !Rs2Tile.isTileReachable(path.get(i - 1)) && !Microbot.getClient().isInInstancedRegion()) {
                            continue;
                        }

                        if (ShortestPathPlugin.getMarker() == null) {
                            break;
                        }

                        /**
                         * CHECK DOORS
                         */
                        doorOrTransportResult = handleDoors(path, i);
                        if (doorOrTransportResult) {
                            break;
                        }

                        if (!Microbot.getClient().isInInstancedRegion()) {
                            doorOrTransportResult = handleTransports(path, i);
                        }

                        if (doorOrTransportResult) {
                            break;
                        }

                        if (!Rs2Tile.isTileReachable(currentWorldPoint) && !Microbot.getClient().isInInstancedRegion()) {
                            continue;
                        }

                        if (currentWorldPoint.distanceTo2D(Rs2Player.getWorldLocation()) > nextWalkingDistance) {
                            nextWalkingDistance = Random.random(7, 11);
                            if (Microbot.getClient().isInInstancedRegion()) {
                                Rs2Walker.walkFastCanvas(currentWorldPoint);
                                sleepGaussian(1200, 300);
                            } else {
                                long movingStart = System.currentTimeMillis();
                                if (currentWorldPoint.distanceTo2D(Rs2Player.getWorldLocation()) > nextWalkingDistance) {
                                    Rs2Walker.walkMiniMap(getPointWithWallDistance(currentWorldPoint));
                                    sleepUntilTrue(() -> currentWorldPoint.distanceTo2D(Rs2Player.getWorldLocation()) < nextWalkingDistance, 100, 2000);
                                    if (System.currentTimeMillis() - movingStart < 120) {
                                        sleepGaussian(600, 150);
                                    }
                                }
                            }
                        }
                    }


                    if (!doorOrTransportResult) {
                        if (path.size() > 0) {
                            var moveableTiles = Rs2Tile.getReachableTilesFromTile(path.get(path.size() - 1), Math.min(3, distance)).keySet().toArray(new WorldPoint[0]);
                            var finalTile = moveableTiles.length > 0 ? moveableTiles[Random.random(0, moveableTiles.length)] : path.get(path.size() - 1);
                            if (Rs2Tile.isTileReachable(finalTile)) {

                                if (Microbot.getClient().isInInstancedRegion())
                                    Rs2Walker.walkFastCanvas(finalTile);
                                else
                                    Rs2Walker.walkMiniMap(finalTile);

                                sleepGaussian(1200, 300);
                            }
                        }
                    }
                }
                return Rs2Player.getWorldLocation().distanceTo(target) < distance;
            } catch (Exception ex) {
                if (ex instanceof InterruptedException) return false;
                Microbot.log("Microbot Walker Exception " + ex.getMessage());
                System.out.println(ex.getMessage());
                ex.printStackTrace(System.out);
                currentTarget = null;
            }
            return false;
        });
        return false;
    }

    public static WorldPoint getPointWithWallDistance(WorldPoint target) {
        var tiles = Rs2Tile.getReachableTilesFromTile(target, 1);

        var localPoint = LocalPoint.fromWorld(Microbot.getClient().getTopLevelWorldView(), target);
        if (Microbot.getClient().getCollisionMaps() != null && localPoint != null) {
            int[][] flags = Microbot.getClient().getCollisionMaps()[Microbot.getClient().getPlane()].getFlags();

            if (hasMinimapRelevantMovementFlag(localPoint, flags)) {
                for (var tile : tiles.keySet()) {
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
                    || movementFlags.contains(MovementFlag.BLOCK_MOVEMENT_SOUTH)) {
                for (var tile : tiles.keySet()) {
                    var localTilePoint = LocalPoint.fromWorld(Microbot.getClient().getTopLevelWorldView(), tile);
                    if (localTilePoint == null)
                        continue;

                    int tileData = flags[localTilePoint.getSceneX()][localTilePoint.getSceneY()];
                    Set<MovementFlag> tileFlags = MovementFlag.getSetFlags(tileData);

                    if (tileFlags.isEmpty())
                        return tile;
                }
            }
        }

        return target;
    }

    static boolean hasMinimapRelevantMovementFlag(LocalPoint point, int[][] flagMap) {
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

        Point point = worldToMinimap(worldPoint);

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

    public static void walkFastCanvas(WorldPoint worldPoint) {
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
    public static boolean canReach(WorldPoint worldPoint, int sizeX, int sizeY, int pathSizeX, int pathSizeY) {
        Pathfinder pathfinder = new Pathfinder(ShortestPathPlugin.getPathfinderConfig(), Rs2Player.getWorldLocation(), worldPoint);
        pathfindingExecutor.submit(pathfinder);
        sleepUntil(pathfinder::isDone);
        WorldArea pathArea = new WorldArea(pathfinder.getPath().get(pathfinder.getPath().size() - 1), pathSizeX, pathSizeY);
        WorldArea objectArea = new WorldArea(worldPoint, sizeX + 2, sizeY + 2);
        boolean result = pathArea
                .intersectsWith2D(objectArea);
        return result;
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

    private static boolean handleDoors(List<WorldPoint> path, int index) {

        if (ShortestPathPlugin.getPathfinder() == null) return false;

        if (index == path.size() - 1) return false;

        var doorActions = Arrays.asList("pay-toll", "pick-lock", "walk-through", "go-through", "open");

        // Check this and the next tile for door objects
        for (int doorIndex = index; doorIndex < index + 2; doorIndex++) {
            var point = path.get(doorIndex);

            // Handle wall and game objects
            TileObject object = null;
            var tile = Rs2GameObject.getTiles(3).stream()
                    .filter(x -> x.getWorldLocation().equals(point))
                    .findFirst().orElse(null);
            if (tile != null)
                object = tile.getWallObject();

            if (object == null)
                object = Rs2GameObject.getGameObject(point);

            if (object == null) continue;

            var objectComp = Rs2GameObject.getObjectComposition(object.getId());
            if (objectComp == null) continue;

            // Match action
            var action = Arrays.stream(objectComp.getActions())
                    .filter(x -> x != null && doorActions.stream().anyMatch(doorAction -> x.toLowerCase().startsWith(doorAction)))
                    .min(Comparator.comparing(x -> doorActions.indexOf(
                            doorActions.stream().filter(doorAction -> x.toLowerCase().startsWith(doorAction)).findFirst().orElse(""))))
                    .orElse(null);

            if (action == null) continue;

            boolean found = false;
            if (object instanceof WallObject) {
                // Match wall objects by orientation
                var orientation = ((WallObject) object).getOrientationA();

                if (doorIndex == index) {
                    // Forward
                    var neighborPoint = path.get(doorIndex + 1);
                    if (orientation == 1 && point.dx(-1).getX() == neighborPoint.getX()
                            || orientation == 4 && point.dx(+1).getX() == neighborPoint.getX()
                            || orientation == 2 && point.dy(1).getY() == neighborPoint.getY()
                            || orientation == 8 && point.dy(-1).getY() == neighborPoint.getY())
                        found = true;
                } else if (doorIndex == index + 1) {
                    // Backward
                    var neighborPoint = path.get(doorIndex - 1);
                    if (orientation == 1 && point.dx(-1).getX() == neighborPoint.getX()
                            || orientation == 4 && point.dx(+1).getX() == neighborPoint.getX()
                            || orientation == 2 && point.dy(1).getY() == neighborPoint.getY()
                            || orientation == 8 && point.dy(-1).getY() == neighborPoint.getY())
                        found = true;

                    // Diagonal objects with any orientation
                    if (index + 2 < path.size() && (orientation == 16 || orientation == 32 || orientation == 64 || orientation == 128)) {
                        var prevPoint = path.get(doorIndex - 1);
                        var nextPoint = path.get(doorIndex + 1);

                        if (Math.abs(prevPoint.getX() - nextPoint.getX()) > 0 && Math.abs(prevPoint.getY() - nextPoint.getY()) > 0)
                            found = true;
                    }
                }
            } else if (object instanceof GameObject) {
                // Match game objects by name
                // Orientation does not work as game objects are not strictly oriented like walls
                var objectNames = Arrays.asList("door");

                if (objectNames.contains(objectComp.getName().toLowerCase()))
                    found = true;
            }


            if (found) {
                Rs2GameObject.interact(object, action);
                Rs2Player.waitForWalking();
                return true;
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
        if (!Microbot.isLoggedIn() && target != null) return;
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
        for (Transport transport : ShortestPathPlugin.getTransports().getOrDefault(path.get(indexOfStartPoint), new HashSet<>())) {
            for (WorldPoint origin : WorldPoint.toLocalInstance(Microbot.getClient(), transport.getOrigin())) {
                if (transport.getOrigin() != null && Rs2Player.getWorldLocation().getPlane() != transport.getOrigin().getPlane()) {
                    continue;
                }

                for (int i = indexOfStartPoint; i < path.size(); i++) {
                    if (origin != null && origin.getPlane() != Rs2Player.getWorldLocation().getPlane())
                        continue;
                    if (path.stream().noneMatch(x -> x.equals(transport.getDestination()))) continue;

                    if (transport.getType() != TransportType.TELEPORTATION_ITEM && transport.getType() != TransportType.TELEPORTATION_SPELL) {
                        int indexOfOrigin = IntStream.range(0, path.size())
                                .filter(f -> path.get(f).equals(transport.getOrigin()))
                                .findFirst()
                                .orElse(-1);
                        int indexOfDestination = IntStream.range(0, path.size())
                                .filter(f -> path.get(f).equals(transport.getDestination()))
                                .findFirst()
                                .orElse(-1);
                        if (indexOfDestination == -1) continue;
                        if (indexOfOrigin == -1) continue;
                        if (indexOfDestination < indexOfOrigin) continue;
                    }

                    if (path.get(i).equals(origin)) {
                        if (transport.getType() == TransportType.SHIP || transport.getType() == TransportType.NPC || transport.getType() == TransportType.BOAT) {
                            var npcAndAction = String.format("%s %s", transport.getAction(), transport.getName());
                            NPC npc = null;
                            String action = "";
                            for (int n = npcAndAction.indexOf(" "); n >= 0; n = npcAndAction.indexOf(" ", n + 1)) {
                                npc = Rs2Npc.getNpc(npcAndAction.substring(n + 1));
                                if (npc != null) {
                                    action = npcAndAction.substring(0, n);
                                    break;
                                }
                            }

                            if (Rs2Npc.canWalkTo(npc, 20)) {
                                Rs2Npc.interact(npc, action);
                                Rs2Player.waitForWalking();
                            } else {
                                Rs2Walker.walkFastCanvas(path.get(i));
                                sleep(1200, 1600);
                            }
                        }
                    }


                    if (transport.getName() != null && transport.getName().contains("trapdoor") && transport.getDestination().distanceTo2D(Rs2Player.getWorldLocation()) > 20) {
                        if (handleTrapdoor(transport))
                            break;
                    }

                    if (transport.getType() == TransportType.SPIRIT_TREE) {
                        handleSpiritTree(transport);
                    }


                    if (transport.getType() == TransportType.GNOME_GLIDER && transport.getOrigin().distanceTo(Rs2Player.getWorldLocation()) < 12) {
                        handleGlider(transport);
                    }

                    if (transport.getType() == TransportType.FAIRY_RING && !Rs2Player.getWorldLocation().equals(transport.getDestination())) {
                        handleFairyRing(transport);
                    }

                    if (transport.getType() == TransportType.TELEPORTATION_ITEM) {
                        boolean succesfullAction = false;
                        for (Set<Integer> itemIds : transport.getItemIdRequirements()) {
                            if (succesfullAction)
                                break;
                            for (Integer itemId : itemIds) {
                                //TODO: jewellery teleport in inventory
                                if (Rs2Walker.currentTarget == null) break;
                                if (Rs2Player.getWorldLocation().distanceTo2D(transport.getDestination()) < config.reachedDistance())
                                    break;
                                if (succesfullAction) break;

                                //If an action is succesfully we break out of the loop
                                succesfullAction = handleInventoryTeleports(transport, itemId) || handleWearableTeleports(transport, itemId);

                            }
                        }
                    }

                    if (transport.getType() == TransportType.TELEPORTATION_SPELL) {
                        for (Set<Integer> itemIds : transport.getItemIdRequirements()) {
                            for (Integer itemId : itemIds) {
                                if (Rs2Inventory.hasItem(itemId)) {
                                    if (Rs2Inventory.use(itemId)) {
                                        sleep(GAME_TICK_LENGTH * transport.getDuration());
                                        break;
                                    }
                                }
                            }
                        }
                    }


                    GameObject gameObject = Rs2GameObject.getGameObjects(transport.getObjectId(), transport.getOrigin()).stream().findFirst().orElse(null);
                    //check game objects
                    if (gameObject != null && gameObject.getId() == transport.getObjectId()) {
                        if (!Rs2Tile.isTileReachable(transport.getOrigin())) {
                            break;
                        }
                        Rs2GameObject.interact(gameObject, transport.getAction());
                        if (transport.getDestination().getPlane() == Rs2Player.getWorldLocation().getPlane()) {
                            if (transport.getType() == TransportType.AGILITY_SHORTCUT) {
                                Rs2Player.waitForAnimation();
                            } else {
                                Rs2Player.waitForWalking();
                            }
                        } else {
                            int z = Rs2Player.getWorldLocation().getPlane();
                            sleepUntil(() -> Rs2Player.getWorldLocation().getPlane() != z);
                            sleep(Random.randomGaussian(1000, 300));
                        }
                        return true;
                    }

                    //check tile objects
                    List<TileObject> tileObjects = Rs2GameObject.getTileObjects(transport.getObjectId(), transport.getOrigin());
                    TileObject tileObject = tileObjects.stream().findFirst().orElse(null);
                    if (tileObject instanceof GroundObject)
                        tileObject = tileObjects.stream()
                                .filter(x -> !x.getWorldLocation().equals(Rs2Player.getWorldLocation()))
                                .min(Comparator.comparing(x -> ((TileObject) x).getWorldLocation().distanceTo(transport.getOrigin()))
                                        .thenComparing(x -> ((TileObject) x).getWorldLocation().distanceTo(transport.getDestination()))).orElse(null);

                    if (tileObject != null && tileObject.getId() == transport.getObjectId()) {
                        if (tileObject.getId() != 16533 && !Rs2Tile.isTileReachable(transport.getOrigin())) {
                            break;
                        }
                        Rs2GameObject.interact(tileObject, transport.getAction());
                        if (transport.getDestination().getPlane() == Rs2Player.getWorldLocation().getPlane()) {
                            if (transport.getType() == TransportType.AGILITY_SHORTCUT) {
                                Rs2Player.waitForAnimation();
                            } else {
                                Rs2Player.waitForWalking();
                            }
                        } else {
                            int z = Rs2Player.getWorldLocation().getPlane();
                            sleepUntil(() -> Rs2Player.getWorldLocation().getPlane() != z);
                            sleep(Random.randomGaussian(1000, 300));
                        }
                        return true;
                    }
                }

            }
        }
        return false;
    }

    public static boolean handleInventoryTeleports(Transport transport, int itemId) {
        Rs2Item rs2Item = Rs2Inventory.get(itemId);
        boolean hasItem = rs2Item != null;
        List<String> actions = Arrays.asList("break", "teleport", "empty", "lletya", "prifddinas", "commune", "Rellekka", "Waterbirth Island", "Neitiznot", "Jatiszo",
                "Ver Sinhaza", "Darkmeyer", "Slepe", "Troll Stronghold", "Weiss", "invoke", "rub");
        if (!hasItem) return false;
        boolean hasMultipleDestination = transport.getDisplayInfo().contains(":");
        //hasMultipleDestination is stuff like: games neck, ring of dueling etc...
        if (hasMultipleDestination) {
            String[] values = transport.getDisplayInfo().split(":");
            String destination = values[1].trim().toLowerCase();
            String itemAction = Arrays.stream(rs2Item.getInventoryActions())
                    .filter(action -> action != null && actions.contains(action.toLowerCase()))
                    .findFirst()
                    .orElse(null);
            if (itemAction == null) return false;
            if (itemAction.equalsIgnoreCase("rub")) {
                if (Rs2Inventory.interact(itemId, itemAction)) {
                    sleepUntil(() -> Rs2Widget.getWidget(219, 1) != null);
                    Rs2Widget.sleepUntilHasWidgetText(destination, 219, 1, false, 5000);
                    Rs2Widget.clickWidget(destination, Optional.of(219), 1, false);
                    return sleepUntilTrue(() -> Rs2Player.getWorldLocation().equals(transport.getDestination()), 100, 5000);
                }
            }
        }

        //Simple items with one destination like teleport tabs, teleport scrolls etc...
        String itemAction = Arrays.stream(rs2Item.getInventoryActions())
                .filter(action -> action != null && actions.contains(action.toLowerCase()))
                .findFirst()
                .orElse(null);
        if (itemAction == null) return false;
        if (Rs2Inventory.interact(itemId, itemAction)) {
            return sleepUntilTrue(() -> Rs2Player.getWorldLocation().equals(transport.getDestination()), 100, 5000);
        }

        return false;
    }

    private static boolean handleWearableTeleports(Transport transport, int itemId) {
        if (Rs2Equipment.isWearing(itemId)) {
            if (transport.getDisplayInfo().contains(":")) {
                String[] values = transport.getDisplayInfo().split(":");
                String jewelleryName = values[0].trim().toLowerCase();
                String destination = values[1].trim().toLowerCase();
                JewelleryLocationEnum jewelleryTransport = Arrays.stream(JewelleryLocationEnum.values()).filter(x -> x.getTooltip().toLowerCase().contains(jewelleryName) && x.getDestination().toLowerCase().contains(destination)).findFirst().orElse(null);
                if (jewelleryTransport == null) return false;
                if (Rs2Equipment.useAmuletAction(jewelleryTransport) || Rs2Equipment.useRingAction(jewelleryTransport)) {
                    return sleepUntilTrue(() -> Rs2Player.getWorldLocation().equals(transport.getDestination()), 100, 5000);
                }
            } else {
                JewelleryLocationEnum jewelleryTransport = Arrays.stream(JewelleryLocationEnum.values()).filter(x -> x.getTooltip().toLowerCase().contains(transport.getDisplayInfo().toLowerCase())).findFirst().orElse(null);
                if (jewelleryTransport == null) return false;
                if (Rs2Equipment.useAmuletAction(jewelleryTransport) || Rs2Equipment.useRingAction(jewelleryTransport)) {
                    return sleepUntilTrue(() -> Rs2Player.getWorldLocation().equals(transport.getDestination()), 100, 5000);
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
            sleepGaussian(1200, 300);
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

    /**
     * Checks the distance between startpoint and endpoint using ShortestPath
     *
     * @param startpoint
     * @param endpoint
     * @return distance
     */
    public static int getDistanceBetween(WorldPoint startpoint, WorldPoint endpoint) {
        ExecutorService pathfindingExecutor = Executors.newSingleThreadExecutor();
        Pathfinder pathfinder = new Pathfinder(ShortestPathPlugin.getPathfinderConfig(), startpoint, endpoint);
        pathfindingExecutor.submit(pathfinder);
        sleepUntil(pathfinder::isDone);
        return pathfinder.getPath().size();
    }

    private static boolean completedQuests(Transport transport) {
        for (Quest quest : transport.getQuests()) {
            if (!QuestState.FINISHED.equals(quest.getState(Microbot.getClient()))) {
                return false;
            }
        }
        return true;
    }

    public static boolean handleSpiritTree(Transport transport) {
        int spiritTreeMenu = 12255232;

        // Get Transport Information
        String displayInfo = transport.getDisplayInfo();
        String objectName = transport.getName();
        int objectId = transport.getObjectId();
        String action = transport.getAction();
        WorldPoint origin = transport.getOrigin();
        WorldPoint destination = transport.getDestination();

        System.out.println("Display info: " + displayInfo);
        System.out.println("Object Name: " + objectName);
        System.out.println("Object ID: " + objectId);
        System.out.println("Action: " + action);
        System.out.println("Origin: " + origin);
        System.out.println("Destination: " + destination);

        // Check if the widget is already visible
        if (!Rs2Widget.isHidden(spiritTreeMenu)) {
            System.out.println("Widget is already visible. Skipping interaction.");
            char key = displayInfo.charAt(0);
            System.out.println(key);
            Rs2Keyboard.keyPress(key);
            System.out.println("Pressing: " + key);
            return true;
        }

        // Find the spirit tree object
        TileObject spiritTree = Rs2GameObject.findObjectByImposter(objectId, "Travel");
        if (spiritTree == null) {
            System.out.println("Spirit tree not found.");
            return false;
        }

        // Interact with the spirit tree
        Rs2GameObject.interact(spiritTree);

        // Wait for the widget to become visible
        boolean widgetVisible = !Rs2Widget.isHidden(spiritTreeMenu);
        if (!widgetVisible) {
            System.out.println("Widget did not become visible within the timeout.");
            return false;
        }

        System.out.println("Widget is now visible.");
        char key = displayInfo.charAt(0);
        Rs2Keyboard.keyPress(key);
        System.out.println("Pressing: " + key);
        return true;
    }

    public static boolean handleGlider(Transport transport) {
        int gliderMenu = 9043968;
        int TA_QUIR_PRIW = 9043972;
        int SINDARPOS = 9043975;
        int LEMANTO_ANDRA = 9043978;
        int KAR_HEWO = 9043981;
        int GANDIUS = 9043984;
        int OOKOOKOLLY_UNDRI = 9043993;

        // Get Transport Information
        String displayInfo = transport.getDisplayInfo();
        String npcName = transport.getName();
        int objectId = transport.getObjectId();
        String action = transport.getAction();
        WorldPoint origin = transport.getOrigin();
        WorldPoint destination = transport.getDestination();

        System.out.println("Display info: " + displayInfo);
        System.out.println("NPC Name: " + npcName);
        System.out.println("Object ID: " + objectId);
        System.out.println("Action: " + action);
        System.out.println("Origin: " + origin);
        System.out.println("Destination: " + destination);

        // Check if the widget is already visible
        if (Rs2Widget.isHidden(gliderMenu)) {
            // Find the glider NPC
            NPC gnome = Rs2Npc.getNpc(npcName);  // Use the NPC name to find the NPC
            if (gnome == null) {
                System.out.println("Gnome not found.");
                return false;
            }

            // Interact with the gnome glider NPC
            Rs2Npc.interact(gnome, action);
            sleepUntil(() -> !Rs2Widget.isHidden(gliderMenu));
        }


        // Wait for the widget to become visible
        boolean widgetVisible = !Rs2Widget.isHidden(gliderMenu);
        if (!widgetVisible) {
            System.out.println("Widget did not become visible within the timeout.");
            return false;
        }

        System.out.println("Widget is now visible.");

        switch (displayInfo) {
            case "Kar-Hewo":
                Rs2Widget.clickWidget(KAR_HEWO);
            case "Gnome Stronghold":
                Rs2Widget.clickWidget(TA_QUIR_PRIW);
            case "Sindarpos":
                Rs2Widget.clickWidget(SINDARPOS);
            case "Lemanto Andra":
                Rs2Widget.clickWidget(LEMANTO_ANDRA);
            case "Gandius":
                Rs2Widget.clickWidget(GANDIUS);
            case "Ookookolly Undri":
                Rs2Widget.clickWidget(OOKOOKOLLY_UNDRI);
        }
        return true;
    }

    // Constants for widget IDs
    private static final int FAIRY_RING_MENU = 26083328;

    private static final int SLOT_ONE = 26083331;
    private static final int SLOT_TWO = 26083332;
    private static final int SLOT_THREE = 26083333;
    private static final int TELEPORT_BUTTON = 26083354;

    private static final int SLOT_ONE_CW_ROTATION = 26083347;
    private static final int SLOT_ONE_ACW_ROTATION = 26083348;
    private static final int SLOT_TWO_CW_ROTATION = 26083349;
    private static final int SLOT_TWO_ACW_ROTATION = 26083350;
    private static final int SLOT_THREE_CW_ROTATION = 26083351;
    private static final int SLOT_THREE_ACW_ROTATION = 26083352;
    private static Rs2Item startingWeapon = null;
    private static int startingWeaponId;

    public static boolean handleFairyRing(Transport transport) {
        // Get Transport Information
        String displayInfo = transport.getDisplayInfo();
        String objectName = transport.getName();
        int objectId = transport.getObjectId();
        String action = transport.getAction();
        WorldPoint origin = transport.getOrigin();
        WorldPoint destination = transport.getDestination();

        if (startingWeapon == null) {


            startingWeapon = Rs2Equipment.get(EquipmentInventorySlot.WEAPON);
            System.out.println(startingWeapon);
            startingWeaponId = startingWeapon.getId();
        }

        System.out.println("Display info: " + displayInfo);
        System.out.println("Object Name: " + objectName);
        System.out.println("Object ID: " + objectId);
        System.out.println("Action: " + action);
        System.out.println("Origin: " + origin);
        System.out.println("Destination: " + destination);
        System.out.println("Starting Weapon ID: " + startingWeaponId);

        // Check if the widget is already visible
        if (!Rs2Widget.isHidden(FAIRY_RING_MENU)) {
            System.out.println("Widget is already visible. Skipping interaction.");
            rotateSlotToDesiredRotation(SLOT_ONE, Rs2Widget.getWidget(SLOT_ONE).getRotationY(), getDesiredRotation(transport.getDisplayInfo().charAt(0)), SLOT_ONE_ACW_ROTATION, SLOT_ONE_CW_ROTATION);
            rotateSlotToDesiredRotation(SLOT_TWO, Rs2Widget.getWidget(SLOT_TWO).getRotationY(), getDesiredRotation(transport.getDisplayInfo().charAt(1)), SLOT_TWO_ACW_ROTATION, SLOT_TWO_CW_ROTATION);
            rotateSlotToDesiredRotation(SLOT_THREE, Rs2Widget.getWidget(SLOT_THREE).getRotationY(), getDesiredRotation(transport.getDisplayInfo().charAt(2)), SLOT_THREE_ACW_ROTATION, SLOT_THREE_CW_ROTATION);
            Rs2Widget.clickWidget(TELEPORT_BUTTON);
            Rs2Player.waitForAnimation();
            if (!Rs2Equipment.isWearing(startingWeaponId)) {
                sleep(3000, 3600); // Required due to long animation time
                System.out.println("Equipping Starting Weapon: " + startingWeaponId);
                Rs2Inventory.equip(startingWeaponId);
            }
            return true;
        }

        if (Rs2Equipment.isWearing("Dramen staff") || Rs2Equipment.isWearing("Lunar staff")) {
            System.out.println("Interacting with the fairy ring directly.");
            var fairyRing = Rs2GameObject.findObjectByLocation(origin);
            Rs2GameObject.interact(fairyRing, "Configure");
            Rs2Player.waitForWalking();
        } else if (Rs2Inventory.contains("Dramen staff")) {
            Rs2Inventory.equip("Dramen staff");
            sleep(600);
        } else if (Rs2Inventory.contains("Lunar staff")) {
            Rs2Inventory.equip("Lunar staff");
            sleep(600);
        }
        return true;
    }

    private static boolean rotateSlotToDesiredRotation(int slotId, int currentRotation, int desiredRotation, int slotAcwRotationId, int slotCwRotationId) {
        int anticlockwiseTurns = (desiredRotation - currentRotation + 2048) % 2048;
        int clockwiseTurns = (currentRotation - desiredRotation + 2048) % 2048;

        if (clockwiseTurns <= anticlockwiseTurns) {
            System.out.println("Rotating slot " + slotId + " clockwise " + (clockwiseTurns / 512) + " times.");
            for (int i = 0; i < clockwiseTurns / 512; i++) {
                Rs2Widget.clickWidget(slotCwRotationId);
                sleep(600, 1200);
            }
            return true;
        } else {
            System.out.println("Rotating slot " + slotId + " anticlockwise " + (anticlockwiseTurns / 512) + " times.");
            for (int i = 0; i < anticlockwiseTurns / 512; i++) {
                Rs2Widget.clickWidget(slotAcwRotationId);
                sleep(600, 1200);
            }
            return true;
        }

    }

    private static int getDesiredRotation(char letter) {
        switch (letter) {
            case 'A':
            case 'I':
            case 'P':
                return 0;
            case 'B':
            case 'J':
            case 'Q':
                return 512;
            case 'C':
            case 'K':
            case 'R':
                return 1024;
            case 'D':
            case 'L':
            case 'S':
                return 1536;
            default:
                return -1;
        }
    }
}
