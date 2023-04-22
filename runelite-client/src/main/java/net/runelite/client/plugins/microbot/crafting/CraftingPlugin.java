package net.runelite.client.plugins.microbot.crafting;

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
        name = "MicroCrafting",
        description = "Microbot crafting plugin",
        tags = {"skilling", "microbot", "crafting"}
)
@Slf4j
public class CraftingPlugin extends Plugin {

    @Inject
    private CraftingConfig config;

    @Provides
    CraftingConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(CraftingConfig.class);
    }

    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private CraftingOverlay constructionOverlay;

    private CraftingScript craftingScript = new CraftingScript();

    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(constructionOverlay);
        }
        craftingScript.run(config);
    }

    protected void shutDown() {
        craftingScript.shutdown();
        overlayManager.remove(constructionOverlay);
    }
}
