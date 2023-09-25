package net.runelite.client.plugins.griffinplugins.util.helpers

import net.runelite.api.GameObject
import net.runelite.client.plugins.microbot.Microbot
import net.runelite.client.plugins.microbot.util.Global
import net.runelite.client.plugins.microbot.util.camera.Camera
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject
import net.runelite.client.plugins.microbot.util.player.Rs2Player

class MiningHelper {

    companion object {
        fun findAndMineOre(oreName: String): Boolean {
            val player = Microbot.getClientForKotlin().localPlayer
            val objects = Rs2GameObject.getGameObjects()
                .filterNotNull()
                .filter { gameObject: GameObject -> Microbot.getClientForKotlin().getObjectDefinition(gameObject.id).name.equals(oreName, ignoreCase = true) }

            if (objects.isEmpty()) {
                Microbot.status = "No ${oreName} ores found"
                return false
            }

            val nearestOre = objects.minBy { gameObject: GameObject -> gameObject.worldLocation.distanceTo(player.worldLocation) }
            if (nearestOre.worldLocation.distanceTo(player.worldLocation) >= 2) {
                Microbot.status = "Walking to nearest ${oreName} ore"
                if (!Camera.isTileOnScreen(nearestOre.localLocation)) {
                    Microbot.getWalkerForKotlin().staticWalkTo(nearestOre.worldLocation)
                }
            }

            if (!Camera.isTileOnScreen(nearestOre.localLocation)) {
                Camera.turnTo(nearestOre.localLocation)
            }

            val success = Rs2GameObject.interact(nearestOre, "Mine")
            if (!success) {
                return false
            }

            Global.sleepUntilTrue({ Rs2Player.isAnimating() }, 100, 3000)
            Microbot.status = "Waiting to finish mining ${oreName} ore"
            return Global.sleepUntilTrue({ !Rs2Player.isWalking() && !Rs2Player.isAnimating() }, 100, 1000 * 90)
        }
    }
}