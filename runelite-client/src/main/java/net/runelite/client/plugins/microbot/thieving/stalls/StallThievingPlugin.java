package net.runelite.client.plugins.microbot.thieving.stalls;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Basm + "Stall Thieving",
        description = "Stall Thieving",
        tags = {"thieving", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class StallThievingPlugin extends Plugin {
    @Inject
    private StallThievingConfig config;
    @Provides
    StallThievingConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(StallThievingConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private StallThievingOverlay overlay;

    @Inject
    StallThievingScript script;


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
