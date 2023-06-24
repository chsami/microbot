package net.runelite.client.plugins.microbot.driftnet;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = "Micro Drifnet",
        description = "Microbot drifnet plugin",
        tags = {"fishing", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class DriftnetPlugin extends Plugin {
    @Inject
    private DriftnetConfig config;
    @Provides
    DriftnetConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(DriftnetConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private DriftnetOverlay driftnetOverlay;

    @Inject
    DriftnetScript driftnetScript;


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(driftnetOverlay);
        }
        driftnetScript.run(config);
    }

    protected void shutDown() {
        driftnetScript.shutdown();
        overlayManager.remove(driftnetOverlay);
    }
}
