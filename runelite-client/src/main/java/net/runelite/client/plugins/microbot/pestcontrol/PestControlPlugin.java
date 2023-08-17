package net.runelite.client.plugins.microbot.pestcontrol;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@PluginDescriptor(
        name = PluginDescriptor.Mocrosoft + "Pest Control",
        description = "Microbot Pest Control plugin",
        tags = {"pest control", "microbot", "minigames"},
        enabledByDefault = false
)
@Slf4j
public class PestControlPlugin extends Plugin {
    @Inject
    private PestControlConfig config;

    @Provides
    PestControlConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(PestControlConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private PestControlOverlay pestControlOverlay;

    @Inject
    PestControlScript pestControlScript;

    private final Pattern SHIELD_DROP = Pattern.compile("The ([a-z]+), [^ ]+ portal shield has dropped!", Pattern.CASE_INSENSITIVE);


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(pestControlOverlay);
        }
        pestControlScript.run(config);
    }

    protected void shutDown() {
        pestControlScript.shutdown();
        overlayManager.remove(pestControlOverlay);
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage) {
        if (chatMessage.getType() == ChatMessageType.GAMEMESSAGE) {
            Matcher matcher = SHIELD_DROP.matcher(chatMessage.getMessage());
            if (matcher.lookingAt()) {
                switch (matcher.group(1)) {
                    case "purple":
                        PestControlScript.setPurpleShield(false);
                        break;
                    case "blue":
                        PestControlScript.setBlueShield(false);
                        break;
                    case "red":
                        PestControlScript.setRedShield(false);
                        break;
                    case "yellow":
                        PestControlScript.setYellowShield(false);
                        break;
                }
            }
        }
    }
}
