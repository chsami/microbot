package net.runelite.client.plugins.hoseaplugins.Firemaking;


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
import javax.sound.sampled.Line;
import java.awt.*;
import java.util.Optional;

public class FiremakingOverlay extends Overlay {
    private final PanelComponent panelComponent = new PanelComponent();
    private final Client client;
    private final FiremakingPlugin plugin;

    @Inject
    private FiremakingOverlay(Client client, FiremakingPlugin plugin) {
        this.client = client;
        this.plugin = plugin;
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setDragTargetable(true);

    }

    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.getChildren().clear();

        LineComponent logName = buildLine("Logs: ", plugin.logName);
        LineComponent timeout = buildLine("Timeout: ", String.valueOf(plugin.timeout));

        panelComponent.getChildren().add(logName);

        if (plugin.location != null) {
            LineComponent location = buildLine("Location: ", plugin.location.toString());
            panelComponent.getChildren().add(location);
        }
        panelComponent.getChildren().add(timeout);

        if (plugin.startTiles != null) {
            String lineString = String.format("%s/%s",
                    plugin.lastStartTile + 1,
                    plugin.startTiles.size());
            LineComponent line = buildLine("Line: ", lineString);
            panelComponent.getChildren().add(line);

        }

        return panelComponent.render(graphics);
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

    private void renderTile(final Graphics2D graphics, final LocalPoint dest, final Color color, final Color fillColor, @Nullable String label) {
        if (dest == null) {
            return;
        }
        final Polygon poly = Perspective.getCanvasTilePoly(client, dest);
        if (poly == null) {
            return;
        }
        OverlayUtil.renderPolygon(graphics, poly, color, fillColor, new BasicStroke((float) 1.5));
        if (!Strings.isNullOrEmpty(label)) {
            Point canvasTextLocation = Perspective.getCanvasTextLocation(client, graphics, dest, label, 0);
            if (canvasTextLocation != null) {
                OverlayUtil.renderTextLocation(graphics, canvasTextLocation, label, color);
            }
        }
    }

}
