package net.runelite.client.plugins.hoseaplugins.Trapper;


import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.TileObjects;
import com.google.common.base.Strings;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.awt.*;
import java.util.Optional;

public class TrapTileOverlay extends Overlay {
    private final PanelComponent panelComponent = new PanelComponent();
    private final Client client;
    private final AutoTrapperPlugin plugin;

    @Inject
    private TrapTileOverlay(Client client, AutoTrapperPlugin plugin) {
        this.client = client;
        this.plugin = plugin;

        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);

    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (plugin.startTile != null) {
            TileObjects.search().filter(t -> plugin.startTile.distanceTo(t.getWorldLocation()) <= plugin.config.maxDist()).withName("Young tree").result().forEach(tree -> {
                renderTile(graphics, LocalPoint.fromWorld(client, tree.getWorldLocation()),
                        Color.GREEN, 2, new Color(0, 255, 0, 20));
            });
        } else {
            TileObjects.search().withinDistance(plugin.config.maxDist()).withName("Young tree").result().forEach(tree -> {
                renderTile(graphics, LocalPoint.fromWorld(client, tree.getWorldLocation()),
                        Color.GREEN, 2, new Color(0, 255, 0, 20));
            });
        }
        return null;
    }

    /**
     * Builds a line component with the given left and right text
     *
     * @param left
     * @param right
     * @return Returns a built line component with White left text and Yellow right text
     */
    private LineComponent buildLine(String left, String right) {
        return LineComponent.builder()
                .left(left)
                .right(right)
                .leftColor(Color.WHITE)
                .rightColor(Color.YELLOW)
                .build();
    }

    private void renderTile(Graphics2D graphics, LocalPoint dest, Color color, double borderWidth, Color fillColor) {
        if (dest != null) {
            Polygon poly = Perspective.getCanvasTilePoly(this.client, dest);
            if (poly != null) {
                OverlayUtil.renderPolygon(graphics, poly, color, fillColor, new BasicStroke((float) borderWidth));
            }
        }
    }

}
