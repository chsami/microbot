package net.runelite.client.plugins.microbot.fishing.eel;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;

@PluginDescriptor(
        name = PluginDescriptor.See1Duck + " Eel Fishing",
        description = "Automates fishing for Infernal and Sacred eels",
        tags = {"fishing", "eel", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class EelFishingPlugin extends Plugin {

    @Inject
    private EelFishingConfig config;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private EelFishingScript eelFishingScript;

    @Inject
    private EelFishingOverlay eelFishingOverlay;

    @Provides
    EelFishingConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(EelFishingConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        log.info("Eel Fishing Plugin started!");
        overlayManager.add(eelFishingOverlay);
        eelFishingScript.run(config);
    }

    @Override
    protected void shutDown() throws Exception {
        log.info("Eel Fishing Plugin stopped!");
        eelFishingScript.shutdown();
        overlayManager.remove(eelFishingOverlay);
    }
}
