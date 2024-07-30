package net.runelite.client.plugins.microbot.flipperschaser;

import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class FlippersChaserOverlay extends OverlayPanel {

    private final FlippersChaserPlugin plugin;

    @Inject
    public FlippersChaserOverlay(FlippersChaserPlugin plugin) {
        this.plugin = plugin;
        setPosition(OverlayPosition.TOP_LEFT);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.getChildren().add(TitleComponent.builder()
            .text("Flippers Chaser")
            .color(Color.GREEN)
            .build());

        panelComponent.getChildren().add(LineComponent.builder()
            .left("Discord Webhook")
            .right(Boolean.toString(plugin.config.useDiscordWebhook()))
            .build());

        if (plugin.config.useDiscordWebhook()) {
            panelComponent.getChildren().add(LineComponent.builder()
                .left("Webhook URL")
                .right(plugin.config.discordWebhookUrl())
                .build());
        }

        return super.render(graphics);
    }
}
