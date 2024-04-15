package net.runelite.client.plugins.griffinplugins.util.helpers

import net.runelite.api.GameObject
import net.runelite.client.plugins.griffinplugins.griffintrainer.TrainerInterruptor
import net.runelite.client.plugins.microbot.Microbot
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject
import net.runelite.client.plugins.microbot.util.player.Rs2Player
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker

class MiningHelper {

    companion object {
        fun findAndMineOre(oreName: String): Boolean {
            val player = Microbot.getClientForKotlin().localPlayer
            val objects = Microbot.getClientThreadForKotlin().runOnClientThread {
                return@runOnClientThread Rs2GameObject.getGameObjects()
                    .filterNotNull()
                    .filter { gameObject: GameObject -> Microbot.getClientForKotlin().getObjectDefinition(gameObject.id).name.equals(oreName, ignoreCase = true) }
            }

            if (objects.isEmpty()) {
                Microbot.status = "No ${oreName} ores found"
                return false
            }

            val nearestOre = objects.minBy { gameObject: GameObject -> gameObject.worldLocation.distanceTo(player.worldLocation) }
            if (nearestOre.worldLocation.distanceTo(player.worldLocation) >= 2) {
                Microbot.status = "Walking to nearest ${oreName} ore"
                if (!Rs2Camera.isTileOnScreen(nearestOre.localLocation)) {
                    Rs2Walker.walkTo(nearestOre.worldLocation)
                }
            }

            if (TrainerInterruptor.isInterrupted) {
                return false
            }

            if (nearestOre != null) {
                if (!Rs2Camera.isTileOnScreen(nearestOre.localLocation)) {
                    Rs2Camera.turnTo(nearestOre.localLocation)
                }
            }

            val success = Rs2GameObject.interact(nearestOre, "Mine")
            if (!success) {
                return false
            }

            if (nearestOre != null) {
                TrainerInterruptor.sleepUntilTrue({ Rs2Player.isAnimating() || Rs2GameObject.findObject(nearestOre.id, nearestOre.worldLocation) == null }, 100, 3000)
            }
            if (nearestOre != null) {
                if (Rs2GameObject.findObject(nearestOre.id, nearestOre.worldLocation) == null) {
                    return false
                }
            }

            Microbot.status = "Waiting to finish mining ${oreName} ore"
            return TrainerInterruptor.sleepUntilTrue({
                val doneMining = !Rs2Player.isWalking() && !Rs2Player.isAnimating()
                val oreDisappeared = nearestOre?.let { Rs2GameObject.findObject(it.id, nearestOre.worldLocation) } == null
                return@sleepUntilTrue doneMining || oreDisappeared
            }, 100, 1000 * 90)
        }
    }
}