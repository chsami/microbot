package net.runelite.client.plugins.exyzabc.cooking.onetickcooker;

import com.google.inject.Provides;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Exyzabc + " 1T Cooker",
        description = "Performs 1 Tick Cooking",
        tags = { "cooking", "microbot", "skills", "exyzabc", "exyzabc" },
        enabledByDefault = false
)
public class OneTickCookerPlugin extends Plugin {
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private OneTickCookerConfig config;
    @Inject
    private OneTickCookerOverlay oneTickCookerOverlay;
    @Inject
    private OneTickCookerScript oneTickCookerScript;

    @Provides
    OneTickCookerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(OneTickCookerConfig.class);
    }

    @Override
    protected void startUp() throws AWTException {
        overlayManager.add(oneTickCookerOverlay);
        oneTickCookerScript.run();
    }

    protected void shutDown() {
        oneTickCookerScript.shutdown();
        overlayManager.remove(oneTickCookerOverlay);
    }
}
