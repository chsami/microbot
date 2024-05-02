package net.runelite.client.plugins.nateplugins.jadprayers;

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
        name = PluginDescriptor.Nate +"Magic Plugin",
        description = "Nate's Magic plugin",
        tags = {"Magic", "nate", "combat"},
        enabledByDefault = false
)
@Slf4j
public class PrayerPlugin extends Plugin {
    @Inject
    private PrayerConfig config;
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    Notifier notifier;

    @Provides
    PrayerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(PrayerConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private PrayerOverlay teleportOverlay;

    @Inject
    PrayerScript prayerScript;


    @Override
    protected void startUp() throws AWTException {
        Microbot.pauseAllScripts = false;
        Microbot.setClient(client);
        Microbot.setClientThread(clientThread);
        Microbot.setNotifier(notifier);
        Microbot.setMouse(new VirtualMouse());
        if (overlayManager != null) {
            overlayManager.add(teleportOverlay);
        }
        prayerScript.run(config);
    }

    protected void shutDown() {
        prayerScript.shutdown();
        overlayManager.remove(teleportOverlay);
    }
}
