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

//        if (Microbot.getClient().isInInstancedRegion()) {
//            WorldPoint playerInstancedWorldLocation =  WorldPoint.fromLocal(Microbot.getClient(), Microbot.getClient().getLocalPlayer().getLocalLocation());
//            LocalPoint l = LocalPoint.fromWorld(Microbot.getClient(), point);
//            //in some instances areas like (tithe farm) the conversion is not needed
//            if (Microbot.getClient().getLocalPlayer().getLocalLocation().equals(l)) return Microbot.getClient().getLocalPlayer().getWorldLocation();
//            playerInstancedWorldLocation = WorldPoint.fromLocalInstance(Microbot.getClient(), l);
//            return playerInstancedWorldLocation;
//        } else {
//            localPoint = LocalPoint.fromWorld(Microbot.getClient(), point);
//        }



        if (localPoint == null) return null;

        return Microbot.getClientThread().runOnClientThread(() -> Perspective.localToMinimap(Microbot.getClient(), localPoint));
    }
}
