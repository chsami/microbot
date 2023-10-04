package net.runelite.client.plugins.griffinplugins.transporthelper.transporttypes

import net.runelite.api.Tile
import net.runelite.api.coords.WorldPoint

class DoorTransportCollector(tile: Tile) : BaseTransportCollector(tile) {

    override fun getStartingActions(): List<String> {
        return listOf("Open")
    }

    override fun getEndingActions(): List<String> {
        return listOf("Close")
    }

    override fun getTransportUnblockNorthSouth(): Boolean {
        return !getTransportUnblockEastWest()
    }

    override fun getTransportUnblockEastWest(): Boolean {
        val objectData = getObjectDataFromTile(tile)
        return objectData?.eastWest ?: false
    }

    override fun getTransportEndWorldPoint(): WorldPoint {
        return WorldPoint(0, 0, 0)
    }

    override fun getTransportAction(): String {
        return "Open"
    }
}