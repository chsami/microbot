package net.runelite.client.plugins.microbot.walker.pathfinder

data class PathTransport(
    val startPathNode: PathNode,
    val endPathNode: PathNode?,
    val name: String,
    val action: String,
    val objectName: String,
    val objectId: Int
)
