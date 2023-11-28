package net.runelite.client.plugins.griffinplugins.util.helpers

import net.runelite.api.GameState
import net.runelite.api.Player
import net.runelite.api.coords.WorldArea
import net.runelite.client.plugins.griffinplugins.griffintrainer.TrainerInterruptor
import net.runelite.client.plugins.microbot.Microbot
import net.runelite.client.plugins.microbot.util.Global
import net.runelite.client.plugins.microbot.util.security.Login
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget

class WorldHelper {

    companion object {
        fun hopToWorld(world: Int): Boolean {
            Microbot.hopToWorld(world)
            TrainerInterruptor.sleepUntilTrue({ Rs2Widget.findWidget("Switch World") != null }, 100, 5000)
            Global.sleep(1000)
            if (!Rs2Widget.clickWidget("Switch World")) {
                return false
            }

            TrainerInterruptor.sleepUntilTrue({ Microbot.getClientForKotlin().gameState != GameState.LOGGED_IN }, 100, 5000)

            return TrainerInterruptor.sleepUntilTrue({
                val inCorrectWorld = Microbot.getClientForKotlin().world == world
                val inCorrectGameState = Microbot.getClientForKotlin().gameState == GameState.LOGGED_IN
                val loadingWidgetAbsent = Rs2Widget.findWidget("please wait") == null
                return@sleepUntilTrue inCorrectWorld && inCorrectGameState && loadingWidgetAbsent
            }, 100, 30000)
        }

        fun hopToWorldWithoutPlayersInArea(isMembers: Boolean, worldArea: WorldArea, maxPlayers: Int, maxWorldsToTry: Int): Boolean {
            val player = Microbot.getClientForKotlin().localPlayer
            val usedWorldIds = mutableListOf<Int>()

            for (index in 0..maxWorldsToTry) {
                if (TrainerInterruptor.isInterrupted) {
                    return false
                }

                val worldId = Login.getRandomWorld(isMembers)

                if (worldId == Microbot.getClientForKotlin().world) {
                    continue
                }

                if (usedWorldIds.contains(worldId)) {
                    continue
                }

                usedWorldIds.add(worldId)

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
            }

            return false
        }
    }
}