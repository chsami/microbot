package net.runelite.client.plugins.microbot.mining.motherloadmine;

import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Mocrosoft + "MotherloadMine",
        description = "A bot that mines paydirt in the motherload mine",
        tags = {"paydirt", "mine", "motherload"},
        enabledByDefault = false
)
public class MotherloadMinePlugin extends Plugin {
    @Inject
    private OverlayManager overlayManager;

    @Inject
    private MotherloadMineOverlay motherloadMineOverlay;
    @Inject
    private MotherloadMineScript motherloadMineScript;

    @Override
    protected void startUp() throws AWTException {
        overlayManager.add(motherloadMineOverlay);
        motherloadMineScript.run();
    }

    protected void shutDown() {
        motherloadMineScript.shutdown();
        overlayManager.remove(motherloadMineOverlay);
    }
}
