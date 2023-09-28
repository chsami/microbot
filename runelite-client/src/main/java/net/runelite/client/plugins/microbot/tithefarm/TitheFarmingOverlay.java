package net.runelite.client.plugins.microbot.tithefarm;

import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.tithefarm.enums.TitheFarmMaterial;
import net.runelite.client.plugins.microbot.tithefarm.models.TitheFarmPlant;
import net.runelite.client.plugins.microbot.util.math.RateCalculator;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class TitheFarmingOverlay extends OverlayPanel {
    TitheFarmingPlugin plugin;

    @Inject
    TitheFarmingOverlay(TitheFarmingPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
        setPosition(OverlayPosition.TOP_LEFT);
        setNaughty();
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.setPreferredSize(new Dimension(300, 300));
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("TitheFarm")
                .color(Color.GREEN)
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Script State: ")
                .right(TitheFarmingScript.state.toString())
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Fruit per hour:")
                .right(String.valueOf(RateCalculator.getRatePerHour(TitheFarmingScript.fruits)))
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("")
                .right("")
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Fruits farmed")
                .right("")
                .build());

        panelComponent.getChildren().add(new ImageComponent(getImage(Objects.requireNonNull(TitheFarmMaterial.getSeedForLevel()).getFruitId(), TitheFarmingScript.fruits)));

        if (plugin.config.enableOverlay()) {
            for (TitheFarmPlant plant : TitheFarmingScript.plants) {
                if (plant == null || plant.getGameObject() == null) continue;
                final Polygon polygon = Perspective.getCanvasTilePoly(Microbot.getClient(), plant.getGameObject().getLocalLocation());

                if (polygon != null) {
                    OverlayUtil.renderPolygon(graphics, polygon, Color.CYAN);
                }

                final LocalPoint localLocation = LocalPoint.fromWorld(Microbot.getClient(), plant.getGameObject().getWorldLocation());

                if (localLocation == null) {
                    continue;
                }
            }
        }

        if (plugin.config.enableDebugging()) {
            for (TitheFarmPlant plant : TitheFarmingScript.plants) {
                if (plant == null || plant.getGameObject() == null) continue;

                Point textLocation = Perspective.getCanvasTextLocation(Microbot.getClient(), graphics, plant.getGameObject().getLocalLocation(), "p: " + plant.isEmptyPatchOrSeedling(), 0);
                if (textLocation != null) {
                    OverlayUtil.renderTextLocation(graphics, textLocation, "plant: " + plant.isEmptyPatchOrSeedling(), Color.green);
                    OverlayUtil.renderTextLocation(graphics, new Point(textLocation.getX(), textLocation.getY() + 20), "harvest: " + plant.isValidToHarvest(), Color.green);
                    OverlayUtil.renderTextLocation(graphics, new Point(textLocation.getX(), textLocation.getY() + 40), "water: " + plant.isValidToWater(), Color.green);
                }
            }
        }
        return super.render(graphics);
    }

    private BufferedImage getImage(int itemID, int amount)
    {
        BufferedImage image = Microbot.getItemManager().getImage(itemID, amount, true);
        return image;
    }
}
