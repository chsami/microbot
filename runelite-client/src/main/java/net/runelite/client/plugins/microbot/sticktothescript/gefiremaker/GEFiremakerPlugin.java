package net.runelite.client.plugins.microbot.sticktothescript.gefiremaker;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.StickToTheScript + "GE Firemaker",
        description = "StickToTheScript\'s GE firemaking plugin",
        tags = {"StickToTheScript", "STTS", "firemaker", "firemaking"},
        enabledByDefault = false
)
@Slf4j
public class GEFiremakerPlugin extends Plugin {
    @Inject
    private GEFiremakerConfig config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private GEFiremakerOverlay overlay;

    @Inject
    GEFiremakerScript script;

    @Provides
    GEFiremakerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(GEFiremakerConfig.class);
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
