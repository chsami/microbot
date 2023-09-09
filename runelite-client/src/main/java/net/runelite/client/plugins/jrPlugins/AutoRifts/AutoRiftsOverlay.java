package net.runelite.client.plugins.jrPlugins.AutoRifts;

import com.google.inject.Inject;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import java.awt.*;

public class AutoRiftsOverlay extends OverlayPanel {

    private final AutoRiftsPlugin plugin;

    @Inject
    private AutoRiftsOverlay(AutoRiftsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("Auto Rifts")
                .color(Color.WHITE)
                .build());

        panelComponent.getChildren().add(TitleComponent.builder()
                .text(plugin.isStarted() ? "Running" : "Paused")
                .color(plugin.isStarted() ? Color.GREEN : Color.RED)
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Elapsed Time: ")
                .leftColor(Color.YELLOW)
                .right(plugin.getElapsedTime())
                .rightColor(Color.WHITE)
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("State")
                .leftColor(Color.YELLOW)
                .right(plugin.getState() == null ? "null" : plugin.getState().name())
                .rightColor(Color.WHITE)
                .build());

        return super.render(graphics);
    }
}
