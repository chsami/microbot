package net.runelite.client.plugins.microbot.bossassist;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.bossassist.models.PRAY_MODE;
import net.runelite.client.plugins.microbot.derangedarchaeologist.DerangedAchaeologistPlugin;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class BossAssistOverlay extends OverlayPanel {

    private final BossAssistPlugin plugin;

    @Inject
    BossAssistOverlay(BossAssistPlugin plugin)
    {
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
                    .text("Pumsters Boss Assist V" + BossAssistScript.version)
                    .color(Color.GREEN)
                    .build());


            panelComponent.getChildren().add(LineComponent.builder().build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left(Microbot.status)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left(plugin.bossAssistScript.currentBoss.toString())
                    .build());

            if (plugin.bossAssistScript.config.PRAYER_MODE() == PRAY_MODE.VISUAL) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left(plugin.bossAssistScript.prayStyle.toString())
                        .build());
            }


        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
        return super.render(graphics);
    }
}
