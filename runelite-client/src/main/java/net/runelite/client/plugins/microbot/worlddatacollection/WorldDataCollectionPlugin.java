package net.runelite.client.plugins.microbot.worlddatacollection;

import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;

import static net.runelite.client.plugins.PluginDescriptor.Griffin;

@PluginDescriptor(name = Griffin + "World Data Collection", enabledByDefault = false)
public class WorldDataCollectionPlugin extends Plugin {
    static final String CONFIG_GROUP = "Alfred World Collection";
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private WorldDataCollectionOverlay overlay;

    private WorldDataCollectionThread worldDataCollectionThread;
    @Override
    protected void startUp() throws Exception {
        overlayManager.add(overlay);
        worldDataCollectionThread = new WorldDataCollectionThread();
        worldDataCollectionThread.start();
    }

    @Override
    protected void shutDown() throws Exception {
        worldDataCollectionThread.executor.shutdown();
        worldDataCollectionThread.executor.shutdownNow();
        while (!worldDataCollectionThread.executor.isTerminated()) {
        }
        worldDataCollectionThread.stop();
        overlayManager.remove(overlay);
    }
}
