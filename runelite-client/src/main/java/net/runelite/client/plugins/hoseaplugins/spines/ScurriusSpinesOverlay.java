package net.runelite.client.plugins.hoseaplugins.spines;


import ch.qos.logback.classic.pattern.LineSeparatorConverter;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.TileObjects;
import com.google.common.base.Strings;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.awt.*;
import java.util.Optional;

public class ScurriusSpinesOverlay extends Overlay {
    private final PanelComponent panelComponent = new PanelComponent();
    private final Client client;
    private final ScurriusSpinesPlugin plugin;

    @Inject
    private ScurriusSpinesOverlay(Client client, ScurriusSpinesPlugin plugin) {
        this.client = client;
        this.plugin = plugin;
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        setDragTargetable(true);

    }

    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.getChildren().clear();
        panelComponent.setPreferredSize(new Dimension(175,0));

        panelComponent.getChildren().add(TitleComponent.builder().text("Scurrius Spine Redeemer").build());
        LineComponent spinesRedeemed = buildLine("Spines Redeemed: ", String.valueOf(plugin.spinesRedeemed));
        LineComponent timeout = buildLine("Started: ", String.valueOf(plugin.started));
        LineComponent skill = buildLine("Skill: ", plugin.config.spineSkill().name());

        panelComponent.getChildren().add(timeout);
        panelComponent.getChildren().add(spinesRedeemed);
        panelComponent.getChildren().add(skill);

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

    private void renderTile(Graphics2D graphics, LocalPoint dest, Color color, double borderWidth, Color fillColor) {
        if (dest != null) {
            Polygon poly = Perspective.getCanvasTilePoly(this.client, dest);
            if (poly != null) {
                OverlayUtil.renderPolygon(graphics, poly, color, fillColor, new BasicStroke((float) borderWidth));
            }
        }
    }

}
