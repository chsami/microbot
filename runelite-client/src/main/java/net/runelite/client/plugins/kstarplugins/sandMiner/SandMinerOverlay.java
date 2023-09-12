package net.runelite.client.plugins.kstarplugins.sandMiner;


import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import javax.inject.Inject;
import java.awt.*;

public class SandMinerOverlay extends OverlayPanel {
    @Inject
    SandMinerOverlay(SandMiner plugin)
    {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
    }
    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            panelComponent.setPreferredSize(new Dimension(200, 300));
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Sand Miner V" + SandMiner.version)
                    .color(Color.GREEN)
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Current Action: " + SandMiner.currentAction.toString())
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("XP Gained: " + SandMiner.xpGained)
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("XP/Hr: " + SandMiner.xpHr)
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Running: " + SandMiner.time)
                    .build());

        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
        return super.render(graphics);
    }
}

