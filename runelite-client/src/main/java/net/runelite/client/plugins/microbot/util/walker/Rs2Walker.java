package net.runelite.client.plugins.microbot.util.walker;

import net.runelite.api.MenuAction;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.shortestpath.ShortestPathPlugin;
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera;
import net.runelite.client.plugins.microbot.util.math.Calculations;
import net.runelite.client.plugins.microbot.util.menu.NewMenuEntry;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

import java.awt.*;

import static net.runelite.client.plugins.microbot.util.Global.sleepUntilOnClientThread;

public class Rs2Walker {
    public static boolean walkTo(WorldPoint target) {
        if (Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(target) <= 1) {
            return true;
        }
        ShortestPathPlugin.walkerScript.walkTo(target);
        return false;
    }


    public static WorldPoint walkMiniMap(WorldPoint worldPoint) {
        if (Microbot.getClient().getMinimapZoom() > 2)
            Microbot.getClient().setMinimapZoom(2);

        Point point = Calculations.worldToMinimap(worldPoint.getX(), worldPoint.getY());

        if (point == null) return null;

        Microbot.getMouse().click(point);

        return worldPoint;
    }

    /**
     * Used in instances like pest control
     * @param regionX
     * @param regionY
     * @return
     */
    public static WorldPoint walkFastRegion(int regionX, int regionY) {
        WorldPoint worldPoint = WorldPoint.fromRegion(Microbot.getClient().getLocalPlayer().getWorldLocation().getRegionID(),
                regionX,
                regionY,
                Microbot.getClient().getPlane());

        LocalPoint localPoint = LocalPoint.fromWorld(Microbot.getClient(), worldPoint);

        if (!Calculations.tileOnScreen(localPoint)) {
            Rs2Walker.walkMiniMap(worldPoint); //use minimap if tile is not on screen
            return worldPoint;
        }

        Point canv = Perspective.localToCanvas(Microbot.getClient(), localPoint, Microbot.getClient().getPlane());
        int canvasX = canv != null ? canv.getX() : -1;
        int canvasY = canv != null ? canv.getY() : -1;

        Microbot.doInvoke(new NewMenuEntry(canvasX, canvasY, MenuAction.WALK.getId(), 0, -1, "Walk here"), new Rectangle(1, 1, Microbot.getClient().getCanvasWidth(), Microbot.getClient().getCanvasHeight()));
        //Rs2Reflection.invokeMenu(canvasX, canvasY, MenuAction.WALK.getId(), 0, -1, "Walk here", "", -1, -1);

        return worldPoint;
    }
    /**
     * Used in instances like vorkath, jad
     * @param localPoint
     */
    public static void walkFastLocal(LocalPoint localPoint) {
        Point canv = Perspective.localToCanvas(Microbot.getClient(), localPoint, Microbot.getClient().getPlane());
        int canvasX = canv != null ? canv.getX() : -1;
        int canvasY = canv != null ? canv.getY() : -1;

        Microbot.doInvoke(new NewMenuEntry(canvasX, canvasY, MenuAction.WALK.getId(), 0, -1, "Walk here"), new Rectangle(1, 1, Microbot.getClient().getCanvasWidth(), Microbot.getClient().getCanvasHeight()));
        //Rs2Reflection.invokeMenu(canvasX, canvasY, MenuAction.WALK.getId(), 0, -1, "Walk here", "", -1, -1);
    }

    public static void walkFastCanvas(WorldPoint worldPoint) {
        Rs2Player.toggleRunEnergy(true);
        LocalPoint localPoint = LocalPoint.fromWorld(Microbot.getClient(), worldPoint);
        if (!Calculations.tileOnScreen(localPoint)) {
            Rs2Walker.walkMiniMap(worldPoint); //use minimap if tile is not on screen
            return;
        }
        Point canv = Perspective.localToCanvas(Microbot.getClient(), LocalPoint.fromScene(worldPoint.getX() - Microbot.getClient().getBaseX(), worldPoint.getY() - Microbot.getClient().getBaseY()), Microbot.getClient().getPlane());
        int canvasX = canv != null ? canv.getX() : -1;
        int canvasY = canv != null ? canv.getY() : -1;

        if (canvasX == -1 && canvasY == -1) {
            Rs2Camera.turnTo(localPoint);
        }
        Microbot.doInvoke(new NewMenuEntry(canvasX, canvasY, MenuAction.WALK.getId(), 0, -1, "Walk here"), new Rectangle(1, 1, Microbot.getClient().getCanvasWidth(), Microbot.getClient().getCanvasHeight()));
        //Rs2Reflection.invokeMenu(canvasX, canvasY, MenuAction.WALK.getId(), 0, -1, "Walk here", "", -1, -1);
    }

    public static WorldPoint walkCanvas(WorldPoint worldPoint) {
        Point point = Calculations.worldToCanvas(worldPoint.getX(), worldPoint.getY());

        if (point == null) return null;

        Microbot.getMouse().click(point);

        return worldPoint;
    }

    public static boolean canReach(WorldPoint target) {

        ShortestPathPlugin.walkerScript.setTarget(target);

        sleepUntilOnClientThread(() ->  ShortestPathPlugin.getPathfinder().isDone(), 60000);

        return ShortestPathPlugin.getPathfinder().getPath().get( ShortestPathPlugin.getPathfinder().getPath().size() - 1).distanceTo(target) <= 1;
    }

    public static boolean canReach(WorldPoint target, int objectSize) {
        ShortestPathPlugin.walkerScript.setTarget(target);

        sleepUntilOnClientThread(() ->  ShortestPathPlugin.getPathfinder().isDone(), 60000);

        return ShortestPathPlugin.getPathfinder().getPath().get( ShortestPathPlugin.getPathfinder().getPath().size() - 1).distanceTo2D(target) <= objectSize;
    }

    public static boolean isCloseToRegion(int distance, int regionX, int regionY) {
        WorldPoint worldPoint = WorldPoint.fromRegion(Microbot.getClient().getLocalPlayer().getWorldLocation().getRegionID(),
                regionX,
                regionY,
                Microbot.getClient().getPlane());

        return worldPoint.distanceTo(Microbot.getClient().getLocalPlayer().getWorldLocation()) < distance;
    }
}
