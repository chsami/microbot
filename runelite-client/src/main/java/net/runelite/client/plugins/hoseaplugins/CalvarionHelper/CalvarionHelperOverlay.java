package net.runelite.client.plugins.hoseaplugins.CalvarionHelper;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import java.awt.*;
import java.awt.geom.Area;
import java.util.concurrent.atomic.AtomicReference;


public class CalvarionHelperOverlay extends Overlay {
    CalvarionHelper plugin;
    Client client;

    CalvarionHelperOverlay(Client client, CalvarionHelper plugin) {
        this.plugin = plugin;
        this.client = client;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);

    }

    @Override
    public Dimension render(Graphics2D graphics) {
        //spiderlingTiles
        plugin.lightning.forEach((lightningTile, i) ->
        {
            if (lightningTile == null) {
                return;
            }
            renderArea(graphics, LocalPoint.fromWorld(client, lightningTile), plugin.config.lightning(), 1, plugin.config.lightningFill());
        });
        AtomicReference<Area> area = new AtomicReference<>();
        plugin.swing.forEach((swingTile, i) ->
        {
            if (swingTile == null) {
                return;
            }
            OverlayUtil.renderPolygon(graphics, Perspective.getCanvasTilePoly(client, LocalPoint.fromWorld(client,
                    swingTile)), plugin.config.swing(), plugin.config.swingFill(), new BasicStroke((float) 1));
        });
        return null;
    }

    private void renderArea(final Graphics2D graphics, final LocalPoint dest, final Color color,
                            final double borderWidth, final Color fillColor) {
        if (dest == null) {
            return;
        }

        final Polygon poly = Perspective.getCanvasTileAreaPoly(client, dest, 3);

        if (poly == null) {
            return;
        }
        OverlayUtil.renderPolygon(graphics, poly, color, fillColor, new BasicStroke((float) borderWidth));
    }
}
