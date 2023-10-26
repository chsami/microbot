package net.runelite.client.plugins.microbot.staticwalker;

import net.runelite.api.Perspective;
import net.runelite.api.Tile;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.staticwalker.pathfinder.PathFinder;
import net.runelite.client.plugins.microbot.staticwalker.pathfinder.PathNode;
import net.runelite.client.plugins.microbot.staticwalker.pathfinder.PathWalker;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;

public class StaticWalkerOverlay extends Overlay {

    @Inject
    private StaticWalkerOverlay() {
        setPosition(OverlayPosition.DYNAMIC);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
//        renderPathWalkerOverlay(graphics);
        return null;
    }

    public static void renderPathWalkerOverlay(Graphics2D graphics) {
        if (!PathWalker.Companion.getEnabled()) {
            return;
        }

        int pathSize = PathFinder.Companion.getPath().size();
        if (pathSize < 2) {
            return;
        }

        for (Tile tile : Rs2GameObject.getTiles(10)) {
            for (PathNode node : PathFinder.Companion.getPath()) {
                if (tile.getWorldLocation().equals(node.getWorldLocation())) {
                    Polygon poly = Perspective.getCanvasTilePoly(Microbot.getClient(), tile.getLocalLocation());

                    if (poly == null) {
                        continue;
                    }

                    int currentIndexOfNode = PathFinder.Companion.getPath().indexOf(node);
                    float step = currentIndexOfNode / (float) pathSize;

                    Color newColor = generateGradient(step);
                    newColor = new Color(newColor.getRed(), newColor.getGreen(), newColor.getBlue(), 75);

                    graphics.setColor(newColor);
                    graphics.drawPolygon(poly);
                    graphics.fill(poly);

                    int centerX = (int) poly.getBounds().getCenterX();
                    int centerY = (int) poly.getBounds().getCenterY();
                    int percentage = (int) (step * 100);

                    graphics.setColor(Color.WHITE);
                    graphics.drawString(percentage + "%", centerX - 9, centerY + 5);

                    if (!node.getPathTransports().isEmpty()) {
                        graphics.setColor(Color.BLUE);
                        graphics.drawPolygon(poly);
                    }
                }
            }
        }
    }

    public static Color generateGradient(float step) {
        if (step < 0) {
            step = 0;
        } else if (step > 1) {
            step = 1;
        }

        float[] startComponents = Color.RED.getRGBColorComponents(null);
        float[] endComponents = Color.GREEN.getRGBColorComponents(null);

        float[] interpolatedComponents = new float[3];

        for (int j = 0; j < 3; j++) {
            interpolatedComponents[j] = startComponents[j] + step * (endComponents[j] - startComponents[j]);
        }

        return new Color(interpolatedComponents[0], interpolatedComponents[1], interpolatedComponents[2]);
    }

}
