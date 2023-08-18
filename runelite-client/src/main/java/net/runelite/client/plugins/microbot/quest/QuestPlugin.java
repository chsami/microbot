package net.runelite.client.plugins.microbot.quest;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Mocrosoft + "Quester",
        description = "Microbot quest plugin",
        tags = {"example", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class QuestPlugin extends Plugin {
    @Inject
    private QuestConfig config;
    @Provides
    QuestConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(QuestConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private QuestOverlay exampleOverlay;

    @Inject
    QuestScript questScript;


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(exampleOverlay);
        }
        questScript.run(config);
    }

    protected void shutDown() {
        questScript.shutdown();
        overlayManager.remove(exampleOverlay);
    }
}
