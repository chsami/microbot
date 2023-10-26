package net.runelite.client.plugins.microbot.staticwalker.pathfinder

import net.runelite.api.Tile
import net.runelite.client.plugins.griffinplugins.worlddatacollection.WorldMovementFlag
import net.runelite.client.plugins.microbot.Microbot
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject

class LiveWorldDataLoader {
    private val nodes: MutableList<PathNode> = mutableListOf()


    fun getNodeMap(): MutableMap<String, PathNode> {
        readTiles()

        val gridDictionary: MutableMap<String, PathNode> = mutableMapOf()
        for (node in nodes) {
            val key = "${node.worldLocation.x}_${node.worldLocation.y}_${node.worldLocation.plane}"
            gridDictionary[key] = node
        }

        return gridDictionary
    }

    private fun readTiles(): MutableList<PathNode> {
        val tiles = Rs2GameObject.getTiles()
        for (tile in tiles) {
            val collisionMap = getMovementFlagsForTile(tile)
            val blocked = collisionMap.contains(WorldMovementFlag.BLOCK_MOVEMENT_OBJECT) || collisionMap.contains(WorldMovementFlag.BLOCK_MOVEMENT_FLOOR_DECORATION) || collisionMap.contains(WorldMovementFlag.BLOCK_MOVEMENT_FLOOR) || collisionMap.contains(WorldMovementFlag.BLOCK_MOVEMENT_FULL)

            val node = PathNode(
                id = -1,
                gCost = 0,
                hCost = 0,
                parent = null,
                penalty = 0,
                pathTransports = mutableListOf(),
                worldLocation = tile.worldLocation,
                blocked = blocked,
                blockedMovementNorth = collisionMap.contains(WorldMovementFlag.BLOCK_MOVEMENT_NORTH),
                blockedMovementSouth = collisionMap.contains(WorldMovementFlag.BLOCK_MOVEMENT_SOUTH),
                blockedMovementEast = collisionMap.contains(WorldMovementFlag.BLOCK_MOVEMENT_EAST),
                blockedMovementWest = collisionMap.contains(WorldMovementFlag.BLOCK_MOVEMENT_WEST),
            )
            nodes.add(node)
        }

        return nodes
    }

    private fun getMovementFlagsForTile(tile: Tile): Set<WorldMovementFlag> {
        val client = Microbot.getClientForKotlin()
        val collisionMap = client.collisionMaps ?: return HashSet()

        val flags: Array<IntArray> = collisionMap[client.getPlane()].getFlags()
        val data = flags[tile.sceneLocation.x][tile.sceneLocation.y]
        return WorldMovementFlag.getSetFlags(data)
    }

}
