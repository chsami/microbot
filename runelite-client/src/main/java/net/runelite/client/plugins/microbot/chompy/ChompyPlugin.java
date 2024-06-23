package net.runelite.client.plugins.microbot.chompy;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Default + "Chompy",
        description = "Chompy Hunt plugin - start near some toads with bow and arrows equipped. ",
        tags = {"Chompy", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class ChompyPlugin extends Plugin {
    @Inject
    private ChompyConfig config;
    @Provides
    ChompyConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ChompyConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ChompyOverlay chompyOverlay;

    @Inject
    ChompyScript chompyScript;


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(chompyOverlay);
        }
        chompyScript.startup();
        chompyScript.run(config);
    }

    protected void shutDown() {
        chompyScript.shutdown();
        overlayManager.remove(chompyOverlay);
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage) {
        if (!((chatMessage.getType() == ChatMessageType.SPAM) || (chatMessage.getType() == ChatMessageType.GAMEMESSAGE)|| (chatMessage.getType() == ChatMessageType.ENGINE))) {
            return;
        }

        String message = chatMessage.getMessage().toLowerCase();
        System.out.println(message);
        if (message.contains("you scratch a notch on your bow for the chompy bird kill")) {
            chompyScript.chompy_notch();
        }
        if (message.contains("This is not your Chompy Bird to shoot".toLowerCase())) {
            chompyScript.not_my_chompy();
        }
        if (message.contains("can't reach that")) {
            chompyScript.cant_reach();
        }
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
