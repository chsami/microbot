package net.runelite.client.plugins.microbot.walker;

import com.google.inject.Provides;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;

import static net.runelite.client.plugins.PluginDescriptor.Griffin;

@PluginDescriptor(name = Griffin + WalkerPlugin.CONFIG_GROUP, enabledByDefault = false)
public class WalkerPlugin extends Plugin {
    static final String CONFIG_GROUP = "Static Walker";
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private WalkerOverlay overlay;
    @Inject
    private WalkerConfig config;
    @Inject
    private WalkerScript walkerScript;

    @Provides
    WalkerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(WalkerConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        overlayManager.add(overlay);
        walkerScript.run(config);
    }

    @Override
    protected void shutDown() throws Exception {
        walkerScript.shutdown();
        overlayManager.remove(overlay);
    }
}
