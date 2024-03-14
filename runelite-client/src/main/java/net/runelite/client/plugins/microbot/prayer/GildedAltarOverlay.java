package net.runelite.client.plugins.microbot.prayer;

import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetUtil;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GildedAltarOverlay extends OverlayPanel {
    private final GildedAltarPlugin plugin;

    @Inject
    GildedAltarOverlay(GildedAltarPlugin plugin)
    {
        super(plugin);
        this.plugin = plugin;
        setPosition(OverlayPosition.TOP_LEFT);
        setNaughty();
    }
    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            panelComponent.setPreferredSize(new Dimension(200, 300));
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Micro Gilded Altar V1.0")
                    .color(Color.GREEN)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder().build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left(plugin.state.toString())
                    .build());

        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
        return super.render(graphics);
    }
}
