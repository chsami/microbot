package net.runelite.client.plugins.microbot.hunter;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.hunter.scripts.AutoChinScript;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Mocrosoft + "AutoHunter",
        description = "Microbot AutoHunter plugin",
        tags = {"hunter", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class AutoHunterPlugin extends Plugin {
    @Inject
    private AutoHunterConfig config;
    @Provides
    AutoHunterConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoHunterConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private AutoHunterOverlay autoHunterOverlay;

    @Inject
    AutoChinScript autoChinScript;


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(autoHunterOverlay);
        }
        autoChinScript.run(config);
    }

    protected void shutDown() {
        autoChinScript.shutdown();
        overlayManager.remove(autoHunterOverlay);
    }

}