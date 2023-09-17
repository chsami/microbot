package net.runelite.client.plugins.microbot.staticwalker;

import com.google.inject.Provides;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;

import static net.runelite.client.plugins.PluginDescriptor.Griffin;

@PluginDescriptor(name = Griffin + StaticWalkerPlugin.CONFIG_GROUP, enabledByDefault = false)
public class StaticWalkerPlugin extends Plugin {
    static final String CONFIG_GROUP = "Static Walker";
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private StaticWalkerOverlay overlay;
    @Inject
    private StaticWalkerConfig config;
    @Inject
    private StaticWalkerScript staticWalkerScript;

    @Provides
    StaticWalkerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(StaticWalkerConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        overlayManager.add(overlay);
        staticWalkerScript.run(config);
    }

    @Override
    protected void shutDown() throws Exception {
        staticWalkerScript.shutdown();
        overlayManager.remove(overlay);
    }
}
