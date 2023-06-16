package net.runelite.client.plugins.microbot.firemaking;

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
        description = "Microbot firemaking plugin",
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
    private FiremakingOverlay firemakingOverlay;

    @Inject
    FiremakingScript firemakingScript;


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(firemakingOverlay);
        }
        firemakingScript.run(config);
    }

    protected void shutDown() {
        firemakingScript.shutdown();
        overlayManager.remove(firemakingOverlay);
    }
}
