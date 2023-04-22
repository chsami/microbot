package net.runelite.client.plugins.microbot.util.walker;

import com.google.inject.Inject;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.Tile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.MicrobotConfig;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

public class PathTileOverlay extends Overlay {
    private final Client client;
    private final MicrobotConfig config;

    @Inject
    public PathTileOverlay(Client client, MicrobotConfig config) {
        this.client = client;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.LOW);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    private void renderTransports(Graphics2D graphics) {
        for (WorldPoint a : Microbot.getWalker().pathfinderConfig.getTransports().keySet()) {
            drawTile(graphics, a, config.colourTransports(), -1, true);

            Point ca = tileCenter(a);

            if (ca == null) {
                continue;
            }

            StringBuilder s = new StringBuilder();
            for (Transport b : Microbot.getWalker().pathfinderConfig.getTransports().getOrDefault(a, new ArrayList<>())) {
                for (WorldPoint origin : WorldPoint.toLocalInstance(client, b.getOrigin())) {
                    Point cb = tileCenter(origin);
                    if (cb != null) {
                        graphics.drawLine(ca.getX(), ca.getY(), cb.getX(), cb.getY());
                    }
                    if (origin.getPlane() > a.getPlane()) {
                        s.append("+");
                    } else if (origin.getPlane() < a.getPlane()) {
                        s.append("-");
                    } else {
                        s.append("=");
                    }
                }
            }
            graphics.setColor(Color.WHITE);
            graphics.drawString(s.toString(), ca.getX(), ca.getY());
        }
    }

    private void renderCollisionMap(Graphics2D graphics) {
        for (Tile[] row : client.getScene().getTiles()[client.getPlane()]) {
            for (Tile tile : row) {
                if (tile == null) {
                    continue;
                }

                Polygon tilePolygon = Perspective.getCanvasTilePoly(client, tile.getLocalLocation());

                if (tilePolygon == null) {
                    continue;
                }

                WorldPoint location = WorldPoint.fromLocalInstance(client, tile.getLocalLocation());
                int x = location.getX();
                int y = location.getY();
                int z = location.getPlane();

                String s = (!Microbot.getWalker().pathfinderConfig.getMap().n(x, y, z) ? "n" : "") +
                        (!Microbot.getWalker().pathfinderConfig.getMap().s(x, y, z) ? "s" : "") +
                        (!Microbot.getWalker().pathfinderConfig.getMap().e(x, y, z) ? "e" : "") +
                        (!Microbot.getWalker().pathfinderConfig.getMap().w(x, y, z) ? "w" : "");

                if (Microbot.getWalker().pathfinderConfig.getMap().isBlocked(x, y, z)) {
                    graphics.setColor(config.colourCollisionMap());
                    graphics.fill(tilePolygon);
                }
                if (!s.isEmpty() && !s.equals("nsew")) {
                    graphics.setColor(Color.WHITE);
                    int stringX = (int) (tilePolygon.getBounds().getCenterX() - graphics.getFontMetrics().getStringBounds(s, graphics).getWidth() / 2);
                    int stringY = (int) tilePolygon.getBounds().getCenterY();
                    graphics.drawString(s, stringX, stringY);
                }
            }
        }
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (config.drawTransports()) {
            this.renderTransports(graphics);
        }

        if (config.drawCollisionMap()) {
            this.renderCollisionMap(graphics);
        }

        if (config.drawTiles() && Microbot.getWalker().getPathfinder() != null && Microbot.getWalker().getPathfinder().getPath() != null) {
            Color color;
            if (Microbot.getWalker().getPathfinder().isDone()) {
                color = new Color(
                    config.colourPath().getRed(),
                    config.colourPath().getGreen(),
                    config.colourPath().getBlue(),
                    config.colourPath().getAlpha() / 2);
            } else {
                color = new Color(
                    config.colourPathCalculating().getRed(),
                    config.colourPathCalculating().getGreen(),
                    config.colourPathCalculating().getBlue(),
                    config.colourPathCalculating().getAlpha() / 2);
            }

            List<WorldPoint> path = Microbot.getWalker().getPathfinder().getPath();
            int counter = 0;
            if (TileStyle.LINES.equals(config.pathStyle())) {
                for (int i = 1; i < path.size(); i++) {
                    drawLine(graphics, path.get(i - 1), path.get(i), color, 1 + counter++);
                }
            } else {
                boolean showTiles = TileStyle.TILES.equals(config.pathStyle());
                for (WorldPoint point : path) {
                    drawTile(graphics, point, color, counter++, showTiles);
                }
            }
        }

        return null;
    }

    private Point tileCenter(WorldPoint b) {
        if (b.getPlane() != client.getPlane()) {
            return null;
        }

        LocalPoint lp = LocalPoint.fromWorld(client, b);
        if (lp == null) {
            return null;
        }

        Polygon poly = Perspective.getCanvasTilePoly(client, lp);
        if (poly == null) {
            return null;
        }

        int cx = poly.getBounds().x + poly.getBounds().width / 2;
        int cy = poly.getBounds().y + poly.getBounds().height / 2;
        return new Point(cx, cy);
    }

    private void drawTile(Graphics2D graphics, WorldPoint location, Color color, int counter, boolean draw) {
        for (WorldPoint point : WorldPoint.toLocalInstance(client, location)) {
            if (point.getPlane() != client.getPlane()) {
                continue;
            }

            LocalPoint lp = LocalPoint.fromWorld(client, point);
            if (lp == null) {
                continue;
            }

            Polygon poly = Perspective.getCanvasTilePoly(client, lp);
            if (poly == null) {
                continue;
            }

            if (draw) {
                graphics.setColor(color);
                graphics.fill(poly);
            }

            drawCounter(graphics, poly.getBounds().getCenterX(), poly.getBounds().getCenterY(), counter);
        }
    }

    private void drawLine(Graphics2D graphics, WorldPoint startLoc, WorldPoint endLoc, Color color, int counter) {
        WorldPoint start = WorldPoint.toLocalInstance(client, startLoc).iterator().next();
        WorldPoint end = WorldPoint.toLocalInstance(client, endLoc).iterator().next();

        final int z = client.getPlane();
        if (start.getPlane() != z) {
            return;
        }

        LocalPoint lpStart = LocalPoint.fromWorld(client, start);
        LocalPoint lpEnd = LocalPoint.fromWorld(client, end);

        if (lpStart == null || lpEnd == null) {
            return;
        }

        final int startHeight = Perspective.getTileHeight(client, lpStart, z);
        final int endHeight = Perspective.getTileHeight(client, lpEnd, z);

        Point p1 = Perspective.localToCanvas(client, lpStart.getX(), lpStart.getY(), startHeight);
        Point p2 = Perspective.localToCanvas(client, lpEnd.getX(), lpEnd.getY(), endHeight);

        if (p1 == null || p2 == null) {
            return;
        }

        Line2D.Double line = new Line2D.Double(p1.getX(), p1.getY(), p2.getX(), p2.getY());

        graphics.setColor(color);
        graphics.setStroke(new BasicStroke(4));
        graphics.draw(line);

        if (counter == 1) {
            drawCounter(graphics, p1.getX(), p1.getY(), 0);
        }
        drawCounter(graphics, p2.getX(), p2.getY(), counter);
    }

    private void drawCounter(Graphics2D graphics, double x, double y, int counter) {
        if (counter >= 0 && !TileCounter.DISABLED.equals(config.showTileCounter())) {
            int n = config.tileCounterStep() > 0 ? config.tileCounterStep() : 1;
            int s = Microbot.getWalker().getPathfinder().getPath().size();
            if ((counter % n != 0) && (s != (counter + 1))) {
                return;
            }
            if (TileCounter.REMAINING.equals(config.showTileCounter())) {
                counter = s - counter - 1;
            }
            if (n > 1 && counter == 0) {
                return;
            }
            String counterText = Integer.toString(counter);
            graphics.setColor(Color.WHITE);
            graphics.drawString(
                counterText,
                (int) (x - graphics.getFontMetrics().getStringBounds(counterText, graphics).getWidth() / 2), (int) y);
        }
    }
}
