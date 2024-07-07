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

            if (!Microbot.getPluginManager().isActive(MageTrainingArenaScript.mtaPlugin)){
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Make sure to enable the 'Mage Training Arena' plugin!")
                        .leftColor(Color.RED)
                        .build());
            } else {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Room: " + (MageTrainingArenaScript.currentRoom != null ? MageTrainingArenaScript.currentRoom : "-"))
                        .build());
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Reward: " + config.reward())
                        .build());

                panelComponent.getChildren().add(LineComponent.builder().build());
                for (var points : MageTrainingArenaScript.currentPoints.entrySet()){
                    var rewardPoints = config.reward().getPoints().get(points.getKey());
                    panelComponent.getChildren().add(LineComponent.builder()
                            .left(String.format("%s: %d / %d", points.getKey(), points.getValue(), rewardPoints))
                            .build());
                }

                panelComponent.getChildren().add(LineComponent.builder().build());

                double progress = 0;
                for (var points : MageTrainingArenaScript.currentPoints.entrySet()){
                    var rewardPoints = config.reward().getPoints().get(points.getKey());
                    progress += Math.min((double) points.getValue() / rewardPoints, 1) * 25;
                }

                var progressBar = new ProgressBarComponent();
                progressBar.setValue(progress);
                panelComponent.getChildren().add(progressBar);

                if (config.buyRewards() && MageTrainingArenaScript.bought > 0)
                    panelComponent.getChildren().add(LineComponent.builder().left("Bought: " + MageTrainingArenaScript.bought).build());
                else if (!config.buyRewards() && MageTrainingArenaScript.buyable > 0)
                    panelComponent.getChildren().add(LineComponent.builder().left("Buyable: " + MageTrainingArenaScript.bought).build());


                panelComponent.getChildren().add(LineComponent.builder().build());

                panelComponent.getChildren().add(LineComponent.builder()
                        .left(Microbot.status)
                        .build());
            }
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
        return super.render(graphics);
    }
}
