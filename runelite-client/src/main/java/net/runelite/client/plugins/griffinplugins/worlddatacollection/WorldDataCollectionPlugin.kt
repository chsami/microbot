package net.runelite.client.plugins.griffinplugins.worlddatacollection

import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.ui.overlay.OverlayManager
import javax.inject.Inject

@PluginDescriptor(name = PluginDescriptor.Griffin + "World Data Collection", enabledByDefault = false)
class WorldDataCollectionPlugin : Plugin() {
    companion object {
        const val CONFIG_GROUP = "Alfred World Collection"
    }

    @Inject
    private lateinit var overlayManager: OverlayManager

    @Inject
    private lateinit var overlay: WorldDataCollectionOverlay

    private var worldDataCollectionThread: WorldDataCollectionThread? = null
    override fun startUp() {
        overlayManager!!.add(overlay)
        worldDataCollectionThread = WorldDataCollectionThread()
        worldDataCollectionThread!!.start()
    }

    override fun shutDown() {
        worldDataCollectionThread!!.executor.shutdown()
        worldDataCollectionThread!!.executor.shutdownNow()
        while (!worldDataCollectionThread!!.executor.isTerminated) {
        }
        worldDataCollectionThread!!.stop()
        overlayManager!!.remove(overlay)
    }
}
