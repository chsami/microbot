package net.runelite.client.plugins.ogPlugins.ogRunecrafting;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.SoundEffectPlayed;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.OG + "Runecrafter",
        description = "OG Runecrafting Plugin",
        tags = {"og","rune","craft", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class ogRunecraftingPlugin extends Plugin {
    @Inject
    private ogRunecraftingConfig config;
    @Provides
    ogRunecraftingConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ogRunecraftingConfig.class);
    }
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ogRunecraftingOverlay ogRunecraftingOverlay;

    @Inject
    ogRunecraftingScript ogRunecraftingScript;

    @Subscribe
    private void onGameTick(GameTick gameTick){
        ogRunecraftingScript.onGameTick(gameTick);
    }
    @Subscribe
    private void onSoundEffectPlayed(SoundEffectPlayed event){
    ogRunecraftingScript.onSoundEffectPlayed(event);
    }



    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(ogRunecraftingOverlay);
        }
        ogRunecraftingScript.run(config);
    }

    protected void shutDown() {
        ogRunecraftingScript.shutdown();
        overlayManager.remove(ogRunecraftingOverlay);
    }
}
