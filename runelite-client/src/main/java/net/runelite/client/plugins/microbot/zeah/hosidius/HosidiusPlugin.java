package net.runelite.client.plugins.microbot.zeah.hosidius;

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
        name = PluginDescriptor.Mocrosoft + "Hosidius",
        description = "Microbot hosidius favour plugin",
        tags = {"microbot", "minigame", "hosidius", "favour", "zeah"},
        enabledByDefault = false,
        hidden = true
)
@Slf4j
public class HosidiusPlugin extends Plugin {

    @Inject
    private HosidiusConfig config;

    @Provides
    HosidiusConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(HosidiusConfig.class);
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
    private HosidiusOverlay hosidiusOverlay;

    private final HosidiusScript hosidiusScript = new HosidiusScript();

    @Override
    protected void startUp() throws AWTException {
        Microbot.pauseAllScripts = false;
        Microbot.setClient(client);
        Microbot.setClientThread(clientThread);
        Microbot.setNotifier(notifier);
        Microbot.setMouse(new VirtualMouse());
        if (overlayManager != null) {
            overlayManager.add(hosidiusOverlay);
        }
        hosidiusScript.run(config);
    }

    protected void shutDown() {
        hosidiusScript.shutdown();
        overlayManager.remove(hosidiusOverlay);
    }
}
