package net.runelite.client.plugins.hoseaplugins.luciddukehelper;

import net.runelite.api.Client;
import net.runelite.api.GraphicsObject;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.*;
import java.util.Map;

public class DukeHelperOverlay extends OverlayPanel
{
    private final Client client;
    private final DukeHelperPlugin plugin;
    private final DukeHelperConfig config;

    @Inject
    private DukeHelperOverlay(Client client, DukeHelperPlugin plugin, DukeHelperConfig config)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;

        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D graphics2D)
    {
        if (!config.enableOverlays() || !plugin.isTrackingDuke())
        {
            return null;
        }

        if (!plugin.dukeAwake())
        {
            // Track dangerous tiles
            renderFallingTiles(graphics2D);
        }

        renderVents(graphics2D);

        return null;
    }

    private void renderVents(Graphics2D graphics2D)
    {
        if (plugin.getVentTiles().isEmpty())
        {
            return;
        }

        for (Map.Entry<WorldPoint, Integer> entry : plugin.getVentTiles().entrySet())
        {
            int ticksSinceSpawn = client.getTickCount() - entry.getValue();
            LocalPoint centerLp = LocalPoint.fromWorld(client.getTopLevelWorldView(), entry.getKey().dx(1).dy(1));
            if (centerLp != null)
            {
                final Polygon poly = Perspective.getCanvasTileAreaPoly(client, centerLp, 3);
                final int firstHalfTicks = Math.max(-1, 5 - ticksSinceSpawn);
                final int secondHalfTicks = Math.max(-1, 11 - ticksSinceSpawn);
                if (firstHalfTicks > 0)
                {
                    drawPoly(graphics2D, config.safeTileColor(), poly);
                    String tileText = config.enableText() ? firstHalfTicks + "" : "";
                    drawTextLocalPoint(graphics2D, centerLp, tileText, config.safeTextColor());
                }
                else
                {
                    if (secondHalfTicks > 0)
                    {
                        drawPoly(graphics2D, config.unsafeTileColor(), poly);
                        String tileText = config.enableText() ? "Safe In: " + secondHalfTicks : "";
                        drawTextLocalPoint(graphics2D, centerLp, tileText, config.unsafeTextColor());
                    }
                }
            }
        }
    }

    private void renderFallingTiles(Graphics2D graphics2D)
    {
        if (plugin.getFallingCeilingTiles().isEmpty())
        {
            return;
        }

        for (Map.Entry<GraphicsObject, Integer> entry : plugin.getFallingCeilingTiles().entrySet())
        {
            int ticksLeft = entry.getValue() - client.getTickCount();
            Color tileColor = ticksLeft > 0 ? config.safeTileColor() : config.unsafeTileColor();
            Color textColor = ticksLeft > 0 ? config.safeTextColor() : config.unsafeTextColor();
            if (ticksLeft >= 0)
            {
                String tileText = config.enableText() ? ticksLeft + "" : "";
                renderTileMarkerLocalPoint(entry.getKey().getLocation(), graphics2D, tileText, tileColor, textColor);
            }
        }
    }


    private void renderTileMarkerLocalPoint(LocalPoint lp, Graphics2D graphics2D, String text, Color color, Color textColor)
    {
        if (lp == null)
        {
            return;
        }

        final Polygon polygon = Perspective.getCanvasTileAreaPoly(client, lp, 1);
        if (polygon == null)
        {
            return;
        }

        final Point point = Perspective.getCanvasTextLocation(client, graphics2D, lp, text, -25);
        if (point == null)
        {
            return;
        }

        final Font originalFont = graphics2D.getFont();
        graphics2D.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));

        drawOutlineAndFill(graphics2D, null, color, 2, polygon);
        OverlayUtil.renderTextLocation(graphics2D, point, text, textColor);
        graphics2D.setFont(originalFont);
    }

    private void drawOutlineAndFill(final Graphics2D graphics2D, final Color outlineColor, final Color fillColor, final float strokeWidth, final Shape shape)
    {
        final Color originalColor = graphics2D.getColor();
        final Stroke originalStroke = graphics2D.getStroke();

        final Color outline = outlineColor != null ? outlineColor : new Color(0, 0, 0, 0);
        final Color fill = fillColor != null ? fillColor : new Color(0, 0, 0, 0);

        graphics2D.setStroke(new BasicStroke(strokeWidth));
        graphics2D.setColor(outline);
        graphics2D.draw(shape);

        graphics2D.setColor(fill);
        graphics2D.fill(shape);

        graphics2D.setColor(originalColor);
        graphics2D.setStroke(originalStroke);
    }

    private void drawPoly(Graphics2D graphics, Color fillColor, Shape polygon)
    {
        if (polygon != null)
        {
            graphics.setColor(fillColor);
            graphics.fill(polygon);
        }
    }

    private void drawTextLocalPoint(Graphics2D graphics2D, LocalPoint localPoint, String text, Color color)
    {
        final Point point = Perspective.getCanvasTextLocation(client, graphics2D, localPoint, text, -25);
        if (point == null)
        {
            return;
        }

        final Font originalFont = graphics2D.getFont();
        graphics2D.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        OverlayUtil.renderTextLocation(graphics2D, point, text, color);
        graphics2D.setFont(originalFont);
    }
}
