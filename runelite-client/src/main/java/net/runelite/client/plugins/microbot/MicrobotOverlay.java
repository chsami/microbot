package net.runelite.client.plugins.microbot;

import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;

import java.awt.*;

public class MicrobotOverlay extends OverlayPanel {

    public MicrobotOverlay() {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
       /* for (Point mousePosition: Microbot.getMouse().mousePositions) {
            final Stroke STROKE = new BasicStroke(4f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
            graphics.setStroke(STROKE);
            graphics.setColor(Color.RED);
            graphics.setFont(new Font("TimesRoman", Font.PLAIN, 24));
            graphics.drawString("X", mousePosition.getX(), mousePosition.getY());
        }*/
        /*graphics.setColor(new Color(0, 0, 0, 80));
        graphics.drawRect(100, 100, 100, 100);
        graphics.fillRect(100, 100, 100, 100);
        graphics.setColor(Color.WHITE);
        graphics.drawString("Status lootscript: " + Microbot.isLoggedIn(), 120, 120);*/
        return null;
    }
}
