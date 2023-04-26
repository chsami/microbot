package net.runelite.client.plugins.microbot.farming.tithefarm;

import net.runelite.client.plugins.tithefarm.TitheFarmPlant;
import net.runelite.client.plugins.tithefarm.TitheFarmPlugin;
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
    }
    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.setPreferredSize(new Dimension(200, 300));
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("TitheFarm")
                .color(Color.GREEN)
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Current Plant:")
                .right(Integer.toString(TitheFarmScript.currentPlant))
                .build());
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Plants left:")
                .right(Integer.toString(TitheFarmPlugin.getPlants().size()))
                .build());
        return super.render(graphics);
    }
}
