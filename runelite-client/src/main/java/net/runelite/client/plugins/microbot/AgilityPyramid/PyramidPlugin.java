package net.runelite.client.plugins.microbot.AgilityPyramid;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Default + "Agility-Pyramid",
        description = "Blomgreen Agility Pyramid for microbot. \bStart with waterskins and food",
        tags = {"agility", "pyramid", "blomgreen"},
        enabledByDefault = false
)
@Slf4j
public class PyramidPlugin extends Plugin {
    @Inject
    public PyramidConfig config;
    @Provides
    PyramidConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(PyramidConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private PyramidOverlay PyramidOverlay;

    @Inject
    AgilitypyramidScript AgilitypyramidScript;


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(PyramidOverlay);
        }
        AgilitypyramidScript.run();
    }

    protected void shutDown() {
        AgilitypyramidScript.shutdown();
        overlayManager.remove(PyramidOverlay);
    }
    int ticks = 10;
    @Subscribe
    public void onGameTick(GameTick tick)
    {
        //System.out.println(getName().chars().mapToObj(i -> (char)(i + 3)).map(String::valueOf).collect(Collectors.joining()));

        if (ticks > 0) {
            ticks--;
        } else {
            ticks = 10;
        }

    }

}
