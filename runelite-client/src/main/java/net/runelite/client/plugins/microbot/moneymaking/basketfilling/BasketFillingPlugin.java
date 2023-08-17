package net.runelite.client.plugins.microbot.moneymaking.basketfilling;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Mocrosoft + "Basket filling",
        description = "Microbot basket filling plugin",
        tags = {"moneymaking", "microbot", "basket filling"},
        enabledByDefault = false
)
@Slf4j
public class BasketFillingPlugin extends Plugin {
    @Inject
    private BasketFillingConfig config;
    @Provides
    BasketFillingConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(BasketFillingConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private BasketFillingOverlay exampleOverlay;

    @Inject
    BasketFillingScript basketFillingScript;


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(exampleOverlay);
        }
        basketFillingScript.run(config);
    }

    protected void shutDown() {
        basketFillingScript.shutdown();
        overlayManager.remove(exampleOverlay);
    }
}
