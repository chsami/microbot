package net.runelite.client.plugins.microbot.woodcutting;

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
import net.runelite.client.plugins.microbot.accountselector.AutoLoginConfig;
import net.runelite.client.plugins.microbot.util.mouse.VirtualMouse;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

import static net.runelite.client.plugins.natepainthelper.Info.*;

@PluginDescriptor(
        name = PluginDescriptor.Mocrosoft + "Woodcutting",
        description = "Microbot woodcutting plugin",
        tags = {"Woodcutting", "microbot", "skilling"},
        enabledByDefault = false
)
@Slf4j
public class AutoWoodcuttingPlugin extends Plugin {
    @Inject
    private AutoWoodcuttingConfig config;
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    Notifier notifier;

    @Provides
    AutoWoodcuttingConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoWoodcuttingConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private AutoWoodcuttingOverlay woodcuttingOverlay;

    @Inject
    AutoWoodcuttingScript autoWoodcuttingScript;


    @Override
    protected void startUp() throws AWTException {
        Microbot.pauseAllScripts = false;
        Microbot.setClient(client);
        Microbot.setClientThread(clientThread);
        Microbot.setNotifier(notifier);
        Microbot.setMouse(new VirtualMouse());
        expstarted = Microbot.getClient().getSkillExperience(Skill.WOODCUTTING);
        startinglevel = Microbot.getClient().getRealSkillLevel(Skill.WOODCUTTING);
        timeBegan = System.currentTimeMillis();
        if (overlayManager != null) {
            overlayManager.add(woodcuttingOverlay);
        }
        autoWoodcuttingScript.run(config);
    }

    protected void shutDown() {
        autoWoodcuttingScript.shutdown();
        overlayManager.remove(woodcuttingOverlay);
    }
}
