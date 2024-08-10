package net.runelite.client.plugins.microbot.util.misc;

import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.TileObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.plugins.microbot.Microbot;

import java.awt.*;
import java.util.Objects;
import java.util.Random;

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
        java.awt.Point mousePos = Microbot.getMouse().getMousePosition();
        if (rectangle.contains(mousePos)) return new Point(mousePos.x, mousePos.y);

        if (!randomize) return new Point((int) rectangle.getCenterX(), (int) rectangle.getCenterY());
        Random random = new Random();
        // Calculate mean and standard deviation for the normal distribution
        double meanX = rectangle.getX() + rectangle.getWidth() / 2.0;
        double meanY = rectangle.getY() + rectangle.getHeight() / 2.0;
        double stddevX = rectangle.getWidth() / 6.0;
        double stddevY = rectangle.getHeight() / 6.0;

        // Generate normally distributed random values
        double normalX = random.nextGaussian() * stddevX + meanX;
        double normalY = random.nextGaussian() * stddevY + meanY;

        // Add jitter with a small uniformly distributed random offset
        double jitterRangeX = rectangle.getWidth() / 10.0;  // Adjust the range as needed
        double jitterRangeY = rectangle.getHeight() / 10.0; // Adjust the range as needed
        double jitterX = random.nextDouble() * jitterRangeX - jitterRangeX / 2.0;
        double jitterY = random.nextDouble() * jitterRangeY - jitterRangeY / 2.0;

        // Apply jitter to the normally distributed values
        normalX += jitterX;
        normalY += jitterY;

        // Clamp the values to ensure they lie within the rectangle
        int x = (int) Math.max(rectangle.getX(), Math.min(normalX, rectangle.getX() + rectangle.getWidth() - 1));
        int y = (int) Math.max(rectangle.getY(), Math.min(normalY, rectangle.getY() + rectangle.getHeight() - 1));

        return new Point(x, y);
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
