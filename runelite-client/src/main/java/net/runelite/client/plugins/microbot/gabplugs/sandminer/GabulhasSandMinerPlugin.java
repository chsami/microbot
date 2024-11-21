package net.runelite.client.plugins.microbot.gabplugs.sandminer;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Gabulhas + "Sandstone miner",
        description = "",
        tags = {"GabulhasSandMiner", "Gabulhas"},
        enabledByDefault = false
)
@Slf4j
public class GabulhasSandMinerPlugin extends Plugin {
    @Inject
    private GabulhasSandMinerConfig config;
    @Provides
    GabulhasSandMinerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(GabulhasSandMinerConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private PluginManager pluginManager;
    @Inject
    private GabulhasSandMinerOverlay gabulhasSandMinerOverlay;

    @Inject
    GabulhasSandMinerScript gabulhasSandMinerScript;


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(gabulhasSandMinerOverlay);
        }
        gabulhasSandMinerScript.run(config);
        GabulhasSandMinerInfo.botStatus = config.STARTINGSTATE();
    }

    protected void shutDown() {
        gabulhasSandMinerScript.shutdown();
        overlayManager.remove(gabulhasSandMinerOverlay);
    }
}
