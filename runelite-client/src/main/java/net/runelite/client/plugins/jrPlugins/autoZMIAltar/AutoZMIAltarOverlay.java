package net.runelite.client.plugins.jrPlugins.autoZMIAltar;

import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class AutoZMIAltarOverlay extends OverlayPanel {
    @Inject
    AutoZMIAltarOverlay(AutoZMIAltar plugin){
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        setNaughty();
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            panelComponent.setPreferredSize(new Dimension(200, 300));
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Auto ZMI Altar V" + AutoZMIAltar.version)
                    .color(Color.GREEN)
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Current State: " + AutoZMIAltar.currentState.toString())
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Lvls Gained: " + AutoZMIAltar.lvlsGained)
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Total Runs: " + AutoZMIAltar.totalRuns)
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("XP Gained: " + AutoZMIAltar.xpGained)
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("XP/Hr: " + AutoZMIAltar.xpHr)
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Running: " + AutoZMIAltar.time)
                    .build());

        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
        return super.render(graphics);
    }
}