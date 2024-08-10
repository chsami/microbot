package net.runelite.client.plugins.devtools;

import net.runelite.api.Client;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.*;

public class MicrobotClickOverlay extends Overlay {
    private final Client client;
    private final DevToolsPlugin plugin;

    @Inject
    MicrobotClickOverlay(Client client, DevToolsPlugin plugin) {
        this.client = client;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        setPriority(Overlay.PRIORITY_LOW);
    }

    @Override
    public Dimension render(Graphics2D g) {
        if (plugin.getMouseClick().isActive()) {

            g.setFont(new Font("Tahoma", Font.BOLD, 18));

            OverlayUtil.renderTextLocation(g, new net.runelite.api.Point(Microbot.getMouse().getLastClick().getX() - (g.getFont().getSize() / 3),
                    Microbot.getMouse().getLastClick().getY() + (g.getFont().getSize() / 3)), "X", Color.WHITE);
            OverlayUtil.renderTextLocation(g, new net.runelite.api.Point(Microbot.getMouse().getLastClick2().getX() - (g.getFont().getSize() / 3),
                    Microbot.getMouse().getLastClick2().getY() + (g.getFont().getSize() / 3)), "X", Color.GREEN);


        }

        return null;
    }
}

