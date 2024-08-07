package net.runelite.client.plugins.microbot.roguesden;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

@PluginDescriptor(
        name = PluginDescriptor.Basm + "Rogues Den",
        description = "Rogues Den minigame",
        tags = {"rogues den", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class RoguesDenPlugin extends Plugin {
    @Inject
    private RoguesDenConfig config;
    @Provides
    RoguesDenConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(RoguesDenConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private RoguesDenOverlay overlay;

    @Inject
    @Getter
    RoguesDenScript script;


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(overlay);
        }
        script.run(config);
    }

    protected void shutDown() {
        script.shutdown();
        overlayManager.remove(overlay);
    }
}
