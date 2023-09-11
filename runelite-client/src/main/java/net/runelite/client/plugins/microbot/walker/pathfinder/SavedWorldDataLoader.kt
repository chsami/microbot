package net.runelite.client.plugins.microbot.walker.pathfinder

import net.runelite.api.coords.WorldPoint
import java.io.File
import java.sql.DriverManager


class SavedWorldDataLoader(private val worldDataFile: File) {
    fun getGrid(): Array<Array<Array<PathNode?>>> {
        val nodes = readNodes()
        val transports = readTransports(nodes)

        val maxX = nodes.maxBy { pathNode -> pathNode.worldLocation.x }.worldLocation.x
        val maxY = nodes.maxBy { pathNode -> pathNode.worldLocation.y }.worldLocation.y
        val maxZ = nodes.maxBy { pathNode -> pathNode.worldLocation.plane }.worldLocation.plane

        val grid = Array(maxZ + 1) { Array(maxY + 1) { arrayOfNulls<PathNode>(maxX + 1) } }

        for (node in nodes) {
            grid[node.worldLocation.plane][node.worldLocation.y][node.worldLocation.x] = node
        }

        for (transport in transports) {
            val transportLocation = transport.startPathNode.worldLocation
            val node = grid[transportLocation.plane][transportLocation.y][transportLocation.x]
            node?.pathTransports?.add(transport)
        }

        return grid
    }

    private fun readNodes(): MutableList<PathNode> {
        val nodes: MutableList<PathNode> = mutableListOf()
        val url = "jdbc:sqlite:${worldDataFile.absolutePath}"
//            Class.forName("org.sqlite.JDBC");
        val connection = DriverManager.getConnection(url)

        try {
            val statement = connection.createStatement()
            val rs = statement.executeQuery("select * from tiles_tile;")

            while (rs.next()) {
                val id = rs.getInt("id")
                val x = rs.getInt("x")
                val y = rs.getInt("y")
                val z = rs.getInt("z")
                val operableName = rs.getString("operable_object_name")
                val isOperable = rs.getBoolean("is_operable") && rs.getBoolean("operable_verified") && operableName != null

                val node = PathNode(
                    id = id,
                    gCost = 0,
                    hCost = 0,
                    parent = null,
                    penalty = rs.getInt("penalty"),
                    pathTransports = mutableListOf(),
                    worldLocation = WorldPoint(x, y, z),
                    operableName = operableName,
                    blocked = rs.getBoolean("blocked"),
                    blockedMovementNorth = rs.getBoolean("blocked_movement_north"),
                    blockedMovementSouth = rs.getBoolean("blocked_movement_south"),
                    blockedMovementEast = rs.getBoolean("blocked_movement_east"),
                    blockedMovementWest = rs.getBoolean("blocked_movement_west"),
                )
                nodes.add(node)

            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection.close()
        }
        return nodes
    }

    private fun readTransports(nodes: MutableList<PathNode>): MutableList<PathTransport> {
        val transports = mutableListOf<PathTransport>()
        val url = "jdbc:sqlite:${worldDataFile.absolutePath}"
//            Class.forName("org.sqlite.JDBC");
        val connection = DriverManager.getConnection(url)

        try {

            val statement = connection.createStatement()
            val rs = statement.executeQuery("select t.transport_name, t.object_id, t.object_name, tc.action, tc.start_tile_id, tc.end_tile_id from tiles_transport t join tiles_transportconnection tc on t.id = tc.transport_id;")

            while (rs.next()) {
                val startTileId = rs.getInt("start_tile_id")
                val endTileId = rs.getInt("end_tile_id")

                val startTile = nodes.first { pathNode -> pathNode.id == startTileId }
                val endTile = nodes.firstOrNull { pathNode -> pathNode.id == endTileId }

                val transport = PathTransport(
                    startPathNode = startTile,
                    endPathNode = endTile,
                    name = rs.getString("transport_name").uppercase(),
                    action = rs.getString("action"),
                    objectName = rs.getString("object_name"),
                    objectId = rs.getInt("object_id")
                )
                transports.add(transport)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection.close()
        }
        return transports
    }
}
