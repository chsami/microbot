package net.runelite.client.plugins.microbot.giantsfoundry;

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
                .text("GiantsFoundryScript")
                .color(Color.GREEN)
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Heat change needed:")
                .right(Integer.toString(GiantsFoundryState.getHeatChangeNeeded()))
                .build());
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Current heat section:")
                .right(GiantsFoundryState.getCurrentHeat().getName())
                .build());
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Current heat:")
                .right(Integer.toString(GiantsFoundryState.getHeatAmount()))
                .build());
        panelComponent.getChildren().add(LineComponent.builder()
                .left("actions for heatlevel:")
                .right(Integer.toString(GiantsFoundryState.getActionsForHeatLevel()))
                .build());
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Progress:")
                .right(Integer.toString(GiantsFoundryState.getProgressAmount()) + "/1000" )
                .build());
        return super.render(graphics);
    }
}
