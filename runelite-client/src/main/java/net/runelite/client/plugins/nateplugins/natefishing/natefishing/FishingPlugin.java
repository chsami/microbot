package net.runelite.client.plugins.nateplugins.natefishing.natefishing;

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
        name = PluginDescriptor.Nate +"Power Fisher",
        description = "Nate's Power Fisher plugin",
        tags = {"Fishing", "nate", "skilling"},
        enabledByDefault = false
)
@Slf4j
public class FishingPlugin extends Plugin {
    @Inject
    private FishingConfig config;
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    Notifier notifier;

    @Provides
    FishingConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(FishingConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private FishingOverlay fishingOverlay;

    @Inject
    FishingScript fishingScript;


    @Override
    protected void startUp() throws AWTException {
        Microbot.pauseAllScripts = false;
        Microbot.setClient(client);
        Microbot.setClientThread(clientThread);
        Microbot.setNotifier(notifier);
        Microbot.setMouse(new VirtualMouse());
        expstarted = Microbot.getClient().getSkillExperience(Skill.FISHING);
        startinglevel = Microbot.getClient().getRealSkillLevel(Skill.FISHING);
        timeBegan = System.currentTimeMillis();
        if (overlayManager != null) {
            overlayManager.add(fishingOverlay);
        }
        fishingScript.run(config);
    }

    protected void shutDown() {
        fishingScript.shutdown();
        overlayManager.remove(fishingOverlay);
    }
}
