package net.runelite.client.plugins.microbot.autobanker;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Mocrosoft + "AutoBanker",
        description = "Automatically banks items and walks back to the original location",
        tags = {"banking", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class AutoBankerPlugin extends Plugin {
    @Inject
    private AutoBankerConfig config;
    @Provides
    AutoBankerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoBankerConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private AutoBankerOverlay autoBankerOverlay;

    @Inject
    AutoBankerScript autoBankerScript;


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(autoBankerOverlay);
        }
        autoBankerScript.run(config);
    }

    protected void shutDown() {
        autoBankerScript.shutdown();
        overlayManager.remove(autoBankerOverlay);
    }
}
