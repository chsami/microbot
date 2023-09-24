package net.runelite.client.plugins.griffinplugins.transporthelper

import net.runelite.api.Constants
import net.runelite.api.ObjectID
import net.runelite.api.Tile
import net.runelite.api.TileObject
import net.runelite.api.coords.WorldPoint
import net.runelite.client.plugins.microbot.Microbot

data class TileObjectData(
    var objectName: String?,
    var objectId: Int,
    var objectHash: Int,
    var eastWest: Boolean = false,
)

class TransportUtility {

    companion object {

        fun getTileObjects(): MutableList<Triple<Tile, TileObject, WorldPoint>> {
            val player = Microbot.getClientForKotlin().getLocalPlayer()
            val scene = Microbot.getClientForKotlin().getScene()
            val tiles = scene.getTiles()
            val z = Microbot.getClientForKotlin().getPlane()
            val tileObjects: MutableList<Triple<Tile, TileObject, WorldPoint>> = mutableListOf()

            for (x in 0 until Constants.SCENE_SIZE) {
                for (y in 0 until Constants.SCENE_SIZE) {
                    val tile = tiles[z][x][y] ?: continue

                    if (player.getLocalLocation().distanceTo(tile.getLocalLocation()) > 2400) {
                        continue
                    }

                    for (gameObject in tile.gameObjects) {
                        if (gameObject == null) {
                            continue
                        }

                        tileObjects.add(Triple(tile, gameObject, tile.worldLocation))
                    }

                    if (tile.wallObject != null) {
                        tileObjects.add(Triple(tile, tile.wallObject, tile.worldLocation))
                    }

                    if (tile.groundObject != null) {
                        tileObjects.add(Triple(tile, tile.groundObject, tile.worldLocation))
                    }

                    if (tile.decorativeObject != null) {
                        tileObjects.add(Triple(tile, tile.decorativeObject, tile.worldLocation))
                    }

                }
            }
            return tileObjects
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

        fun getObjectDataFromTile(tile: Tile): TileObjectData? {
            if (tile.wallObject != null) {
                return TileObjectData(
                    getObjectIdVariableName(tile.wallObject.id),
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
                    getObjectIdVariableName(gameObject.id),
                    gameObject.id,
                    gameObject.hash.toInt(),
                    false
                )
            }
            if (tile.groundObject != null) {
                return TileObjectData(
                    getObjectIdVariableName(tile.groundObject.id),
                    tile.groundObject.id,
                    tile.groundObject.hash.toInt(),
                    false
                )
            }
            if (tile.decorativeObject != null) {
                return TileObjectData(
                    getObjectIdVariableName(tile.decorativeObject.id),
                    tile.decorativeObject.id,
                    tile.decorativeObject.hash.toInt(),
                    false
                )
            }

            return null
        }
    }
}