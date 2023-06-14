package net.runelite.client.plugins.microbot.cooking;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = "Micro Cooking",
        description = "Microbot cooking plugin",
        tags = {"cooking", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class CookingPlugin extends Plugin {
    @Inject
    private CookingConfig config;
    @Provides
    CookingConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(CookingConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private CookingOverlay exampleOverlay;

    @Inject
    CookingScript exampleScript;


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
