package net.runelite.client.plugins.microbot.plankmake;

import java.awt.AWTException;

import javax.inject.Inject;

import com.google.inject.Provides;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.mouse.VirtualMouse;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
        name = PluginDescriptor.Geoff + "Plank Make",
        description = "Geoff's lunar plank maker",
        tags = {"magic", "moneymaking"},
        enabledByDefault = false
)
@Slf4j
public class PlankMakePlugin extends Plugin {
    @Inject
    private PlankMakeConfig config;

    @Provides
    PlankMakeConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(PlankMakeConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private PlankMakeOverlay PlankMakeOverlay;

    @Inject
    PlankMakeScript PlankMakeScript;

    @Override
    protected void startUp() throws AWTException {
        Microbot.setMouse(new VirtualMouse());
        log.info("Starting up PlankMakePlugin");
        if (overlayManager != null) {
            overlayManager.add(PlankMakeOverlay);
        }
        PlankMakeScript.run(config);
    }

    @Override
    protected void shutDown() {
        log.info("Shutting down PlankMakePlugin");
        PlankMakeScript.shutdown();
        overlayManager.remove(PlankMakeOverlay);
    }
}
