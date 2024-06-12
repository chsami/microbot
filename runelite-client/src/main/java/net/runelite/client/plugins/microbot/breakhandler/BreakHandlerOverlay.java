package net.runelite.client.plugins.microbot.breakhandler;

import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class BreakHandlerOverlay extends OverlayPanel {
    private final BreakHandlerConfig config;

    @Inject
    BreakHandlerOverlay(BreakHandlerPlugin plugin, BreakHandlerConfig config)
    {
        super(plugin);
        this.config = config;
        setPosition(OverlayPosition.TOP_LEFT);
        setNaughty();
    }
    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            panelComponent.setPreferredSize(new Dimension(200, 300));
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("BreakHandler V" + BreakHandlerScript.version)
                    .color(Color.GREEN)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder().build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Total breaks: " + BreakHandlerScript.totalBreaks)
                    .build());

            long hours = BreakHandlerScript.duration.toHours();
            long minutes = BreakHandlerScript.duration.toMinutes() % 60;
            long seconds = BreakHandlerScript.duration.getSeconds() % 60;

            if (BreakHandlerScript.breakIn > 0) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left(String.format("Break in: %02d:%02d:%02d%n", hours, minutes, seconds))
                        .build());
            }
            if (BreakHandlerScript.breakDuration > 0) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left(String.format("Break duration: %02d:%02d:%02d%n", hours, minutes, seconds))
                        .build());
            }

        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
        return super.render(graphics);
    }
}
