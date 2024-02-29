package net.runelite.client.plugins.microbot.vorkath;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.playerassist.combat.PrayerPotionScript;
import net.runelite.client.plugins.microbot.util.prayer.Prayer;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Mocrosoft + "Vorkath",
        description = "Microbot Vorkath plugin",
        tags = {"vorkath", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class VorkathPlugin extends Plugin {
    @Inject
    private VorkathConfig config;
    @Provides
    VorkathConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(VorkathConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private VorkathOverlay exampleOverlay;

    @Inject
    VorkathScript vorkathScript;

    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(exampleOverlay);
        }
        vorkathScript.run(config);
    }

    protected void shutDown() {
        vorkathScript.shutdown();
        overlayManager.remove(exampleOverlay);
    }
}
