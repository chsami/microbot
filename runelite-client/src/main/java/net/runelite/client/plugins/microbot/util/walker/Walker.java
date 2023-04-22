package net.runelite.client.plugins.microbot.util.walker;

import lombok.Getter;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.MicrobotConfig;
import net.runelite.client.plugins.microbot.util.walker.pathfinder.CollisionMap;
import net.runelite.client.plugins.microbot.util.walker.pathfinder.Pathfinder;
import net.runelite.client.plugins.microbot.util.walker.pathfinder.PathfinderConfig;

import java.util.List;
import java.util.Map;

public class Walker {
    @Getter
    public Pathfinder pathfinder;
    public PathfinderConfig pathfinderConfig;

    public Walker(MicrobotConfig config) {
        CollisionMap map = CollisionMap.fromResources();
        Map<WorldPoint, List<Transport>> transports = Transport.fromResources(config);
        pathfinderConfig = new PathfinderConfig(map, transports, Microbot.getClient(), config);
    }

    public void walkTo(WorldPoint target) {
        Player localPlayer = Microbot.getClient().getLocalPlayer();
        if (localPlayer == null) {
            return;
        }

        if (target == null) {
            pathfinder = null;
        } else {
            WorldPoint start = WorldPoint.fromLocalInstance(Microbot.getClient(), localPlayer.getLocalLocation());
            if (pathfinder != null) {
                start = pathfinder.getStart();
            }
            pathfinder = new Pathfinder(pathfinderConfig, start, target);
        }
    }
}
