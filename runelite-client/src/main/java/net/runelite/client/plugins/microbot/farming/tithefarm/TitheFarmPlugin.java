package net.runelite.client.plugins.microbot.farming.tithefarm;


import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.mouse.VirtualMouse;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = "Micro TitheFarm",
        description = "Microbot TitheFarm plugin",
        tags = {"farming", "microbot", "skills", "minigame"},
        enabledByDefault = false
)
@Slf4j
public class TitheFarmPlugin extends Plugin {

    @Inject
    private TitheFarmConfig config;

    @Provides
    TitheFarmConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(TitheFarmConfig.class);
    }

    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private Notifier notifier;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private TitheFarmOverlay titheFarmOverlay;

    private TitheFarmScript titheFarmScript = new TitheFarmScript();

    @Override
    protected void startUp() throws AWTException {
        Microbot.pauseAllScripts = false;
        Microbot.setClient(client);
        Microbot.setClientThread(clientThread);
        Microbot.setNotifier(notifier);
        Microbot.setMouse(new VirtualMouse());
        if (overlayManager != null) {
            overlayManager.add(titheFarmOverlay);
        }
        titheFarmScript.run(config);
    }

    protected void shutDown() {
        titheFarmScript.shutdown();
        overlayManager.remove(titheFarmOverlay);
    }
}
