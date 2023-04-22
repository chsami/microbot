package net.runelite.client.plugins.microbot.fletching;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.MicrobotConfig;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = "MicroFletcher",
        description = "Microbot fletching plugin",
        tags = {"fletching", "microbot", "skills"}
)
@Slf4j
public class FletchingPlugin extends Plugin {

    @Inject
    private FletchingConfig config;

    @Provides
    FletchingConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(FletchingConfig.class);
    }

    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private FletchingOverlay fletchingOverlay;

    private FletchingScript fletchingScript = new FletchingScript();

    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(fletchingOverlay);
        }
        fletchingScript.run(config);
    }

    protected void shutDown() {
        fletchingScript.shutdown();
        overlayManager.remove(fletchingOverlay);
    }
}
