package net.runelite.client.plugins.microbot.scurrius;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.ProjectileMoved;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.magic.orbcharger.enums.OrbChargerState;
import net.runelite.client.plugins.microbot.scurrius.enums.State;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Default + "Scurrius",
        description = "Scurrius example plugin",
        tags = {"microbot", "scurrius", "boss"},
        enabledByDefault = false
)
@Slf4j
public class ScurriusPlugin extends Plugin {
    @Inject
    private ScurriusConfig config;
    @Provides
    ScurriusConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ScurriusConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ScurriusOverlay exampleOverlay;

    @Inject
    ScurriusScript scurriusScript;


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(exampleOverlay);
        }
        ScurriusScript.state = State.BANKING;
        scurriusScript.run(config);
    }

    protected void shutDown() {
        scurriusScript.shutdown();
        overlayManager.remove(exampleOverlay);
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (event.getType() != ChatMessageType.GAMEMESSAGE) return;
        
        if (event.getMessage().equalsIgnoreCase("oh dear, you are dead!") && config.shutdownAfterDeath()) {
            Rs2Walker.setTarget(null);
            shutDown();
        }
    }
}
