package net.runelite.client.plugins.microbot.sandcrabs;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Mocrosoft + "SandCrabs",
        description = "Kills SandCrab & resets",
        tags = {"Combat", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class SandCrabPlugin extends Plugin {
    @Inject
    private SandCrabConfig config;
    @Provides
    SandCrabConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(SandCrabConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private SandCrabOverlay sandCrabOverlay;

    @Inject
    public SandCrabScript sandCrabScript;


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(sandCrabOverlay);
        }
        sandCrabScript.run(config);
    }

    protected void shutDown() {
        sandCrabScript.shutdown();
        overlayManager.remove(sandCrabOverlay);
    }
}
