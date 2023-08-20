package net.runelite.client.plugins.ogPlugins.ogfiremaking;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = "OG Firemaking",
        description = "OG Firemaking plugin",
        tags = {"og","firemaking", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class firemakingPlugin extends Plugin {
    @Inject
    private firemakingConfig config;
    @Provides
    firemakingConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(firemakingConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private firemakingOverlay firemakingOverlay;

    @Inject
    firemakingScript firemakingScript;
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
