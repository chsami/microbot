/*
 * Copyright (c) 2024. By Jrod7938
 *
 */
package net.runelite.client.plugins.jrPlugins.autoVorkath;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

import javax.inject.Inject;
import java.awt.*;

public class AutoVorkathOverlay extends Overlay {
    private final PanelComponent panelComponent = new PanelComponent();
    private final AutoVorkathPlugin plugin;
    private final Client client;

    @Inject
    private AutoVorkathOverlay(Client client, AutoVorkathPlugin plugin) {
        this.client = client;
        this.plugin = plugin;
        setPosition(OverlayPosition.BOTTOM_LEFT);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setDragTargetable(true);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.getChildren().clear();

        LineComponent state = buildLine("State: ", plugin.getBotState().toString());
        LineComponent tickDelay = buildLine("Tick Delay: ", String.valueOf(plugin.getTickDelay()));
        LineComponent killCount = buildLine("Kill Count: ", String.valueOf(plugin.getKillCount()));

        panelComponent.getChildren().add(state);
        panelComponent.getChildren().add(tickDelay);
        panelComponent.getChildren().add(killCount);

        return panelComponent.render(graphics);
    }

    /**
     * Builds a line component with the given left and right text.
     *
     * @param left  the left text
     * @param right the right text
     * @return a built line component with White left text and Yellow right text
     */
    private LineComponent buildLine(String left, String right) {
        return LineComponent.builder()
                .left(left)
                .right(right)
                .leftColor(Color.WHITE)
                .rightColor(Color.YELLOW)
                .build();
    }
}
