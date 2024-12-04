package net.runelite.client.plugins.microbot.doughmaker;

import net.runelite.api.GameObject;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.*;

import static net.runelite.client.plugins.microbot.playerassist.combat.AttackNpcScript.attackableNpcs;
import static net.runelite.client.ui.overlay.OverlayUtil.renderPolygon;

public class DoughMakerOverlay extends OverlayPanel {
    private static final Color WHITE_TRANSLUCENT = new Color(0, 255, 255, 127);
    private static final Color RED_TRANSLUCENT = new Color(255, 0, 0, 127);
    private final ModelOutlineRenderer modelOutlineRenderer;
    private DoughMakerConfig config;

    @Inject
    DoughMakerOverlay(ModelOutlineRenderer modelOutlineRenderer, DoughMakerConfig config) {
        this.modelOutlineRenderer = modelOutlineRenderer;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPriority(OverlayPriority.HIGH);
        setNaughty();
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            // Render wheat field
            LocalPoint lp =  LocalPoint.fromWorld(Microbot.getClient(), new WorldPoint(3141, 3461, 0));
            int fieldRadio = 3;
            if (lp != null) {
                Polygon poly = Perspective.getCanvasTileAreaPoly(Microbot.getClient(), lp, fieldRadio * 2);

                if (poly != null)
                {
                    renderPolygon(graphics, poly, WHITE_TRANSLUCENT);
                }
            }

            GameObject sink = Rs2GameObject.findObject(1763, new WorldPoint(3138, 3449, 0));
            if (sink != null && sink.getCanvasTilePoly() != null) {
                try {
                    graphics.setColor(Color.CYAN);
                    modelOutlineRenderer.drawOutline(sink, 2, Color.RED, 4);
                    graphics.draw(sink.getCanvasTilePoly());
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }

            GameObject basePlantMill = Rs2GameObject.findObject(14960, new WorldPoint(3140, 3449, 0));
            if (basePlantMill != null && basePlantMill.getCanvasTilePoly() != null) {
                try {
                    graphics.setColor(Color.CYAN);
                    modelOutlineRenderer.drawOutline(basePlantMill, 2, Color.RED, 4);
                    graphics.draw(basePlantMill.getCanvasTilePoly());
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }

            GameObject firstFloorStaircase = Rs2GameObject.findObject(2608, new WorldPoint(3144, 3447, 0));
            if (firstFloorStaircase != null && firstFloorStaircase.getCanvasTilePoly() != null) {
                try {
                    graphics.setColor(Color.CYAN);
                    modelOutlineRenderer.drawOutline(firstFloorStaircase, 2, Color.RED, 4);
                    graphics.draw(firstFloorStaircase.getCanvasTilePoly());
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }

            GameObject secondFloorStaircase = Rs2GameObject.findObject(2609, new WorldPoint(3144, 3447, 1));
            if (secondFloorStaircase != null && secondFloorStaircase.getCanvasTilePoly() != null) {
                try {
                    graphics.setColor(Color.CYAN);
                    modelOutlineRenderer.drawOutline(secondFloorStaircase, 2, Color.RED, 4);
                    graphics.draw(secondFloorStaircase.getCanvasTilePoly());
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }

            GameObject thirdFloorStaircase = Rs2GameObject.findObject(2610, new WorldPoint(3144, 3447, 2));
            if (thirdFloorStaircase != null && thirdFloorStaircase.getCanvasTilePoly() != null) {
                try {
                    graphics.setColor(Color.CYAN);
                    modelOutlineRenderer.drawOutline(thirdFloorStaircase, 2, Color.RED, 4);
                    graphics.draw(thirdFloorStaircase.getCanvasTilePoly());
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }

            GameObject hopper = Rs2GameObject.findObject(2586, new WorldPoint(3142, 3452, 2));
            if (hopper != null && hopper.getCanvasTilePoly() != null) {
                try {
                    graphics.setColor(Color.CYAN);
                    modelOutlineRenderer.drawOutline(hopper, 2, Color.RED, 4);
                    graphics.draw(hopper.getCanvasTilePoly());
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }

            GameObject hopperControls = Rs2GameObject.findObject(2607, new WorldPoint(3141, 3453, 2));
            if (hopperControls != null && hopperControls.getCanvasTilePoly() != null) {
                try {
                    graphics.setColor(Color.CYAN);
                    modelOutlineRenderer.drawOutline(hopperControls, 2, Color.RED, 4);
                    graphics.draw(hopperControls.getCanvasTilePoly());
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }

            panelComponent.setPreferredSize(new Dimension(200, 300));
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Dough Maker" + DoughMakerPlugin.version)
                    .color(Color.GREEN)
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
