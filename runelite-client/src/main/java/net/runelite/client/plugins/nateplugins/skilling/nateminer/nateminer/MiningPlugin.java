package net.runelite.client.plugins.nateplugins.skilling.nateminer.nateminer;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.envisionplugins.breakhandler.BreakHandlerScript;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.mouse.VirtualMouse;
import net.runelite.client.ui.overlay.OverlayManager;
import static net.runelite.client.plugins.natepainthelper.Info.*;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Nate +"PowerMiner",
        description = "Nate's PowerMiner plugin",
        tags = {"Mining", "nate", "skilling"},
        enabledByDefault = false
)
@Slf4j
public class MiningPlugin extends Plugin {
    @Inject
    private MiningConfig config;
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    Notifier notifier;

    @Provides
    MiningConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(MiningConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private MiningOverlay miningOverlay;

    @Inject
    MiningScript miningScript;


    @Override
    protected void startUp() throws AWTException {
        Microbot.pauseAllScripts = false;
        Microbot.setClient(client);
        Microbot.setClientThread(clientThread);
        Microbot.setNotifier(notifier);
        Microbot.setMouse(new VirtualMouse());
        expstarted = Microbot.getClient().getSkillExperience(Skill.MINING);
        startinglevel = Microbot.getClient().getRealSkillLevel(Skill.MINING);
        timeBegan = System.currentTimeMillis();
        if (overlayManager != null) {
            overlayManager.add(miningOverlay);
        }

        initBreakHandlerSetup();
        miningScript.run(config);
    }

    protected void shutDown() {
        BreakHandlerScript.disableParentPlugin();

        miningScript.shutdown();
        overlayManager.remove(miningOverlay);
    }

    private void initBreakHandlerSetup() {
        BreakHandlerScript.setIsParentPluginRunning(true);
        BreakHandlerScript.setParentPluginName("Nate's Power Miner");
        BreakHandlerScript.setDetailedReportNotification(true);        // Let's send extra details to the breakhandler
    }
}
