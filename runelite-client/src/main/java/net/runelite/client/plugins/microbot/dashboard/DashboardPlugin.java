package net.runelite.client.plugins.microbot.dashboard;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Mocrosoft + "Dashboard",
        description = "Microbot Dashboard Plugin",
        tags = {"dashboard", "microbot"},
        enabledByDefault = false,
        hidden = true
)
@Slf4j
public class DashboardPlugin extends Plugin {
    @Inject
    private DashboardConfig config;

    @Provides
    DashboardConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(DashboardConfig.class);
    }

    @Override
    protected void startUp() throws AWTException {
        DashboardWebSocket.start(config);
    }

    protected void shutDown() {
        DashboardWebSocket.stop();
    }
}
