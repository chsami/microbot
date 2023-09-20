package net.runelite.client.plugins.griffinplugins.transporthelper

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.inject.Provides
import net.runelite.api.ObjectID
import net.runelite.api.Perspective
import net.runelite.api.Tile
import net.runelite.client.config.ConfigManager
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.plugins.PluginDescriptor.Griffin
import net.runelite.client.plugins.microbot.Microbot
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject
import net.runelite.client.ui.overlay.OverlayManager
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import javax.inject.Inject

@PluginDescriptor(name = Griffin + TransportHelperPlugin.CONFIG_GROUP, enabledByDefault = false)
class TransportHelperPlugin : Plugin() {
    companion object {
        const val CONFIG_GROUP = "Transport Helper"
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

    override fun startUp() {
        overlayManager.add(overlay)
        setupMouseListener()
    }

    override fun shutDown() {
        overlayManager.remove(overlay)
        Microbot.getClientForKotlin().getCanvas().removeMouseListener(mouseListener)
    }

    private fun setupMouseListener() {
        mouseListener = object : MouseListener {
            override fun mouseClicked(e: MouseEvent) {
                val client = Microbot.getClientForKotlin()
                for (tile in Rs2GameObject.getTiles()) {
                    val tileLocalLocation = tile.localLocation
                    val poly = Perspective.getCanvasTilePoly(client, tileLocalLocation)
                    if (poly != null && poly.contains(client.getMouseCanvasPosition().x, client.getMouseCanvasPosition().y)) {
                        getTileTransportInformation(tile)
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

    private fun getTileTransportInformation(tile: Tile) {
        var objectName: String? = ""
        var objectId = -1
        var objectHash = -1
        val worldPoint = tile.worldLocation
        var eastWest = false
        var found = false

        if (tile.wallObject != null) {
            objectName = getObjectIdVariableName(tile.wallObject.id)
            objectId = tile.wallObject.getId()
            objectHash = tile.wallObject.hash.toInt()
            if (tile.wallObject.orientationA == 1 || tile.wallObject.orientationA == 4) {
                eastWest = true
            }
            found = true
        }

        if (!found) {
            for (gameObject in tile.gameObjects) {
                if (gameObject == null) {
                    continue
                }
                objectName = getObjectIdVariableName(gameObject.id)
                objectId = gameObject.id
                objectHash = gameObject.hash.toInt()
                println("Game Object Orientation: ${gameObject.orientation}")
                println("Game Object Model Orientation: ${gameObject.modelOrientation}")
                found = true
                break
            }
        }

        if (!found) {
            if (tile.groundObject != null) {
                objectName = getObjectIdVariableName(tile.groundObject.id)
                objectId = tile.groundObject.id
                objectHash = tile.groundObject.hash.toInt()
                found = true
            }
        }

        if (!found) {
            if (tile.decorativeObject != null) {
                objectName = getObjectIdVariableName(tile.decorativeObject.id)
                objectId = tile.decorativeObject.id
                objectHash = tile.decorativeObject.hash.toInt()
                found = true
            }
        }

        val transport = JsonObject()
        transport.addProperty("transport_name", "")
        transport.addProperty("object_hash", objectHash)
        transport.addProperty("object_id", objectId)
        transport.addProperty("object_name", objectName)

        if (config.transportType() == TransportTypes.DOOR) {
            if (eastWest) {
                transport.addProperty("unblock_north_south", false)
                transport.addProperty("unblock_east_west", true)
            } else {
                transport.addProperty("unblock_north_south", true)
                transport.addProperty("unblock_east_west", false)
            }
        } else {
            transport.addProperty("unblock_north_south", false)
            transport.addProperty("unblock_east_west", false)
        }

        val startTile = JsonObject()
        startTile.addProperty("x", worldPoint.x)
        startTile.addProperty("y", worldPoint.y)
        startTile.addProperty("z", worldPoint.plane)

        val endTile = JsonObject()
        endTile.addProperty("x", 0)
        endTile.addProperty("y", 0)
        endTile.addProperty("z", 0)

        val connection = JsonObject()
        connection.add("start_tile", startTile)
        connection.add("end_tile", endTile)
        connection.addProperty("action", "")

        val connections = JsonArray()
        connections.add(connection)

        transport.add("connections", connections)

        val gson = GsonBuilder().setPrettyPrinting().create()

        val stringSelection = StringSelection(gson.toJson(transport))
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        clipboard.setContents(stringSelection, null)
        println(gson.toJson(transport))
    }

    fun getObjectIdVariableName(objectId: Int): String? {
        val fields = ObjectID::class.java.getDeclaredFields()
        for (field in fields) {
            field.setAccessible(true)
            if (field.type == Int::class.javaPrimitiveType) {
                try {
                    val fieldValue = field.getInt(null)
                    if (fieldValue == objectId) {
                        return field.name
                    }
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }
}