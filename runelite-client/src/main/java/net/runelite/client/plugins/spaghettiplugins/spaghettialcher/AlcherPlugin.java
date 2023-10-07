package net.runelite.client.plugins.spaghettiplugins.spaghettialcher;

import com.google.inject.Provides;
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
        name = PluginDescriptor.Spaghetti +"Alcher",
        description = "Plugin for alching.",
        tags = {"Money", "spaghetti", "skilling"},
        enabledByDefault = false
)
public class AlcherPlugin extends Plugin {
    @Inject
    private AlcherConfig config;
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    Notifier notifier;

    @Provides
    AlcherConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AlcherConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private AlcherOverlay alcherOverlay;

    @Inject
    AlcherScript alcherScript;


    @Override
    protected void startUp() throws AWTException {
        Microbot.pauseAllScripts = false;
        Microbot.setClient(client);
        Microbot.setClientThread(clientThread);
        Microbot.setNotifier(notifier);
        Microbot.setMouse(new VirtualMouse());
        if (overlayManager != null) {
            overlayManager.add(alcherOverlay);
        }
        alcherScript.run(config);
    }

    protected void shutDown() {
        alcherScript.shutdown();
        overlayManager.remove(alcherOverlay);
    }
}
