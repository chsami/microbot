package net.runelite.client.plugins.microbot.chompy;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class ChompyOverlay extends OverlayPanel {
    @Inject
    ChompyOverlay(ChompyPlugin plugin)
    {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        setNaughty();
    }
    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            panelComponent.setPreferredSize(new Dimension(200, 300));
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Chompy Hunter " + ChompyScript.version)
                    .color(Color.GREEN)
                    .build());

            // Kills per hour:
            long elapsed= System.currentTimeMillis() - ChompyScript.start_time;
            double hours = elapsed / 3600000.0;
            double killsPerHour = ChompyScript.chompy_kills / hours;

            panelComponent.getChildren().add(TitleComponent.builder()
                    .text(String.format("Chompy Kills: " + ChompyScript.chompy_kills + " [%.1f kph]",killsPerHour))
                    .color(Color.GREEN)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder().build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left(Microbot.status)
                    .build());


        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
        return super.render(graphics);
    }
}
