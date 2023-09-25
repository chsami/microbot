package net.runelite.client.plugins.ogPlugins.ogTest;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.OG + "Test",
        description = "Test plugin",
        tags = {"test", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class TestPlugin extends Plugin {
    @Inject
    private TestConfig config;
    @Provides
    TestConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(TestConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private TestOverlay testOverlay;

    @Inject
    TestScript testScript;
    @Subscribe
    public void onVarbitChanged(VarbitChanged varbitChanged)
    {
        testScript.onVarbitChanged(varbitChanged);
    }


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(testOverlay);
        }
        testScript.run(config);
    }


    protected void shutDown() {
        testScript.shutdown();
        overlayManager.remove(testOverlay);
    }
}
