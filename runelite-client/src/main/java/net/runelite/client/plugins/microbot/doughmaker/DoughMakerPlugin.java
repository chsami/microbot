package net.runelite.client.plugins.microbot.doughmaker;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.doughmaker.scripts.DoughMakerScript;
import net.runelite.client.plugins.microbot.util.mouse.VirtualMouse;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.GMason + "Dough Maker",
        description = "Microbot processing plugin",
        tags = {"cooking", "processing", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class DoughMakerPlugin extends Plugin {
    public static double version = 1.0;
    @Inject
    DoughMakerScript doughMakerScript;
    @Inject
    private DoughMakerConfig config;
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private DoughMakerOverlay overlay;

    @Provides
    DoughMakerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(DoughMakerConfig.class);
    }

    @Override
    protected void startUp() throws AWTException {
        Microbot.pauseAllScripts = false;
        Microbot.setClient(client);
        Microbot.setClientThread(clientThread);
        Microbot.setMouse(new VirtualMouse());
        if (overlayManager != null) {
            overlayManager.add(overlay);
        }
        doughMakerScript.run(config);
    }

    protected void shutDown() {
        doughMakerScript.shutdown();
        overlayManager.remove(overlay);
    }
}
