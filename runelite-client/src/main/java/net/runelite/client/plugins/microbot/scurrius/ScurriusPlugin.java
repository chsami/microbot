package net.runelite.client.plugins.microbot.scurrius;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Projectile;
import net.runelite.api.events.ProjectileMoved;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Default + "Scurrius",
        description = "Scurrius example plugin",
        tags = {"microbot", "scurrius", "boss"},
        enabledByDefault = false
)
@Slf4j
public class ScurriusPlugin extends Plugin {
    @Inject
    private ScurriusConfig config;
    @Provides
    ScurriusConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ScurriusConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ScurriusOverlay exampleOverlay;

    @Inject
    ScurriusScript scurriusScript;


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(exampleOverlay);
        }
        scurriusScript.run(config);
    }

    protected void shutDown() {
        scurriusScript.shutdown();
        overlayManager.remove(exampleOverlay);
    }

    @Subscribe
    private void onProjectileMoved(ProjectileMoved event) {
        final Projectile projectile = event.getProjectile();
        scurriusScript.prayAgainstProjectiles(projectile);
    }
}
