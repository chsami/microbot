package net.runelite.client.plugins.hoseaplugins.RooftopAgility;

import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import com.google.inject.Inject;
import java.awt.*;

public class RooftopAgilityOverlay extends OverlayPanel {

    private final RooftopAgilityPlugin plugin;
    @Inject
    private RooftopAgilityOverlay(RooftopAgilityPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
        setPosition(OverlayPosition.BOTTOM_LEFT);
        setPreferredSize(new Dimension(160, 160));
    }

    @Override
    public Dimension render(Graphics2D graphics) {

        panelComponent.getChildren().add(TitleComponent.builder()
                .text("Rooftop Agility")
                .color(Color.WHITE)
                .build());

        panelComponent.getChildren().add(TitleComponent.builder()
                .text(plugin.isStartAgility() ? "Running" : "Paused")
                .color(plugin.isStartAgility() ? Color.GREEN : Color.RED)
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Elapsed Time: ")
                .leftColor(Color.YELLOW)
                .right(plugin.getElapsedTime())
                .rightColor(Color.WHITE)
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Course: ")
                .leftColor(Color.YELLOW)
                .right(plugin.getCourseName())
                .rightColor(Color.WHITE)
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("State: ")
                .leftColor(Color.YELLOW)
                .right(plugin.getState() != null ? plugin.getState().name() : "null")
                .rightColor(Color.WHITE)
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Marks collected: ")
                .leftColor(Color.YELLOW)
                .right(String.valueOf(plugin.getMogCollectCount()))
                .rightColor(Color.WHITE)
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Marks/hr: ")
                .leftColor(Color.YELLOW)
                .right(String.valueOf(plugin.getMarksPH()))
                .rightColor(Color.WHITE)
                .build());
        return super.render(graphics);
    }
}
