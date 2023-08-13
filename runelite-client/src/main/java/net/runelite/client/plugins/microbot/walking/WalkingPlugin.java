package net.runelite.client.plugins.microbot.walking;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = "Micro Walker",
        description = "Microbot example plugin",
        tags = {"walker", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class WalkingPlugin extends Plugin {
    @Inject
    private WalkingConfig config;
    @Provides
    WalkingConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(WalkingConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private WalkingOverlay exampleOverlay;

    @Inject
    WalkingScript exampleScript;


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
