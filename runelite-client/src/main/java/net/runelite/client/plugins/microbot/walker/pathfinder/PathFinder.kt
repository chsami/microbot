package net.runelite.client.plugins.microbot.walker.pathfinder

import net.runelite.api.coords.WorldPoint
import java.util.*
import kotlin.math.abs

class PathFinder(private val grid: Array<Array<Array<PathNode?>>>) {
    companion object {
        var path: List<PathNode> = emptyList()
        fun get(): List<PathNode> {
            return path
        }

        fun resetPath() {
            path = emptyList()
        }
    }


    fun findPath(startPoint: WorldPoint, endPoint: WorldPoint): List<PathNode> {
        val startNode = grid[startPoint.plane][startPoint.y][startPoint.x]
        val endNode = grid[endPoint.plane][endPoint.y][endPoint.x]

        if (startNode == null || endNode == null) {
            println("Start or end node is null")
            return emptyList()
        }

        val closedNodes = HashSet<PathNode>()
        val openNodes = PriorityQueue<PathNode> { nodeA, nodeB -> nodeA.fCost.compareTo(nodeB.fCost) }

        openNodes.add(startNode)

        while (openNodes.isNotEmpty()) {
            val currentNode = openNodes.peek()

            openNodes.remove(currentNode)
            closedNodes.add(currentNode)

            if (currentNode == endNode) {
                path = getPath(startNode, currentNode)
                return path
            }

            for (neighbor in getNeighbors(currentNode)) {
                if (closedNodes.contains(neighbor)) {
                    continue
                }

                val newCostToNeighbor = currentNode.gCost + getDistance(currentNode, neighbor) + neighbor.penalty
                if (newCostToNeighbor < neighbor.gCost || !openNodes.contains(neighbor)) {
                    neighbor.gCost = newCostToNeighbor
                    neighbor.hCost = getDistance(neighbor, endNode)
                    neighbor.parent = currentNode

                    if (!openNodes.contains(neighbor)) {
                        openNodes.add(neighbor)
                    }
                }
            }
        }
        return emptyList()
    }

    private fun getDistance(nodeA: PathNode, nodeB: PathNode): Int {
        val distanceX = abs((nodeA.worldLocation.x - nodeB.worldLocation.x).toDouble()).toInt()
        val distanceY = abs((nodeA.worldLocation.y - nodeB.worldLocation.y).toDouble()).toInt()
        return if (distanceX > distanceY) {
            14 * distanceY + 10 * (distanceX - distanceY)
        } else 14 * distanceX + 10 * (distanceY - distanceX)
    }

    private fun getNeighbors(node: PathNode): List<PathNode> {
        var neighbors = mutableListOf<PathNode>()

        // if the node is a transport node with an endpoint, then we should only return the endpoint neighbor
        if (node.pathTransports.isNotEmpty()) {
            val endNodes = node.pathTransports.mapNotNull { pathTransport -> pathTransport.endPathNode }
            if (endNodes.isNotEmpty()) {
                return endNodes
            }
//            return node.pathTransports.map { pathTransport -> pathTransport.startPathNode }.toList()
        }

        val northNeighbor = grid[node.worldLocation.plane][node.worldLocation.y + 1][node.worldLocation.x]
        val southNeighbor = grid[node.worldLocation.plane][node.worldLocation.y - 1][node.worldLocation.x]
        val eastNeighbor = grid[node.worldLocation.plane][node.worldLocation.y][node.worldLocation.x + 1]
        val westNeighbor = grid[node.worldLocation.plane][node.worldLocation.y][node.worldLocation.x - 1]

        northNeighbor?.let { neighbors.add(it) }
        southNeighbor?.let { neighbors.add(it) }
        eastNeighbor?.let { neighbors.add(it) }
        westNeighbor?.let { neighbors.add(it) }

        neighbors = neighbors
            .filter { pathNode -> isNeighborWalkable(node, pathNode) }
            .filter { pathNode -> !pathNode.blocked || pathNode.pathTransports.isNotEmpty() }
            .toMutableList()

        return neighbors
    }

    private fun isNeighborWalkable(parent: PathNode, neighbor: PathNode): Boolean {
        val parentX = parent.worldLocation.x
        val parentY = parent.worldLocation.y
        val parentZ = parent.worldLocation.plane

        val neighborX = neighbor.worldLocation.x
        val neighborY = neighbor.worldLocation.y
        val neighborZ = neighbor.worldLocation.plane

        // north, north is + 1
        if (neighborX == parentX && neighborY == parentY + 1 && neighborZ == parentZ) {
            return !neighbor.blockedMovementSouth
        }

        // south, south is - 1
        if (neighborX == parentX && neighborY == parentY - 1 && neighborZ == parentZ) {
            return !neighbor.blockedMovementNorth
        }

        // east, east is + 1
        if (neighborX == parentX + 1 && neighborY == parentY && neighborZ == parentZ) {
            return !neighbor.blockedMovementWest
        }

        // west, west is - 1
        if (neighborX == parentX - 1 && neighborY == parentY && neighborZ == parentZ) {
            return !neighbor.blockedMovementEast
        }

        return false
    }

    private fun getPath(startNode: PathNode, endNode: PathNode): List<PathNode> {
        val paths = mutableListOf<PathNode>()
        var currentNode = endNode

        while (currentNode != startNode) {
            paths.add(currentNode)
            currentNode = currentNode.parent!!
        }

        return paths.reversed()
    }
}
