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
        // Check if the overlay toggle is enabled
        if (!config.showAreaOverlay()) {
            return null;  // Don't render anything if the overlay is disabled
        }

        // Get the WorldArea for the selected bird from the config
        WorldArea birdHuntingArea = config.BIRD().getArea();

        // Draw the outline of the WorldArea if it's available
        if (birdHuntingArea != null) {
            drawAreaOutline(graphics, birdHuntingArea);
        }

        return super.render(graphics);
    }

    private void drawAreaOutline(Graphics2D graphics, WorldArea area) {
        // Get the LocalPoint for the southwest corner of the area
        LocalPoint localPoint = LocalPoint.fromWorld(client, area.toWorldPoint());

        if (localPoint == null) {
            return;
        }

        // Use Perspective.getCanvasTileAreaPoly to get the polygon representing the area
        Polygon areaPoly = Perspective.getCanvasTileAreaPoly(client, localPoint, area.getWidth());

        // If we got a polygon, render it
        if (areaPoly != null) {
            // Use a transparent yellow color for the outline
            graphics.setColor(new Color(255, 255, 0, 127));
            graphics.setStroke(new BasicStroke(2));  // Set the stroke width for the outline
            graphics.draw(areaPoly);  // Draw only the outline, not filling the polygon
        }
    }
}
