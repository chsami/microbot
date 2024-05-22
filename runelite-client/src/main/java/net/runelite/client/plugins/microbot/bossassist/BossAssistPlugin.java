package net.runelite.client.plugins.microbot.bossassist;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Pumster + "Boss Assist",
        description = "Helps u defeat bosses legit",
        tags = {"pumster", "assist", "bossing"},
        enabledByDefault = false
)
@Slf4j
public class BossAssistPlugin extends Plugin {
    @Inject
    public BossAssistConfig config;
    @Provides
    BossAssistConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(BossAssistConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private BossAssistOverlay exampleOverlay;

    @Inject
    BossAssistScript bossAssistScript;


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(exampleOverlay);
        }
        bossAssistScript.run(config);
    }

    protected void shutDown() {
        bossAssistScript.shutdown();
        overlayManager.remove(exampleOverlay);
    }
}
