package net.runelite.client.plugins.griffinplugins.worlddatacollection

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.runelite.api.Client
import net.runelite.api.Constants
import net.runelite.api.Player
import net.runelite.api.Tile
import net.runelite.client.RuneLite
import net.runelite.client.plugins.griffinplugins.transporthelper.TransportCollectorDispatch
import net.runelite.client.plugins.microbot.Microbot
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

class TileCollector {
    fun collect() {
        val tiles = getTiles()
        val json = getJson(tiles)
        writeToFile(json)
    }

    private fun getTiles(): List<Tile> {
        return Microbot.getClientThreadForKotlin().runOnClientThread {
            val tileList: MutableList<Tile> = ArrayList()
            val player: Player = Microbot.getClientForKotlin().localPlayer
            val tiles: Array<Array<Array<Tile>>> = Microbot.getClientForKotlin().getScene().getTiles()
            val z: Int = Microbot.getClientForKotlin().getPlane()

            for (x in 0 until Constants.SCENE_SIZE) {
                for (y in 0 until Constants.SCENE_SIZE) {
                    val tile = tiles[z][x][y] ?: continue
                    if (player.getLocalLocation().distanceTo(tile.getLocalLocation()) <= 2400) {
                        tileList.add(tile)
                    }
                }
            }
            return@runOnClientThread tileList
        }
    }

    private fun getJson(tiles: List<Tile>): JsonArray {
        val rows = JsonArray()
        for (tile in tiles) {
            val movementFlags: Set<WorldMovementFlag> = getMovementFlagsForTile(tile)
            val row = JsonObject()
            row.addProperty("x", tile.getWorldLocation().x)
            row.addProperty("y", tile.getWorldLocation().y)
            row.addProperty("z", tile.getWorldLocation().plane)
            row.addProperty("block_movement_full", movementFlags.contains(WorldMovementFlag.BLOCK_MOVEMENT_FULL))
            row.addProperty("block_movement_floor", movementFlags.contains(WorldMovementFlag.BLOCK_MOVEMENT_FLOOR))
            row.addProperty("block_movement_floor_decoration", movementFlags.contains(WorldMovementFlag.BLOCK_MOVEMENT_FLOOR_DECORATION))
            row.addProperty("block_movement_object", movementFlags.contains(WorldMovementFlag.BLOCK_MOVEMENT_OBJECT))
            row.addProperty("block_movement_north", movementFlags.contains(WorldMovementFlag.BLOCK_MOVEMENT_NORTH))
            row.addProperty("block_movement_east", movementFlags.contains(WorldMovementFlag.BLOCK_MOVEMENT_EAST))
            row.addProperty("block_movement_south", movementFlags.contains(WorldMovementFlag.BLOCK_MOVEMENT_SOUTH))
            row.addProperty("block_movement_west", movementFlags.contains(WorldMovementFlag.BLOCK_MOVEMENT_WEST))
            row.addProperty("block_movement_north_east", movementFlags.contains(WorldMovementFlag.BLOCK_MOVEMENT_NORTH_EAST))
            row.addProperty("block_movement_north_west", movementFlags.contains(WorldMovementFlag.BLOCK_MOVEMENT_NORTH_WEST))
            row.addProperty("block_movement_south_east", movementFlags.contains(WorldMovementFlag.BLOCK_MOVEMENT_SOUTH_EAST))
            row.addProperty("block_movement_south_west", movementFlags.contains(WorldMovementFlag.BLOCK_MOVEMENT_SOUTH_WEST))

            val transportCollector = TransportCollectorDispatch().getCollector(tile)
            if (transportCollector != null) {
                if (transportCollector.shouldCollect()) {
                    row.add("transports", transportCollector.getJson())
                }
            } else {
                row.add("transports", JsonObject())
            }
            
            rows.add(row)
        }
        return rows
    }

    private fun getMovementFlagsForTile(tile: Tile): Set<WorldMovementFlag> {
        val client: Client = Microbot.getClientForKotlin()
        if (client.getCollisionMaps() != null) {
            val flags = client.getCollisionMaps()!![client.getPlane()].getFlags()
            val data = flags[tile.getSceneLocation().x][tile.getSceneLocation().y]
            return WorldMovementFlag.getSetFlags(data)
        }
        return HashSet()
    }

    private fun writeToFile(rows: JsonArray) {
        try {
            val worldDataDirectory = File(RuneLite.CACHE_DIR, "worlddata")
            if (!worldDataDirectory.exists()) {
                worldDataDirectory.mkdir()
            }

            val worldDataFile = File(worldDataDirectory, System.currentTimeMillis().toString() + ".json")
            val writer = FileWriter(worldDataFile)
            val out = BufferedWriter(writer, 32768)

            out.write(rows.toString())
            out.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}