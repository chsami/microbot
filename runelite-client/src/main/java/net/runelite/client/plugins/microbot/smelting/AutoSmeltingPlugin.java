package net.runelite.client.plugins.microbot.smelting;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Vince + "Auto Smelting",
        description = "Smelt ores/coal into bars",
        tags = {"smithing", "smelting", "microbot", "skilling"},
        enabledByDefault = false
)
@Slf4j
public class AutoSmeltingPlugin extends Plugin {
    @Inject
    private AutoSmeltingConfig config;
    @Provides
    AutoSmeltingConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoSmeltingConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private AutoSmeltingOverlay autoSmeltingOverlay;

    @Inject
	AutoSmeltingScript autoSmeltingScript;


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(autoSmeltingOverlay);
        }
        autoSmeltingScript.run(config);
    }

    protected void shutDown() {
        autoSmeltingScript.shutdown();
        overlayManager.remove(autoSmeltingOverlay);
    }
}
