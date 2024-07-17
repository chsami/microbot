package net.runelite.client.plugins.microbot.magetrainingarena;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Basche + "Mage Training Arena",
        description = "Basche's Mage Training Arena plugin",
        tags = {"basche", "mta", "moneymaking"},
        enabledByDefault = false
)
@Slf4j
public class MageTrainingArenaPlugin extends Plugin {
    @Inject
    private MageTrainingArenaConfig config;
    @Provides
    MageTrainingArenaConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(MageTrainingArenaConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private MageTrainingArenaOverlay overlay;

    @Inject
    MageTrainingArenaScript script;


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
