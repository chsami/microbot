package net.runelite.client.plugins.nateplugins.misc.cluehunter;

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
        name = PluginDescriptor.Nate +"Cluehunter",
        description = "Nate's Cluehunter - Make sure you have all the required items- Spade,Nature Runes, Leather Boots, SuperAntiPoison (1)",
        tags = {"magic", "nate", "Cluehunter","misc"},
        enabledByDefault = false
)
@Slf4j
public class CluePlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    private ClueConfig config;
    @Inject
    private ClientThread clientThread;
    @Inject
    Notifier notifier;

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ClueOverlay clueOverlay;

    @Inject
    ClueScript clueScript;

    @Provides
    ClueConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ClueConfig.class);
    }


    @Override
    protected void startUp() throws AWTException {
        Microbot.pauseAllScripts = false;
        Microbot.setClient(client);
        Microbot.setClientThread(clientThread);
        Microbot.setNotifier(notifier);
        Microbot.setMouse(new VirtualMouse());
        if (overlayManager != null) {
            overlayManager.add(clueOverlay);
        }
        clueScript.run(config);
    }

    protected void shutDown() {
        clueScript.shutdown();
        overlayManager.remove(clueOverlay);
    }
}
