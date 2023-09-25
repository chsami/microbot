package net.runelite.client.plugins.griffinplugins.transporthelper

import com.google.inject.Provides
import net.runelite.api.Perspective
import net.runelite.api.Tile
import net.runelite.api.TileObject
import net.runelite.api.coords.WorldPoint
import net.runelite.api.events.*
import net.runelite.client.config.ConfigManager
import net.runelite.client.eventbus.Subscribe
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.plugins.PluginDescriptor.Griffin
import net.runelite.client.plugins.griffinplugins.transporthelper.transporttypes.BaseTransportCollector
import net.runelite.client.plugins.griffinplugins.transporthelper.transporttypes.DoorTransportCollector
import net.runelite.client.plugins.microbot.Microbot
import net.runelite.client.plugins.microbot.staticwalker.pathfinder.PathNode
import net.runelite.client.plugins.microbot.staticwalker.pathfinder.PathTransport
import net.runelite.client.plugins.microbot.staticwalker.pathfinder.SavedWorldDataLoader
import net.runelite.client.plugins.microbot.staticwalker.pathfinder.WorldDataDownloader
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject
import net.runelite.client.ui.overlay.OverlayManager
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import javax.inject.Inject

@PluginDescriptor(name = Griffin + TransportHelperPlugin.CONFIG_GROUP, enabledByDefault = false)
class TransportHelperPlugin : Plugin() {
    companion object {
        const val CONFIG_GROUP = "Transport Helper"
        val addedTransportWorldPoints: MutableMap<String, WorldPoint> = mutableMapOf()
        val unaddedTransportTiles: MutableMap<String, Tile> = mutableMapOf()
        val needsWorkTransportTiles: MutableMap<String, Tile> = mutableMapOf()
    }

    @Inject
    private lateinit var config: TransportHelperConfig

    @Provides
    fun provideConfig(configManager: ConfigManager): TransportHelperConfig {
        return configManager.getConfig(TransportHelperConfig::class.java)
    }

    @Inject
    private lateinit var overlayManager: OverlayManager

    @Inject
    private lateinit var overlay: TransportHelperOverlay
    private var mouseListener: MouseListener? = null
    private lateinit var transportCollectorDispatch: TransportCollectorDispatch

    override fun startUp() {
        overlayManager.add(overlay)
        setupMouseListener()
        transportCollectorDispatch = TransportCollectorDispatch()

        val dataLoader = SavedWorldDataLoader(WorldDataDownloader.worldDataFile)
        val nodeMap = dataLoader.getNodeMap()

        nodeMap.forEach { (key: String, value: PathNode) ->
            if (value.pathTransports.isNotEmpty()) {
                value.pathTransports.forEach { transport: PathTransport ->
                    addedTransportWorldPoints[transport.startPathNode.worldLocation.toString()] = transport.startPathNode.worldLocation
                }
            }
        }
    }

    override fun shutDown() {
        overlayManager.remove(overlay)
        Microbot.getClientForKotlin().getCanvas().removeMouseListener(mouseListener)
    }

    @Subscribe
    fun onWallObjectSpawned(event: WallObjectSpawned) {
        handleNewTileObject(event.tile, event.wallObject)
    }

    @Subscribe
    fun onGameObjectSpawned(event: GameObjectSpawned) {
        handleNewTileObject(event.tile, event.gameObject)
    }

    @Subscribe
    fun onGroundObjectSpawned(event: GroundObjectSpawned) {
        handleNewTileObject(event.tile, event.groundObject)
    }

    @Subscribe
    fun onDecorativeObjectSpawned(event: DecorativeObjectSpawned) {
        handleNewTileObject(event.tile, event.decorativeObject)
    }

    @Subscribe
    fun onWallObjectDespawned(event: WallObjectDespawned) {
        handleDespawnedTileObjects(event.tile, event.wallObject)
    }

    @Subscribe
    fun onGameObjectDespawned(event: GameObjectDespawned) {
        handleDespawnedTileObjects(event.tile, event.gameObject)
    }

    @Subscribe
    fun onGroundObjectDespawned(event: GroundObjectDespawned) {
        handleDespawnedTileObjects(event.tile, event.groundObject)
    }

    @Subscribe
    fun onDecorativeObjectDespawned(event: DecorativeObjectDespawned) {
        handleDespawnedTileObjects(event.tile, event.decorativeObject)
    }

    private fun handleNewTileObject(tile: Tile, tileObject: TileObject) {
        if (addedTransportWorldPoints.containsKey(tile.worldLocation.toString())) {
            return
        }

        val transportCollector = transportCollectorDispatch.getCollector(tile) ?: return
        when {
            transportCollector.getStartingActions().any { it.contains("open", true) } -> unaddedTransportTiles[tile.worldLocation.toString()] = tile
            transportCollector.getStartingActions().any { it.contains("climb", true) } -> unaddedTransportTiles[tile.worldLocation.toString()] = tile
            transportCollector.getStartingActions().any { it.contains("enter", true) } -> unaddedTransportTiles[tile.worldLocation.toString()] = tile
            transportCollector.getStartingActions().any { it.contains("close", true) } -> needsWorkTransportTiles[tile.worldLocation.toString()] = tile
        }
    }

    private fun handleDespawnedTileObjects(tile: Tile, tileObject: TileObject) {
        if (unaddedTransportTiles.containsKey(tile.worldLocation.toString())) {
            unaddedTransportTiles.remove(tile.worldLocation.toString())
        }

        if (needsWorkTransportTiles.containsKey(tile.worldLocation.toString())) {
            needsWorkTransportTiles.remove(tile.worldLocation.toString())
        }
    }

    private fun setupMouseListener() {
        mouseListener = object : MouseListener {
            override fun mouseClicked(e: MouseEvent) {
                val client = Microbot.getClientForKotlin()
                for (tile in Rs2GameObject.getTiles()) {
                    val tileLocalLocation = tile.localLocation
                    val poly = Perspective.getCanvasTilePoly(client, tileLocalLocation)
                    if (poly != null && poly.contains(client.getMouseCanvasPosition().x, client.getMouseCanvasPosition().y)) {
                        logTransportData(tile)
                    }
                }
            }

            override fun mousePressed(e: MouseEvent) {}
            override fun mouseReleased(e: MouseEvent) {}
            override fun mouseEntered(e: MouseEvent) {}
            override fun mouseExited(e: MouseEvent) {}
        }
        Microbot.getClientForKotlin().getCanvas().addMouseListener(mouseListener)
    }

    private fun logTransportData(tile: Tile) {
        var collector: BaseTransportCollector? = null
        if (config.transportType() == TransportTypes.DOOR) {
            collector = DoorTransportCollector(tile)
        } else if (config.transportType() == TransportTypes.STAIR) {
            collector = DoorTransportCollector(tile)
        }

        if (collector == null) {
            return
        }

        collector.getJson()
    }
}