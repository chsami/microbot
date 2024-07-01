package net.runelite.client.plugins.microbot.eventdismiss;

import javax.inject.Inject;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Default + "Event Dismiss</html>",
        description = "Random Event Dismisser",
        tags = {"random", "events", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class EventDismissPlugin extends Plugin {
    @Inject
    EventDismissScript eventDismissScript;
    @Inject
    private ConfigManager configManager;
    @Inject
    private EventDismissConfig config;

    @Provides
    EventDismissConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(EventDismissConfig.class);
    }

    @Override
    protected void startUp() throws AWTException {
        eventDismissScript.run(config);
    }

    protected void shutDown() {
        eventDismissScript.shutdown();
    }
}
