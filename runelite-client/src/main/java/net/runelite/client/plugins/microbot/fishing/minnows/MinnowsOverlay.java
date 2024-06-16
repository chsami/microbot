package net.runelite.client.plugins.microbot.fishing.minnows;


import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class MinnowsOverlay extends OverlayPanel {
    @Inject
    MinnowsOverlay(MinnowsPlugin plugin) {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        setNaughty();
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            panelComponent.setPreferredSize(new Dimension(250, 400));
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("\uD83E\uDD86 Auto Minnows \uD83E\uDD86")
                    .color(Color.ORANGE)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder().build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left(Microbot.status)
                    .right("Version:" + MinnowsScript.version)
                    .build());


        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return super.render(graphics);
    }
}
