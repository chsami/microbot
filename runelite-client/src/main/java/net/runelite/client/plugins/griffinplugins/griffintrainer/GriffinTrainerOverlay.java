package net.runelite.client.plugins.griffinplugins.griffintrainer;

import net.runelite.api.Point;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class GriffinTrainerOverlay extends OverlayPanel {

    @Inject
    private GriffinTrainerOverlay() {
        setPosition(OverlayPosition.DYNAMIC);
        setNaughty();
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
        panelComponent.setPreferredLocation(new java.awt.Point(5, 30));
        panelComponent.setPreferredSize(new Dimension(300, 300));
        panelComponent.getChildren().add(TitleComponent.builder().text(GriffinTrainerPlugin.CONFIG_GROUP).color(Color.GREEN).build());
        panelComponent.getChildren().add(LineComponent.builder().build());

        panelComponent.getChildren().add(LineComponent.builder().left("Task/Overall Max Time:").right(TrainerThread.Companion.getTaskTimer().getTimeout() + " minutes / " + TrainerThread.Companion.getOverallTimer().getTimeout() + " minutes").build());
        panelComponent.getChildren().add(LineComponent.builder().left("Task/Overall Elapsed Time:").right(TrainerThread.Companion.getTaskTimer().getElapsedTimeString() + " / " + TrainerThread.Companion.getOverallTimer().getElapsedTimeString()).build());
        panelComponent.getChildren().add(LineComponent.builder().left("Task: " + TrainerThread.Companion.getCurrentTask()).build());
        panelComponent.getChildren().add(LineComponent.builder().build());

        panelComponent.getChildren().add(LineComponent.builder().left("Status: " + Microbot.status).build());
        panelComponent.getChildren().add(LineComponent.builder().build());

        if (!TrainerThread.Companion.getCountLabel().isEmpty()) {
            panelComponent.getChildren().add(LineComponent.builder().left(TrainerThread.Companion.getCountLabel() + ":").right(Integer.toString(TrainerThread.Companion.getCount())).build());
        }

        panelComponent.getChildren().add(LineComponent.builder().left("Random Events Dismissed:").right(Integer.toString(TrainerThread.Companion.getRandomEventDismissedCount())).build());
    }

    private void drawMouse(Graphics2D graphics) {
        // Mocrosoft uncommented this as we no longer use getLastMousePosition
        // The new api basically uses invokes instead of mouse
        // therefor this is not really needed anymore
       /* Point cursorPosition = Microbot.getMouse().getLastMousePosition();
        if (cursorPosition == null) return;
        graphics.setColor(Color.RED);
        graphics.fillOval(cursorPosition.getX() - 5, cursorPosition.getY() - 5, 10, 10);*/
    }
}
