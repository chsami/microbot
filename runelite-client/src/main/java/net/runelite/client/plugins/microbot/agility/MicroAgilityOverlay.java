package net.runelite.client.plugins.microbot.agility;

import net.runelite.api.Point;
import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.walker.PathTileOverlay;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class MicroAgilityOverlay extends OverlayPanel {
    @Inject
    MicroAgilityOverlay(MicroAgilityPlugin plugin) {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            panelComponent.setPreferredSize(new Dimension(200, 300));
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Micro Agility V" + AgilityScript.version)
                    .color(Color.GREEN)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder().build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("AgilityExp")
                    .right(Integer.toString(Microbot.getClient().getSkillExperience(Skill.AGILITY)))
                    .build());

            PathTileOverlay.render(graphics);

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return super.render(graphics);
    }
}
