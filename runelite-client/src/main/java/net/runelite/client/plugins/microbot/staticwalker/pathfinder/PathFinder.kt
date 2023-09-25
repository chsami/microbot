package net.runelite.client.plugins.microbot.staticwalker.pathfinder

import net.runelite.api.coords.WorldPoint
import java.util.*
import kotlin.math.abs

class PathFinder(private val nodeMap: MutableMap<String, PathNode>) {
    companion object {
        var path: List<PathNode> = emptyList()
        fun get(): List<PathNode> {
            return path;
        }
        fun resetPath() {
            path = emptyList()
        }
    }

    fun findPath(startPoint: WorldPoint, endPoint: WorldPoint, useNearest: Boolean = false): List<PathNode> {
        val startPointKey = "${startPoint.x}_${startPoint.y}_${startPoint.plane}"
        val endPointKey = "${endPoint.x}_${endPoint.y}_${endPoint.plane}"

        val startNode = nodeMap[startPointKey]
        val endNode = nodeMap[endPointKey]

        if (startNode == null || endNode == null) {
            println("Start or end node is null")
            return emptyList()
        }

        val closedNodes = HashSet<PathNode>()
        val openNodes = PriorityQueue<PathNode> { nodeA, nodeB -> nodeA.fCost.compareTo(nodeB.fCost) }

        var nearestNode: PathNode? = null
        var nearestDistance = startPoint.distanceTo(endPoint)

        openNodes.add(startNode)

        while (openNodes.isNotEmpty()) {
            val currentNode = openNodes.peek()

            if (nearestNode != null) {
                val distanceToEndPoint = currentNode.worldLocation.distanceTo(endPoint)
                if (distanceToEndPoint < nearestDistance) {
                    nearestNode = currentNode;
                    nearestDistance = distanceToEndPoint
                }
            } else {
                nearestNode = currentNode
            }

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

        if (useNearest && nearestNode != null) {
            println("FOUND PARTIAL PATH")
            path = getPath(startNode, nearestNode)
            return path
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
        }

        val northNeighborKey = "${node.worldLocation.x}_${node.worldLocation.y + 1}_${node.worldLocation.plane}"
        val southNeighborKey = "${node.worldLocation.x}_${node.worldLocation.y - 1}_${node.worldLocation.plane}"
        val eastNeighborKey = "${node.worldLocation.x + 1}_${node.worldLocation.y}_${node.worldLocation.plane}"
        val westNeighborKey = "${node.worldLocation.x - 1}_${node.worldLocation.y}_${node.worldLocation.plane}"

        val northNeighbor = nodeMap.getOrDefault(northNeighborKey, null)
        val southNeighbor = nodeMap.getOrDefault(southNeighborKey, null)
        val eastNeighbor = nodeMap.getOrDefault(eastNeighborKey, null)
        val westNeighbor = nodeMap.getOrDefault(westNeighborKey, null)

        northNeighbor?.let { neighbors.add(it) }
        southNeighbor?.let { neighbors.add(it) }
        eastNeighbor?.let { neighbors.add(it) }
        westNeighbor?.let { neighbors.add(it) }

        neighbors = neighbors
            .filter { pathNode -> isNeighborWalkable(node, pathNode) }
            .filter { pathNode -> !pathNode.blocked || pathNode.pathTransports.isNotEmpty() }
            .toMutableList()

        if (node.pathTransports.isNotEmpty()) {
            neighbors.addAll(node.pathTransports.map { pathTransport: PathTransport -> pathTransport.startPathNode })
        }

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

