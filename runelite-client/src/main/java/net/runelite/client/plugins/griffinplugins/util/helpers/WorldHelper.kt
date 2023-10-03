package net.runelite.client.plugins.griffinplugins.util.helpers

import net.runelite.api.GameState
import net.runelite.api.Player
import net.runelite.api.coords.WorldArea
import net.runelite.client.plugins.microbot.Microbot
import net.runelite.client.plugins.microbot.util.Global
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget
import net.runelite.http.api.worlds.WorldType

class WorldHelper {

    companion object {
        fun hopToWorld(world: Int): Boolean {
            Microbot.hopToWorld(world)
            Global.sleep(1000)
            if (!Rs2Widget.clickWidget("Switch World")) {
                return false
            }
            return Global.sleepUntilTrue({
                val inCorrectWorld = Microbot.getClientForKotlin().world == world
                val inCorrectGameState = Microbot.getClientForKotlin().gameState == GameState.LOGGED_IN
                val loadingWidgetAbsent = Rs2Widget.findWidget("please wait") == null
                return@sleepUntilTrue inCorrectWorld && inCorrectGameState && loadingWidgetAbsent
            }, 100, 15000)
        }

        fun hopToWorldWithoutPlayersInArea(isMembers: Boolean, worldArea: WorldArea, maxPlayers: Int, maxWorldsToTry: Int): Boolean {
            val worlds = Microbot.getWorldServiceForKotlin().worlds ?: return false

            val worldsToHopTo = worlds.worlds.filterNotNull()
            if (worldsToHopTo.isEmpty()) {
                return false
            }

            val worldIds: MutableList<Int> = mutableListOf()

            if (!isMembers) {
                worldIds.addAll(worldsToHopTo.filter { !it.types.contains(WorldType.MEMBERS) }.map { it.id })
            } else {
                worldIds.addAll(worldsToHopTo.map { it.id })
            }

            worldIds.shuffle()

            val player = Microbot.getClientForKotlin().localPlayer
            var triedWorlds = 0
            for (worldId in worldIds) {
                if (worldId == Microbot.getClientForKotlin().world) {
                    continue
                }

                if (hopToWorld(worldId)) {
                    val players = Microbot.getClientForKotlin().players
                    val playerCount = players
                        .filterNotNull()
                        .filter { otherPlayer: Player -> otherPlayer.id != player.id }
                        .filter { otherPlayer: Player -> worldArea.contains(player.worldLocation) }
                        .count()

                    if (playerCount <= maxPlayers) {
                        return true
                    }
                }

                triedWorlds++
                if (triedWorlds >= maxWorldsToTry) {
                    return false
                }
            }

            return false
        }
    }
}