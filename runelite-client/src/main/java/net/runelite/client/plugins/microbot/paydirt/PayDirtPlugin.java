package net.runelite.client.plugins.microbot.paydirt;

import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Default + "PayDirt",
        description = "A bot that mines paydirt",
        tags = {"paydirt", "mine", "motherlord"},
        enabledByDefault = false
)
public class PayDirtPlugin extends Plugin {
    @Inject
    private OverlayManager overlayManager;

    @Inject
    private PayDirtOverlay payDirtOverlay;
    @Inject
    private PayDirtScript payDirtScript;

    @Override
    protected void startUp() throws AWTException {
        overlayManager.add(payDirtOverlay);
        payDirtScript.run();
    }

    protected void shutDown() {
        payDirtScript.shutdown();
        overlayManager.remove(payDirtOverlay);
    }
}
