package net.runelite.client.plugins.microbot.zerozero.bluedragons;


import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;


import javax.inject.Inject;
import java.awt.*;

public class BlueDragonsOverlay extends Overlay {

    private final Client client;
    @Setter
    private BlueDragonsScript script;

    @Inject
    public BlueDragonsOverlay(Client client) {
        this.client = client;
        setPosition(OverlayPosition.TOP_LEFT);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (script.isRunning()) {
            graphics.setColor(Color.CYAN);
            graphics.drawString("Farming Blue Dragons", 10, 10);
        }
        return null;
    }
}
