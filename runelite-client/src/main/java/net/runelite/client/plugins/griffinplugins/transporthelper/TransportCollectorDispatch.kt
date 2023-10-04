package net.runelite.client.plugins.griffinplugins.transporthelper

import net.runelite.api.Tile
import net.runelite.client.plugins.griffinplugins.transporthelper.transporttypes.BaseTransportCollector
import net.runelite.client.plugins.griffinplugins.transporthelper.transporttypes.DoorTransportCollector

class TransportCollectorDispatch {

    fun getCollector(tile: Tile): BaseTransportCollector? {
        val objectData = TransportUtility.getObjectDataFromTile(tile) ?: return null
        if (objectData.objectName == null) {
            return null
        }

        val name = objectData.objectName!!.lowercase()

        if (name.contains("door", true)) {
            return DoorTransportCollector(tile)
        } else if (name.contains("gate", true)) {
            return DoorTransportCollector(tile)
        } else {
            return null
        }
    }
}