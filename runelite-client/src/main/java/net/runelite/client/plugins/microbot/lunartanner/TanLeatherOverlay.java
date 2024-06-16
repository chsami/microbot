package net.runelite.client.plugins.microbot.lunartanner;

import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class TanLeatherOverlay extends OverlayPanel {

    @Inject
    TanLeatherOverlay(TanLeatherPlugin plugin)
    {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        setNaughty();
        TanLeatherScript script = plugin.exampleScript;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.setPreferredSize(new Dimension(200, 300));
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("Tan Leather V" + TanLeatherScript.version)
                .color(Color.GREEN)
                .build());

        // Update to display the combined message
        panelComponent.getChildren().add(LineComponent.builder()
                .left(TanLeatherScript.combinedMessage)
                .build());

        return super.render(graphics);
    }
}