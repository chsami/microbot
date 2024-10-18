package net.runelite.client.plugins.microbot.shortestpath.pathfinder;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.shortestpath.TransportType;

public class TransportNode extends Node implements Comparable<TransportNode> {
    public final TransportType transportType;

    public TransportNode(WorldPoint position, Node previous, int wait, TransportType transportType) {
        super(position, previous, wait);
        this.transportType = transportType;
    }

    @Override
    public int compareTo(TransportNode other) {
        return Integer.compare(cost, other.cost);
    }
}
