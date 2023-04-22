package net.runelite.client.plugins.microbot.construction;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = "MicroConstruction",
        description = "Microbot construction plugin",
        tags = {"skilling", "microbot", "construction"}
)
@Slf4j
public class ConstructionPlugin extends Plugin {

    @Inject
    private ConstructionConfig config;

    @Provides
    ConstructionConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ConstructionConfig.class);
    }

    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ConstructionOverlay constructionOverlay;

    private ConstructionScript constructionScript = new ConstructionScript();

    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(constructionOverlay);
        }
        constructionScript.run(config);
    }

    protected void shutDown() {
        constructionScript.shutdown();
        overlayManager.remove(constructionOverlay);
    }
}
