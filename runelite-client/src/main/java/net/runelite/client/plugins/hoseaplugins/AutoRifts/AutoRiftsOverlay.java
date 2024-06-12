package net.runelite.client.plugins.hoseaplugins.AutoRifts;


import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

import static org.apache.commons.lang3.time.DurationFormatUtils.formatDuration;

public class AutoRiftsOverlay extends OverlayPanel {

    private final Client client;
    private final AutoRiftsPlugin plugin;
    public String overlayState = "";

    @Inject
    private AutoRiftsOverlay(Client client, AutoRiftsPlugin plugin) {
        this.client = client;
        this.plugin = plugin;
        setPosition(OverlayPosition.BOTTOM_LEFT);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        String timeFormat = (plugin.runningDuration.toHours() < 1) ? "mm:ss" : "HH:mm:ss";
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("AutoRifts")
                .color(plugin.started ? Color.GREEN : Color.RED)
                .build());

        if (plugin.started) {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Time running:")
                    .leftColor(Color.WHITE)
                    .right(formatDuration(plugin.runningDuration.toMillis(), timeFormat))
                    .rightColor(Color.WHITE)
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Status:")
                    .leftColor(Color.WHITE)
                    .right(overlayState)
                    .rightColor(Color.WHITE)
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Game Started:")
                    .leftColor(Color.WHITE)
                    .right(String.valueOf(plugin.riftState.isGameStarted()))
                    .rightColor(Color.WHITE)
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Portal Spawned:")
                    .leftColor(Color.WHITE)
                    .right(String.valueOf(plugin.riftState.isPortalSpawned()))
                    .rightColor(Color.WHITE)
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("First Portal Spawned:")
                    .leftColor(Color.WHITE)
                    .right(String.valueOf(plugin.riftState.hasFirstPortalSpawned))
                    .rightColor(Color.WHITE)
                    .build());
            panelComponent.setPreferredSize(new Dimension(250, 250));
        }
        return super.render(graphics);
    }
}
