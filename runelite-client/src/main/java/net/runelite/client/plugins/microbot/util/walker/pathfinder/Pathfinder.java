package net.runelite.client.plugins.microbot.util.walker.pathfinder;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.magic.Teleport;

public class Pathfinder implements Runnable {
    @Getter
    private final WorldPoint start;
    @Getter
    private final WorldPoint target;
    private final PathfinderConfig config;

    private final Deque<Node> boundary = new LinkedList<>();
    private final Set<WorldPoint> visited = new HashSet<>();
    @Getter
    private final Queue<Node> pending = new PriorityQueue<>();

    @Getter
    @Setter
    private List<WorldPoint> path = new ArrayList<>();
    @Getter
    private boolean done = false;
    @Getter
    int totalCost = 0;

    boolean isTeleport = false;

    @Getter
    @Setter
    private boolean useTransport = true;


    public Pathfinder(PathfinderConfig config, WorldPoint start, WorldPoint target) {
        this.config = config;
        this.start = start;
        this.target = target;
        this.config.refresh();

        new Thread(this).start();
    }

    public Pathfinder(PathfinderConfig config, WorldPoint start, WorldPoint target, boolean useTransport) {
        this.config = config;
        this.start = start;
        this.target = target;
        this.config.refresh();
        this.useTransport = useTransport;

        for (Teleport teleport : Teleport.values()) {
            if (teleport.getDestination().equals(start)) {
                isTeleport = true;
                break;
            }
        }

        new Thread(this).start();
    }

    public Pathfinder(PathfinderConfig config) {
        this.config = config;
        this.config.refresh();
        start = null;
        target = null;
        done = true;
    }

    private void addNeighbors(Node node) {
        for (Node neighbor : config.getMap().getNeighbors(node, config, useTransport)) {
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

    @Override
    public void run() {
        Microbot.status = "Calculating webwalking, please wait...";
        boundary.addFirst(new Node(start, null));

        int bestDistance = Integer.MAX_VALUE;
        long bestHeuristic = Integer.MAX_VALUE;
        Instant cutoffTime = Instant.now().plus(Duration.ofSeconds(3));

        while (!boundary.isEmpty() || !pending.isEmpty()) {
            System.out.println(boundary.size());
            Node node = boundary.peekFirst();
            Node p = pending.peek();

            if (p != null && (node == null || p.cost < node.cost)) {
                boundary.addFirst(p);
                pending.poll();
            }

            node = boundary.removeFirst();

            if (node.position.equals(target) || (!isTeleport && !config.isNear(start))) {
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
                cutoffTime = Instant.now().plus(Duration.ofSeconds(3));
            }

            if (Instant.now().isAfter(cutoffTime)) {
                break;
            }

            addNeighbors(node);
        }

        Collections.reverse(path);

        done = true;
        boundary.clear();
        visited.clear();
        pending.clear();
    }
}
