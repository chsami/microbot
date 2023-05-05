package net.runelite.client.plugins.microbot.util.walker.pathfinder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import net.runelite.api.coords.WorldPoint;

public class Node {
    public final WorldPoint position;
    public final Node previous;
    public final int cost;

    public Node(WorldPoint position, Node previous, int wait) {
        this.position = position;
        this.previous = previous;
        this.cost = cost(previous, position, wait);
    }

    public Node(WorldPoint position, Node previous) {
        this(position, previous, 0);
    }

    public List<WorldPoint> getPath() {
        List<WorldPoint> path = new LinkedList<>();
        Node node = this;

        while (node != null) {
            path.add(0, node.position);
            node = node.previous;
        }

        return new ArrayList<>(path);
    }

    private static int cost(Node previous, WorldPoint current, int wait) {
        int previousCost = 0;
        int distance = 0;

        if (previous != null) {
            previousCost = previous.cost;
            distance = distanceBetween(previous.position, current);

            boolean isTransport = distance > 1 || previous.position.getPlane() != current.getPlane();
            if (isTransport) {
                distance = wait;
            }
        }

        return previousCost + distance;
    }

    public static int distanceBetween(WorldPoint previous, WorldPoint current, int diagonal) {
        int dx = Math.abs(previous.getX() - current.getX());
        int dy = Math.abs(previous.getY() - current.getY());

        if (diagonal == 1) {
            return Math.max(dx, dy);
        } else if (diagonal == 2) {
            return dx + dy;
        }

        return Integer.MAX_VALUE;
    }

    public static int distanceBetween(WorldPoint previous, WorldPoint current) {
        return distanceBetween(previous, current, 1);
    }
}
