package net.runelite.client.plugins.jrPlugins.autoChin;


import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import javax.inject.Inject;
import java.awt.*;

public class AutoChinOverlay extends OverlayPanel {
    @Inject
    AutoChinOverlay(AutoChin plugin)
    {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        setNaughty();
    }
    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            panelComponent.setPreferredSize(new Dimension(200, 300));
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Auto Chin V" + AutoChin.version)
                    .color(Color.GREEN)
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Current State: " + AutoChin.currentState.toString())
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Lvls Gained: " + AutoChin.lvlsGained)
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Caught: " + AutoChin.caught)
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("XP Gained: " + AutoChin.xpGained)
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("XP/Hr: " + AutoChin.xpHr)
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Running: " + AutoChin.time)
                    .build());

        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
        return super.render(graphics);
    }
}

