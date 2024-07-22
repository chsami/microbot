package net.runelite.client.plugins.microbot.magetrainingarena;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.*;

import javax.inject.Inject;
import java.awt.*;

public class MageTrainingArenaOverlay extends OverlayPanel {
    MageTrainingArenaConfig config;

    @Inject
    MageTrainingArenaOverlay(MageTrainingArenaPlugin plugin, MageTrainingArenaConfig config)
    {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        setPriority(PRIORITY_HIGHEST);
        setNaughty();

        this.config = config;
    }
    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            panelComponent.setPreferredSize(new Dimension(200, 300));
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Basche's Mage Training Arena " + MageTrainingArenaScript.version)
                    .color(Color.GREEN)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder().build());

            if (!Microbot.getPluginManager().isActive(MageTrainingArenaScript.getMtaPlugin())){
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Make sure to enable the 'Mage Training Arena' plugin!")
                        .leftColor(Color.RED)
                        .build());
            } else {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Room: " + (MageTrainingArenaScript.getCurrentRoom() != null ? MageTrainingArenaScript.getCurrentRoom() : "-"))
                        .build());
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Reward: " + config.reward())
                        .build());

                panelComponent.getChildren().add(LineComponent.builder().build());
                for (var points : MageTrainingArenaScript.getCurrentPoints().entrySet()){
                    var rewardPoints = MageTrainingArenaScript.getRequiredPoints(config).get(points.getKey());
                    panelComponent.getChildren().add(LineComponent.builder()
                            .left(String.format("%s: %d / %d", points.getKey(), points.getValue(), rewardPoints))
                            .build());
                }

                panelComponent.getChildren().add(LineComponent.builder().build());

                double progress = 0;
                for (var points : MageTrainingArenaScript.getCurrentPoints().entrySet()){
                    var rewardPoints = MageTrainingArenaScript.getRequiredPoints(config).get(points.getKey());
                    progress += Math.min((double) (points.getValue() - (config.buyRewards() ? 0 : MageTrainingArenaScript.getBuyable()) * rewardPoints) / rewardPoints, 1) * 25;
                }


                if (config.buyRewards() && MageTrainingArenaScript.getBought() > 0)
                    panelComponent.getChildren().add(LineComponent.builder().left("Bought: " + MageTrainingArenaScript.getBought()).build());
                else if (!config.buyRewards() && MageTrainingArenaScript.getBuyable() > 0)
                    panelComponent.getChildren().add(LineComponent.builder().left("Buyable: " + MageTrainingArenaScript.getBuyable()).build());

                var progressBar = new ProgressBarComponent();
                progressBar.setValue(progress);
                panelComponent.getChildren().add(progressBar);
            }
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
        return super.render(graphics);
    }
}
