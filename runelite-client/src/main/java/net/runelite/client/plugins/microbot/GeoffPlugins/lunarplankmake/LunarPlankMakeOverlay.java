package net.runelite.client.plugins.microbot.GeoffPlugins.lunarplankmake;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

import javax.inject.Inject;

import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

public class LunarPlankMakeOverlay extends OverlayPanel {

    @Inject
    LunarPlankMakeOverlay(LunarPlankMakePlugin plugin)
    {
        super(plugin);
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        setNaughty();
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.setPreferredSize(new Dimension(200, 300));
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("Plank Make " + LunarPlankMakeScript.version)
                .color(Color.YELLOW)
                .build());

        // Update to display the combined message
        panelComponent.getChildren().add(LineComponent.builder()
                .left(LunarPlankMakeScript.combinedMessage)
                .build());

        return super.render(graphics);
    }
}