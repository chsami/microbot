package net.runelite.client.plugins.microbot.gabplugs.glassmake;

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
        name = PluginDescriptor.Gabulhas + "Glass Make",
        description = "",
        tags = {"GabulhasGlassMake", "Gabulhas"},
        enabledByDefault = false
)
@Slf4j
public class GabulhasGlassMakePlugin extends Plugin {
    @Inject
    private GabulhasGlassMakeConfig config;
    @Provides
    GabulhasGlassMakeConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(GabulhasGlassMakeConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private PluginManager pluginManager;
    @Inject
    private GabulhasGlassMakeOverlay gabulhasGlassMakeOverlay;

    @Inject
    GabulhasGlassMakeScript gabulhasGlassMakeScript;


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(gabulhasGlassMakeOverlay);
        }
        gabulhasGlassMakeScript.run(config);
        GabulhasGlassMakeInfo.botStatus = GabulhasGlassMakeInfo.states.Starting;
    }

    protected void shutDown() {
        gabulhasGlassMakeScript.shutdown();
        overlayManager.remove(gabulhasGlassMakeOverlay);
    }
}
