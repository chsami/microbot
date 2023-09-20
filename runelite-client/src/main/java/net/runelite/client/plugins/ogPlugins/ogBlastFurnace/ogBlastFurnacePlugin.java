package net.runelite.client.plugins.ogPlugins.ogBlastFurnace;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.OG + "Blast Furnace",
        description = "OG Blast Furnace Plugin",
        tags = {"og", "blast", "furnace"},
        enabledByDefault = false
)
@Slf4j
public class ogBlastFurnacePlugin extends Plugin {
    @Inject
    private ogBlastFurnaceConfig config;
    @Provides
    ogBlastFurnaceConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ogBlastFurnaceConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ogBlastFurnaceOverlay ogBlastFurnaceOverlay;
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;

    @Inject
    ogBlastFurnaceScript ogBlastFurnaceScript;


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(ogBlastFurnaceOverlay);
        }
        ogBlastFurnaceScript.run(config);
    }

    protected void shutDown() {
        ogBlastFurnaceScript.shutdown();
        overlayManager.remove(ogBlastFurnaceOverlay);
    }
}
