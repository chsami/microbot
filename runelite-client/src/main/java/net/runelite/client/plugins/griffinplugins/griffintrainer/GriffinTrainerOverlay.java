package net.runelite.client.plugins.griffinplugins.griffintrainer;

import net.runelite.api.Point;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class GriffinTrainerOverlay extends OverlayPanel {

    @Inject
    private GriffinTrainerOverlay() {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
        setPriority(OverlayPriority.HIGHEST);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            drawPanel(graphics);
            drawMouse(graphics);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return super.render(graphics);
    }

    private void drawPanel(Graphics2D graphics) {
        panelComponent.setPreferredSize(new Dimension(300, 300));
        panelComponent.getChildren().add(TitleComponent.builder().text("Griffin Trainer").color(Color.GREEN).build());
        panelComponent.getChildren().add(LineComponent.builder().build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Task/Overall Max Time: " + GriffinTrainerPlugin.Companion.getTaskTimer().getTimeout() + " minutes / " + GriffinTrainerPlugin.Companion.getOverallTimer().getTimeout() + " minutes")
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Task/Overall Elapsed Time: " + GriffinTrainerPlugin.Companion.getTaskTimer().getElapsedTimeString() + " / " + GriffinTrainerPlugin.Companion.getOverallTimer().getElapsedTimeString())
                .build());

        panelComponent.getChildren().add(LineComponent.builder().build());
        panelComponent.getChildren().add(LineComponent.builder().left("Status: " + Microbot.status).build());

        if (!GriffinTrainerPlugin.Companion.getCountLabel().isEmpty()) {
            panelComponent.getChildren().add(LineComponent.builder().left(GriffinTrainerPlugin.Companion.getCountLabel() + ": " + GriffinTrainerPlugin.Companion.getCount()).build());
        }
    }

    private void drawMouse(Graphics2D graphics) {
        Point cursorPosition = Microbot.getMouse().getLastMousePosition();
        if (cursorPosition == null) return;
        graphics.setColor(Color.RED);
        graphics.fillOval(cursorPosition.getX() - 5, cursorPosition.getY() - 5, 10, 10);
    }
}
