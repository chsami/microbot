package net.runelite.client.plugins.hoseaplugins.LavaRunecrafter;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import java.awt.*;
import java.time.Duration;
import java.time.Instant;

public class LavaRunecrafterOverlay extends OverlayPanel {
    private final Client client;
    private final LavaRunecrafterPlugin plugin;

    @Inject
    public LavaRunecrafterOverlay(Client client, LavaRunecrafterPlugin plugin) {
        this.client = client;
        this.plugin = plugin;
        setPosition(OverlayPosition.TOP_LEFT);
    }

    private String formatNumber(double value) {
        if (value >= 1_000_000) {
            return String.format("%.1fm", value / 1_000_000);
        } else if (value >= 100_000) {
            return String.format("%.0fk", value / 1_000);
        } else if (value >= 10_000) {
            return String.format("%.1fk", value / 1_000);
        } else {
            return String.valueOf((int) value);
        }
    }


    @Override
    public Dimension render(Graphics2D graphics) {
        if (!plugin.isOverlayVisible()) {
            return null;
        }

        Instant startTime = plugin.getStartTime();
        Duration runtime = Duration.between(startTime, Instant.now());

        long seconds = runtime.getSeconds();
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secondsLeft = seconds % 60;

        int expGained = plugin.getExperienceGained();

        double xpPerHour = expGained * 3600.0 / seconds;

        panelComponent.getChildren().add(TitleComponent.builder()
                .text("[PP] Lava Runecrafter")
                .color(Color.WHITE)
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Runtime:")
                .right(String.format("%02d:%02d:%02d", hours, minutes, secondsLeft))
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("XP Gained:")
                .right(String.valueOf(expGained))
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("XP/Hour:")
                .right(formatNumber(xpPerHour))
                .build());

        return super.render(graphics);
    }

}
