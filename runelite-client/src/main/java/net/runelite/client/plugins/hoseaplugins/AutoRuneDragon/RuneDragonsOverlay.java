package net.runelite.client.plugins.hoseaplugins.AutoRuneDragon;

import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;

class RuneDragonsOverlay extends OverlayPanel {
    private final RuneDragonsPlugin plugin;
    private final RuneDragonsConfig config;

    @Inject
    private RuneDragonsOverlay(RuneDragonsPlugin runeDragonsPlugin, RuneDragonsConfig runeDragonsConfig) {
        super(runeDragonsPlugin);
        plugin = runeDragonsPlugin;
        config = runeDragonsConfig;

        setPosition(OverlayPosition.BOTTOM_LEFT);
        setPreferredSize(new Dimension(350, 160));
    }

    @Override
    public Dimension render(Graphics2D graphics) {

        panelComponent.getChildren().add(TitleComponent.builder()
                .text("Rune Dragons Killer")
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
                .left("Main state: ")
                .leftColor(Color.YELLOW)
                .right(plugin.getState() != null ? plugin.getState().name() : "null")
                .rightColor(Color.WHITE)
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("State: ")
                .leftColor(Color.YELLOW)
                .right(plugin.getSubState() != null ? plugin.getSubState().name() : "null")
                .rightColor(Color.WHITE)
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Kills (p/h): ")
                .leftColor(Color.YELLOW)
                .right(plugin.getKillCount() + " (" + plugin.getKillsPerHour() + ")")
                .rightColor(Color.WHITE)
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Profit (p/h): ")
                .leftColor(Color.YELLOW)
                .right(NumberFormat.getNumberInstance(Locale.US).format(plugin.getTotalLoot())
                        + " (" + NumberFormat.getNumberInstance(Locale.US).format(plugin.getLootPerHour()) + ")")
                .rightColor(Color.WHITE)
                .build());

        return super.render(graphics);
    }
}