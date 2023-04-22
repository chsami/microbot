package net.runelite.client.plugins.microbot.util.walker;

import com.google.inject.Inject;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.List;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.MicrobotConfig;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.JagexColors;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

public class PathMapTooltipOverlay extends Overlay {
    private static final int TOOLTIP_OFFSET_HEIGHT = 25;
    private static final int TOOLTIP_OFFSET_WIDTH = 15;
    private static final int TOOLTIP_PADDING_HEIGHT = 1;
    private static final int TOOLTIP_PADDING_WIDTH = 2;
    private static final int TOOLTIP_TEXT_OFFSET_HEIGHT = -2;

    private final Client client;
    private final MicrobotConfig config;

    @Inject
    private PathMapTooltipOverlay(Client client, MicrobotConfig config) {
        this.client = client;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.LOW);
        setLayer(OverlayLayer.MANUAL);
        drawAfterInterface(WidgetID.WORLD_MAP_GROUP_ID);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!config.drawMap() || client.getWidget(WidgetInfo.WORLD_MAP_VIEW) == null) {
            return null;
        }

        if (Microbot.getWalker().getPathfinder() != null) {
            List<WorldPoint> path = Microbot.getWalker().getPathfinder().getPath();
            Point cursorPos = client.getMouseCanvasPosition();
            for (int i = 0; i < path.size(); i++) {
                if (drawTooltip(graphics, cursorPos, path.get(i), i + 1)) {
                    return null;
                }
            }
        }

        return null;
    }

    private boolean drawTooltip(Graphics2D graphics, Point cursorPos, WorldPoint point, int n) {
        Point start = Microbot.getWalker().pathfinderConfig.mapWorldPointToGraphicsPoint(point);
        Point end = Microbot.getWalker().pathfinderConfig.mapWorldPointToGraphicsPoint(point.dx(1).dy(-1));

        if (start == null || end == null) {
            return false;
        }

        int width = end.getX() - start.getX();

        if (cursorPos.getX() < (start.getX() - width / 2) || cursorPos.getX() > (end.getX() - width / 2) ||
            cursorPos.getY() < (start.getY() - width / 2) || cursorPos.getY() > (end.getY() - width / 2)) {
            return false;
        }

        List<String> rows = Arrays.asList("Shortest path:", "Step " + n + " of " + Microbot.getWalker().getPathfinder().getPath().size());

        graphics.setFont(FontManager.getRunescapeFont());
        FontMetrics fm = graphics.getFontMetrics();
        int tooltipHeight = fm.getHeight();
        int tooltipWidth = rows.stream().map(fm::stringWidth).max(Integer::compareTo).get();

        int clippedHeight = tooltipHeight + TOOLTIP_PADDING_HEIGHT * 2;
        int clippedWidth = tooltipWidth + TOOLTIP_PADDING_WIDTH * 2;

        Rectangle worldMapBounds = client.getWidget(WidgetInfo.WORLD_MAP_VIEW).getBounds();
        int worldMapRightBoundary = worldMapBounds.width + worldMapBounds.x;
        int worldMapBottomBoundary = worldMapBounds.height + worldMapBounds.y;

        int drawPointX = start.getX() + TOOLTIP_OFFSET_WIDTH;
        int drawPointY = start.getY();
        if (drawPointX + clippedWidth > worldMapRightBoundary) {
            drawPointX = worldMapRightBoundary - clippedWidth;
        }
        if (drawPointY + clippedHeight > worldMapBottomBoundary) {
            drawPointY = start.getY() - TOOLTIP_OFFSET_HEIGHT * 2 - tooltipHeight;
        }
        drawPointY += TOOLTIP_OFFSET_HEIGHT;

        Rectangle tooltipRect = new Rectangle(
            drawPointX - TOOLTIP_PADDING_WIDTH,
            drawPointY - TOOLTIP_PADDING_HEIGHT,
            tooltipWidth + TOOLTIP_PADDING_WIDTH * 2,
            tooltipHeight * rows.size() + TOOLTIP_PADDING_HEIGHT * 2);

        graphics.setColor(JagexColors.TOOLTIP_BACKGROUND);
        graphics.fillRect(tooltipRect.x, tooltipRect.y, tooltipRect.width, tooltipRect.height);

        graphics.setColor(JagexColors.TOOLTIP_BORDER);
        graphics.drawRect(tooltipRect.x, tooltipRect.y, tooltipRect.width, tooltipRect.height);

        graphics.setColor(JagexColors.TOOLTIP_TEXT);
        for (int i = 0; i < rows.size(); i++) {
            graphics.drawString(rows.get(i), drawPointX, drawPointY + TOOLTIP_TEXT_OFFSET_HEIGHT + (i + 1) * tooltipHeight);
        }

        return true;
    }
}
