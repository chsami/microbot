package net.runelite.client.plugins.microbot.farming.tithefarm;

import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class TitheFarmOverlay extends OverlayPanel {
    @Inject
    TitheFarmOverlay(net.runelite.client.plugins.microbot.farming.tithefarm.TitheFarmPlugin plugin)
    {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        setNaughty();
    }
    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.setPreferredSize(new Dimension(200, 300));
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("TitheFarm")
                .color(Color.GREEN)
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Plants left:")
                .right(Long.toString(TitheFarmScript.getPlantedPlants()))
                .build());
        return super.render(graphics);
    }
}
