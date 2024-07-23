package net.runelite.client.plugins.microbot.plankmake;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

import javax.inject.Inject;

import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

public class PlankMakeOverlay extends OverlayPanel {

    @Inject
    PlankMakeOverlay(PlankMakePlugin plugin)
    {
        super(plugin);
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        setNaughty();
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.setPreferredSize(new Dimension(300, 400));
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("Plank Make " + PlankMakeScript.version)
                .color(Color.YELLOW)
                .build());

        // Update to display the combined message
        panelComponent.getChildren().add(LineComponent.builder()
                .left(PlankMakeScript.combinedMessage)
                .build());

        return super.render(graphics);
    }
}