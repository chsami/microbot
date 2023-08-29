package net.runelite.client.plugins.microbot.tutorialisland;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Mocrosoft + "TutorialIsland",
        description = "Microbot tutorialIsland plugin",
        tags = {"TutorialIsland", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class TutorialislandPlugin extends Plugin {
    @Inject
    private TutorialIslandConfig config;
    @Provides
    TutorialIslandConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(TutorialIslandConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private TutorialIslandOverlay exampleOverlay;

    @Inject
    TutorialIslandScript exampleScript;


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(exampleOverlay);
        }
        exampleScript.run(config);
    }

    protected void shutDown() {
        exampleScript.shutdown();
        overlayManager.remove(exampleOverlay);
    }
}
