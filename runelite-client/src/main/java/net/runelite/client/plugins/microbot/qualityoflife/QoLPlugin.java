package net.runelite.client.plugins.microbot.qualityoflife;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.See1Duck + "QoL",
        description = "Quality of Life Plugin",
        tags = {"QoL", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class QoLPlugin extends Plugin {
    public static String version = "1.0.0";
    @Inject
    QoLScript qoLScript;
    @Inject
    private QoLConfig config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private QoLOverlay qoLOverlay;

    @Provides
    QoLConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(QoLConfig.class);
    }

    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(qoLOverlay);
        }
        qoLScript.run(config);
    }

    protected void shutDown() {
        qoLScript.shutdown();
        overlayManager.remove(qoLOverlay);
    }


}
