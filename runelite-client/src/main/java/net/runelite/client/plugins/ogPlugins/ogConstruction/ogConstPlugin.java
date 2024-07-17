package net.runelite.client.plugins.ogPlugins.ogConstruction;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameObject;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
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

    private String moneyBagTopUpAmount;

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

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals("ogConstConfig")) {
            return;
        }

        if (event.getKey().equals("moneyBagTopUpAmount")) {
            this.moneyBagTopUpAmount = config.moneyBagTopUpAmount();
            System.out.println("Updated Money Bag Top-Up Amount: " + this.moneyBagTopUpAmount);
        }
    }


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
