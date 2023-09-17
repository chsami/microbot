package net.runelite.client.plugins.microbot.staticwalker.pathfinder

import net.runelite.api.coords.WorldPoint

class PathNode(
    var id: Int,
    var gCost: Int,
    var hCost: Int,
    var parent: PathNode?,
    val penalty: Int,
    var pathTransports: MutableList<PathTransport> = mutableListOf(),
    val worldLocation: WorldPoint,
    val blocked: Boolean,
    var blockedMovementNorth: Boolean,
    var blockedMovementSouth: Boolean,
    var blockedMovementEast: Boolean,
    var blockedMovementWest: Boolean,
) {
    val fCost get() = gCost + hCost
    val mapKey get() = "${worldLocation.x}_${worldLocation.y}_${worldLocation.plane}"
}
