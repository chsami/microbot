package net.runelite.client.plugins.hoseaplugins.lucid1tkarambwans;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class Lucid1TKarambwansPanelOverlay extends OverlayPanel
{
    private final Client client;
    private final Lucid1TKarambwansPlugin plugin;
    private final Lucid1TKarambwansConfig config;

    @Inject
    private Lucid1TKarambwansPanelOverlay(Client client, Lucid1TKarambwansPlugin plugin, Lucid1TKarambwansConfig config)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics2D)
    {
        if (!config.showOverlay())
        {
            return null;
        }

        panelComponent.setPreferredSize(new Dimension(300, 150));
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("Lucid 1Tick Karambwans")
                .color(Color.WHITE)
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Run State:")
                .leftColor(Color.WHITE)
                .right(plugin.isRunning() ? "Running" : "Stopped")
                .build());

        int ticks = plugin.getBreakTicks() == 0 ? Math.max(0, plugin.getNextBreakTick() - client.getTickCount()) : plugin.getBreakTicks();
        panelComponent.getChildren().add(LineComponent.builder()
                .left(plugin.getBreakTicks() == 0 ? "Next Break In:" : "Breaking For:")
                .leftColor(Color.WHITE)
                .right(ticks + (ticks == 1 ? " Tick" : " Ticks"))
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Ticks Running:")
                .leftColor(Color.WHITE)
                .right(plugin.getTicksRunning() + (plugin.getTicksRunning() == 1 ? " Tick" : " Ticks"))
                .build());

        return super.render(graphics2D);
    }
}
