package net.runelite.client.plugins.microbot.util.walker;

import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.coords.Rs2LocalPoint;

import javax.annotation.Nullable;

public class Rs2MiniMap {
    @Nullable
    public static Point localToMinimap(LocalPoint localPoint) {
        if (localPoint == null) return null;

        return Microbot.getClientThread().runOnClientThread(() -> Perspective.localToMinimap(Microbot.getClient(), localPoint));
    }

    @Nullable
    public static Point worldToMinimap(WorldPoint worldPoint) {
        if (worldPoint == null) return null;

        LocalPoint localPoint = LocalPoint.fromWorld(Microbot.getClient(), worldPoint);

        if (Microbot.getClient().getTopLevelWorldView().isInstance() && localPoint == null) {
            localPoint = Rs2LocalPoint.fromWorldInstance(worldPoint);
        }

        if (localPoint == null) {
            Microbot.log("Tried to walk worldpoint " + worldPoint + " using the canvas but localpoint returned null");
            return null;
        }

        final LocalPoint lp = localPoint;

        return Microbot.getClientThread().runOnClientThread(() -> Perspective.localToMinimap(Microbot.getClient(), lp));
    }
}
