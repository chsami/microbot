package net.runelite.client.plugins.microbot.qualityoflife;

import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.qualityoflife.scripts.WintertodtScript;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.npc.Rs2NpcManager;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.ProgressBarComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static net.runelite.client.plugins.microbot.Microbot.log;

public class QoLOverlay extends OverlayPanel {
    QoLConfig config;
    QoLPlugin plugin;

    @Inject
    QoLOverlay(QoLPlugin plugin, QoLConfig config) {
        super(plugin);
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        //setPosition(OverlayPosition.TOP_LEFT);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        setNaughty();
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            //graphics.setFont(new Font("Arial", Font.PLAIN, 14));
            if (WintertodtScript.isInWintertodtRegion()) {
                setPosition(OverlayPosition.TOP_LEFT);
                final ProgressBarComponent wintertodtHealthBar = getWintertodtHealthBar();

                panelComponent.setPreferredSize(new Dimension(500, 300));
                panelComponent.setBackgroundColor(new Color(93, 72, 41, 255));
                panelComponent.setBorder(new Rectangle(5, 5, 5, 5));
                panelComponent.getChildren().add(TitleComponent.builder()
                        .text("QoL Plugin")
                        .color(Color.GREEN)
                        .build());

                panelComponent.getChildren().add(LineComponent.builder().build());

                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Was Interrupted: " + config.interrupted())
                        .right("Stored Action: " + config.wintertodtActions().getAction())
                        .build());
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Unlit Brazier Distance: ").right((WintertodtScript.unlitBrazier == null) ? "N/A" : String.valueOf(WintertodtScript.unlitBrazier.getWorldLocation().distanceTo2D(Microbot.getClient().getLocalPlayer().getWorldLocation())))
                        .build());
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Broken Brazier Distance: ").right((WintertodtScript.brokenBrazier == null) ? "N/A" : String.valueOf(WintertodtScript.brokenBrazier.getWorldLocation().distanceTo2D(Microbot.getClient().getLocalPlayer().getWorldLocation())))
                        .build());
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Pyromancer Distance: ").right((WintertodtScript.pyromancer == null) ? "N/A" : String.valueOf(WintertodtScript.pyromancer.getWorldLocation().distanceTo2D(Microbot.getClient().getLocalPlayer().getWorldLocation())))
                        .build());
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Incapitated Pyromancer Distance: ").right((WintertodtScript.incapitatedPyromancer == null) ? "N/A" : String.valueOf(WintertodtScript.incapitatedPyromancer.getWorldLocation().distanceTo2D(Microbot.getClient().getLocalPlayer().getWorldLocation())))
                        .build());
                panelComponent.getChildren().add(LineComponent.builder().build());
                panelComponent.getChildren().add(wintertodtHealthBar);
            }

            if (config.renderMaxHitOverlay())
                renderNpcs(graphics);

        } catch (Exception ex) {
            log("Error in QoLOverlay: " + ex.getMessage());
        }
        return super.render(graphics);
    }

    private static @NotNull ProgressBarComponent getWintertodtHealthBar() {
        final ProgressBarComponent wintertodtHealthBar = new ProgressBarComponent();
        wintertodtHealthBar.setBackgroundColor(new Color(255, 0, 0, 255));
        wintertodtHealthBar.setForegroundColor(new Color(37, 196, 37, 255));
        wintertodtHealthBar.setMaximum(100);
        wintertodtHealthBar.setValue(WintertodtScript.wintertodtHp);
        wintertodtHealthBar.setPreferredSize(new Dimension(500, 40));
        wintertodtHealthBar.setLabelDisplayMode(ProgressBarComponent.LabelDisplayMode.PERCENTAGE);
        return wintertodtHealthBar;
    }

    private void renderNpcs(Graphics2D graphics) {
        List<NPC> npcs;
        npcs = Microbot.getClientThread().runOnClientThread(() -> Rs2Npc.getNpcs()
                .filter(npc -> npc.getName() != null)
                .collect(Collectors.toList()));
        for (NPC npc : npcs) {
            if (npc != null && npc.getCanvasTilePoly() != null) {
                try {
                    String text = ("Max Hit: " + Objects.requireNonNull(Rs2NpcManager.getStats(npc.getId())).getMaxHit());


                    //npc.setOverheadText(text);
                    LocalPoint lp = npc.getLocalLocation();
                    Point textLocation = Perspective.getCanvasTextLocation(Microbot.getClient(), graphics, lp, text, npc.getLogicalHeight());
                    if (textLocation == null) {
                        continue;
                    }
                    textLocation = new Point(textLocation.getX(), textLocation.getY() - 25);

                    OverlayUtil.renderTextLocation(graphics, textLocation, text, Color.YELLOW);
                } catch (Exception ignored) {
                }

            }
        }
    }
}
