package net.runelite.client.plugins.microbot.mining.motherloadmine;

import com.google.inject.Provides;
import net.runelite.api.Skill;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

import static net.runelite.client.plugins.natepainthelper.Info.*;

@PluginDescriptor(
        name = PluginDescriptor.Mocrosoft + "MotherloadMine",
        description = "A bot that mines paydirt in the motherload mine",
        tags = {"paydirt", "mine", "motherload"},
        enabledByDefault = false
)
public class MotherloadMinePlugin extends Plugin {
    @Inject
    private MotherloadMineConfig config;
    @Inject
    private OverlayManager overlayManager;

    @Inject
    private MotherloadMineOverlay motherloadMineOverlay;
    @Inject
    private MotherloadMineScript motherloadMineScript;

    @Provides
    MotherloadMineConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(MotherloadMineConfig.class);
    }

    @Override
    protected void startUp() throws AWTException {
        expstarted = Microbot.getClient().getSkillExperience(Skill.MINING);
        startinglevel = Microbot.getClient().getRealSkillLevel(Skill.MINING);
        timeBegan = System.currentTimeMillis();
        overlayManager.add(motherloadMineOverlay);
        motherloadMineScript.run();
    }

    protected void shutDown() {
        motherloadMineScript.shutdown();
        overlayManager.remove(motherloadMineOverlay);
    }
}
