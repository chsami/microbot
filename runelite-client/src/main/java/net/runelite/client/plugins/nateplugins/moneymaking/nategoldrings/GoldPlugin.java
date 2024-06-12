package net.runelite.client.plugins.nateplugins.moneymaking.nategoldrings;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Skill;
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
        name = PluginDescriptor.Nate +"Gold Ring Maker",
        description = "Nate's Gold Ring Maker",
        tags = {"MoneyMaking", "nate", "gold"},
        enabledByDefault = false
)
@Slf4j
public class GoldPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    private GoldConfig config;
    @Inject
    private ClientThread clientThread;
    @Inject
    Notifier notifier;

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private GoldOverlay goldOverlay;

    @Inject
    GoldScript goldScript;

    @Provides
    GoldConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(GoldConfig.class);
    }


    @Override
    protected void startUp() throws AWTException {
        Microbot.pauseAllScripts = false;
        Microbot.setClient(client);
        Microbot.setClientThread(clientThread);
        Microbot.setNotifier(notifier);
        Microbot.setMouse(new VirtualMouse());
        if (overlayManager != null) {
            overlayManager.add(goldOverlay);
        }
        goldScript.run(config);
    }

    protected void shutDown() {
        goldScript.shutdown();
        overlayManager.remove(goldOverlay);
    }
}
