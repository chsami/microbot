package net.runelite.client.plugins.envisionplugins.breakhandler;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.image.BufferedImage;

@PluginDescriptor(
        name = PluginDescriptor.Envision + "Break Handler",
        description = "Break Handler for Microbot",
        tags = {"microbot", "utility"},
        enabledByDefault = false
)
@Slf4j
public class BreakHandlerPlugin extends Plugin {

    @Inject
    BreakHandlerConfig config;

    @Inject
    private ClientToolbar clientToolbar;

    @Provides
    BreakHandlerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(BreakHandlerConfig.class);
    }

    private BreakHandlerPanel breakHandlerPanel;
    private NavigationButton navButton;


    @Override
    protected void startUp() throws Exception {
        final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "breakhandler_watch.png");

        breakHandlerPanel = injector.getInstance(BreakHandlerPanel.class);

        navButton = NavigationButton.builder()
                .tooltip("Microbot Break Handler")
                .icon(icon)
                .panel(breakHandlerPanel)
                .priority(1)
                .build();

        clientToolbar.addNavigation(navButton);
    }

    @Override
    protected void shutDown() throws Exception{
        clientToolbar.removeNavigation(navButton);
    }

}
