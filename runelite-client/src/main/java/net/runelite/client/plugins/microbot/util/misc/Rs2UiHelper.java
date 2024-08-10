package net.runelite.client.plugins.microbot.util.misc;

import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.TileObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.antiban.Rs2AntibanSettings;
import net.runelite.client.plugins.microbot.util.math.Rs2Random;

import java.awt.*;
import java.util.Objects;

public class Rs2UiHelper {
    public static boolean isRectangleWithinViewport(Rectangle rectangle) {
        int viewportHeight = Microbot.getClient().getViewportHeight();
        int viewportWidth = Microbot.getClient().getViewportWidth();

        return !(rectangle.getX() > (double) viewportWidth) &&
                !(rectangle.getY() > (double) viewportHeight) &&
                !(rectangle.getX() < 0.0) &&
                !(rectangle.getY() < 0.0);
    }

    public static Point getClickingPoint(Rectangle rectangle, boolean randomize) {
        if (rectangle.getX() == 1 && rectangle.getY() == 1) return new Point(1, 1);
        if (rectangle.getX() == 0 && rectangle.getY() == 0) return new Point(1, 1);
        //check if mouse is already within the rectangle and return current position
        if (Rs2AntibanSettings.naturalMouse) {
            java.awt.Point mousePos = Microbot.getMouse().getMousePosition();
            if (rectangle.contains(mousePos)) return new Point(mousePos.x, mousePos.y);
            else return Rs2Random.randomPointEx(new Point(mousePos.x, mousePos.y), rectangle, 0.3);
        } else
            return Rs2Random.randomPointEx(Microbot.getMouse().getLastClick(), rectangle, 0.3);
    }

    public static Rectangle getNpcClickbox(NPC npc) {

        LocalPoint lp = npc.getLocalLocation();
        if (lp == null) {
            Microbot.log("LocalPoint is null");
            return new Rectangle(1, 1);
        }
        Shape clickbox = Microbot.getClientThread().runOnClientThread(() -> Perspective.getClickbox(Microbot.getClient(), npc.getModel(), npc.getCurrentOrientation(), lp.getX(), lp.getY(),
                Perspective.getTileHeight(Microbot.getClient(), lp, npc.getWorldLocation().getPlane())));


        //check if any of the values are negative and return a new rectangle with positive values
        assert clickbox != null;
        if (clickbox.getBounds().getX() < 0 || clickbox.getBounds().getY() < 0 || clickbox.getBounds().getWidth() < 0 || clickbox.getBounds().getHeight() < 0) {
            return new Rectangle((int) Math.abs(clickbox.getBounds().getX()), (int) Math.abs(clickbox.getBounds().getY()), (int) Math.abs(clickbox.getBounds().getWidth()), (int) Math.abs(clickbox.getBounds().getHeight()));
        }

        return new Rectangle(clickbox.getBounds());
    }

    public static Rectangle getObjectClickbox(TileObject object) {

        if (object == null) return new Rectangle(1, 1);  //return a small rectangle if object is null
        Shape clickbox = Microbot.getClientThread().runOnClientThread(() -> Objects.requireNonNull(object.getClickbox()));


        //check if any of the values are negative and return a new rectangle with positive values
        assert clickbox != null;
        if (clickbox.getBounds().getX() < 0 || clickbox.getBounds().getY() < 0 || clickbox.getBounds().getWidth() < 0 || clickbox.getBounds().getHeight() < 0) {
            return new Rectangle((int) Math.abs(clickbox.getBounds().getX()), (int) Math.abs(clickbox.getBounds().getY()), (int) Math.abs(clickbox.getBounds().getWidth()), (int) Math.abs(clickbox.getBounds().getHeight()));
        }

        return new Rectangle(clickbox.getBounds());
    }


}
