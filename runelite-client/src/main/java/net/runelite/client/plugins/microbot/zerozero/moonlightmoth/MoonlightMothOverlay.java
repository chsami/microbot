package net.runelite.client.plugins.microbot.zerozero.moonlightmoth;

import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class MoonlightMothOverlay extends OverlayPanel {

    private final Client client;
    @Setter
    private MoonlightMothScript script;

    @Inject
    public MoonlightMothOverlay(Client client) {
        this.client = client;
        setPosition(OverlayPosition.TOP_LEFT);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            panelComponent.setPreferredSize(new Dimension(200, 300));
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("00 Moonlight Moth")
                    .color(Color.RED)
                    .build());
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Status: " + Microbot.status)
                    .color(Color.GREEN)
                    .build());

        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
        return super.render(graphics);
    }
}
