package net.runelite.client.plugins.griffinplugins.griffinmining;

import net.runelite.api.Point;
import net.runelite.client.plugins.griffinplugins.griffintrainer.GriffinMiningPlugin;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class GriffinMiningOverlay extends OverlayPanel {

    @Inject
    private GriffinMiningOverlay() {
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
        panelComponent.getChildren().add(TitleComponent.builder().text(GriffinMiningPlugin.CONFIG_GROUP).color(Color.GREEN).build());
        panelComponent.getChildren().add(LineComponent.builder().build());
        panelComponent.getChildren().add(LineComponent.builder().left("Status: " + Microbot.status).build());

        if (!GriffinMiningPlugin.Companion.getCountLabel().isEmpty()) {
            panelComponent.getChildren().add(LineComponent.builder().left(GriffinMiningPlugin.Companion.getCountLabel() + ": " + GriffinMiningPlugin.Companion.getCount()).build());
        }
    }

    private void drawMouse(Graphics2D graphics) {
        Point cursorPosition = Microbot.getMouse().getLastMousePosition();
        if (cursorPosition == null) return;
        graphics.setColor(Color.RED);
        graphics.fillOval(cursorPosition.getX() - 5, cursorPosition.getY() - 5, 10, 10);
    }
}
