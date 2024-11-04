package net.runelite.client.plugins.microbot.shortestpath.pathfinder;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.shortestpath.TransportType;

public class TransportNode extends Node implements Comparable<TransportNode> {
    public final TransportType transportType;
    public final String displayInfo;

    public TransportNode(WorldPoint position, Node previous, int wait, TransportType transportType, String displayInfo) {
        super(position, previous, wait);
        this.transportType = transportType;
        this.displayInfo = displayInfo;
    }

    @Override
    public int compareTo(TransportNode other) {
        return Integer.compare(cost, other.cost);
    }
}
