package net.runelite.client.plugins.microbot.pottery;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.AnimationID;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.events.AnimationChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.mouse.VirtualMouse;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;
import java.time.Instant;

@PluginDescriptor(
        name = PluginDescriptor.GMason + "Pottery",
        description = "Microbot pottery plugin",
        tags = {"crafting", "microbot", "skilling"},
        enabledByDefault = false
)
@Slf4j
public class PotteryPlugin extends Plugin {
    @Inject
    PotteryScript potteryScript;
    @Inject
    private PotteryConfig config;
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private PotteryOverlay overlay;

    @Provides
    PotteryConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(PotteryConfig.class);
    }

    @Override
    protected void startUp() throws AWTException {
        Microbot.pauseAllScripts = false;
        Microbot.setClient(client);
        Microbot.setClientThread(clientThread);
        Microbot.setMouse(new VirtualMouse());
        if (overlayManager != null) {
            overlayManager.add(overlay);
        }
        potteryScript.run(config);
    }

    protected void shutDown() {
        potteryScript.shutdown();
        overlayManager.remove(overlay);
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        if (!(event.getActor() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getActor();
        if (player != client.getLocalPlayer()) {
            return;
        }

        int CRAFTING_POTTERY_OVEN_1317 = 1317;
        if (player.getAnimation() == AnimationID.CRAFTING_POTTERS_WHEEL || player.getAnimation() == AnimationID.CRAFTING_POTTERY_OVEN || player.getAnimation() == CRAFTING_POTTERY_OVEN_1317 || player.getAnimation() == AnimationID.LOOKING_INTO) {
            potteryScript.lastAnimationTime = System.currentTimeMillis();
        }
    }
}
