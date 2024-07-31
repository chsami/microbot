package net.runelite.client.plugins.microbot.vorkath;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import javax.inject.Named;
import java.awt.*;

public class VorkathOverlay extends OverlayPanel {
    private final VorkathPlugin plugin;
    private final String vorkathPluginVersion;

    @Inject
    VorkathOverlay(VorkathPlugin plugin, @Named("vorkath.plugin.version") String vorkathPluginVersion)
    {
        super(plugin);
        this.plugin = plugin;
        this.vorkathPluginVersion = vorkathPluginVersion;
        setPosition(OverlayPosition.TOP_LEFT);
        setNaughty();
    }
    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            panelComponent.setPreferredSize(new Dimension(300, 300));
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Micro Vorkath V" + vorkathPluginVersion)
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
                    .left("Vorkath kills until selling: " + plugin.vorkathScript.tempVorkathKills)
                    .build());


        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
        return super.render(graphics);
    }
}
