package net.runelite.client.plugins.microbot.shunter;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.plugins.microbot.shunter.*;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Default + "[S] Hunter",
        description = "hunter",
        tags = {"hunter", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class sHunterPlugin extends Plugin {
    @Inject
    private sHunterConfig config;
    @Inject
    public static Client client;
    @Provides
    sHunterConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(sHunterConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private sHunterOverlay sHunterOverlay;
    @Inject
    sHunterScript sHunterScript;


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(sHunterOverlay);
        }
        System.out.println("testing123123123123123");
        client = Microbot.getClient();
        sHunterScript.run(config);
    }


    protected void shutDown() {
        sHunterScript.shutdown();
        overlayManager.remove(sHunterOverlay);
    }
}
