package net.runelite.client.plugins.nateplugins.combat.nateteleporter;

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

public class TeleportOverlay extends OverlayPanel {


    @Inject
    TeleportOverlay(TeleportPlugin plugin)
    {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        setNaughty();
    }
    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            xpGained = Microbot.getClient().getSkillExperience(Skill.MAGIC) - expstarted;
            int xpPerHour = (int)( xpGained / ((System.currentTimeMillis() - timeBegan) / 3600000.0D));
            nextLevelXp = XP_TABLE[Microbot.getClient().getRealSkillLevel(Skill.MAGIC) + 1];
            xpTillNextLevel = nextLevelXp - Microbot.getClient().getSkillExperience(Skill.MAGIC);
            if (xpGained >= 1)
            {
                timeTNL = (long) ((xpTillNextLevel / xpPerHour) * 3600000);
            }
            panelComponent.setPreferredSize(new Dimension(275, 700));
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Nate's Power Teleporter")
                    .color(Color.blue)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Magic Exp Gained (hr): " + (xpGained)  + " ("+xpPerHour+")")
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Magic Levels Gained: " + ( Microbot.getClient().getRealSkillLevel(Skill.MAGIC) - startinglevel))
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Time till next level: " + PaintFormat.ft(timeTNL))
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Teleports left: " + TeleportPlugin.teleportamount)
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
