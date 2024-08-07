package net.runelite.client.plugins.microbot.piebaker;

import net.runelite.api.Skill;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class PieBakerOverlay extends OverlayPanel {
    @Inject
    private PieBakerPlugin plugin;

    @Inject
    public PieBakerOverlay(PieBakerPlugin plugin) {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        this.plugin = plugin;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("Pie Baker")
                .color(Color.GREEN)
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Pie:")
                .right(plugin.getSelectedPie())
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Magic XP/h:")
                .right(String.valueOf(plugin.getMagicXPPerHour()))
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Cooking XP/h:")
                .right(String.valueOf(plugin.getCookingXPPerHour()))
                .build());

        return super.render(graphics);
    }
}
