package net.runelite.client.plugins.eeng1n.woodcutting.threetickteaks;

import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.natepainthelper.PaintFormat;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

import static net.runelite.client.plugins.natepainthelper.Info.*;

public class ThreeTickTeaksOverlay extends OverlayPanel {

    @Inject
    ThreeTickTeaksOverlay(ThreeTickTeaksPlugin threeTickTeaksPlugin) {
        super(threeTickTeaksPlugin);
        setPosition(OverlayPosition.BOTTOM_LEFT);
        setNaughty();
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            long timeElapsed = System.currentTimeMillis() - timeBegan;
            xpGained = Microbot.getClient().getSkillExperience(Skill.WOODCUTTING) - expstarted;

            int xpPerHour = (int) (xpGained / ((System.currentTimeMillis() - timeBegan) / 3600000.0D));
            nextLevelXp = XP_TABLE[Microbot.getClient().getRealSkillLevel(Skill.WOODCUTTING) + 1];
            xpTillNextLevel = nextLevelXp - Microbot.getClient().getSkillExperience(Skill.WOODCUTTING);
            if (xpGained >= 1) {
                timeTNL = (long) ((xpTillNextLevel / xpPerHour) * 3600000);
            }
            panelComponent.setPreferredSize(new Dimension(275, 800));
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("3T Teaks")
                    .color(Color.magenta)
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Time Ran: " + PaintFormat.ft(timeElapsed))
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Woodcutting Exp Gained (hr): " + (xpGained) + " (" + xpPerHour + ")")
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Woodcutting Levels Gained: " + (Microbot.getClient().getRealSkillLevel(Skill.WOODCUTTING) - startinglevel))
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Time till next level: " + PaintFormat.ft(timeTNL))
                    .build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .build());


        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return super.render(graphics);
    }
}
