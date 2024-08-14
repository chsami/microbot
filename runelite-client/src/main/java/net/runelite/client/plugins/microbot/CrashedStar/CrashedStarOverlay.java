package net.runelite.client.plugins.microbot.CrashedStar;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class CrashedStarOverlay extends OverlayPanel {
    private final CrashedStarPlugin plugin;
    private final CrashedStarConfig config;

    @Inject
    CrashedStarOverlay(CrashedStarPlugin plugin, CrashedStarConfig config)
    {
        super(plugin);
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.TOP_LEFT);
        setNaughty();
    }
    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            panelComponent.setPreferredSize(new Dimension(200, 300));
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Crashed Star Miner v" + CrashedStarScript.version)
                    .color(Color.GREEN)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder().build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Status: ")
                    .right(Microbot.status)
                    .build());
            String tierText = plugin.getCurrentTier();
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Tier: ")
                    .right(tierText)
                    .build());
            panelComponent.getChildren().add(LineComponent.builder().build());


        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
        return super.render(graphics);
    }
}
