package net.runelite.client.plugins.microbot.mining.amethyst;

import com.google.inject.Provides;
import net.runelite.api.ObjectID;
import net.runelite.api.WallObject;
import net.runelite.api.events.WallObjectSpawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.mining.amethyst.enums.Status;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.See1Duck + " Amethyst Miner",
        description = "Automates mining amethyst in the mining guild",
        enabledByDefault = false,
        tags = {"mining", "amethyst", "mining guild"}
)
public class AmethystMiningPlugin extends Plugin {
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private AmethystMiningOverlay amethystMiningOverlay;
    @Inject
    private AmethystMiningScript amethystMiningScript;
    @Inject
    private AmethystMiningConfig amethystMiningConfig;

    @Provides
    AmethystMiningConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AmethystMiningConfig.class);
    }

    @Override
    protected void startUp() throws AWTException {
        overlayManager.add(amethystMiningOverlay);
        amethystMiningScript.run(amethystMiningConfig);
    }

    @Subscribe
    public void onWallObjectSpawned(WallObjectSpawned event) {
        WallObject wallObject = event.getWallObject();
        if (wallObject == null)
            return;
        if (AmethystMiningScript.status == Status.MINING && wallObject.getId() == ObjectID.EMPTY_WALL) {
            if (wallObject.getWorldLocation().equals(AmethystMiningScript.oreVein.getWorldLocation())) {
                AmethystMiningScript.oreVein = null;
            }
        }
    }

    protected void shutDown() {
        amethystMiningScript.shutdown();
        overlayManager.remove(amethystMiningOverlay);
    }
}
