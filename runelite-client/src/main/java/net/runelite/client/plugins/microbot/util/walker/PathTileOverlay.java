package net.runelite.client.plugins.microbot.util.walker;

import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.Tile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.ui.overlay.Overlay;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

public class PathTileOverlay {

    private static void renderTransports(Graphics2D graphics) {
        for (WorldPoint a : Microbot.getWalker().pathfinderConfig.getTransports().keySet()) {
            drawTile(graphics, a, new Color(0, 255, 0, 128), -1, true);

            Point ca = tileCenter(a);

            if (ca == null) {
                continue;
            }

            StringBuilder s = new StringBuilder();
            for (Transport b : Microbot.getWalker().pathfinderConfig.getTransports().getOrDefault(a, new ArrayList<>())) {
                for (WorldPoint origin : WorldPoint.toLocalInstance(Microbot.getClient(), b.getOrigin())) {
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

    public static Dimension render(Graphics2D graphics) {
        if (Microbot.getWalker().getPathfinder() != null && Microbot.getWalker().getPathfinder().getPath() != null) {
            Color color;
            if (Microbot.getWalker().getPathfinder().isDone()) {
                color = new Color(
                        new Color(40, 250, 250).getRed(),
                        new Color(40, 250, 250).getGreen(),
                        new Color(40, 250, 250).getBlue(),
                        new Color(40, 250, 250).getAlpha() / 2);
            } else {
                color = new Color(
                        new Color(0, 0, 255).getRed(),
                        new Color(0, 0, 255).getGreen(),
                        new Color(0, 0, 255).getBlue(),
                        new Color(0, 0, 255).getAlpha() / 2);
            }

            List<WorldPoint> path = Microbot.getWalker().getPathfinder().getPath();
            int counter = 0;
            for (int i = 1; i < path.size(); i++) {
                drawLine(graphics, path.get(i - 1), path.get(i), color);
            }
        }

        return null;
    }

    private static Point tileCenter(WorldPoint b) {
        if (b.getPlane() != Microbot.getClient().getPlane()) {
            return null;
        }

        LocalPoint lp = LocalPoint.fromWorld(Microbot.getClient(), b);
        if (lp == null) {
            return null;
        }

        Polygon poly = Perspective.getCanvasTilePoly(Microbot.getClient(), lp);
        if (poly == null) {
            return null;
        }

        int cx = poly.getBounds().x + poly.getBounds().width / 2;
        int cy = poly.getBounds().y + poly.getBounds().height / 2;
        return new Point(cx, cy);
    }

    private static void drawTile(Graphics2D graphics, WorldPoint location, Color color, int counter, boolean draw) {
        for (WorldPoint point : WorldPoint.toLocalInstance(Microbot.getClient(), location)) {
            if (point.getPlane() != Microbot.getClient().getPlane()) {
                continue;
            }

            LocalPoint lp = LocalPoint.fromWorld(Microbot.getClient(), point);
            if (lp == null) {
                continue;
            }

            Polygon poly = Perspective.getCanvasTilePoly(Microbot.getClient(), lp);
            if (poly == null) {
                continue;
            }

            if (draw) {
                graphics.setColor(color);
                graphics.fill(poly);
            }
        }
    }

    private static void drawLine(Graphics2D graphics, WorldPoint startLoc, WorldPoint endLoc, Color color) {
        WorldPoint start = WorldPoint.toLocalInstance(Microbot.getClient(), startLoc).iterator().next();
        WorldPoint end = WorldPoint.toLocalInstance(Microbot.getClient(), endLoc).iterator().next();

        final int z = Microbot.getClient().getPlane();
        if (start.getPlane() != z) {
            return;
        }

        LocalPoint lpStart = LocalPoint.fromWorld(Microbot.getClient(), start);
        LocalPoint lpEnd = LocalPoint.fromWorld(Microbot.getClient(), end);

        if (lpStart == null || lpEnd == null) {
            return;
        }

        final int startHeight = Perspective.getTileHeight(Microbot.getClient(), lpStart, z);
        final int endHeight = Perspective.getTileHeight(Microbot.getClient(), lpEnd, z);

        Point p1 = Perspective.localToCanvas(Microbot.getClient(), lpStart.getX(), lpStart.getY(), startHeight);
        Point p2 = Perspective.localToCanvas(Microbot.getClient(), lpEnd.getX(), lpEnd.getY(), endHeight);

        if (p1 == null || p2 == null) {
            return;
        }

        Line2D.Double line = new Line2D.Double(p1.getX(), p1.getY(), p2.getX(), p2.getY());

        graphics.setColor(color);
        graphics.setStroke(new BasicStroke(4));
        graphics.draw(line);
    }
}
