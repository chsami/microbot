package net.runelite.client.plugins.microbot.magic.housetab;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.magic.housetab.enums.HOUSETABS_CONFIG;
import net.runelite.client.plugins.microbot.zeah.hosidius.HosidiusScript;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = "MicroHouseTab",
        description = "Microbot HouseTab plugin",
        tags = {"microbot", "magic", "moneymaking"}
)
@Slf4j
public class HouseTabPlugin extends Plugin {

    @Inject
    private HouseTabConfig config;

    @Provides
    HouseTabConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(HouseTabConfig.class);
    }

    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private HouseTabOverlay hosidiusOverlay;

    private HouseTabScript houseTabScript = new HouseTabScript(HOUSETABS_CONFIG.HOUSE_ADVERTISEMENT,
            new String[]{"xGrace", "workless", "Lego Batman", "Batman 321", "Batman Chest"});

    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(hosidiusOverlay);
        }
        houseTabScript.run(config);
    }

    protected void shutDown() {
        houseTabScript.shutdown();
        overlayManager.remove(hosidiusOverlay);
    }
}
