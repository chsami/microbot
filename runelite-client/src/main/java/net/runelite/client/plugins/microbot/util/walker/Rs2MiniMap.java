package net.runelite.client.plugins.microbot.util.walker;

import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;

import javax.annotation.Nullable;

public class Rs2MiniMap {
    @Nullable
    public static Point localToMinimap(LocalPoint localPoint) {
        if (localPoint == null) return null;

        return Microbot.getClientThread().runOnClientThread(() -> Perspective.localToMinimap(Microbot.getClient(), localPoint));
    }

    @Nullable
    public static Point worldToMinimap(WorldPoint point) {
        if (point == null) return null;

        LocalPoint localPoint = LocalPoint.fromWorld(Microbot.getClient(), point);

        if (localPoint == null) return null;

        return Microbot.getClientThread().runOnClientThread(() -> Perspective.localToMinimap(Microbot.getClient(), localPoint));
    }
}
