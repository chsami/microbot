package net.runelite.client.plugins.microbot.runecrafting.arceuus;

import com.google.inject.Provides;
import lombok.Getter;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;

@PluginDescriptor(
        name = PluginDescriptor.See1Duck + "Arceuus RC",
        description = "Runecrafting at Arceuus",
        tags = {"runecrafting", "blood rune", "soul rune" ,"arceuus", "microbot"},
        enabledByDefault = false
)
public class ArceuusRcPlugin extends Plugin {
    @Getter
    @Inject
    private ArceuusRcConfig config;

    @Provides
    ArceuusRcConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ArceuusRcConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ArceuusRcOverlay arceuusRcOverlay;
    @Inject
    ArceuusRcScript arceuusRcScript;

    @Override
    protected void startUp() {
        if (overlayManager != null) {
            overlayManager.add(arceuusRcOverlay);
        }
        arceuusRcScript.run(config);
    }

    protected void shutDown() {
        arceuusRcScript.shutdown();
        overlayManager.remove(arceuusRcOverlay);
    }
}
