package net.runelite.client.plugins.microbot.lunartanner;

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
        name = PluginDescriptor.Default + "Lunar Tanner",
        description = "Tans hides on the lunar spellbook",
        tags = {"example", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class TanLeatherPlugin extends Plugin {
    @Inject
    private TanLeatherConfig config;
    @Provides
    TanLeatherConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(TanLeatherConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private TanLeatherOverlay exampleOverlay;

    @Inject
    TanLeatherScript exampleScript;


    @Override
    protected void startUp() throws AWTException {
        log.info("Starting up TanLeatherPlugin");
        if (overlayManager != null) {
            overlayManager.add(exampleOverlay);
        }
        exampleScript.run(config);
    }

    protected void shutDown() {
        log.info("Shutting down TanLeatherPlugin");
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
