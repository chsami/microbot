package net.runelite.client.plugins.microbot.shortestpath.pathfinder;

import net.runelite.api.coords.WorldPoint;

public class TransportNode extends Node implements Comparable<TransportNode> {
    public TransportNode(WorldPoint position, Node previous, int wait) {
        super(position, previous, wait);
    }

    @Override
    public int compareTo(TransportNode other) {
        return Integer.compare(cost, other.cost);
    }
}
