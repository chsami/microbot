package net.runelite.client.plugins.hoseaplugins.CalvarionHelper;

import com.google.inject.Inject;
import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GraphicsObjectCreated;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.util.HashMap;

@PluginDescriptor(
        name = "<html><font color=\"#FF9DF9\">[PP]</font> CalvarionHelper </html>",
        description = "CalvarionHelper by EthanVann, maintained by Piggy Plugins",
        enabledByDefault = false,
        tags = {"ethan"}
)
public class CalvarionHelper extends Plugin {
    @Inject
    Client client;
    @Inject
    OverlayManager overlayManager;
    @Inject
    CalvarionHelperConfig config;
    HashMap<WorldPoint, Integer> lightning = new HashMap<>();
    HashMap<WorldPoint, Integer> swing = new HashMap<>();
    CalvarionHelperOverlay overlay;

    @Provides
    public CalvarionHelperConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(CalvarionHelperConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        overlay = new CalvarionHelperOverlay(client, this);
        overlayManager.add(overlay);
    }

    @Subscribe
    public void onGameTick(GameTick e) {
        lightning.forEach((k, v) -> lightning.put(k, v - 1));
        lightning.entrySet().removeIf(entry -> entry.getValue() <= 0);
        swing.forEach((k, v) -> swing.put(k, v - 1));
        swing.entrySet().removeIf(entry -> entry.getValue() <= 0);
    }

    @Override
    protected void shutDown() throws Exception {
        overlayManager.remove(overlay);
    }

    @Subscribe
    public void onGraphicsObjectCreated(GraphicsObjectCreated e) {
        if (e.getGraphicsObject().getId() == 2346 || e.getGraphicsObject().getId() == 2347) {
            lightning.put(WorldPoint.fromLocal(client, e.getGraphicsObject().getLocation()), 4);
            return;
        }
        if (e.getGraphicsObject().getId() == 1446) {
            swing.put(WorldPoint.fromLocal(client, e.getGraphicsObject().getLocation()), 4);
        }
    }
}
