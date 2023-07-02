package net.runelite.client.plugins.microbot;

import net.runelite.client.plugins.microbot.cooking.CookingScript;
import net.runelite.client.plugins.microbot.mining.MiningScript;
import net.runelite.client.plugins.microbot.thieving.ThievingScript;
import net.runelite.client.plugins.microbot.util.walker.PathTileOverlay;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class MicrobotOverlay extends OverlayPanel {
    MicrobotPlugin plugin;
    @Inject
    MicrobotOverlay(MicrobotPlugin plugin)
    {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        this.plugin = plugin;
    }
    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            PathTileOverlay.render(graphics);

            if (plugin.thievingScript != null) {
                drawThievingOverlay();
            }

            if (plugin.cookingScript != null) {
                drawCookingOverlay();
            }

            if (plugin.miningScript != null) {
                drawMiningOverlay();
            }

        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
        return super.render(graphics);
    }

    private void drawThievingOverlay() {
        panelComponent.setPreferredSize(new Dimension(200, 300));
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("Micro Thieving V" + ThievingScript.version)
                .color(Color.GREEN)
                .build());

        panelComponent.getChildren().add(LineComponent.builder().build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left(Microbot.status)
                .build());
    }

    private void drawCookingOverlay() {
        panelComponent.setPreferredSize(new Dimension(200, 300));
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("Micro Cooking V" + CookingScript.version)
                .color(Color.GREEN)
                .build());

        panelComponent.getChildren().add(LineComponent.builder().build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left(Microbot.status)
                .build());
    }
    private void drawMiningOverlay() {
        panelComponent.setPreferredSize(new Dimension(200, 300));
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("Micro Mining V" + MiningScript.version)
                .color(Color.GREEN)
                .build());

        panelComponent.getChildren().add(LineComponent.builder().build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left(Microbot.status)
                .build());
    }

}

