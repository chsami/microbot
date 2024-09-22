package net.runelite.client.plugins.microbot.DesolesticePlugins.Smelting;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Desolestice + "Auto Smelting2",
        description = "Smelt ores/coal into bars",
        tags = {"smithing", "smelting", "microbot", "skilling"},
        enabledByDefault = false
)
@Slf4j
public class AutoSmeltingPlugin2 extends Plugin {
    @Inject
    private AutoSmeltingConfig2 config;
    @Provides
    AutoSmeltingConfig2 provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoSmeltingConfig2.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private AutoSmeltingOverlay2 autoSmeltingOverlay;

    @Inject
    AutoSmeltingScript2 autoSmeltingScript;

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
