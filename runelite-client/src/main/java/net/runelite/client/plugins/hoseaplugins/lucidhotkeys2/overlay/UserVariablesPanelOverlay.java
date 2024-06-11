package net.runelite.client.plugins.hoseaplugins.lucidhotkeys2.overlay;

import net.runelite.client.plugins.hoseaplugins.lucidhotkeys2.LucidHotkeys2Config;
import net.runelite.client.plugins.hoseaplugins.lucidhotkeys2.LucidHotkeys2Plugin;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.util.Map;

public class UserVariablesPanelOverlay extends OverlayPanel
{
    private final Client client;
    private final LucidHotkeys2Plugin plugin;
    private final LucidHotkeys2Config config;

    @Inject
    private UserVariablesPanelOverlay(Client client, LucidHotkeys2Plugin plugin, LucidHotkeys2Config config)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics2D)
    {
        if (!config.customVarPanel())
        {
            return null;
        }

        panelComponent.setPreferredSize(new Dimension(300, 150));
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("Lucid Hotkeys 2 User Vars")
                .color(Color.WHITE)
                .build());

        if (plugin.getUserVariables().isEmpty())
        {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("No Vars Set.")
                    .leftColor(Color.WHITE)
                    .right("")
                    .build());
        }
        else
        {
            for (Map.Entry<String, String> userVariable : plugin.getUserVariables().entrySet())
            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left(userVariable.getKey())
                        .leftColor(Color.WHITE)
                        .right(userVariable.getValue())
                        .build());
            }
        }

        return super.render(graphics2D);
    }
}