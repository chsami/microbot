package net.runelite.client.plugins.microbot.util.walker;

import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.magic.Teleport;
import net.runelite.client.plugins.microbot.util.math.Calculations;
import net.runelite.client.plugins.microbot.util.walker.pathfinder.CollisionMap;
import net.runelite.client.plugins.microbot.util.walker.pathfinder.Node;
import net.runelite.client.plugins.microbot.util.walker.pathfinder.Pathfinder;
import net.runelite.client.plugins.microbot.util.walker.pathfinder.PathfinderConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

import static net.runelite.client.plugins.microbot.util.Global.*;

public class Walker {

    public Point travelToCanvasPoint;

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

    private Pair<Pathfinder, Teleport> walkToWithTeleports(WorldPoint start, WorldPoint target) {
        List<Teleport> teleportsAvaible = new ArrayList<>();
        Pathfinder currentPath = new Pathfinder(pathfinderConfig, start, target, false);
        final Pathfinder _currentPath = currentPath;
        sleepUntil(() -> _currentPath.isDone(), 10000);
        Teleport currentTeleport = null;
        for (Teleport teleport : Teleport.values()) {
            boolean hasTablet = Inventory.hasItem(teleport.getTabletName());
            boolean hasRunes = true;
            for (Pair itemRequired : teleport.getItemsRequired()) {
                if (!Inventory.hasItemAmountStackable(itemRequired.getLeft().toString(), (int) itemRequired.getRight()))
                    hasRunes = false;
            }

            if (hasTablet || hasRunes) {
                teleportsAvaible.add(teleport);
            }
        }
        for (Teleport teleportAvailble : teleportsAvaible) {
            final Pathfinder p = new Pathfinder(pathfinderConfig, teleportAvailble.getDestination(), target, false);
            sleepUntil(() -> p.isDone(), 10000);
            if (currentPath.getTotalCost() > p.getTotalCost()) {
                currentTeleport = teleportAvailble;
                currentPath = p;
            }
        }
        return Pair.of(currentPath, currentTeleport);
    }

    public WorldPoint walkFastRegion(int regionX, int regionY) {
        WorldPoint worldPoint = WorldPoint.fromRegion(Microbot.getClient().getLocalPlayer().getWorldLocation().getRegionID(),
                regionX,
                regionY,
                Microbot.getClient().getPlane());

        Point point = Calculations.worldToMinimap(worldPoint.getX(), worldPoint.getY());

        if (point == null) return null;

        Microbot.getMouse().click(point);

        return worldPoint;
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

    public WorldPoint walkFastMinimap(WorldPoint worldPoint) {
        Point point = Calculations.worldToMinimap(worldPoint.getX(), worldPoint.getY());

        if (point == null) return null;

        Microbot.getMouse().click(point);

        return worldPoint;
    }

    public void walkFastCanvas(LocalPoint localPoint) {
//        WorldPoint worldPoint = WorldPoint.fromRegion(Microbot.getClient().getLocalPlayer().getWorldLocation().getRegionID(),
//                0,
//                54,
//                Microbot.getClient().getPlane());
//        LocalPoint localPoint1 = LocalPoint.fromWorld(Microbot.getClient(), worldPoint);
        Point point = Perspective.localToCanvas(Microbot.getClient(), localPoint.getX(), localPoint.getY() - 128, Microbot.getClient().getPlane());

        travelToCanvasPoint = point;
        System.out.println(point);
        Microbot.getMouse().clickFast(1, 1);
        sleep(100);
        travelToCanvasPoint = null;
    }
    public void handleMenuSwapper(MenuEntry menuEntry) {
        if (travelToCanvasPoint == null) return;
        menuEntry.setOption("Walk here");
        menuEntry.setIdentifier(0);
        menuEntry.setParam0(travelToCanvasPoint.getX());
        menuEntry.setParam1(travelToCanvasPoint.getY());
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

    public boolean walkTo(WorldPoint target) {
        return walkTo(target, true);
    }

    public boolean walkTo(WorldPoint target, boolean useTransport) {
        pathfinder = null;
        WorldPoint start = WorldPoint.fromLocalInstance(Microbot.getClient(), Microbot.getClient().getLocalPlayer().getLocalLocation());
        if (pathfinder != null) {
            start = pathfinder.getStart();
        }
        pathfinder = new Pathfinder(pathfinderConfig, start, target, useTransport, false);
        currentDestination = null;
        ignoreTransport = new ArrayList<>();
        pathOrigin = new ArrayList<>();
        sleepUntilOnClientThread(() -> pathfinder.isDone(), 60000);
        return false;
    }

    public boolean canReach(WorldPoint target) {
        pathfinder = null;
        WorldPoint start = WorldPoint.fromLocalInstance(Microbot.getClient(), Microbot.getClient().getLocalPlayer().getLocalLocation());
        if (pathfinder != null) {
            start = pathfinder.getStart();
        }
        pathfinder = new Pathfinder(pathfinderConfig, start, target, true);
        currentDestination = null;
        ignoreTransport = new ArrayList<>();
        pathOrigin = new ArrayList<>();
        sleepUntilOnClientThread(() -> pathfinder.isDone(), 60000);

        boolean result = pathfinder.getPath().get(pathfinder.getPath().size() - 1).position.equals(target);

        return result;
    }

    public boolean walkPath(WorldPoint[] worldPoints) {
        if (worldPoints[worldPoints.length -1].distanceTo(Microbot.getClient().getLocalPlayer().getWorldLocation()) < 4) return true;
        pathfinder = new Pathfinder(pathfinderConfig);
        List<Node> path = new ArrayList();
        for (WorldPoint worldPoint: worldPoints) {
            path.add(new Node(worldPoint, null, 0));
        }
        pathfinder.setPath(path);
        sleepUntilOnClientThread(() -> pathfinder.isDone(), 60000);
        return false;
    }
}
