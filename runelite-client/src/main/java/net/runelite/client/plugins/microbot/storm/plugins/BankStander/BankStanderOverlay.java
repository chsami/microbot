package net.runelite.client.plugins.microbot.storm.plugins.BankStander;

import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class BankStanderOverlay extends OverlayPanel {
    @Inject
    BankStanderOverlay(BankStanderPlugin plugin) {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        setNaughty();
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            panelComponent.setPreferredSize(new Dimension(200, 300));
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Storm's BankStander V" + BankStanderScript.version)
                    .color(PluginDescriptor.stormColor)
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Items processed : " + BankStanderScript.itemsProcessed)
                    .leftColor(PluginDescriptor.stormColor)
                    .build());
            panelComponent.getChildren().add(LineComponent.builder().build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left(Microbot.status)
                    .build());


        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return super.render(graphics);
    }
}
