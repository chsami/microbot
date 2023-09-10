package net.runelite.client.plugins.alfred.api.rs.walk.pathfinder

import net.runelite.api.GameObject
import net.runelite.api.Perspective
import net.runelite.api.Point
import net.runelite.api.WallObject
import net.runelite.api.coords.LocalPoint
import net.runelite.api.coords.WorldPoint
import net.runelite.client.plugins.microbot.Microbot
import net.runelite.client.plugins.microbot.util.Global
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject
import net.runelite.client.plugins.microbot.util.menu.Rs2Menu
import net.runelite.client.plugins.microbot.util.player.Rs2Player

class PathWalker(private val nodes: List<PathNode>) {

    fun walkPath() {
        val skipDistance = 4
        val upperBound = nodes.size - 1
        var previousNode: PathNode? = null
        var nextNode: PathNode? = null

//        val player = Microbot.getClientForKotlin().localPlayer

        for (currentNode in nodes) {
            val index = nodes.indexOf(currentNode)
            val isLastNode = nodes.indexOf(currentNode) == nodes.lastIndex


//            Utility.retryFunction(3, true) {
//                if (player.runEnergy >= 30 && !player.isRunningActive) {
//                    player.toggleRunning(true)
//                }
//                return@retryFunction true
//            }

            if (index + 1 <= upperBound) {
                nextNode = nodes.get(index + 1)
            }

            if (isLastNode) {
                val minimapPoint = getMinimapPoint(currentNode.worldLocation) ?: continue
                clickPoint(minimapPoint)
                previousNode = currentNode
                continue
            }

            if (currentNode.pathTransports.isNotEmpty()) {
                if (previousNode != null) {
                    val minimapPoint = getMinimapPoint(previousNode.worldLocation)
                    if (minimapPoint != null) {
                        clickPoint(minimapPoint)
                    }
                }

                nextNode ?: continue
                handleTransport(currentNode, nextNode)
                previousNode = currentNode
                continue
            }

            if (previousNode == null && nodes.count() < skipDistance) {
                val minimapPoint = getMinimapPoint(currentNode.worldLocation) ?: continue
                clickPoint(minimapPoint)
                previousNode = currentNode
                continue

            } else if (previousNode == null) {
                previousNode = currentNode
                continue
            }

            val distance = previousNode.worldLocation.distanceTo(currentNode.worldLocation)
            if (distance >= skipDistance) {
                val minimapPoint = getMinimapPoint(currentNode.worldLocation) ?: continue
                clickPointWhileRunning(minimapPoint, currentNode)
                previousNode = currentNode
            }
        }
    }

    private fun getMinimapPoint(worldPoint: WorldPoint): Point? {
        val localPoint = LocalPoint.fromWorld(Microbot.getClientForKotlin(), worldPoint) ?: return null
        return Perspective.localToMinimap(Microbot.getClientForKotlin(), localPoint)
    }

    private fun clickPoint(minimapPoint: Point) {
        Microbot.getMouseForKotlin().click(minimapPoint)
        Global.sleep(1000)
        Global.sleepUntilTrue({ !Rs2Player.isWalking() && !Rs2Player.isAnimating() }, 200, 1000 * 30)
    }

    private fun clickPointWhileRunning(minimapPoint: Point, node: PathNode) {
        val player = Microbot.getClientForKotlin().localPlayer
        Microbot.getMouseForKotlin().click(minimapPoint)
        Global.sleep(1000)
        Global.sleepUntilTrue({
            val distanceToTarget = player.worldLocation.distanceTo(node.worldLocation)
            val isStill = !Rs2Player.isWalking() && !Rs2Player.isAnimating()
            val nearTile = distanceToTarget <= 2
            isStill || nearTile
        }, 200, 1000 * 30)
    }

    private fun handleTransport(pathNode: PathNode, nextNode: PathNode): Boolean {
        Global.sleepUntilTrue({ !Rs2Player.isWalking() && !Rs2Player.isAnimating() }, 200, 1000 * 10)
        Global.sleep(100, 250)

        val transport: PathTransport? = findTransport(pathNode, nextNode)
        if (transport == null) {
            println("cant find transport")
            return false
        }

        val success = operateTransport(transport)
        if (!success) {
            return false
        }

        Global.sleep(1000)
        Global.sleepUntilTrue({ !Rs2Player.isWalking() && !Rs2Player.isAnimating() }, 200, 1000 * 10)
        Global.sleep(250, 500)
        return true
    }

    private fun findTransport(pathNode: PathNode, nextNode: PathNode): PathTransport? {
        return if (pathNode.pathTransports.count() == 1) {
            pathNode.pathTransports.first()
        } else {
            pathNode.pathTransports.firstOrNull { pathTransport: PathTransport -> pathTransport.endPathNode?.worldLocation == nextNode.worldLocation }
        }
    }

    private fun operateTransport(pathTransport: PathTransport): Boolean {
        val player = Microbot.getClientForKotlin().localPlayer

        val transportGameObject = Rs2GameObject.getGameObjects()
            .filter { gameObject: GameObject -> gameObject.id == pathTransport.objectId }
            .filter { gameObject: GameObject -> gameObject.worldLocation.distanceTo(player.worldLocation) < 8 }
            .minByOrNull { gameObject: GameObject -> gameObject.worldLocation.distanceTo(player.worldLocation) }

        val transportWallObject = Rs2GameObject.getWallObjects()
            .filter { wallObject: WallObject -> wallObject.id == pathTransport.objectId }
            .filter { wallObject: WallObject -> wallObject.worldLocation.distanceTo(player.worldLocation) < 8 }
            .minByOrNull { wallObject: WallObject -> wallObject.worldLocation.distanceTo(player.worldLocation) }


        if (transportGameObject == null && transportWallObject == null) {
            println("No transport: ${pathTransport.name}, found with ID: ${pathTransport.objectId}, at location: ${pathTransport.startPathNode.worldLocation}")
            return false
        }

        println("Operating on Transport: ${pathTransport.name}, with action: ${pathTransport.action}")


        if (transportGameObject != null) {
            Rs2Menu.doAction(pathTransport.action, transportGameObject.canvasTilePoly)
        } else {
            Rs2Menu.doAction(pathTransport.action, transportWallObject?.convexHull?.bounds)
        }

        if (pathTransport.endPathNode != null) {
            Global.sleepUntilTrue({ player.worldLocation == pathTransport.endPathNode.worldLocation }, 200, 5000)
        }

        return true
    }
}