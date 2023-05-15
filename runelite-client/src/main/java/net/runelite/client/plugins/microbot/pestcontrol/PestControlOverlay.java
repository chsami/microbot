package net.runelite.client.plugins.microbot.pestcontrol;

import net.runelite.api.Point;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.example.ExamplePlugin;
import net.runelite.client.plugins.microbot.example.ExampleScript;
import net.runelite.client.plugins.microbot.util.walker.PathTileOverlay;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class PestControlOverlay  extends OverlayPanel {
    @Inject
    PestControlOverlay(PestControlPlugin plugin)
    {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
    }
    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            panelComponent.setPreferredSize(new Dimension(200, 300));
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Micro PestControl V" + ExampleScript.version)
                    .color(Color.GREEN)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder().build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left(Microbot.status)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Games played")
                    .right(Integer.toString(PestControlScript.games))
                    .build());

            PathTileOverlay.render(graphics);

        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
        return super.render(graphics);
    }
}
