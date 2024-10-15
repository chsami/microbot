package net.runelite.client.plugins.microbot.tutorialisland;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class TutorialIslandOverlay extends OverlayPanel {
    
    TutorialislandPlugin plugin;
    @Inject
    TutorialIslandOverlay(TutorialislandPlugin plugin) {
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
                    .text("Micro TutorialIsland V" + TutorialIslandScript.version)
                    .color(Color.GREEN)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder().build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left(Microbot.status)
                    .build());
            
            if (plugin.isToggleDevOverlay()) {
                if (TutorialIslandScript.status != null) {
                    panelComponent.getChildren().add(LineComponent.builder()
                            .left("State:")
                            .right(TutorialIslandScript.status.toString())
                            .build());
                }

                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Progress (281):")
                        .right(Integer.toString(Microbot.getVarbitPlayerValue(281)))
                        .build());
            }
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
        return super.render(graphics);
    }
}
