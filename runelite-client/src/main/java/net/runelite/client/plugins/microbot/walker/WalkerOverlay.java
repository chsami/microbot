package net.runelite.client.plugins.microbot.walker;

import net.runelite.api.Perspective;
import net.runelite.api.Tile;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.walker.pathfinder.PathFinder;
import net.runelite.client.plugins.microbot.walker.pathfinder.PathNode;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;

public class WalkerOverlay extends Overlay {

    private final Color closeColor = Color.GREEN;
    private final Color farColor = Color.RED;

    @Inject
    private WalkerOverlay() {
        setPosition(OverlayPosition.DYNAMIC);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        int pathSize = PathFinder.Companion.getPath().size();
        if (pathSize < 2) {
            return null;
        }

        for (Tile tile : Rs2GameObject.getTiles(1200)) {
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

                    if (!node.getPathTransports().isEmpty()) {
                        graphics.setColor(Color.BLUE);
                        graphics.drawPolygon(poly);
                    }

                }
            }
        }
        return null;
    }

    private Color generateGradient(float step) {
        if (step < 0) {
            step = 0;
        } else if (step > 1) {
            step = 1;
        }

        float[] startComponents = farColor.getRGBColorComponents(null);
        float[] endComponents = closeColor.getRGBColorComponents(null);

        float[] interpolatedComponents = new float[3];

        for (int j = 0; j < 3; j++) {
            interpolatedComponents[j] = startComponents[j] + step * (endComponents[j] - startComponents[j]);
        }

        return new Color(interpolatedComponents[0], interpolatedComponents[1], interpolatedComponents[2]);
    }

}
