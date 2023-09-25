package net.runelite.client.plugins.ogPlugins.ogConstruction;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameObject;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.OG + "Construction",
        description = "OG Construction plugin",
        tags = {"og","construction", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class ogConstPlugin extends Plugin {
    @Inject
    private ogConstConfig config;
    @Provides
    ogConstConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ogConstConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ogConstOverlay ogConstOverlay;

    @Inject
    ogConstScript ogConstScript;

    @Subscribe
    private void onGameObjectSpawned(GameObjectSpawned event) {
        ogConstScript.onGameObjectSpawned(event);
    }

    @Subscribe void onGameTick(GameTick gameTick){ogConstScript.onGameTick(gameTick);}


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(ogConstOverlay);
        }
        ogConstScript.run(config);
    }

    protected void shutDown() {
        ogConstScript.shutdown();
        overlayManager.remove(ogConstOverlay);
    }
}
