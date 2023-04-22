package net.runelite.client.plugins.microbot.scripts.minigames.giantsfoundry;

import net.runelite.client.plugins.microbot.MicrobotPlugin;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class GiantsFoundryOverlay extends OverlayPanel {
    @Inject
    GiantsFoundryOverlay(MicrobotPlugin plugin)
    {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
    }
    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.setPreferredSize(new Dimension(200, 300));
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("GiantsFoundry")
                .color(Color.GREEN)
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Heat change needed:")
                .right(Integer.toString(GiantFoundryState.getHeatChangeNeeded()))
                .build());
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Current heat section:")
                .right(GiantFoundryState.getCurrentHeat().getName())
                .build());
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Current heat:")
                .right(Integer.toString(GiantFoundryState.getHeatAmount()))
                .build());
        panelComponent.getChildren().add(LineComponent.builder()
                .left("actions for heatlevel:")
                .right(Integer.toString(GiantFoundryState.getActionsForHeatLevel()))
                .build());
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Progress:")
                .right(Integer.toString(GiantFoundryState.getProgressAmount()) + "/1000" )
                .build());
        return super.render(graphics);
    }
}
