package net.runelite.client.plugins.microbot.wheat;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

import static net.runelite.client.plugins.natepainthelper.Info.timeBegan;

public class WheatOverlay extends OverlayPanel {
    @Inject
    WheatOverlay(WheatPlugin plugin) {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        setNaughty();
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            panelComponent.setPreferredSize(new Dimension(200, 300));
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Micro Wheat " + WheatScript.version)
                    .color(Color.GREEN)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder().build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left(Microbot.status)
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Profit: " + WheatScript.profit + " gp")
                    .build());
            int runTime = (int) ((System.currentTimeMillis() - timeBegan)) / 1000;
            double runTimePerHour = ((System.currentTimeMillis() - timeBegan) / 3600000.0D);
            int profitK = 0;
            if (WheatScript.profit > 0) {
                profitK = WheatScript.profit / 1000;
            }

            int goldPerHour = (int) (profitK / runTimePerHour);
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Gold per hour: " + goldPerHour + " K")
                    .build());

            int sec = (runTime % 60);
            int min = ((runTime / 60) % 60);
            int hours = ((runTime / 60) / 60);

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Runtime: " + hours + " hour " + min + " min " + sec + " sec")
                    .build());

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return super.render(graphics);
    }
}
