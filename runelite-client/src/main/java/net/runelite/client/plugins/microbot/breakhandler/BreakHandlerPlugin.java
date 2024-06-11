package net.runelite.client.plugins.microbot.breakhandler;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Default + "Example",
        description = "Microbot example plugin",
        tags = {"example", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class BreakHandlerPlugin extends Plugin {
    @Inject
    private BreakHandlerConfig config;
    @Provides
    BreakHandlerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(BreakHandlerConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private BreakHandlerOverlay exampleOverlay;

    @Inject
    BreakHandlerScript exampleScript;


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(exampleOverlay);
        }
        exampleScript.run(config);
    }

    protected void shutDown() {
        exampleScript.shutdown();
        overlayManager.remove(exampleOverlay);
    }
}
