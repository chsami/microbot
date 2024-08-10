package net.runelite.client.plugins.microbot.util.antiban;

import net.runelite.api.Actor;
import net.runelite.api.Point;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ProgressPieComponent;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;


public class AntibanOverlay extends Overlay {

    private static final Color PUBLIC_TIMER_COLOR = Color.YELLOW;
    private static final Color PRIVATE_TIMER_COLOR = Color.GREEN;
    private static final int TIMER_OVERLAY_DIAMETER = 20;
    private final ProgressPieComponent progressPieComponent = new ProgressPieComponent();

    @Inject
    public AntibanOverlay() {
        setPosition(OverlayPosition.DYNAMIC);
    }

    private Point getCanvasTextLocation(Graphics2D graphics, Actor actor) {
        int zOffset = Math.min(actor.getLogicalHeight(), 140);
        // create blank buffered image
        BufferedImage bufferedImage = new BufferedImage(TIMER_OVERLAY_DIAMETER, TIMER_OVERLAY_DIAMETER, BufferedImage.TYPE_INT_ARGB);

        return actor.getCanvasImageLocation(bufferedImage, zOffset);
    }

    private void drawTimerPieOverlay(Graphics2D graphics) {

        Color fillColor = Color.YELLOW;

        // Calculate the remaining time as a fraction of the total time
        int totalTime = Rs2Antiban.getPlayStyle().getSecondaryTickInterval();

        int timeLeft = Rs2Antiban.getTIMEOUT();
        float percent = (float) timeLeft / totalTime;

        // Get the player's screen location
        Point playerLocation = getCanvasTextLocation(graphics, Microbot.getClient().getLocalPlayer());

        progressPieComponent.setDiameter(TIMER_OVERLAY_DIAMETER);
        // Shift over to not be on top of the text
        int x = playerLocation.getX() + (TIMER_OVERLAY_DIAMETER / 2);
        int y = playerLocation.getY() - (15 + (Microbot.getClient().getScale() / 30));
        progressPieComponent.setPosition(new Point(x, y));
        progressPieComponent.setFill(fillColor);
        progressPieComponent.setBorderColor(fillColor);
        progressPieComponent.setProgress(percent); // inverse so pie drains over time
        progressPieComponent.render(graphics);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (Rs2AntibanSettings.actionCooldownActive) {
            drawTimerPieOverlay(graphics);
        }


        return null;
    }
}
