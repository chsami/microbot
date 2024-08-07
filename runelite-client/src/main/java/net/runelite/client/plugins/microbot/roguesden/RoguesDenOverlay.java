package net.runelite.client.plugins.microbot.roguesden;

import net.runelite.client.plugins.microbot.roguesden.steps.Step;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class RoguesDenOverlay extends OverlayPanel {
    private final RoguesDenPlugin roguesDenPlugin;

    @Inject
    RoguesDenOverlay(RoguesDenPlugin plugin)
    {
        super(plugin);
        this.roguesDenPlugin = plugin;
        setPosition(OverlayPosition.TOP_LEFT);
        setNaughty();
    }
    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            panelComponent.setPreferredSize(new Dimension(200, 300));
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Rogues Den " + RoguesDenScript.version)
                    .color(Color.GREEN)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder().build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Current Step:")
                    .right(roguesDenPlugin.script.getCurrentStep().getName())
                    .build());

            if (!roguesDenPlugin.script.getFailuresByStep().isEmpty())
            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Step Run Terminated at:")
                        .build());
            }

            for (Map.Entry<Step, Integer> entry : roguesDenPlugin.script.getFailuresByStep()
                    .entrySet()
                    .stream()
                    .sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).collect(Collectors.toList()))
            {
                panelComponent.getChildren().add(LineComponent.builder()
                            .left(entry.getKey().getName())
                            .right(entry.getValue().toString())
                            .build());
            }


        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
        return super.render(graphics);
    }
}
