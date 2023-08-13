package net.runelite.client.plugins.microbot.farming.tithefarm.farming;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = "Micro Farming",
        description = "Microbot farming plugin",
        tags = {"farming", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class FarmingPlugin extends Plugin {
    @Inject
    private FarmingConfig config;
    @Provides
    FarmingConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(FarmingConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private FarmingOverlay exampleOverlay;

    @Inject
    FarmingScript farmingScript;


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(exampleOverlay);
        }
        farmingScript.run(config);
    }

    protected void shutDown() {
        farmingScript.shutdown();
        overlayManager.remove(exampleOverlay);
    }
}
