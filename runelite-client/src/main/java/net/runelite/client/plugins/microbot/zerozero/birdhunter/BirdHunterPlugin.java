package net.runelite.client.plugins.microbot.zerozero.birdhunter;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.zerozero + "Bird Hunter",
        description = "Hunts birds",
        tags = {"hunting", "mntn", "bird", "skilling"},
        enabledByDefault = false
)
@Slf4j
public class BirdHunterPlugin extends Plugin {
    @Inject
    private BirdHunterConfig config;
    @Provides
    BirdHunterConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(BirdHunterConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private BirdHunterOverlay birdHunterOverlay;

    @Inject
    BirdHunterScript birdHunterScript;


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(birdHunterOverlay);
        }
        birdHunterScript.run(config);
    }

    protected void shutDown() {
        birdHunterScript.shutdown();
        overlayManager.remove(birdHunterOverlay);
    }
}
