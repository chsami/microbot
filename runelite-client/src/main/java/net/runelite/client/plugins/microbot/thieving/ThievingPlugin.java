package net.runelite.client.plugins.microbot.thieving;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.plugins.timersandbuffs.TimersAndBuffsPlugin;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Mocrosoft + "Thieving",
        description = "Microbot thieving plugin",
        tags = {"thieving", "microbot", "skilling"},
        enabledByDefault = false
)
@Slf4j
public class ThievingPlugin extends Plugin {
    @Inject
    private ThievingConfig config;

    @Provides
    ThievingConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ThievingConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ThievingOverlay thievingOverlay;

    @Inject
    ThievingScript thievingScript;


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(thievingOverlay);
        }
        thievingScript.run(config);
    }

    protected void shutDown() {
        thievingScript.shutdown();
        overlayManager.remove(thievingOverlay);
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage)
    {
        if (chatMessage.getType() == ChatMessageType.GAMEMESSAGE && chatMessage.getMessage().contains("protects you")) {
            TimersAndBuffsPlugin.t = null;
        }
    }
}
