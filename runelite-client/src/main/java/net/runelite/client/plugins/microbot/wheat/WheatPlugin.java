package net.runelite.client.plugins.microbot.wheat;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.ActorDeath;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;


@PluginDescriptor(
        name = PluginDescriptor.SaCo + "Wheat",
        description = "Microbot Wheat",
        tags = {"wheat", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class WheatPlugin extends Plugin {
    @Inject
    private WheatConfig config;

    @Provides
    WheatConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(WheatConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private WheatOverlay wheatOverlay;

    @Inject
    WheatScript wheatScript;

    @Override
    protected void startUp() throws AWTException {
        WheatScript.profit = 0;
        if (overlayManager != null) {
            overlayManager.add(wheatOverlay);
        }
        wheatScript.run(config);
    }

    protected void shutDown() {
        wheatScript.shutdown();
        overlayManager.remove(wheatOverlay);
    }

    @Subscribe
    public void onActorDeath(ActorDeath actorDeath) {
        if (config.stopAfterDeath() && actorDeath.getActor() == Microbot.getClient().getLocalPlayer()) {
            wheatScript.logout();
            shutDown();
        }
    }
}
