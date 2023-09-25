package net.runelite.client.plugins.microbot.util.walker.pathfinder;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Point;
import net.runelite.api.Skill;
import net.runelite.api.TileObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.camera.Camera;
import net.runelite.client.plugins.microbot.util.equipment.JewelleryLocationEnum;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.magic.Rs2Magic;
import net.runelite.client.plugins.microbot.util.magic.Teleport;
import net.runelite.client.plugins.microbot.util.math.Calculations;
import net.runelite.client.plugins.microbot.util.tabs.Tab;
import net.runelite.client.plugins.microbot.util.walker.Transport;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import org.apache.commons.lang3.tuple.Pair;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static net.runelite.client.plugins.microbot.util.Global.sleepUntil;
import static net.runelite.client.plugins.microbot.util.walker.pathfinder.CollisionMap.wallNodes;

public class Pathfinder implements Runnable {
    @Getter
    private final WorldPoint start;
    @Getter
    private WorldPoint target;
    private final PathfinderConfig config;

    private final Deque<Node> boundary = new LinkedList<>();
    private final Set<WorldPoint> visited = new HashSet<>();
    @Getter
    private final Queue<Node> pending = new PriorityQueue<>();
    @Getter
    @Setter
    private List<Node> path = new ArrayList<>();
    @Getter
    private boolean done = false;
    @Getter
    int totalCost = 0;

    @Getter
    @Setter
    private boolean useTransport;
    Transport useCurrentTransport = null;
    boolean executeWalking;
    boolean useCanvas;
    public boolean customPath = false;


    public boolean getDebugger() {
        return true;
    }

    public Pathfinder(PathfinderConfig config) {
        this(config, null, null);
    }

    public Pathfinder(PathfinderConfig config, WorldPoint start, WorldPoint target) {
        this(config, start, target, false);
    }

    public Pathfinder(PathfinderConfig config, WorldPoint start, WorldPoint target, boolean canReach) {
        this(config, start, target, false, canReach, false, null);
    }

    public Pathfinder(PathfinderConfig config, WorldPoint start, WorldPoint target, boolean useTransport, boolean canReach, boolean useCanvas, WorldArea[] blockingAreas) {
        this.config = config;
        this.config.refresh();

        this.start = start;
        this.target = target;

        this.executeWalking = !canReach;
        this.useTransport = useTransport;
        this.useCanvas = useCanvas;

        if (blockingAreas != null) {
            CollisionMap.blockingAreas = blockingAreas;
        }

        // Calculate all the staircase, obstacles, ladders in advance before calculating the path to walk
        precalculateTransports(start, target, useTransport);

        final int teleportThreshHoldDistance = 20;

        Transport tunnel = getClosestTunnel();

        if (useTransport && start.distanceTo(target) > teleportThreshHoldDistance) {
            Tab.switchToInventoryTab();

            Teleport shortestPathSpell = null;
            JewelleryLocationEnum shortestPathJewellery = null;
            boolean hasTablet = false;

            // Teleport spells logic
            for (Teleport teleport : Teleport.values()) {
                if (teleport.getDestination().distanceTo2D(target) > tunnel.destination.distanceTo2D(target))
                    continue;
                if (shortestPathSpell != null && shortestPathSpell.getDestination().distanceTo2D(target) <= teleport.getDestination().distanceTo2D(target))
                    continue;
                if (teleport.getDestination().equals(target) || teleport.getDestination().distanceTo2D(target) < start.distanceTo2D(target)) {
                    hasTablet = Inventory.hasItem(teleport.getTabletName());
                    boolean hasRunes = true;
                    for (Pair itemRequired : teleport.getItemsRequired()) {
                        if (!Inventory.hasItemAmountStackable(itemRequired.getLeft().toString(), (int) itemRequired.getRight()))
                            hasRunes = false;
                    }

                    if (hasRunes && Microbot.getClient().getBoostedSkillLevel(Skill.MAGIC) >= teleport.getLevel()) {
                        shortestPathSpell = teleport;
                    }

                    if (hasTablet) {
                        shortestPathSpell = teleport;
                    }
                }
            }
            // Jewellery teleport logic
            for (JewelleryLocationEnum jewelleryLocationEnum : JewelleryLocationEnum.values()) {
                if (jewelleryLocationEnum.getLocation().distanceTo2D(target) >= start.distanceTo2D(target))
                    continue;
                if (jewelleryLocationEnum.getLocation().distanceTo2D(target) > tunnel.destination.distanceTo2D(target))
                    continue;
                if (!Rs2Equipment.hasEquippedContains(jewelleryLocationEnum.getTooltip() + "(")
                        && !Inventory.hasItemContains(jewelleryLocationEnum.getTooltip() + "("))
                    continue;
                if (shortestPathJewellery != null && shortestPathJewellery.getLocation().distanceTo2D(target) <= jewelleryLocationEnum.getLocation().distanceTo2D(target))
                    continue;

                shortestPathJewellery = jewelleryLocationEnum;
            }

            if (shortestPathSpell != null && shortestPathJewellery != null) {
                if (shortestPathSpell.getDestination().distanceTo2D(target) <= shortestPathJewellery.getLocation().distanceTo2D(target)) {
                    useTeleport(shortestPathSpell, hasTablet);
                } else {
                    useJewellery(shortestPathJewellery);
                }
            } else if (shortestPathSpell != null) {
                useTeleport(shortestPathSpell, hasTablet);
            } else if (shortestPathJewellery != null) {
                useJewellery(shortestPathJewellery);
            }
        }

        run();
    }

    private void useTeleport(Teleport shortestPathSpell, boolean hasTablet) {
        //no need to teleport if we are close enough
        if (shortestPathSpell.getDestination().distanceTo2D(Microbot.getClient().getLocalPlayer().getWorldLocation()) < 10)
            return;
        if (hasTablet) {
            Inventory.useItemFast(shortestPathSpell.getTabletName(), "Break");
        } else {
            Rs2Magic.cast(shortestPathSpell.getSpell());
        }
        sleepUntil(Microbot::isAnimating);
        sleepUntil(() -> !Microbot.isAnimating());
    }

    private void useJewellery(JewelleryLocationEnum shortestPathJewellery) {
        if (shortestPathJewellery.getLocation().distanceTo2D(Microbot.getClient().getLocalPlayer().getWorldLocation()) < 10)
            return;
        String itemName = shortestPathJewellery.getTooltip() + "(";
        boolean hasItemEquipped = Rs2Equipment.hasEquippedContains(itemName);
        if (hasItemEquipped) {
            if (shortestPathJewellery.name().contains("ring")) {
                Rs2Equipment.useRingAction(shortestPathJewellery);
            } else {
                Rs2Equipment.useAmuletAction(shortestPathJewellery);
            }
            sleepUntil(Microbot::isAnimating);
            sleepUntil(() -> !Microbot.isAnimating());
        } else if (Inventory.hasItemContains(itemName)) {
            Inventory.useItemFastContains(itemName, "Rub");
            sleepUntil(() -> Rs2Widget.getWidget(219, 1) != null);
            VirtualKeyboard.typeString(Integer.toString(shortestPathJewellery.getIdentifier() - 1));
            sleepUntil(Microbot::isAnimating);
            sleepUntil(() -> !Microbot.isAnimating());
        }
    }

    private void precalculateTransports(WorldPoint start, WorldPoint target, boolean useTransport) {
        if (useTransport) {
            if (start.getY() >= 9000 && target.getY() < 9000) {
                // Use object to go surface
                Transport transportSurface = getClosestTunnel();
                if (transportSurface != null) {
                    this.target = transportSurface.getOrigin();
                    this.useCurrentTransport = transportSurface;
                }
            } else if (target.getPlane() != Microbot.getClient().getLocalPlayer().getWorldLocation().getPlane()) {
                //custom logic for staircase/ladders
                Transport linkedTransport = getMatchingTransport(Microbot.getClient().getLocalPlayer().getWorldLocation().getPlane());
                while (linkedTransport != null) {
                    if (linkedTransport.linkedTransport == null) {
                        this.target = linkedTransport.origin;
                        this.useCurrentTransport = linkedTransport;
                        break;
                    }
                    if (target.getPlane() > Microbot.getClient().getLocalPlayer().getWorldLocation().getPlane()) {
                        if (linkedTransport.linkedTransport.getOrigin().getPlane() == Microbot.getClient().getLocalPlayer().getWorldLocation().getPlane()) {
                            this.target = linkedTransport.linkedTransport.getOrigin();
                            this.useCurrentTransport = linkedTransport.linkedTransport;
                            break;
                        }
                    } else {
                        if (linkedTransport.linkedTransport.getDestination().getPlane() == Microbot.getClient().getLocalPlayer().getWorldLocation().getPlane()) {
                            this.target = linkedTransport.linkedTransport.getDestination();
                            this.useCurrentTransport = linkedTransport.linkedTransport;
                            this.useCurrentTransport.action = this.useCurrentTransport.reverseAction;
                            break;
                        }
                    }
                    linkedTransport = linkedTransport.linkedTransport;
                }
            } else  if (start.getY() < 9000 && target.getY() > 9000) {
                // Use object to go underground
                Transport transportUnderground = getClosestTunnel();
                if (transportUnderground != null) {
                    this.target = transportUnderground.getOrigin();
                    this.useCurrentTransport = transportUnderground;
                }
            }
        }
    }

    private void addNeighbors(Node node) {
        for (Node neighbor : config.getMap().getNeighbors(node, config, useTransport, target, !this.executeWalking)) {
            if (config.avoidWilderness(node.position, neighbor.position, target)) {
                continue;
            }
            if (visited.add(neighbor.position)) {
                if (neighbor instanceof TransportNode) {
                    pending.add(neighbor);
                } else {
                    boundary.addLast(neighbor);
                }
            }
        }
    }

    public void run() {
        if (handleTransports()) {
            done = true;
            boundary.clear();
            visited.clear();
            pending.clear();
            return;
        }
        CollisionMap.nodesChecked = new ArrayList<>();
        wallNodes = new ArrayList<>();
        Microbot.status = "Calculating webwalking, please wait...";
        boundary.addFirst(new Node(start, null));

        int bestDistance = Integer.MAX_VALUE;
        long bestHeuristic = Integer.MAX_VALUE;
        Instant cutoffTime = Instant.now().plus(Duration.ofSeconds(8));

        while (!boundary.isEmpty() || !pending.isEmpty()) {
            Node node = boundary.peekFirst();
            Node p = pending.peek();

            if (p != null && (node == null || p.cost < node.cost)) {
                boundary.addFirst(p);
                pending.poll();
            }

            node = boundary.removeFirst();

            if (node == null || node.position == null) break;

            if (node.position.distanceTo(target) < 1 || (!config.isNear(start))) {
                path = node.getPath();
                break;
            }

            int distance = Node.distanceBetween(node.position, target);
            long heuristic = distance + Node.distanceBetween(node.position, target, 2);
            if (heuristic < bestHeuristic || (heuristic <= bestHeuristic && distance < bestDistance)) {
                path = node.getPath();
                totalCost += node.cost;
                bestDistance = distance;
                bestHeuristic = heuristic;
                cutoffTime = Instant.now().plus(Duration.ofSeconds(8));
            }

            if (Instant.now().isAfter(cutoffTime)) {
                break;
            }

            addNeighbors(node);
        }


        if (this.executeWalking && !handleDoors() && !handleShortcuts()) {
            //  Collections.reverse(path);
            if (useCanvas) {
                handleWalkableNodesCanvas();
            } else {
                handleWalkableNodes();
            }
        }

        done = true;
        boundary.clear();
        visited.clear();
        pending.clear();
    }

    private boolean handleShortcuts() {
        for (Node node : new ArrayList<>(path)) {
            if (Microbot.getClient().getLocalPlayer().getWorldLocation().equals(node.position)) continue;
            Transport transport = getMatchingTransport(node.position);
            if (transport == null) continue;
            if (Camera.isTileOnScreen(LocalPoint.fromWorld(Microbot.getClient(), transport.origin))) {
                TileObject tileObject = Rs2GameObject.findObjectByLocation(transport.origin);
                if (!Rs2GameObject.hasLineOfSight(tileObject)) continue;
                Rs2GameObject.interact(tileObject);
                sleepUntil(Microbot::isAnimating);
                sleepUntil(() -> !Microbot.isAnimating());
                return true;
            }
        }
        return false;
    }

    private void handleWalkableNodes() {
        Node lastNode = path.get(path.size() - 1);
        //if the our target is on the minimap, just navigate to it directly
        if (this.target.distanceTo2D(Microbot.getClient().getLocalPlayer().getWorldLocation()) < 7) {
            if (Calculations.tileOnMap(this.target)) {
                Point point = Calculations.tileToMinimap(this.target);
                Microbot.getMouse().click(point);
                return;
            }
        }
        for (Node node : path) {
            if (Calculations.tileOnMap(node.position)
                    && (node.position.distanceTo(Microbot.getClient().getLocalPlayer().getWorldLocation()) > 14
                    || customPath || lastNode == node)) {
                Point point = Calculations.tileToMinimap(node.position);
                Microbot.getMouse().click(point);
                break;
            }
        }
    }

    private void handleWalkableNodesCanvas() {
        for (Node node : path) {
            if (Calculations.tileOnMap(node.position) && node.position.distanceTo(Microbot.getClient().getLocalPlayer().getWorldLocation()) < 3) {
                LocalPoint localPoint = LocalPoint.fromWorld(Microbot.getClient(), node.position);
                Microbot.getWalker().walkFastLocal(localPoint);
                break;
            }
        }
    }

    private boolean handleTransports() {
        boolean skip = false;
        if (useTransport) {
            // Custom logic for staircase/ladders
            boolean isLadder = useCurrentTransport != null;
            if (isLadder) {
                if (Microbot.getWalker().getReachDistance(target) > 17)
                    return false;
                if (useCurrentTransport.offsetX != 0 || useCurrentTransport.offsetY != 0) {
                    this.target = new WorldPoint(this.target.getX() + useCurrentTransport.offsetX, this.target.getY() + useCurrentTransport.offsetY, this.target.getPlane());
                }
                TileObject tileObject = Rs2GameObject.findObjectByLocation(this.target);
                if (!Rs2GameObject.hasLineOfSight(tileObject)) return false;
                Rs2GameObject.interact(tileObject, this.useCurrentTransport.getAction());
                int currentPlane = Microbot.getClient().getLocalPlayer().getWorldLocation().getPlane();
                sleepUntil(() -> currentPlane != Microbot.getClient().getLocalPlayer().getWorldLocation().getPlane());
                skip = true;
            }
        }
        return skip;
    }

    private boolean handleDoors() {
        if (useCanvas) return false;
        boolean skip = false;
        CheckedNode[] wallNodeArrays = wallNodes.toArray(CheckedNode[]::new);
        for (final CheckedNode wallNode : wallNodeArrays) {
            if (wallNode.node.position.equals(start)) continue;
            if (path.stream().noneMatch(x -> x.position.equals(wallNode.node.position))) continue;
            if (Calculations.tileOnMap(wallNode.node.position) && wallNode.shape != null) {
                Microbot.getMouse().click(wallNode.shape.getBounds());
                sleepUntil(Microbot::isWalking);
                sleepUntil(() -> !Microbot.isWalking());
                skip = true;
                break;
            }
        }
        return skip;
    }

    public Transport getMatchingTransport(WorldPoint worldPoint) {
        List<Transport> matchingTransports = PathfinderConfig.getTransports().get(worldPoint);
        if (matchingTransports == null || matchingTransports.isEmpty()) return null;
        return matchingTransports.stream().findFirst().orElse(null);
    }

    public Transport getClosestTunnel() {
        Transport closestTunnel = null;
        WorldPoint playerPosition = Microbot.getClient().getLocalPlayer().getWorldLocation();
        for (Map.Entry<WorldPoint, List<Transport>> entry : PathfinderConfig.getTransports().entrySet()) {
            Transport transport = entry
                    .getValue()
                    .stream()
                    .filter(x -> x.getOrigin().getY() > 9000 || x.getDestination().getY() > 9000)
                    .min(Comparator.comparingInt((Transport t) -> t.origin.distanceTo2D(playerPosition))).orElse(null);

            if (transport == null)
                continue;
            if (closestTunnel != null && closestTunnel.origin.distanceTo2D(playerPosition) <= transport.origin.distanceTo2D(playerPosition))
                continue;

            closestTunnel = transport;
        }
        return closestTunnel;
    }

    public Transport getMatchingTransport(int plane) {
        Transport closestTransportNode = null;
        boolean isInCave = Microbot.getClient().getLocalPlayer().getWorldLocation().getY() > 9000;
        for (Map.Entry<WorldPoint, List<Transport>> entry : PathfinderConfig.getTransports().entrySet()) {
            for (Transport transport : entry.getValue()) {
                //if the player is in a cave and the destination of a transport is not in a cave we can skip
                if (isInCave && transport.origin.getY() < 9000)
                    continue;
                if (target.getPlane() > 0 && transport.destination.getPlane() == 0)
                    continue;
                // Search for all the transport objects that bring us on the same height
                if (transport.origin.getPlane() == plane) {
                    int closestTransportNodeDistance = 0;
                    int transportDistance = 0;
                    if (closestTransportNode != null) {
                        closestTransportNodeDistance = closestTransportNode.destination.distanceTo2D(target) + closestTransportNode.destination.distanceTo2D(Microbot.getClient().getLocalPlayer().getWorldLocation());
                        transportDistance = transport.destination.distanceTo2D(target) + transport.destination.distanceTo2D(Microbot.getClient().getLocalPlayer().getWorldLocation());
                        boolean isCloserTransport = transportDistance <= closestTransportNodeDistance  || (transport.priority > closestTransportNode.priority && transportDistance < 50);
                        if (!isCloserTransport)
                            continue;
                    }
                    // if the origin of the transport is on another plane, skip it
                    if (transport.getOrigin().getPlane() != Microbot.getClient().getLocalPlayer().getWorldLocation().getPlane())
                        continue;
                    // if our target plane is greater than our players plane, we want to go up
                    if (target.getPlane() > Microbot.getClient().getLocalPlayer().getWorldLocation().getPlane()) {
                        // if the transport plane is lower than the players current plane, skip this transport, because we want to go up
                        if (transport.getDestination().getPlane() < Microbot.getClient().getLocalPlayer().getWorldLocation().getPlane())
                            continue;
                    } else {
                        // go down
                        // if the transport plane is higher than the players current plane, skip this transport, because we want to go down
                        if (transport.getDestination().getPlane() > Microbot.getClient().getLocalPlayer().getWorldLocation().getPlane())
                            continue;
                    }
                    closestTransportNode = transport;
                }
            }
        }
        return closestTransportNode;
    }
}
