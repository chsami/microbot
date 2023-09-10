package net.runelite.client.plugins.alfred.api.rs.walk.pathfinder

import net.runelite.api.coords.WorldPoint

class PathNode(
    var id: Int,
    var gCost: Int,
    var hCost: Int,
    var parent: PathNode?,
    val penalty: Int,
    var pathTransports: MutableList<PathTransport> = mutableListOf(),
    val worldLocation: WorldPoint,
    val operableName: String?,
    val blocked: Boolean,
    val blockedMovementNorth: Boolean,
    val blockedMovementSouth: Boolean,
    val blockedMovementEast: Boolean,
    val blockedMovementWest: Boolean,
) {
    val fCost get() = gCost + hCost
}
