package net.runelite.client.plugins.hoseaplugins.CannonReloader;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.*;

import javax.inject.Singleton;
import java.awt.*;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;
import net.runelite.client.ui.overlay.OverlayPanel;
@Slf4j
@Singleton
public class CannonReloaderTileOverlay extends OverlayPanel {
    CannonReloaderPlugin plugin;
    CannonReloaderConfig config;

    Client client;

    CannonReloaderTileOverlay(Client client, CannonReloaderPlugin plugin, CannonReloaderConfig config) {
        this.plugin = plugin;
        this.client = client;
        this.config = config;

        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "CannonReloaderTile overlay"));
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (plugin.cannonSpot != null) {
            renderArea(graphics, LocalPoint.fromWorld(client, plugin.cannonSpot), plugin.config.cannonSpotTile(), 2, plugin.config.cannonSpotTileFill(), 1, "Cannon");
        }

        if (plugin.safespotTile != null && config.useSafespot()) {
            renderArea(graphics, LocalPoint.fromWorld(client, plugin.safespotTile), plugin.config.safespotTile(), 2, plugin.config.safespotTileFill(), 1, "Safespot");
        }

        return super.render(graphics);
    }

    private void renderArea(final Graphics2D graphics, final LocalPoint dest, final Color color,
                            final double borderWidth, final Color fillColor, int size, String label) {
        if (dest == null) {
            return;
        }

        final Polygon poly = Perspective.getCanvasTileAreaPoly(client, dest, size);

        if (poly == null) {
            return;
        }
        OverlayUtil.renderPolygon(graphics, poly, color, fillColor, new BasicStroke((float) borderWidth));
        if (!Strings.isNullOrEmpty(label)) {
            Point canvasTextLocation = Perspective.getCanvasTextLocation(client, graphics, dest, label, 0);
            if (canvasTextLocation != null) {
                graphics.setFont(new Font("Arial", Font.PLAIN, 9));
                OverlayUtil.renderTextLocation(graphics, canvasTextLocation, label, color);
            }
        }
    }
}
