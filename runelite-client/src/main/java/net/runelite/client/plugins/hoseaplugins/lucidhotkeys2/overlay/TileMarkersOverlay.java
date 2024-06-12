package net.runelite.client.plugins.hoseaplugins.lucidhotkeys2.overlay;

import net.runelite.client.plugins.hoseaplugins.lucidhotkeys2.LocalRegionTile;
import net.runelite.client.plugins.hoseaplugins.lucidhotkeys2.LucidHotkeys2Config;
import net.runelite.client.plugins.hoseaplugins.lucidhotkeys2.LucidHotkeys2Plugin;
import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.*;
import java.util.Map;

public class TileMarkersOverlay extends Overlay
{

    private final Client client;
    private final LucidHotkeys2Plugin plugin;
    private final LucidHotkeys2Config config;
    private Player player;
    @Inject
    TileMarkersOverlay(final Client client, final LucidHotkeys2Plugin plugin, final LucidHotkeys2Config config)
    {
        super(plugin);
        this.client = client;
        this.plugin = plugin;
        this.config = config;

        setPosition(OverlayPosition.DYNAMIC);
        setPriority(PRIORITY_HIGH);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics2D)
    {

        player = client.getLocalPlayer();

        if (player == null)
        {
            return null;
        }

        renderTileMarkers(graphics2D);

        return null;
    }

    private void renderTileMarkers(Graphics2D graphics2D)
    {
        for (Map.Entry<LocalRegionTile, String> entry : plugin.getRegionPointTileMarkers().entrySet())
        {
            final LocalRegionTile tile = entry.getKey();
            final String text = entry.getValue();
            if (tile.getLocalTile() != null && tile.getLocalTile().isInScene())
            {
                renderTileMarkerLocalPoint(tile.getLocalTile(), graphics2D, text, Color.BLUE);
            }
        }

        for (Map.Entry<WorldPoint, String> entry : plugin.getWorldPointTileMarkers().entrySet())
        {
            final WorldPoint worldPoint = entry.getKey();
            final String text = entry.getValue();
            if (worldPoint != null && WorldPoint.isInScene(client.getTopLevelWorldView(), worldPoint.getX(), worldPoint.getY()))
            {
                renderTileMarkerWorldPoint(worldPoint, graphics2D, text, Color.BLUE);
            }
        }

        if (plugin.getPlayersTracked() != null && plugin.getPlayersTracked().size() > 0)
        {
            for (Player p : plugin.getPlayersTracked())
            {
                if (p != null)
                {
                    renderTileMarkerWorldPoint(p.getWorldLocation(), graphics2D, p.getName(), Color.RED);
                }

            }

        }

        if (plugin.getNpcsTracked() != null && plugin.getNpcsTracked().size() > 0)
        {
            for (NPC npcTracked : plugin.getNpcsTracked())
            {
                if (npcTracked != null)
                {
                    renderTileMarkerWorldPoint(npcTracked.getWorldLocation(), graphics2D, npcTracked.getName(), Color.YELLOW);
                }
            }
        }
    }

    private void renderTileMarkerLocalPoint(LocalPoint lp, Graphics2D graphics2D, String text, Color color)
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

        drawOutlineAndFill(graphics2D, color, null, 2, polygon);
        OverlayUtil.renderTextLocation(graphics2D, point, text, color);
        graphics2D.setFont(originalFont);
    }

    private void renderTileMarkerWorldPoint(WorldPoint wp, Graphics2D graphics2D, String text, Color color)
    {
        if (wp == null)
        {
            return;
        }

        renderTileMarkerLocalPoint(LocalPoint.fromWorld(client.getTopLevelWorldView(), wp), graphics2D, text, color);
    }
}
