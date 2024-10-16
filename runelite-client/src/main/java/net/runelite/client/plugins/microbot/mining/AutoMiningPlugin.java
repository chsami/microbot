package net.runelite.client.plugins.microbot.mining;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Mocrosoft + "Auto Mining",
        description = "Mines and banks ores",
        tags = {"mining", "microbot", "skilling"},
        enabledByDefault = false
)
@Slf4j
public class AutoMiningPlugin extends Plugin {

    @Inject
    private Client client;

    @Inject
    private AutoMiningConfig config;

    @Inject
    private AutoMiningScript autoMiningScript;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private AutoMiningOverlay autoMiningOverlay;

    @Inject
    private CalcifiedRocks calcifiedRocks;

    @Provides
    AutoMiningConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoMiningConfig.class);
    }

    @Override
    protected void startUp() throws AWTException {
        overlayManager.add(autoMiningOverlay);
        autoMiningScript.run(config);
    }

    @Override
    protected void shutDown() {
        autoMiningScript.shutdown();
        overlayManager.remove(autoMiningOverlay);
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        // Check for the specific message indicating the cracks are oozing water
        if (event.getMessage().contains("Some cracks around the cave begin to ooze water.")) {
            Microbot.log("Detected cracks oozing water.");

            // Trigger the crack interaction logic in CalcifiedRocks
            calcifiedRocks.interactWithCracks();
        }
    }
}
