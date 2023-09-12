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
        name = PluginDescriptor.OG + "Firemaking",
        description = "OG Firemaking plugin",
        tags = {"og","firemaking", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class ogFiremakingPlugin extends Plugin {
    @Inject
    private ogFiremakingConfig config;
    @Provides
    ogFiremakingConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ogFiremakingConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ogFiremakingOverlay ogFiremakingOverlay;

    @Inject
    ogFiremakingScript ogFiremakingScript;
    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(ogFiremakingOverlay);
        }
        ogFiremakingScript.run(config);
    }

    protected void shutDown() {
        ogFiremakingScript.shutdown();
        overlayManager.remove(ogFiremakingOverlay);
    }
}
