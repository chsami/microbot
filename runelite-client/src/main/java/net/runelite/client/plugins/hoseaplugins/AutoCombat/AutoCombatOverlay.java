package net.runelite.client.plugins.hoseaplugins.AutoCombat;


import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.NPCs;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.TileObjects;
import com.google.common.base.Strings;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.lang.model.type.ArrayType;
import javax.sound.sampled.Line;
import java.awt.*;
import java.util.Arrays;
import java.util.Optional;

public class AutoCombatOverlay extends Overlay {

    private final PanelComponent panelComponent = new PanelComponent();
    private final PanelComponent slPanel = new PanelComponent();
    private final Client client;
    private final AutoCombatPlugin plugin;

    @Inject
    private AutoCombatOverlay(Client client, AutoCombatPlugin plugin) {
        this.client = client;
        this.plugin = plugin;
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setDragTargetable(true);

    }

    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.getChildren().clear();
        slPanel.getChildren().clear();

        LineComponent started = buildLine("Started: ", String.valueOf(plugin.started));
        LineComponent timeout = buildLine("Timeout: ", String.valueOf(plugin.timeout));
        LineComponent idleTicks = buildLine("Idle Ticks: ", String.valueOf(plugin.idleTicks));
        LineComponent lootQ = buildLine("Loot Q: ", String.valueOf(plugin.lootQueue.size()));



        panelComponent.getChildren().addAll(Arrays.asList(started, timeout, idleTicks, lootQ));
        if (client.getLocalPlayer().getInteracting() != null) {
            Actor intr = plugin.player.getInteracting();
            LineComponent healthRatio = buildLine("Ratio/Scale: ",
                    intr.getHealthRatio() + "/" + intr.getHealthScale());
            panelComponent.getChildren().add(healthRatio);
        }
        LineComponent isSlayerNpc = buildLine("isSlayerNpc: ", String.valueOf(plugin.isSlayerNpc));
        panelComponent.getChildren().add(isSlayerNpc);

        if (plugin.isSlayerNpc) {
            LineComponent undisturbedName = buildLine("Undist: ", String.valueOf(plugin.slayerInfo.getUndisturbedName()));
            LineComponent disturbAction = buildLine("Disturb: ", String.valueOf(plugin.slayerInfo.getDisturbAction()));
            LineComponent useHp = buildLine("Use HP: ", String.valueOf(plugin.slayerInfo.getUseHp()));
            LineComponent itemName = buildLine("Item Name: ", String.valueOf(plugin.slayerInfo.getItemName()));
            panelComponent.getChildren().addAll(Arrays.asList(undisturbedName, disturbAction, useHp, itemName));
        }

        return panelComponent.render(graphics);
    }

    /**
     * Builds a line component with the given left and right text
     *
     * @param left
     * @param right
     * @return Returns a built line component with White left text and Yellow right text
     */
    private LineComponent buildLine(String left, String right) {
        return LineComponent.builder()
                .left(left)
                .right(right)
                .leftColor(Color.WHITE)
                .rightColor(Color.YELLOW)
                .build();
    }
}
