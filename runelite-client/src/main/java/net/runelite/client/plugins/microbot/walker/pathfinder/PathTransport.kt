package net.runelite.client.plugins.microbot.walker.pathfinder

data class PathTransport(
    val startPathNode: PathNode,
    val endPathNode: PathNode?,
    val unblockStartTile: Boolean,
    val unblockNorthSouth: Boolean,
    val unblockEastWest: Boolean,
    val objectName: String,
    val action: String,
    val additionalAction: String?,
    val objectId: Int,
    val addtionalObjectId: Int?
)
