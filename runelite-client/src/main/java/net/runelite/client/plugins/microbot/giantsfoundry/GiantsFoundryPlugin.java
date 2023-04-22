package net.runelite.client.plugins.microbot.giantsfoundry;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = "MicroGiantsFoundry",
        description = "Microbot giants foundry plugin",
        tags = {"minigame", "microbot", "smithing"}
)
@Slf4j
public class GiantsFoundryPlugin extends Plugin {

    @Inject
    private GiantsFoundryConfig config;

    @Provides
    GiantsFoundryConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(GiantsFoundryConfig.class);
    }

    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private GiantsFoundryOverlay giantsFoundryOverlay;

    private GiantsFoundryScript giantsFoundryScript = new GiantsFoundryScript();

    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(giantsFoundryOverlay);
        }
        giantsFoundryScript.run(config);
    }

    protected void shutDown() {
        giantsFoundryScript.shutdown();
        overlayManager.remove(giantsFoundryOverlay);
    }
}
