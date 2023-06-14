package net.runelite.client.plugins.microbot.example;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = "Micro Firemaking",
        description = "Microbot Firemaking plugin",
        tags = {"firemaking", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class FiremakingPlugin extends Plugin {
    @Inject
    private FiremakingConfig config;
    @Provides
    FiremakingConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(FiremakingConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private FiremakingOverlay exampleOverlay;

    @Inject
    FiremakingScript exampleScript;


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
