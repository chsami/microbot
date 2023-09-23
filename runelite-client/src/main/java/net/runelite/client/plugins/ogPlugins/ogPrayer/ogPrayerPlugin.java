package net.runelite.client.plugins.ogPlugins.ogPrayer;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.OG + "Prayer",
        description = "OG Prayer plugin",
        tags = {"og","chaos","alter", "microbot"},

        enabledByDefault = false
)
@Slf4j
public class ogPrayerPlugin extends Plugin {
    @Inject
    private ogPrayerConfig config;
    @Provides
    ogPrayerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ogPrayerConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private net.runelite.client.plugins.ogPlugins.ogPrayer.ogPrayerOverlay ogPrayerOverlay;

    @Inject
    net.runelite.client.plugins.ogPlugins.ogPrayer.ogPrayerScript ogPrayerScript;

    @Subscribe
    public void onGameTick(GameTick gameTick){ ogPrayerScript.onGameTick(gameTick); }


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(ogPrayerOverlay);
        }
        ogPrayerScript.run(config);
    }

    protected void shutDown() {
        ogPrayerScript.shutdown();
        overlayManager.remove(ogPrayerOverlay);
    }
}
