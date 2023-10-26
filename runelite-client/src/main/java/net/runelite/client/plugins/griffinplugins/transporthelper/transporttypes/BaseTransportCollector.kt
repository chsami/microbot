package net.runelite.client.plugins.griffinplugins.transporthelper.transporttypes

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.runelite.api.Tile
import net.runelite.api.coords.WorldPoint
import net.runelite.client.plugins.griffinplugins.transporthelper.TileObjectData
import net.runelite.client.plugins.microbot.Microbot
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

abstract class BaseTransportCollector(val tile: Tile) {

    fun shouldCollect(): Boolean {
        val tileObjectData = getObjectDataFromTile(tile) ?: return false

        val composition = Microbot.getClientThreadForKotlin().runOnClientThread { return@runOnClientThread Microbot.getClientForKotlin().getObjectDefinition(tileObjectData.objectId) }
        if (composition.actions == null) {
            return false
        }

        val actions = composition.actions.toMutableList().filterNotNull()
        if (actions.isEmpty()) {
            return false
        }

        return getStartingActions().any { actions.contains(it) }
    }

    abstract fun getStartingActions(): List<String>
    abstract fun getEndingActions(): List<String>
    fun getTransportName(): String {
        return "Unknown Transport"
    }

    fun getTransportId(): Int {
        val objectData = getObjectDataFromTile(tile)
        return objectData?.objectId ?: -1
    }

    fun getTransportHash(): Int {
        val objectData = getObjectDataFromTile(tile)
        return objectData?.objectHash ?: -1
    }

    fun getTransportObjectName(): String {
        val objectData = getObjectDataFromTile(tile)
        return objectData?.objectName ?: "null"
    }

    abstract fun getTransportUnblockNorthSouth(): Boolean
    abstract fun getTransportUnblockEastWest(): Boolean
    fun getTransportStartWorldPoint(): WorldPoint {
        return tile.worldLocation
    }

    abstract fun getTransportEndWorldPoint(): WorldPoint
    abstract fun getTransportAction(): String

    fun getJson(): JsonObject {
        val transport = JsonObject()
        transport.addProperty("transport_name", getTransportName())
        transport.addProperty("object_hash", getTransportHash())
        transport.addProperty("object_id", getTransportId())
        transport.addProperty("object_name", getTransportObjectName())
        transport.addProperty("unblock_north_south", getTransportUnblockNorthSouth())
        transport.addProperty("unblock_east_west", getTransportUnblockEastWest())

        val startTile = JsonObject()
        startTile.addProperty("x", getTransportStartWorldPoint().x)
        startTile.addProperty("y", getTransportStartWorldPoint().y)
        startTile.addProperty("z", getTransportStartWorldPoint().plane)

        val endTile = JsonObject()
        endTile.addProperty("x", getTransportEndWorldPoint().x)
        endTile.addProperty("y", getTransportEndWorldPoint().y)
        endTile.addProperty("z", getTransportEndWorldPoint().plane)

        val connection = JsonObject()
        connection.add("start_tile", startTile)
        connection.add("end_tile", endTile)
        connection.addProperty("action", getTransportAction())

        val connections = JsonArray()
        connections.add(connection)

        transport.add("connections", connections)

        val gson = GsonBuilder().setPrettyPrinting().create()
        val json = gson.toJson(transport)

//        val stringSelection = StringSelection(json)
//        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
//        clipboard.setContents(stringSelection, null)

        println(json)
        return transport
    }

    fun getObjectDataFromTile(tile: Tile): TileObjectData? {
        val client = Microbot.getClientForKotlin()
        if (tile.wallObject != null) {
            return TileObjectData(
//                TransportUtility.getObjectIdVariableName(tile.wallObject.id),
                Microbot.getClientThreadForKotlin().runOnClientThread { return@runOnClientThread client.getObjectDefinition(tile.wallObject.id).name },
                tile.wallObject.getId(),
                tile.wallObject.hash.toInt(),
                tile.wallObject.orientationA == 1 || tile.wallObject.orientationA == 4
            )
        }

        for (gameObject in tile.gameObjects) {
            if (gameObject == null) {
                continue
            }

            return TileObjectData(
//                TransportUtility.getObjectIdVariableName(gameObject.id),
                Microbot.getClientThreadForKotlin().runOnClientThread { return@runOnClientThread client.getObjectDefinition(gameObject.id).name },
                gameObject.id,
                gameObject.hash.toInt(),
                false
            )
        }
        if (tile.groundObject != null) {
            return TileObjectData(
//                TransportUtility.getObjectIdVariableName(tile.groundObject.id),
                Microbot.getClientThreadForKotlin().runOnClientThread { return@runOnClientThread client.getObjectDefinition(tile.groundObject.id).name },
                tile.groundObject.id,
                tile.groundObject.hash.toInt(),
                false
            )
        }
        if (tile.decorativeObject != null) {
            return TileObjectData(
//                TransportUtility.getObjectIdVariableName(tile.decorativeObject.id),
                Microbot.getClientThreadForKotlin().runOnClientThread { return@runOnClientThread client.getObjectDefinition(tile.decorativeObject.id).name },
                tile.decorativeObject.id,
                tile.decorativeObject.hash.toInt(),
                false
            )
        }
        return null
    }
}