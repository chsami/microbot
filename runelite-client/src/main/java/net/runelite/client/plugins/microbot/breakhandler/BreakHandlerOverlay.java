package net.runelite.client.plugins.microbot.breakhandler;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class BreakHandlerOverlay extends OverlayPanel {
    @Inject
    BreakHandlerOverlay(BreakHandlerPlugin plugin)
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
                    .text("Micro Example V" + BreakHandlerScript.version)
                    .color(Color.GREEN)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder().build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left(Microbot.status)
                    .build());


            if (BreakHandlerScript.npc != null && BreakHandlerScript.npc.getCanvasTilePoly() != null) {
                try {
                    panelComponent.getChildren().add(LineComponent.builder()
                            .left("Health: " + Rs2Npc.getHealth(BreakHandlerScript.npc))
                            .build());
                    panelComponent.getChildren().add(LineComponent.builder()
                            .left("index: " + BreakHandlerScript.npc.getIndex())
                            .build());
                    panelComponent.getChildren().add(LineComponent.builder()
                            .left("Name: " + BreakHandlerScript.npc.getName())
                            .build());
                    panelComponent.getChildren().add(LineComponent.builder()
                            .left("combat: " + BreakHandlerScript.npc.getCombatLevel())
                            .build());
                    graphics.setColor(Color.CYAN);
                    graphics.draw(BreakHandlerScript.npc.getCanvasTilePoly());
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }


        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
        return super.render(graphics);
    }
}
