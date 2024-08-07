package net.runelite.client.plugins.microbot.fishing.eel;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.fishing.eel.enums.EelFishingSpot;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class EelFishingOverlay extends OverlayPanel {

    private final EelFishingPlugin plugin;
    private final EelFishingConfig config;

    @Inject
    public EelFishingOverlay(EelFishingPlugin plugin, EelFishingConfig config) {
        super(plugin);
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.TOP_LEFT);
        setNaughty();
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.setPreferredSize(new Dimension(225, 300));

        panelComponent.getChildren().add(TitleComponent.builder()
                .text("\uD83E\uDD86 Eel Fishing \uD83E\uDD86")
                .color(Color.ORANGE)
                .build());

        panelComponent.getChildren().add(LineComponent.builder().build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Fishing Spot:")
                .right(config.fishingSpot().toString())
                .build());
        if (config.fishingSpot().equals(EelFishingSpot.INFERNAL_EEL)) {
            // check if player is in the correct region(10063 or 9807)
            String region = Rs2Player.getWorldLocation() != null ? (Rs2Player.getWorldLocation().getRegionID() == 10063) || (Rs2Player.getWorldLocation().getRegionID() == 9807) ? "In Region" : "Not in Region" : "Not in Region";
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Region: " + region)
                    .build());
            // check if we have the Oily fishing rod in the inventory
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Oily Fishing Rod:")
                    .right(Rs2Inventory.hasItem("Oily fishing rod") ? "✔" : "❌")
                    .rightColor(Rs2Inventory.hasItem("Oily fishing rod") ? Color.GREEN : Color.RED)
                    .build());

            // check if we have ice gloves equipped
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Ice Gloves Equipped:")
                    .right(EelFishingScript.hasRequiredGloves() ? "✔" : "❌")
                    .rightColor(EelFishingScript.hasRequiredGloves() ? Color.GREEN : Color.RED)
                    .build());
            // check if we have hammer in inventory
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Hammer:")
                    .right(Rs2Inventory.hasItem("hammer") ? "✔" : "❌")
                    .rightColor(Rs2Inventory.hasItem("hammer") ? Color.GREEN : Color.RED)
                    .build());
        }
        if (config.fishingSpot().equals(EelFishingSpot.SACRED_EEL)) {
            // check if player is in the correct region(8751)
            String region = Rs2Player.getWorldLocation() != null ? Rs2Player.getWorldLocation().getRegionID() == 8751 ? "In Region" : "Not in Region" : "Not in Region";
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Region: " + region)
                    .build());
            // check if we have fishing rod in inventory
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Fishing Rod:")
                    .right(Rs2Inventory.hasItem("fishing rod") ? "✔" : "❌")
                    .rightColor(Rs2Inventory.hasItem("fishing rod") ? Color.GREEN : Color.RED)
                    .build());

            // check if we have knife in inventory
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Knife:")
                    .right(Rs2Inventory.hasItem("knife") ? "✔" : "❌")
                    .rightColor(Rs2Inventory.hasItem("knife") ? Color.GREEN : Color.RED)
                    .build());

        }


        panelComponent.getChildren().add(LineComponent.builder()
                .left("Inventory Full:")
                .right(Rs2Inventory.isFull() ? "Yes" : "No")
                .build());

        panelComponent.getChildren().add(LineComponent.builder().build());
        panelComponent.getChildren().add(LineComponent.builder()
                .left(Microbot.status)
                .right("Version: " + EelFishingScript.version)
                .build());

        return super.render(graphics);
    }
}
