package net.runelite.client.plugins.microbot.sticktothescript.barbarianvillagefisher;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
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
        name = PluginDescriptor.StickToTheScript + "BarbVill Fisher",
        description = "StickToTheScript\'s Barbarian Village fishing plugin",
        tags = {"fishing", "StickToTheScript", "STTS", "fisher", "barbarian", "village"},
        enabledByDefault = false
)
@Slf4j
public class BarbarianVillageFisherPlugin extends Plugin {
    @Inject
    private BarbarianVillageFisherConfig config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private BarbarianVillageFisherOverlay overlay;

    @Provides
    BarbarianVillageFisherConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(BarbarianVillageFisherConfig.class);
    }

    @Inject
    BarbarianVillageFisherScript script;


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(overlay);
        }

        script.run(config);
    }

    protected void shutDown() {
        script.shutdown();
        overlayManager.remove(overlay);
    }
}
