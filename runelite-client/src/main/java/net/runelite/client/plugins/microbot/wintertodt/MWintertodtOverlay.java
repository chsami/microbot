package net.runelite.client.plugins.microbot.wintertodt;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class MWintertodtOverlay extends OverlayPanel {
    private final MWintertodtPlugin plugin;
    MWintertodtConfig config;

    @Inject
    MWintertodtOverlay(MWintertodtPlugin plugin, MWintertodtConfig config)
    {
        super(plugin);
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.TOP_CENTER);
        setNaughty();
    }
    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            panelComponent.setPreferredSize(new Dimension(200, 300));
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Micro Wintertodt V" + MWintertodtScript.version)
                    .color(Color.GREEN)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder().build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Running: " + plugin.getTimeRunning())
                    .leftColor(Color.GREEN)
                    .build());
            panelComponent.getChildren().add(LineComponent.builder().build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Won: " + plugin.getWon())
                    .leftColor(Color.GREEN)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Lost: " + plugin.getLost())
                    .leftColor(Color.RED)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Cut: " + plugin.getLogsCut())
                    .leftColor(Color.GREEN)
                    .build());


            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Fletched: " + plugin.getLogsFletched())
                    .leftColor(Color.GREEN)
                    .build());


            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Braziers fixed: " + plugin.getBraziersFixed())
                    .leftColor(Color.GREEN)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Braziers lit: " + plugin.getBraziersLit())
                    .leftColor(Color.GREEN)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Food consumed: " + plugin.getFoodConsumed())
                    .leftColor(Color.GREEN)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Times banked: " + plugin.getTimesBanked())
                    .leftColor(Color.GREEN)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left(MWintertodtScript.state.toString())
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
