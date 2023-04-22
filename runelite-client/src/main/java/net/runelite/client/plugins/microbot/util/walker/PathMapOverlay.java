package net.runelite.client.plugins.microbot.util.walker;

import com.google.inject.Inject;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.List;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.MicrobotConfig;
import net.runelite.client.plugins.microbot.util.walker.pathfinder.PathfinderConfig;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.worldmap.WorldMapOverlay;

public class PathMapOverlay extends Overlay {
    private final Client client;
    private final MicrobotConfig config;

    @Inject
    private WorldMapOverlay worldMapOverlay;

    @Inject
    private PathMapOverlay(Client client, MicrobotConfig config) {
        this.client = client;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.LOW);
        setLayer(OverlayLayer.MANUAL);
        drawAfterLayer(WidgetInfo.WORLD_MAP_VIEW);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!config.drawMap()) {
            return null;
        }

        if (client.getWidget(WidgetInfo.WORLD_MAP_VIEW) == null) {
            return null;
        }

        Area worldMapClipArea = getWorldMapClipArea(client.getWidget(WidgetInfo.WORLD_MAP_VIEW).getBounds());
        graphics.setClip(worldMapClipArea);

        if (config.drawCollisionMap()) {
            graphics.setColor(config.colourCollisionMap());
            Rectangle extent = getWorldMapExtent(client.getWidget(WidgetInfo.WORLD_MAP_VIEW).getBounds());
            final int z = client.getPlane();
            for (int x = extent.x; x < (extent.x + extent.width + 1); x++) {
                for (int y = extent.y - extent.height; y < (extent.y + 1); y++) {
                    if (Microbot.getWalker().pathfinderConfig.getMap().isBlocked(x, y, z)) {
                        drawOnMap(graphics, new WorldPoint(x, y, z), false);
                    }
                }
            }
        }

        if (config.drawTransports()) {
            graphics.setColor(Color.WHITE);
            for (WorldPoint a : Microbot.getWalker().pathfinderConfig.getTransports().keySet()) {
                Point mapA = worldMapOverlay.mapWorldPointToGraphicsPoint(a);
                if (mapA == null || !worldMapClipArea.contains(mapA.getX(), mapA.getY())) {
                    continue;
                }

                for (Transport b : Microbot.getWalker().pathfinderConfig.getTransports().getOrDefault(a, new ArrayList<>())) {
                    Point mapB = worldMapOverlay.mapWorldPointToGraphicsPoint(b.getDestination());
                    if (mapB == null || !worldMapClipArea.contains(mapB.getX(), mapB.getY())) {
                        continue;
                    }

                    graphics.drawLine(mapA.getX(), mapA.getY(), mapB.getX(), mapB.getY());
                }
            }
        }

        if (Microbot.getWalker().getPathfinder() != null) {
            Color colour = Microbot.getWalker().getPathfinder().isDone() ? config.colourPath() : config.colourPathCalculating();
            List<WorldPoint> path = Microbot.getWalker().getPathfinder().getPath();
            for (int i = 0; i < path.size(); i++) {
                graphics.setColor(colour);
                WorldPoint point = path.get(i);
                WorldPoint last = (i > 0) ? path.get(i - 1) : point;
                if (point.distanceTo(last) > 1) {
                    drawOnMap(graphics, last, point, true);
                }
                drawOnMap(graphics, point, true);
            }
        }

        return null;
    }

    private void drawOnMap(Graphics2D graphics, WorldPoint point, boolean checkHover) {
        drawOnMap(graphics, point, point.dx(1).dy(-1), checkHover);
    }

    private void drawOnMap(Graphics2D graphics, WorldPoint point, WorldPoint offset, boolean checkHover) {
        Point start = Microbot.getWalker().pathfinderConfig.mapWorldPointToGraphicsPoint(point);
        Point end = Microbot.getWalker().pathfinderConfig.mapWorldPointToGraphicsPoint(offset);

        if (start == null || end == null) {
            return;
        }

        int x = start.getX();
        int y = start.getY();
        final int width = end.getX() - x;
        final int height = end.getY() - y;
        x -= width / 2;
        y -= height / 2;

        if (point.distanceTo(offset) > 1) {
            graphics.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));
            graphics.drawLine(start.getX(), start.getY(), end.getX(), end.getY());
        } else {
            Point cursorPos = client.getMouseCanvasPosition();
            if (checkHover &&
                cursorPos.getX() >= x && cursorPos.getX() <= (end.getX() - width / 2) &&
                cursorPos.getY() >= y && cursorPos.getY() <= (end.getY() - width / 2)) {
                graphics.setColor(graphics.getColor().darker());
            }
            graphics.fillRect(x, y, width, height);
        }
    }

    private Area getWorldMapClipArea(Rectangle baseRectangle) {
        final Widget overview = client.getWidget(WidgetInfo.WORLD_MAP_OVERVIEW_MAP);
        final Widget surfaceSelector = client.getWidget(WidgetInfo.WORLD_MAP_SURFACE_SELECTOR);

        Area clipArea = new Area(baseRectangle);

        if (overview != null && !overview.isHidden()) {
            clipArea.subtract(new Area(overview.getBounds()));
        }

        if (surfaceSelector != null && !surfaceSelector.isHidden()) {
            clipArea.subtract(new Area(surfaceSelector.getBounds()));
        }

        return clipArea;
    }

    private Rectangle getWorldMapExtent(Rectangle baseRectangle) {
        WorldPoint topLeft = Microbot.getWalker().pathfinderConfig.calculateMapPoint(new Point(baseRectangle.x, baseRectangle.y));
        WorldPoint bottomRight = Microbot.getWalker().pathfinderConfig.calculateMapPoint(
            new Point(baseRectangle.x + baseRectangle.width, baseRectangle.y + baseRectangle.height));
        return new Rectangle(topLeft.getX(), topLeft.getY(), bottomRight.getX() - topLeft.getX(), topLeft.getY() - bottomRight.getY());
    }
}
