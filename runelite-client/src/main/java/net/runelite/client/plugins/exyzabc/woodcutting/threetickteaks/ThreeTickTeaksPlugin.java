package net.runelite.client.plugins.exyzabc.woodcutting.threetickteaks;

import com.google.inject.Provides;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Exyzabc + " 3T Teaks",
        description = "Performs 3T Teaks",
        tags = { "woodcutting", "microbot", "skills", "exyzabc", "exyzabc" },
        enabledByDefault = false
)
public class ThreeTickTeaksPlugin extends Plugin {
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ThreeTickTeaksConfig config;
    @Inject
    private ThreeTickTeaksOverlay threeTickTeaksOverlay;
    @Inject
    private ThreeTickTeaksScript threeTickTeaksScript;

    @Provides
    ThreeTickTeaksConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ThreeTickTeaksConfig.class);
    }

    @Override
    protected void startUp() throws AWTException {
        overlayManager.add(threeTickTeaksOverlay);
        threeTickTeaksScript.run();
    }

    protected void shutDown() {
        threeTickTeaksScript.shutdown();
        overlayManager.remove(threeTickTeaksOverlay);
    }
}
