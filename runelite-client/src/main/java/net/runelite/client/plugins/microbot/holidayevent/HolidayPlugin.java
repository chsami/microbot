package net.runelite.client.plugins.microbot.holidayevent;

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
        name = PluginDescriptor.Default + "Holiday events",
        description = "Holiday event plugin",
        tags = {"holiday", "microbot"},
        enabledByDefault = false
)

@Slf4j
public class HolidayPlugin extends Plugin {
    @Inject
    private HolidayConfig config;
    @Provides
    HolidayConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(HolidayConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private HolidayOverlay holidayOverlay;

    @Inject
    HolidayScript holidayScript;


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(holidayOverlay);
        }
        holidayScript.run(config);
    }

    protected void shutDown() {
        holidayScript.shutdown();
        overlayManager.remove(holidayOverlay);
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
