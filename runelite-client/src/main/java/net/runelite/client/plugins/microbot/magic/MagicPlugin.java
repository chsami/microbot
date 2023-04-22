package net.runelite.client.plugins.microbot.magic;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.fletching.FletchingConfig;
import net.runelite.client.plugins.microbot.scripts.combat.attack.AttackNpc;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = "MicroMagic",
        description = "Microbot Magic plugin",
        tags = {"magic", "microbot", "skills", "Mage"}
)
@Slf4j
public class MagicPlugin extends Plugin {

    @Inject
    private MagicConfig config;

    @Provides
    FletchingConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(FletchingConfig.class);
    }

    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private MagicOverlay magicOverlay;

    private MagicScript magicScript = new MagicScript();

    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(magicOverlay);
        }
        magicScript.run(config);
    }

    protected void shutDown() {
        magicScript.shutdown();
        overlayManager.remove(magicOverlay);
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (event.getMessage().contains("You do not have enough") && event.getType() == ChatMessageType.GAMEMESSAGE) {
            shutDown();
        }
    }
}