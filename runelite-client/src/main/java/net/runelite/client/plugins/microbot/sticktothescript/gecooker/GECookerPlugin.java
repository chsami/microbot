package net.runelite.client.plugins.microbot.sticktothescript.gecooker;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.StickToTheScript + "GE Cooker",
        description = "StickToTheScript\'s GE cooking plugin",
        tags = {"StickToTheScript", "STTS", "cooker", "cooking"},
        enabledByDefault = false
)
@Slf4j
public class GECookerPlugin extends Plugin {
    @Inject
    private GECookerConfig config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private GECookerOverlay overlay;
    @Inject
    GECookerScript script;

    @Provides
    GECookerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(GECookerConfig.class);
    }

    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(overlay);
        }

        script.run(config);
    }

    protected void shutDown() {
        script.shutdown();
        overlayManager.remove(overlay);
    }
}
