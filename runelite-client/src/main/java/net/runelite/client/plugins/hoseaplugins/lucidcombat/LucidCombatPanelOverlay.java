package net.runelite.client.plugins.hoseaplugins.lucidcombat;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class LucidCombatPanelOverlay extends OverlayPanel
{
    private final Client client;
    private final LucidCombatPlugin plugin;
    private final LucidCombatConfig config;

    @Inject
    private LucidCombatPanelOverlay(Client client, LucidCombatPlugin plugin, LucidCombatConfig config)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics2D)
    {
        if (!config.autocombatOverlay())
        {
            return null;
        }

        panelComponent.setPreferredSize(new Dimension(300, 150));
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("Lucid Combat")
                .color(Color.WHITE)
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Run State:")
                .leftColor(Color.WHITE)
                .right(plugin.isAutoCombatRunning() ? "Running" : "Stopped")
                .build());

        int ticks = Math.max(0, plugin.ticksUntilNextInteraction());
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Next Interaction In:")
                .leftColor(Color.WHITE)
                .right(ticks + (ticks == 1 ? " Tick" : " Ticks"))
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Extra Status:")
                .leftColor(Color.WHITE)
                .right(plugin.getSecondaryStatus())
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Inactive Ticks:")
                .leftColor(Color.WHITE)
                .right(plugin.getInactiveTicks() + "")
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Next Loot Attempt In:")
                .leftColor(Color.WHITE)
                .right(plugin.ticksUntilNextLootAttempt() >= 0 ? plugin.ticksUntilNextLootAttempt() + "" : (config.maxTicksBetweenLooting() + plugin.ticksUntilNextLootAttempt()) + "")
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Restore HP Below:")
                .leftColor(Color.WHITE)
                .right(plugin.getNextHpToRestoreAt() + "")
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Restore Prayer Below:")
                .leftColor(Color.WHITE)
                .right(plugin.getNextPrayerLevelToRestoreAt() + "")
                .build());
        return super.render(graphics2D);
    }
}