package net.runelite.client.plugins.microbot.bankjs.development.other;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class BanksTestOverlay extends OverlayPanel {
    private final BanksTestPlugin plugin;
    private final BanksTestScript script; // Reference to the script

    @Inject
    BanksTestOverlay(BanksTestPlugin plugin, BanksTestScript script) {
        super(plugin);
        this.plugin = plugin;
        this.script = script; // Ensure the correct script instance is used
        setPosition(OverlayPosition.BOTTOM_LEFT);
        setPreferredSize(new Dimension(800, 300));
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            panelComponent.getChildren().clear();

            // Add title with custom color
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Bank's Tester")
                    .color(Color.CYAN)
                    .build());

            // Add separator line
            panelComponent.getChildren().add(LineComponent.builder().build());

            // Add Script state
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("STATE:")
                    .right(script.getCurrentState().toString()) // Display current state
                    .leftColor(Color.WHITE)
                    .rightColor(Color.CYAN)
                    .build());

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return super.render(graphics);
    }
}
