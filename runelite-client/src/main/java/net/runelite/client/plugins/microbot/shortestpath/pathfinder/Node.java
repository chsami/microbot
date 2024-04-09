package net.runelite.client.plugins.microbot.shortestpath.pathfinder;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.shortestpath.WorldPointUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Node {
    public final int packedPosition;
    public final Node previous;
    public final int cost;

    public Node(WorldPoint position, Node previous, int wait) {
        this.packedPosition = WorldPointUtil.packWorldPoint(position);
        this.previous = previous;
        this.cost = cost(previous, wait);
    }

    public Node(WorldPoint position, Node previous) {
        this(position, previous, 0);
    }

    public Node(int packedPosition, Node previous, int wait) {
        this.packedPosition = packedPosition;
        this.previous = previous;
        this.cost = cost(previous, wait);
    }

    public Node(int packedPosition, Node previous) {
        this(packedPosition, previous, 0);
    }

    public List<WorldPoint> getPath() {
        List<WorldPoint> path = new LinkedList<>();
        Node node = this;

        while (node != null) {
            WorldPoint position = WorldPointUtil.unpackWorldPoint(node.packedPosition);
            path.add(0, position);
            node = node.previous;
        }

        return new ArrayList<>(path);
    }

    public List<Integer> getPathPacked() {
        List<Integer> path = new LinkedList<>();
        Node node = this;

        while (node != null) {
            path.add(0, node.packedPosition);
            node = node.previous;
        }

        return new ArrayList<>(path);
    }

    private int cost(Node previous, int wait) {
        int previousCost = 0;
        int distance = 0;

        if (previous != null) {
            previousCost = previous.cost;
            // Travel wait time is converted to distance as if the player is walking 1 tile/tick.
            // TODO: reduce the distance if the player is currently running and has enough run energy for the distance?
            distance = wait > 0 ? wait : WorldPointUtil.distanceBetween(previous.packedPosition, packedPosition);
        }

        return previousCost + distance;
    }
}
