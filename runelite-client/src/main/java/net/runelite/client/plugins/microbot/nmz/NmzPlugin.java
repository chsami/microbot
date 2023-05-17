package net.runelite.client.plugins.microbot.nmz;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = "Micro Nmz",
        description = "Microbot NMZ",
        tags = {"nmz", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class NmzPlugin extends Plugin {
    @Inject
    private NmzConfig config;
    @Provides
    NmzConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(NmzConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private NmzOverlay nmzOverlay;

    @Inject
    NmzScript nmzScript;


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(nmzOverlay);
        }
        nmzScript.run(config);
    }

    protected void shutDown() {
        nmzScript.shutdown();
        overlayManager.remove(nmzOverlay);
    }
}
