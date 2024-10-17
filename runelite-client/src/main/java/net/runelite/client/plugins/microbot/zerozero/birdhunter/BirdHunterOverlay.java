package net.runelite.client.plugins.microbot.zerozero.birdhunter;

import net.runelite.api.Client;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.zerozero.enums.hunter.Birds;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.api.Perspective;

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
        setPosition(OverlayPosition.TOP_LEFT);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            panelComponent.setPreferredSize(new Dimension(200, 300));
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("BirdHunter Plugin V" + BirdHunterScript.version)
                    .color(Color.GREEN)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder().build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left(Microbot.status)
                    .build());

            // Draw the WorldArea outline for the selected bird
            drawBirdAreaOutline(graphics);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return super.render(graphics);
    }

    private void drawBirdAreaOutline(Graphics2D graphics) {
        Birds selectedBird = config.BIRD();
        WorldArea birdArea = selectedBird.getArea();

        if (birdArea != null) {
            drawAreaOutline(graphics, birdArea, Color.YELLOW);
        }
    }

    private void drawAreaOutline(Graphics2D graphics, WorldArea area, Color color) {
        WorldPoint southwestCorner = new WorldPoint(area.getX() - 1, area.getY() - 1, area.getPlane());
        WorldPoint southeastCorner = new WorldPoint(area.getX() + area.getWidth(), area.getY() - 1, area.getPlane());
        WorldPoint northwestCorner = new WorldPoint(area.getX() - 1, area.getY() + area.getHeight(), area.getPlane());
        WorldPoint northeastCorner = new WorldPoint(area.getX() + area.getWidth(), area.getY() + area.getHeight(), area.getPlane());

        drawLineBetweenWorldPoints(graphics, southwestCorner, southeastCorner, color);
        drawLineBetweenWorldPoints(graphics, southeastCorner, northeastCorner, color);
        drawLineBetweenWorldPoints(graphics, northeastCorner, northwestCorner, color);
        drawLineBetweenWorldPoints(graphics, northwestCorner, southwestCorner, color);
    }

    private void drawLineBetweenWorldPoints(Graphics2D graphics, WorldPoint start, WorldPoint end, Color color) {
        LocalPoint localStart = LocalPoint.fromWorld(client, start);
        LocalPoint localEnd = LocalPoint.fromWorld(client, end);

        if (localStart != null && localEnd != null && client.getPlane() == start.getPlane()) {
            net.runelite.api.Point canvasStart = Perspective.localToCanvas(client, localStart, client.getPlane());
            net.runelite.api.Point canvasEnd = Perspective.localToCanvas(client, localEnd, client.getPlane());

            if (canvasStart != null && canvasEnd != null) {
                graphics.setColor(color);
                graphics.drawLine(canvasStart.getX(), canvasStart.getY(), canvasEnd.getX(), canvasEnd.getY());
            }
        }
    }
}
