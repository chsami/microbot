package net.runelite.client.plugins.microbot.breakhandler;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Default + "BreakHandler",
        description = "Microbot breakhandler",
        tags = {"break", "microbot", "breakhandler"},
        enabledByDefault = false
)
@Slf4j
public class BreakHandlerPlugin extends Plugin {
    @Inject
    BreakHandlerScript breakHandlerScript;
    @Inject
    private BreakHandlerConfig config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private BreakHandlerOverlay breakHandlerOverlay;

    @Provides
    BreakHandlerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(BreakHandlerConfig.class);
    }

    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(breakHandlerOverlay);
        }
        breakHandlerScript.run(config);
    }

    protected void shutDown() {
        breakHandlerScript.shutdown();
        overlayManager.remove(breakHandlerOverlay);
    }

    // on settings change
    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("Breakhandler")) {
            if (event.getKey().equals("UsePlaySchedule")) {
                breakHandlerScript.reset();
            }
        }
    }
}
