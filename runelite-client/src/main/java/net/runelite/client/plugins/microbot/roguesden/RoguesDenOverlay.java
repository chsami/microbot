package net.runelite.client.plugins.microbot.roguesden;


import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;

public class RoguesDenOverlay extends Overlay {
    private static final Color OBJECT_BORDER_COLOR;
    private static final Color OBJECT_COLOR;
    private static final Color OBJECT_BORDER_HOVER_COLOR;
    private final Client client;
    private final RoguesDenPlugin plugin;

    @Inject
    public RoguesDenOverlay(Client client, RoguesDenPlugin plugin) {
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setLayer(OverlayLayer.ABOVE_SCENE);
        this.client = client;
        this.plugin = plugin;
    }

    public Dimension render(Graphics2D graphics) {
        if (!this.plugin.isHasGem()) {
            return null;
        } else {
            this.plugin.getObstaclesHull().forEach((obstaclex, tile) -> {
                if (tile.getPlane() == this.client.getPlane()) {
                    Shape clickBox = obstaclex.getClickbox();
                    if (clickBox != null) {
                        Point mouse = this.client.getMouseCanvasPosition();
                        if (clickBox.contains((double)mouse.getX(), (double)mouse.getY())) {
                            graphics.setColor(OBJECT_BORDER_HOVER_COLOR);
                        } else {
                            graphics.setColor(OBJECT_BORDER_COLOR);
                        }

                        graphics.draw(clickBox);
                        graphics.setColor(OBJECT_COLOR);
                        graphics.fill(clickBox);
                    } else {
                        Object p;
                        if (obstaclex instanceof GameObject) {
                            p = ((GameObject)obstaclex).getConvexHull();
                        } else {
                            p = obstaclex.getCanvasTilePoly();
                        }

                        if (p != null) {
                            graphics.setColor(OBJECT_COLOR);
                            graphics.draw((Shape)p);
                        }
                    }
                }

            });
            Obstacles.Obstacle[] obstacles = Obstacles.OBSTACLES;

            for(int i = 0; i < obstacles.length; ++i) {
                Obstacles.Obstacle obstacle = obstacles[i];
                LocalPoint localPoint = LocalPoint.fromWorld(this.client, obstacle.getTile());
                if (localPoint != null && obstacle.getTile().getPlane() == this.client.getPlane()) {
                    if (!obstacle.getHint().isEmpty()) {
                        Polygon polygon = Perspective.getCanvasTilePoly(this.client, localPoint);
                        if (polygon != null) {
                            graphics.setColor(obstacle.getTileColor());
                            graphics.drawPolygon(polygon);
                        }
                    }

                    Point textLocation = Perspective.getCanvasTextLocation(this.client, graphics, localPoint, obstacle.getHint(), 0);
                    if (textLocation != null) {
                        graphics.setColor(Color.LIGHT_GRAY);
                        graphics.drawString(i + ": " + obstacle.getHint(), textLocation.getX(), textLocation.getY());
                    }
                }
            }

            return null;
        }
    }

    static {
        OBJECT_BORDER_COLOR = Color.RED;
        OBJECT_COLOR = new Color(OBJECT_BORDER_COLOR.getRed(), OBJECT_BORDER_COLOR.getGreen(), OBJECT_BORDER_COLOR.getBlue(), 50);
        OBJECT_BORDER_HOVER_COLOR = OBJECT_BORDER_COLOR.darker();
    }
}
