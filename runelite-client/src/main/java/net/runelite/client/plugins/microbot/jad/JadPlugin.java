package net.runelite.client.plugins.microbot.jad;

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
        name = PluginDescriptor.Mocrosoft + "Jad Helper",
        description = "Jad Prayer Switcher plugin",
        tags = {"Jad", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class JadPlugin extends Plugin {
    @Inject
    private JadConfig config;
    @Provides
    JadConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(JadConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private JadOverlay exampleOverlay;

    @Inject
    JadScript exampleScript;


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(exampleOverlay);
        }
        exampleScript.run(config);
    }

    protected void shutDown() {
        exampleScript.shutdown();
        overlayManager.remove(exampleOverlay);
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
