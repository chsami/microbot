package net.runelite.client.plugins.microbot.wintertodt;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Mocrosoft + "Wintertodt",
        description = "Wintertodt Minigame Bot",
        tags = {"Wintertodt", "microbot", "firemaking", "minigame"},
        enabledByDefault = false
)
@Slf4j
public class MWintertodtPlugin extends Plugin {
    @Inject
    private MWintertodtConfig config;
    @Provides
    MWintertodtConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(MWintertodtConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private MWintertodtOverlay wintertodtOverlay;

    @Inject
    MWintertodtScript wintertodtScript;


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(wintertodtOverlay);
        }
        wintertodtScript.run(config);
    }

    protected void shutDown() {
        wintertodtScript.shutdown();
        overlayManager.remove(wintertodtOverlay);
    }

    @Subscribe
    public void onHitsplatApplied(HitsplatApplied hitsplatApplied)
    {
        wintertodtScript.onHitsplatApplied(hitsplatApplied);
    }
}
