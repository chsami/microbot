package net.runelite.client.plugins.microbot.derangedarchaeologist;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.vorkath.VorkathOverlay;
import net.runelite.client.plugins.microbot.vorkath.VorkathScript;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Pumster + "Deranged Achaeo",
        description = "Pumsters Deranged Achaeologist plugin",
        tags = {"derangedchaeologist", "pumster"},
        enabledByDefault = false
)

@Slf4j
public class DerangedAchaeologistPlugin extends Plugin {

    @Inject
    Client client;
    @Inject
    private DerangedaAchaeologistConfig config;

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private DerangedAchaeologistOverlay exampleOverlay;

    @Inject
    public DerangedAchaeologistScript archeoScript;

    @Provides
    DerangedaAchaeologistConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(DerangedaAchaeologistConfig.class);
    }

    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(exampleOverlay);
        }
        archeoScript.run(config);
    }

    protected void shutDown() {
        archeoScript.shutdown();
        overlayManager.remove(exampleOverlay);
    }
}
