package net.runelite.client.plugins.nateplugins.moneymaking.natehumidifier;

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

import static net.runelite.client.plugins.natepainthelper.Info.*;


@PluginDescriptor(
        name = PluginDescriptor.Nate +"Humidifier",
        description = "Nate's Humidifier",
        tags = {"magic", "nate", "humidifier","moneymaking"},
        enabledByDefault = false
)
@Slf4j
public class HumidifierPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    private HumidifierConfig config;
    @Inject
    private ClientThread clientThread;
    @Inject
    Notifier notifier;

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private HumidifierOverlay humidifierOverlay;

    @Inject
    HumidifierScript humidifierScript;

    @Provides
    HumidifierConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(HumidifierConfig.class);
    }


    @Override
    protected void startUp() throws AWTException {
        Microbot.pauseAllScripts = false;
        Microbot.setClient(client);
        Microbot.setClientThread(clientThread);
        Microbot.setNotifier(notifier);
        Microbot.setMouse(new VirtualMouse());
        expstarted = Microbot.getClient().getSkillExperience(Skill.MAGIC);
        startinglevel = Microbot.getClient().getRealSkillLevel(Skill.MAGIC);
        timeBegan = System.currentTimeMillis();
        if (overlayManager != null) {
            overlayManager.add(humidifierOverlay);
        }
        humidifierScript.run(config);
    }

    protected void shutDown() {
        humidifierScript.shutdown();
        overlayManager.remove(humidifierOverlay);
    }
}
