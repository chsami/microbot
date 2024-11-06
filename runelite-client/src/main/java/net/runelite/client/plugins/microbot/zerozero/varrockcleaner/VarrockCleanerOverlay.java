package net.runelite.client.plugins.microbot.zerozero.varrockcleaner;


import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;

public class VarrockCleanerOverlay extends Overlay {

    private final Client client;
    @Setter
    private VarrockCleanerScript script;

    @Inject
    public VarrockCleanerOverlay(Client client) {
        this.client = client;
        setPosition(OverlayPosition.TOP_LEFT);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (script.isRunning()) {
            graphics.setColor(Color.CYAN);
            graphics.drawString("Varrock Cleaner", 10, 10);
        }
        return null;
    }
}
