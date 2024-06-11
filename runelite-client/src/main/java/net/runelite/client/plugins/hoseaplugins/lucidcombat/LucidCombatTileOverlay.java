package net.runelite.client.plugins.hoseaplugins.lucidcombat;

import net.runelite.client.plugins.hoseaplugins.api.utils.InteractionUtils;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.Tile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;

public class LucidCombatTileOverlay extends OverlayPanel
{
    private final Client client;
    private final LucidCombatPlugin plugin;
    private final LucidCombatConfig config;

    @Inject
    private LucidCombatTileOverlay(Client client, LucidCombatPlugin plugin, LucidCombatConfig config)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;

        setPosition(OverlayPosition.DYNAMIC);
        setPriority(Overlay.PRIORITY_HIGH);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D graphics2D)
    {
        if (config.highlightStartTile() && plugin.getStartLocation() != null)
        {
            if (WorldPoint.isInScene(client.getTopLevelWorldView(), plugin.getStartLocation().getX(), plugin.getStartLocation().getY()))
            {
                renderTileMarkerWorldPoint(plugin.getStartLocation(), graphics2D, "(Start) Dist: " + String.format("%.1f", plugin.getDistanceToStart()), new Color(20, 210, 10, 100), 0);
            }

            if (WorldPoint.isInScene(client.getTopLevelWorldView(), plugin.getSafeSpotLocation().getX(), plugin.getSafeSpotLocation().getY()))
            {
                renderTileMarkerWorldPoint(plugin.getSafeSpotLocation(), graphics2D, "(Safespot) Dist: " + String.format("%.1f", plugin.getDistanceToStart()), new Color(210, 210, 210, 100), -25);
            }
        }

        if (config.highlightMaxRangeTiles() && plugin.getStartLocation() != null)
        {
            java.util.List<Tile> edgeTiles = InteractionUtils.getAll(tile -> Math.round(InteractionUtils.distanceTo2DHypotenuse(tile.getWorldLocation(), plugin.getStartLocation())) == config.maxRange());

            if (!edgeTiles.isEmpty())
            {
                for (Tile t : edgeTiles)
                {
                    t.getWorldLocation();
                    if (WorldPoint.isInScene(client.getTopLevelWorldView(), t.getWorldLocation().getX(), t.getWorldLocation().getY()))
                    {
                        renderTileMarkerLocalPoint(t.getLocalLocation(), graphics2D, "", new Color(210, 20, 10, 100), 0);
                    }
                }
            }
        }

        // Leave in for debug
        /*if (!plugin.getExpectedLootLocations().isEmpty())
        {
            for (Map.Entry<LocalPoint, Integer> p : plugin.getExpectedLootLocations().entrySet())
            {
                renderTileMarkerLocalPoint(p.getKey(), graphics2D, "Loot here", new Color(210, 210, 210, 100));
            }
        }*/

        return super.render(graphics2D);
    }

    private void renderTileMarkerLocalPoint(LocalPoint lp, Graphics2D graphics2D, String text, Color color, int offset)
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

        final Point point = Perspective.getCanvasTextLocation(client, graphics2D, lp, text, offset);
        if (point == null)
        {
            return;
        }

        final Font originalFont = graphics2D.getFont();
        graphics2D.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));

        drawOutlineAndFill(graphics2D, color, null, 2, polygon);
        OverlayUtil.renderTextLocation(graphics2D, point, text, color);
        graphics2D.setFont(originalFont);
    }

    private void renderTileMarkerWorldPoint(WorldPoint wp, Graphics2D graphics2D, String text, Color color, int offset)
    {
        if (wp == null)
        {
            return;
        }

        renderTileMarkerLocalPoint(LocalPoint.fromWorld(client.getTopLevelWorldView(), wp), graphics2D, text, color, offset);
    }

    static void drawOutlineAndFill(final Graphics2D graphics2D, final Color outlineColor, final Color fillColor, final float strokeWidth, final Shape shape)
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
}
