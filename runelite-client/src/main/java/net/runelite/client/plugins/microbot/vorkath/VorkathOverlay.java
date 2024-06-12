package net.runelite.client.plugins.microbot.vorkath;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class VorkathOverlay extends OverlayPanel {
    private final VorkathPlugin plugin;
    private final VorkathConfig config;

    @Inject
    VorkathOverlay(VorkathPlugin plugin, VorkathConfig config)
    {
        super(plugin);
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.TOP_LEFT);
        setNaughty();
    }
    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            panelComponent.setPreferredSize(new Dimension(200, 300));
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Micro Vorkath V" + VorkathScript.version)
                    .color(Color.GREEN)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder().build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left(Microbot.status)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left(plugin.vorkathScript.state.toString())
                    .build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Vorkath kills: " + plugin.vorkathScript.vorkathSessionKills)
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Vorkath kills left before selling items: " + plugin.vorkathScript.tempVorkathKills % config.SellItemsAtXKills())
                    .build());


        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
        return super.render(graphics);
    }
}
