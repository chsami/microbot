package net.runelite.client.plugins.microbot.mining.amethyst;

import net.runelite.client.plugins.microbot.util.antiban.Rs2Antiban;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

import static net.runelite.client.plugins.microbot.mining.amethyst.AmethystMiningScript.status;

public class AmethystMiningOverlay extends OverlayPanel {
    @Inject
    AmethystMiningOverlay(AmethystMiningPlugin plugin) {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            panelComponent.setPreferredLocation(new Point(80, 8));
            panelComponent.setPreferredSize(new Dimension(275, 700));
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("\uD83E\uDD86 Amethyst Miner \uD83E\uDD86")
                    .color(Color.ORANGE)
                    .build());

            addEmptyLine();

            Rs2Antiban.renderAntibanOverlayComponents(panelComponent);

            addEmptyLine();

            panelComponent.getChildren().add(LineComponent.builder()
                    .left(status.toString())
                    .right("Version: " + AmethystMiningScript.version)
                    .build());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return super.render(graphics);
    }

    private void addEmptyLine() {
        panelComponent.getChildren().add(LineComponent.builder().build());
    }
}

