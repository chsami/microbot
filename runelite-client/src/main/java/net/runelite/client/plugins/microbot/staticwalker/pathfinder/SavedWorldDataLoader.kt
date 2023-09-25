package net.runelite.client.plugins.microbot.staticwalker.pathfinder

import net.runelite.api.coords.WorldPoint
import java.io.File
import java.sql.DriverManager


class SavedWorldDataLoader(private val worldDataFile: File) {


    fun getNodeMap(): MutableMap<String, PathNode> {
        val nodeMap = readNodes()
        val transports = readTransports(nodeMap)

        for (transport in transports) {
            val transportLocation = transport.startPathNode.worldLocation
            val node = nodeMap[transport.startPathNode.mapKey]
            node ?: continue

            node.pathTransports.add(transport)

            if (transport.unblockStartTile) {
                node.blockedMovementNorth = false
                node.blockedMovementSouth = false
                node.blockedMovementEast = false
                node.blockedMovementWest = false
            }

            if (transport.unblockNorthSouth) {
                // north is + 1
                // south is - 1
                val northNode = nodeMap["${transportLocation.x}_${transportLocation.y + 1}_${transportLocation.plane}"]
                val southNode = nodeMap["${transportLocation.x}_${transportLocation.y - 1}_${transportLocation.plane}"]
                northNode?.blockedMovementSouth = false
                southNode?.blockedMovementNorth = false
            }

            if (transport.unblockEastWest) {
                // east, east is + 1
                // west, west is - 1
                val eastNode = nodeMap["${transportLocation.x + 1}_${transportLocation.y}_${transportLocation.plane}"]
                val westNode = nodeMap["${transportLocation.x - 1}_${transportLocation.y}_${transportLocation.plane}"]
                eastNode?.blockedMovementWest = false
                westNode?.blockedMovementEast = false
            }
        }

        return nodeMap
    }

    private fun readNodes(): MutableMap<String, PathNode> {
        val nodeMap: MutableMap<String, PathNode> = mutableMapOf()
        val url = "jdbc:sqlite:${worldDataFile.absolutePath}"
//            Class.forName("org.sqlite.JDBC");
        val connection = DriverManager.getConnection(url)

        try {
            val statement = connection.createStatement()
            val rs = statement.executeQuery(
                """
                select id,
                       x,
                       y,
                       z,
                       penalty,
                       blocked,
                       blocked_movement_north,
                       blocked_movement_south,
                       blocked_movement_east,
                       blocked_movement_west
                from tiles_tile;
            """.trimIndent()
            )

            while (rs.next()) {
                val id = rs.getInt("id")
                val x = rs.getInt("x")
                val y = rs.getInt("y")
                val z = rs.getInt("z")

                val node = PathNode(
                    id = id,
                    gCost = 0,
                    hCost = 0,
                    parent = null,
                    penalty = rs.getInt("penalty"),
                    pathTransports = mutableListOf(),
                    worldLocation = WorldPoint(x, y, z),
                    blocked = rs.getBoolean("blocked"),
                    blockedMovementNorth = rs.getBoolean("blocked_movement_north"),
                    blockedMovementSouth = rs.getBoolean("blocked_movement_south"),
                    blockedMovementEast = rs.getBoolean("blocked_movement_east"),
                    blockedMovementWest = rs.getBoolean("blocked_movement_west"),
                )
                nodeMap[node.mapKey] = node
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection.close()
        }
        return nodeMap
    }

    private fun readTransports(nodeMap: MutableMap<String, PathNode>): MutableList<PathTransport> {
        val idNodeMap: MutableMap<Int, PathNode> = mutableMapOf()
        for (node in nodeMap.values) {
            idNodeMap[node.id] = node
        }

        val transports = mutableListOf<PathTransport>()
        val url = "jdbc:sqlite:${worldDataFile.absolutePath}"
//            Class.forName("org.sqlite.JDBC");
        val connection = DriverManager.getConnection(url)

        try {

            val statement = connection.createStatement()
            val rs = statement.executeQuery(
                """
                select t.object_id,
                       t.additional_object_id,
                       t.unblock_start_tile,
                       t.unblock_north_south,
                       t.unblock_east_west,
                       t.object_name,
                       tc.action,
                       tc.additional_action,
                       tc.start_tile_id,
                       tc.end_tile_id
                from tiles_transport t
                         join tiles_transportconnection tc on t.id = tc.transport_id;
            """.trimIndent()
            )

            while (rs.next()) {
                val startTileId = rs.getInt("start_tile_id")
                val endTileId = rs.getInt("end_tile_id")

                val startTile = idNodeMap[startTileId] ?: throw Exception("No start tile for transport ID: ${rs.getString("transport_name")} and start tile id: ${startTileId}")
                val endTile = idNodeMap.getOrDefault(endTileId, null)

                val transport = PathTransport(
                    startPathNode = startTile,
                    endPathNode = endTile,
                    unblockStartTile = rs.getBoolean("unblock_start_tile"),
                    unblockNorthSouth = rs.getBoolean("unblock_north_south"),
                    unblockEastWest = rs.getBoolean("unblock_east_west"),
                    objectName = rs.getString("object_name"),
                    action = rs.getString("action"),
                    additionalAction = rs.getString("additional_action"),
                    objectId = rs.getInt("object_id"),
                    addtionalObjectId = rs.getInt("additional_object_id")
                )
                transports.add(transport)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection.close()
        }
        println("loaded ${transports.size} transports")
        return transports
    }
}
