package net.runelite.client.plugins.microbot.zerozero.birdhunter;

import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;

public class BirdHunterOverlay extends OverlayPanel {

    @Inject
    BirdHunterOverlay(BirdHunterPlugin plugin) {
        super(plugin);
        setPosition(OverlayPosition.DYNAMIC);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        return super.render(graphics);
    }
}
