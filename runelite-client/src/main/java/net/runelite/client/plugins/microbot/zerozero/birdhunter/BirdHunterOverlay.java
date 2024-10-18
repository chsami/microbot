package net.runelite.client.plugins.microbot.zerozero.birdhunter;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;

public class BirdHunterOverlay extends OverlayPanel {

    private final Client client;
    private final BirdHunterConfig config;

    @Inject
    BirdHunterOverlay(Client client, BirdHunterPlugin plugin, BirdHunterConfig config) {
        super(plugin);
        this.client = client;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!config.showAreaOverlay()) {
            return null;
        }

        WorldArea birdHuntingArea = config.BIRD().getArea();

        if (birdHuntingArea != null) {
            drawAreaOutline(graphics, birdHuntingArea);
        }

        return super.render(graphics);
    }

    private void drawAreaOutline(Graphics2D graphics, WorldArea area) {
        LocalPoint localPoint = LocalPoint.fromWorld(client, area.toWorldPoint());

        if (localPoint == null) {
            return;
        }

        Polygon areaPoly = Perspective.getCanvasTileAreaPoly(client, localPoint, area.getWidth());

        if (areaPoly != null) {
            graphics.setColor(new Color(255, 255, 0, 127));
            graphics.setStroke(new BasicStroke(2));
            graphics.draw(areaPoly);
        }
    }
}
