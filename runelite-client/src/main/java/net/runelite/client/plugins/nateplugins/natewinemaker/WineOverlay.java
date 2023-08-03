package net.runelite.client.plugins.nateplugins.natewinemaker;

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


public class WineOverlay extends OverlayPanel {

    @Inject
    WineOverlay(WinePlugin plugin)
    {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
    }
    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            long timeElapsed = System.currentTimeMillis() - timeBegan;
            xpGained = Microbot.getClient().getSkillExperience(Skill.COOKING) - expstarted;;
            int xpPerHour = (int)( xpGained / ((System.currentTimeMillis() - timeBegan) / 3600000.0D));
            nextLevelXp = XP_TABLE[Microbot.getClient().getRealSkillLevel(Skill.COOKING) + 1];
            xpTillNextLevel = nextLevelXp - Microbot.getClient().getSkillExperience(Skill.COOKING);
            if (xpGained >= 1)
            {
                timeTNL = (long) ((xpTillNextLevel / xpPerHour) * 3600000);
            }
            panelComponent.setPreferredSize(new Dimension(275, 800));
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Nate's Wine Maker")
                    .color(Color.magenta)
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Time Ran: " + PaintFormat.ft(timeElapsed))
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Cooking Exp Gained (hr): " + (xpGained)  + " ("+xpPerHour+")")
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Cooking Levels Gained: " + ( Microbot.getClient().getRealSkillLevel(Skill.COOKING) - startinglevel))
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Time till next level: " + PaintFormat.ft(timeTNL))
                    .build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left(Microbot.status)
                    .build());


        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
        return super.render(graphics);
    }
}
