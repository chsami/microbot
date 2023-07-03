package net.runelite.client.plugins.microbot.util.walker;

import lombok.Getter;
import net.runelite.api.AnimationID;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.camera.Camera;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.magic.Teleport;
import net.runelite.client.plugins.microbot.util.math.Calculations;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.tabs.Tab;
import net.runelite.client.plugins.microbot.util.walker.pathfinder.CollisionMap;
import net.runelite.client.plugins.microbot.util.walker.pathfinder.Pathfinder;
import net.runelite.client.plugins.microbot.util.walker.pathfinder.PathfinderConfig;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

import static net.runelite.client.plugins.microbot.util.Global.sleep;
import static net.runelite.client.plugins.microbot.util.Global.sleepUntil;
import static net.runelite.client.plugins.microbot.util.math.Random.random;
import static org.apache.commons.lang3.tuple.Pair.of;

public class Walker {
    @Getter
    public Pathfinder pathfinder;
    public PathfinderConfig pathfinderConfig;

    public WorldPoint currentDestination;

    List<Transport> ignoreTransport = new ArrayList();

    List<WorldPoint> pathOrigin = new ArrayList<>();


    public Walker() {
        CollisionMap map = CollisionMap.fromResources();
        Map<WorldPoint, List<Transport>> transports = Transport.fromResources();
        pathfinderConfig = new PathfinderConfig(map, transports);
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

    public WorldPoint walkFastRegionCanvas(int regionX, int regionY) {
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

    public WorldPoint walkFastCanvas(WorldPoint worldPoint) {
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
        return walkTo(target, false, false);
    }

    public boolean walkTo(WorldPoint target, boolean useTransport) {
        return walkTo(target, false, useTransport);
    }

    public boolean walkTo(WorldPoint target, boolean memorizePath, boolean useTransport) {
        if (pathfinder != null && !pathfinder.isDone()) return false;

        if (pathfinder != null && pathfinder.isDone() && (pathfinder.getTarget() == null || !pathfinder.getTarget().equals(target))) {
            pathfinder = null;
        }

        if (useTransport && pathfinder == null) {
            Pair<Pathfinder, Teleport> p = walkToWithTeleports(Microbot.getClient().getLocalPlayer().getWorldLocation(), target);
            if (p != null && p.getRight() != null) {
                pathfinder = p.getLeft();
                if (p.getRight() != null) {
                    if (Inventory.hasItem(p.getRight().getTabletName())) {
                        Inventory.useItem(p.getRight().getTabletName());
                        sleepUntil(() -> Microbot.getClient().getLocalPlayer().getAnimation() == 4069);
                    } else {
                        Tab.switchToMagicTab();
                        Rs2Widget.clickWidget(p.getRight().getWidgetText());
                        sleepUntil(() -> Microbot.getClient().getLocalPlayer().getAnimation() == 714);
                    }
                }
            }
        }
        boolean reverse = false;
        if (Microbot.getClient().getMinimapZoom() != 0.5)
            Microbot.getClient().setMinimapZoom(0.5);

        Player localPlayer = Microbot.getClient().getLocalPlayer();
        if (localPlayer == null) {
            return false;
        }

        if (target == null) {
            pathfinder = null;
        } else {
            if (memorizePath && pathOrigin.size() > 0 && pathfinder == null) {
                pathfinder = new Pathfinder(pathfinderConfig, target);
                pathfinder.setPath(pathOrigin);
                reverse = pathOrigin.get(pathOrigin.size() - 1).distanceTo(target) < 4;
                if (reverse)
                    Collections.reverse(pathfinder.getPath());
                currentDestination = null;
            }
            if (memorizePath && pathfinder != null && pathOrigin.size() == 0)
                pathOrigin = pathfinder.getPath();

            if (pathfinder == null) {
                WorldPoint start = WorldPoint.fromLocalInstance(Microbot.getClient(), localPlayer.getLocalLocation());
                if (pathfinder != null) {
                    start = pathfinder.getStart();
                }
                pathfinder = new Pathfinder(pathfinderConfig, start, target, useTransport);
                currentDestination = null;
                ignoreTransport = new ArrayList<>();
                pathOrigin = new ArrayList<>();
                return false;
            }

            if (Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(pathfinder.getPath().stream().findFirst().get()) < 3) {
                pathfinder = null;
                currentDestination = null;
                ignoreTransport = new ArrayList<>();
                return true;
            }

            if (useTransport) {
                for (WorldPoint worldPoint : pathfinder.getPath()) {
                    if (ignoreTransport.stream().anyMatch(x -> x.getOrigin().equals(worldPoint))) continue;
                    if (Calculations.tileOnMap(worldPoint)) {
                        if (useTransport(worldPoint)) return false;
                    }
                }
            }


            for (WorldPoint worldPoint : pathfinder.getPath()) {
                if (Calculations.tileOnMap(worldPoint)) {
                    Point point = Calculations.tileToMinimap(worldPoint);
                    Microbot.getMouse().click(point);
                    currentDestination = worldPoint;
                    break;
                }
            }


        }
        return false;
    }

    public boolean useTransport(WorldPoint destinationWorldPoint) {
        LocalPoint localPoint = LocalPoint.fromWorld(Microbot.getClient(), destinationWorldPoint);
        if (localPoint == null) return false;
        List<Transport> transportWorldPoints = pathfinderConfig.getTransports().get(destinationWorldPoint);
        if (transportWorldPoints == null) return false;
        List<Transport> matchingTransports = transportWorldPoints.stream().filter(x -> x.getOrigin().equals(destinationWorldPoint)).collect(Collectors.toList());
        for (Transport transport : matchingTransports) {
            if (transport != null) {
                if (Calculations.tileOnScreen(localPoint)) {
                    if (Rs2GameObject.interact(transport.getObjectId())) {
                        if (!transport.getAction().toLowerCase().contains("open")) {
                            sleepUntil(() -> Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(transport.getDestination()) < 3, 10000);
                            for (Transport transportToIgnore : pathfinderConfig.getTransports().get(transport.getDestination()).stream().collect(Collectors.toList())) {
                                ignoreTransport.add(transportToIgnore);
                            }
                        } else {
                            sleep(2000, 3000);
                        }
                        currentDestination = null;
                        return true;
                    }
                } else {
                    Camera.turnTo(localPoint);
                }
            }
        }
        return false;
    }
}
