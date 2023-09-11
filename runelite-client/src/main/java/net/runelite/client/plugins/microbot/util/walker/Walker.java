package net.runelite.client.plugins.microbot.util.walker;

import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.camera.Camera;
import net.runelite.client.plugins.microbot.util.math.Calculations;
import net.runelite.client.plugins.microbot.util.walker.pathfinder.CollisionMap;
import net.runelite.client.plugins.microbot.util.walker.pathfinder.Node;
import net.runelite.client.plugins.microbot.util.walker.pathfinder.Pathfinder;
import net.runelite.client.plugins.microbot.util.walker.pathfinder.PathfinderConfig;
import net.runelite.client.plugins.microbot.walker.pathfinder.*;

import java.util.ArrayList;
import java.util.List;

import static net.runelite.client.plugins.microbot.util.Global.sleep;
import static net.runelite.client.plugins.microbot.util.Global.sleepUntilOnClientThread;

public class Walker {

    public int canvasX;
    public int canvasY;

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

        Point canv = Perspective.localToCanvas(Microbot.getClient(), localPoint, Microbot.getClient().getPlane());
        canvasX = canv != null ? canv.getX() : -1;
        canvasY = canv != null ? canv.getY() : -1;

        Microbot.getMouse().clickFast(1, 1);

        sleep(300);
        canvasX = 0;
        canvasY = 0;

        return worldPoint;
    }
    /**
     * Used in instances like vorkath, jad
     * @param localPoint
     */
    public void walkFastLocal(LocalPoint localPoint) {

        Point canv = Perspective.localToCanvas(Microbot.getClient(), localPoint, Microbot.getClient().getPlane());
        canvasX = canv != null ? canv.getX() : -1;
        canvasY = canv != null ? canv.getY() : -1;

        Microbot.getMouse().clickFast(1, 1);

        sleep(300);
        canvasX = 0;
        canvasY = 0;
    }

    public void walkFastCanvas(WorldPoint worldPoint) {
        Point canv = Perspective.localToCanvas(Microbot.getClient(), LocalPoint.fromScene(worldPoint.getX() - Microbot.getClient().getBaseX(), worldPoint.getY() - Microbot.getClient().getBaseY()), Microbot.getClient().getPlane());
        canvasX = canv != null ? canv.getX() : -1;
        canvasY = canv != null ? canv.getY() : -1;

        Microbot.getMouse().clickFast(1, 1);

        sleep(100);
        canvasX = 0;
        canvasY = 0;
    }

    public void handleMenuSwapper(MenuEntry menuEntry) {
        if (canvasX == 0 && canvasY == 0) return;
        menuEntry.setOption("Walk here");
        menuEntry.setIdentifier(0);
        menuEntry.setParam0(canvasX);
        menuEntry.setParam1(canvasY);
        menuEntry.setTarget("");
        menuEntry.setType(MenuAction.WALK);
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

    private List<PathNode> getPath(WorldPoint startWorldPoint, WorldPoint endWorldPoint) {
        long startTimeDateLoad = System.currentTimeMillis();
        SavedWorldDataLoader savedWorldDataLoader = new SavedWorldDataLoader(WorldDataDownloader.Companion.getWorldDataFile());
        PathNode[][][] grid = savedWorldDataLoader.getGrid();
        long endTimeDataLoad = System.currentTimeMillis();

        PathFinder pathFinder = new PathFinder(grid);

        long startTimePathFind = System.currentTimeMillis();
        List<PathNode> nodes = pathFinder.findPath(startWorldPoint, endWorldPoint);
        long endTimePathFind = System.currentTimeMillis();

        System.out.println("Loaded world data in " + (endTimeDataLoad - startTimeDateLoad) + " milliseconds");
        System.out.println("Found path in " + (endTimePathFind - startTimePathFind) + " milliseconds");
        System.out.println("Num of nodes: " + nodes.stream().count());

        return nodes;
    }

    public boolean staticWalkTo(WorldPoint endWorldPoint) {
        Camera.setAngle(45);
        Camera.setPitch(1.0f);

        Player player = Microbot.getClient().getLocalPlayer();
        WorldPoint start = player.getWorldLocation();

        List<PathNode> nodes = getPath(start, endWorldPoint);
        if (nodes.isEmpty()) return false;

        PathWalker pathWalker = new PathWalker(nodes);
        pathWalker.walkPath();

        return player.getWorldLocation().distanceTo(endWorldPoint) <= 3;
    }

    public boolean hybridWalkTo(WorldPoint target) {
        Player player = Microbot.getClient().getLocalPlayer();
        List<PathNode> nodes = getPath(player.getWorldLocation(), target);

        if (nodes.isEmpty()) {
            System.out.println("Static Walker failed to find path, using dynamic walker");
            return walkTo(target, true);

        } else {
            Camera.setAngle(45);
            Camera.setPitch(1.0f);

            if (nodes.isEmpty()) return false;

            PathWalker pathWalker = new PathWalker(nodes);
            pathWalker.walkPath();

            return player.getWorldLocation().distanceTo(target) <= 3;
        }
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
        pathfinder = new Pathfinder(pathfinderConfig);
        pathfinder.customPath = true;
        List<Node> path = new ArrayList();
        for (WorldPoint worldPoint: worldPoints) {
            path.add(new Node(worldPoint, null, 0));
        }
        pathfinder.setPath(path);
        sleepUntilOnClientThread(() -> pathfinder.isDone(), 60000);
        return false;
    }
}
