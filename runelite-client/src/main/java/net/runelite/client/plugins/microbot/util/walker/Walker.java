package net.runelite.client.plugins.microbot.util.walker;

import lombok.Getter;
import net.runelite.api.MenuAction;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.staticwalker.pathfinder.*;
import net.runelite.client.plugins.microbot.util.Global;
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera;
import net.runelite.client.plugins.microbot.util.math.Calculations;
import net.runelite.client.plugins.microbot.util.menu.NewMenuEntry;
import net.runelite.client.plugins.microbot.util.reflection.Rs2Reflection;
import net.runelite.client.plugins.microbot.util.walker.pathfinder.CollisionMap;
import net.runelite.client.plugins.microbot.util.walker.pathfinder.Node;
import net.runelite.client.plugins.microbot.util.walker.pathfinder.Pathfinder;
import net.runelite.client.plugins.microbot.util.walker.pathfinder.PathfinderConfig;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.runelite.client.plugins.microbot.util.Global.sleepUntilOnClientThread;
import static org.apache.commons.lang3.ArrayUtils.reverse;

public class Walker {

    @Getter
    public Pathfinder pathfinder;
    public PathfinderConfig pathfinderConfig;

    public WorldPoint currentDestination;

    List<Transport> ignoreTransport = new ArrayList();

    List<Node> pathOrigin = new ArrayList<>();


    public Walker() {
        CollisionMap map = new CollisionMap();
        pathfinderConfig = new PathfinderConfig(map);
    }

    public WorldPoint walkRegionCanvas(int regionX, int regionY) {
        WorldPoint worldPoint = WorldPoint.fromRegion(Microbot.getClient().getLocalPlayer().getWorldLocation().getRegionID(),
                regionX,
                regionY,
                Microbot.getClient().getPlane());

        Point point = Calculations.worldToCanvas(worldPoint.getX(), worldPoint.getY());

        if (point == null) return null;

        Microbot.getMouse().click(point);

        return worldPoint;
    }

    public WorldPoint walkMiniMap(WorldPoint worldPoint) {
        if (Microbot.getClient().getMinimapZoom() > 2)
            Microbot.getClient().setMinimapZoom(2);

        Point point = Calculations.worldToMinimap(worldPoint.getX(), worldPoint.getY());

        if (point == null) return null;

        Microbot.getMouse().click(point);

        return worldPoint;
    }

    /**
     * Used in instances like pest control
     * @param regionX
     * @param regionY
     * @return
     */
    public WorldPoint walkFastRegion(int regionX, int regionY) {
        WorldPoint worldPoint = WorldPoint.fromRegion(Microbot.getClient().getLocalPlayer().getWorldLocation().getRegionID(),
                regionX,
                regionY,
                Microbot.getClient().getPlane());

        LocalPoint localPoint = LocalPoint.fromWorld(Microbot.getClient(), worldPoint);

        if (!Calculations.tileOnScreen(localPoint)) {
            Microbot.getWalker().walkMiniMap(worldPoint); //use minimap if tile is not on screen
            return worldPoint;
        }

        Point canv = Perspective.localToCanvas(Microbot.getClient(), localPoint, Microbot.getClient().getPlane());
        int canvasX = canv != null ? canv.getX() : -1;
        int canvasY = canv != null ? canv.getY() : -1;

        Microbot.doInvoke(new NewMenuEntry(canvasX, canvasY, MenuAction.WALK.getId(), 0, -1, "Walk here"), new Rectangle(1, 1, Microbot.getClient().getCanvasWidth(), Microbot.getClient().getCanvasHeight()));
        //Rs2Reflection.invokeMenu(canvasX, canvasY, MenuAction.WALK.getId(), 0, -1, "Walk here", "", -1, -1);

        return worldPoint;
    }
    /**
     * Used in instances like vorkath, jad
     * @param localPoint
     */
    public void walkFastLocal(LocalPoint localPoint) {
        Point canv = Perspective.localToCanvas(Microbot.getClient(), localPoint, Microbot.getClient().getPlane());
        int canvasX = canv != null ? canv.getX() : -1;
        int canvasY = canv != null ? canv.getY() : -1;

        Microbot.doInvoke(new NewMenuEntry(canvasX, canvasY, MenuAction.WALK.getId(), 0, -1, "Walk here"), new Rectangle(1, 1, Microbot.getClient().getCanvasWidth(), Microbot.getClient().getCanvasHeight()));
        //Rs2Reflection.invokeMenu(canvasX, canvasY, MenuAction.WALK.getId(), 0, -1, "Walk here", "", -1, -1);
    }

    public void walkFastCanvas(WorldPoint worldPoint) {
        LocalPoint localPoint = LocalPoint.fromWorld(Microbot.getClient(), worldPoint);
        if (!Calculations.tileOnScreen(localPoint)) {
            Microbot.getWalker().walkMiniMap(worldPoint); //use minimap if tile is not on screen
            return;
        }
        Point canv = Perspective.localToCanvas(Microbot.getClient(), LocalPoint.fromScene(worldPoint.getX() - Microbot.getClient().getBaseX(), worldPoint.getY() - Microbot.getClient().getBaseY()), Microbot.getClient().getPlane());
        int canvasX = canv != null ? canv.getX() : -1;
        int canvasY = canv != null ? canv.getY() : -1;

        if (canvasX == -1 && canvasY == -1) {
            Rs2Camera.turnTo(localPoint);
        }
        Microbot.doInvoke(new NewMenuEntry(canvasX, canvasY, MenuAction.WALK.getId(), 0, -1, "Walk here"), new Rectangle(1, 1, Microbot.getClient().getCanvasWidth(), Microbot.getClient().getCanvasHeight()));
        //Rs2Reflection.invokeMenu(canvasX, canvasY, MenuAction.WALK.getId(), 0, -1, "Walk here", "", -1, -1);
    }

    public WorldPoint walkCanvas(WorldPoint worldPoint) {
        Point point = Calculations.worldToCanvas(worldPoint.getX(), worldPoint.getY());

        if (point == null) return null;

        Microbot.getMouse().click(point);

        return worldPoint;
    }

    public boolean isCloseToRegion(int distance, int regionX, int regionY) {
        WorldPoint worldPoint = WorldPoint.fromRegion(Microbot.getClient().getLocalPlayer().getWorldLocation().getRegionID(),
                regionX,
                regionY,
                Microbot.getClient().getPlane());

        return worldPoint.distanceTo(Microbot.getClient().getLocalPlayer().getWorldLocation()) < distance;
    }

    private List<PathNode> getPath(WorldPoint startWorldPoint, WorldPoint endWorldPoint, boolean useNearest) {
        long startTimeDateLoad = System.currentTimeMillis();
        SavedWorldDataLoader savedWorldDataLoader = new SavedWorldDataLoader(WorldDataDownloader.Companion.getWorldDataFile());
        Map<String, PathNode> pathNodeMap = savedWorldDataLoader.getNodeMap();
        long endTimeDataLoad = System.currentTimeMillis();

        PathFinder pathFinder = new PathFinder(pathNodeMap);

        long startTimePathFind = System.currentTimeMillis();
        List<PathNode> nodes = pathFinder.findPath(startWorldPoint, endWorldPoint, useNearest);
        long endTimePathFind = System.currentTimeMillis();

        System.out.println("Loaded world data in " + (endTimeDataLoad - startTimeDateLoad) + " milliseconds");
        System.out.println("Found path in " + (endTimePathFind - startTimePathFind) + " milliseconds");
        System.out.println("Num of nodes: " + nodes.stream().count());

        return nodes;
    }

    public void interruptStaticWalker() {
        PathWalker.Companion.interrupt();
    }

    public boolean staticWalkTo(WorldPoint endWorldPoint, int maxDestinationDistance) {

        Player player = Microbot.getClient().getLocalPlayer();
        WorldPoint start = player.getWorldLocation();

        List<PathNode> nodes = getPath(start, endWorldPoint, false);
        if (nodes.isEmpty()) return false;

        PathWalker pathWalker = new PathWalker(nodes);
        pathWalker.walkPath();

        PathFinder.Companion.resetPath();
        return player.getWorldLocation().distanceTo(endWorldPoint) <= maxDestinationDistance;
    }

    public boolean staticWalkTo(WorldPoint endWorldPoint) {
        return staticWalkTo(endWorldPoint, 3);
    }

    public boolean hybridWalkTo(WorldPoint target, boolean useNearest) {
        Player player = Microbot.getClient().getLocalPlayer();
        List<PathNode> nodes = getPath(player.getWorldLocation(), target, useNearest);
        if (Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(target) < 5 && canReach(target))
            return true;

        if (nodes.isEmpty()) {
            System.out.println("Static Walker failed to find path, using dynamic walker");
            return walkTo(target, true);

        } else {

            PathWalker pathWalker = new PathWalker(nodes);
            pathWalker.walkPath();

            return player.getWorldLocation().distanceTo(target) <= 3;
        }
    }

    public boolean hybridWalkTo(WorldPoint target) {
        return hybridWalkTo(target, false);
    }

    public boolean walkTo(WorldPoint target) {
        return walkTo(target, true);
    }

    public boolean walkTo(net.runelite.api.NPC npc) {
        if (npc != null)
            return walkTo(npc.getWorldLocation(), true);

        return false;
    }

    public boolean walkTo(WorldPoint target, boolean useTransport) {
        return walkTo(target, useTransport, false, null);
    }

    public boolean walkTo(WorldPoint target, boolean useTransport, boolean useCanvas) {
        return walkTo(target, useTransport, useCanvas, null);
    }

    public boolean walkTo(WorldPoint target, boolean useTransport, boolean useCanvas, WorldArea[] blockingAreas) {
        if (pathfinder != null && !pathfinder.isDone()) return false;
        WorldPoint start = Microbot.getClient().getLocalPlayer().getWorldLocation();

        pathfinder = new Pathfinder(pathfinderConfig, start, target, useTransport, false, useCanvas, blockingAreas);
        setupPathfinderDefaults();

        sleepUntilOnClientThread(() -> pathfinder.isDone(), 60000);

        return false;
    }

    public boolean canReach(WorldPoint target) {
        WorldPoint start = Microbot.getClient().getLocalPlayer().getWorldLocation();

        pathfinder = new Pathfinder(pathfinderConfig, start, target, true);
        setupPathfinderDefaults();

        sleepUntilOnClientThread(() -> pathfinder.isDone(), 60000);

        return pathfinder.getPath().get(pathfinder.getPath().size() - 1).position.equals(target);
    }

    public long getReachDistance(WorldPoint target) {
        WorldPoint start = Microbot.getClient().getLocalPlayer().getWorldLocation();

        pathfinder = new Pathfinder(pathfinderConfig, start, target, true);
        setupPathfinderDefaults();

        sleepUntilOnClientThread(() -> pathfinder.isDone(), 60000);

        return pathfinder.getPath().size();
    }

    public boolean canInteract(WorldPoint target) {
        WorldPoint start = Microbot.getClient().getLocalPlayer().getWorldLocation();

        pathfinder = new Pathfinder(pathfinderConfig, start, target, true);
        setupPathfinderDefaults();

        sleepUntilOnClientThread(() -> pathfinder.isDone(), 60000);

        return pathfinder.getPath().get(pathfinder.getPath().size() - 1).position.distanceTo(target) <= 1;
    }

    public void setupPathfinderDefaults() {
        currentDestination = null;
        ignoreTransport = new ArrayList<>();
        pathOrigin = new ArrayList<>();
    }

    public boolean walkPath(WorldPoint[] worldPoints) {
        if (worldPoints[worldPoints.length -1].distanceTo(Microbot.getClient().getLocalPlayer().getWorldLocation()) < 4) return true;
        pathfinder = new Pathfinder();
        pathfinder.customPath = true;
        List<Node> path = new ArrayList<>();
        reverse(worldPoints);
        for (WorldPoint worldPoint: worldPoints) {
            path.add(new Node(worldPoint, null, 0));
        }
        pathfinder.setStart(Microbot.getClient().getLocalPlayer().getWorldLocation());
        pathfinder.setTarget(worldPoints[0]);
        pathfinder.setConfig(pathfinderConfig);
        pathfinder.setPath(path);
        pathfinder.run();
        sleepUntilOnClientThread(() -> pathfinder.isDone(), 60000);
        return false;
    }
}
