package net.runelite.client.plugins.microbot.mining.shootingstar;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.misc.TimeUtils;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.util.ColorUtil;

import javax.inject.Inject;
import java.awt.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ShootingStarOverlay extends OverlayPanel {

    ShootingStarPlugin plugin;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Inject
    ShootingStarOverlay(ShootingStarPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
        setPosition(OverlayPosition.TOP_LEFT);
        setNaughty();
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            panelComponent.setPreferredSize(new Dimension(200, 300));
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("ShootingStar Miner V" + ShootingStarPlugin.version)
                    .color(ColorUtil.fromHex("0077B6"))
                    .build());

            panelComponent.getChildren().add(LineComponent.builder().build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Status:")
                    .right(Microbot.status)
                    .build());

            if (!plugin.isHideDevOverlay()) {
                if (ShootingStarScript.state != null) {
                    panelComponent.getChildren().add(LineComponent.builder()
                            .left("Current State:")
                            .right(ShootingStarScript.state.name())
                            .build());
                }

                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Run time:")
                        .right(TimeUtils.getFormattedDurationBetween(plugin.getStartTime(), Instant.now()))
                        .build());

                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Total Stars Mined:")
                        .right(Integer.toString(plugin.getTotalStarsMined()))
                        .build());

                if (plugin.getSelectedStar() != null) {
                    panelComponent.getChildren().add(LineComponent.builder().build());

                    panelComponent.getChildren().add(TitleComponent.builder()
                            .text("Current Star Information")
                            .color(ColorUtil.fromHex("0077B6"))
                            .build());

                    panelComponent.getChildren().add(LineComponent.builder().build());

                    panelComponent.getChildren().add(LineComponent.builder()
                            .left("Location:")
                            .right(plugin.getSelectedStar().getShootingStarLocation().getLocationName())
                            .build());

                    panelComponent.getChildren().add(LineComponent.builder()
                            .left("Is in Wilderness:")
                            .right(String.valueOf(plugin.getSelectedStar().isInWilderness()))
                            .build());

                    panelComponent.getChildren().add(LineComponent.builder()
                            .left("World:")
                            .right(Integer.toString(plugin.getSelectedStar().getWorld()))
                            .build());

                    String worldType = plugin.getSelectedStar().isMemberWorld() ? "Member" :
                            plugin.getSelectedStar().isF2PWorld() ? "F2P" : "Unknown";

                    panelComponent.getChildren().add(LineComponent.builder()
                            .left("World Type:")
                            .right(worldType)
                            .build());

                    panelComponent.getChildren().add(LineComponent.builder()
                            .left("Tier:")
                            .right(Integer.toString(plugin.getSelectedStar().getTier()))
                            .build());

                    Instant endInstant = Instant.ofEpochMilli(plugin.getSelectedStar().getEndsAt());
                    String estEndTime = plugin.isDisplayAsMinutes() ? TimeUtils.getFormattedDurationBetween(Instant.now(), endInstant) :
                            LocalDateTime.ofInstant(endInstant, ZoneId.systemDefault()).format(dtf);

                    panelComponent.getChildren().add(LineComponent.builder()
                            .left("Est. End Time:")
                            .right(estEndTime)
                            .build());
                    

                    panelComponent.getChildren().add(LineComponent.builder()
                            .left("Has Requirements:")
                            .right(String.valueOf(plugin.getSelectedStar().hasRequirements()))
                            .build());

                    if (!plugin.getSelectedStar().hasRequirements()) {
                        panelComponent.getChildren().add(LineComponent.builder().build());
                        
                        panelComponent.getChildren().add(LineComponent.builder()
                                .left("Has Location Requirements:")
                                .right(String.valueOf(plugin.getSelectedStar().hasLocationRequirements()))
                                .build());

                        panelComponent.getChildren().add(LineComponent.builder()
                                .left("Has Mining Level:")
                                .right(String.valueOf(plugin.getSelectedStar().hasMiningLevel()))
                                .build());
                    }
                }
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return super.render(graphics);
    }
}
