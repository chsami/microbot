package net.runelite.client.plugins.microbot.zerozero.tormenteddemons;


import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;

import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class TormentedDemonOverlay extends OverlayPanel {

    @Inject
    TormentedDemonOverlay(TormentedDemonPlugin plugin) {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        setNaughty();
    }


    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            panelComponent.setPreferredSize(new Dimension(200, 300));
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("00 TormentedDemon - Version: " + TormentedDemonScript.VERSION)
                    .color(Color.RED)
                    .build());
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Status: " + TormentedDemonScript.BOT_STATUS)
                    .color(Color.GREEN)
                    .build());
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Kill Count: " + TormentedDemonScript.killCount)  // Add kill count display
                    .color(Color.YELLOW)
                    .build());

        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
        return super.render(graphics);
    }
}
