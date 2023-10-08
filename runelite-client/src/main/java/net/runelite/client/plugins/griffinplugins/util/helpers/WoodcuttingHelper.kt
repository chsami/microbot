package net.runelite.client.plugins.griffinplugins.util.helpers

import net.runelite.api.GameObject
import net.runelite.client.plugins.griffinplugins.griffintrainer.TrainerInterruptor
import net.runelite.client.plugins.microbot.Microbot
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject
import net.runelite.client.plugins.microbot.util.player.Rs2Player

class WoodcuttingHelper {

    companion object {
        fun findAndChopTree(treeName: String): Boolean {
            val player = Microbot.getClientForKotlin().localPlayer
            val nearestTree = Rs2GameObject.getGameObjects()
                .filterNotNull()
                .filter { gameObject: GameObject -> Microbot.getClientForKotlin().getObjectDefinition(gameObject.id).name == treeName }
                .minByOrNull { gameObject: GameObject -> gameObject.worldLocation.distanceTo(player.worldLocation) } ?: return false

            if (TrainerInterruptor.isInterrupted) {
                return false
            }

            if (nearestTree.worldLocation.distanceTo(player.worldLocation) >= 2) {
                Microbot.getWalkerForKotlin().staticWalkTo(nearestTree.worldLocation)
            }

            val success = Rs2GameObject.interact(nearestTree, "Chop down")
            if (!success) {
                return false
            }

            TrainerInterruptor.sleepUntilTrue({ Rs2Player.isAnimating() || Rs2GameObject.findObject(nearestTree.id, nearestTree.worldLocation) == null }, 100, 3000)
            if (Rs2GameObject.findObject(nearestTree.id, nearestTree.worldLocation) == null) {
                return false
            }

            Microbot.status = "Waiting to finish chopping down ${treeName}"
            return TrainerInterruptor.sleepUntilTrue({
                val doneChopping = !Rs2Player.isWalking() && !Rs2Player.isAnimating()
                val treeDisappeared = Rs2GameObject.findObject(nearestTree.id, nearestTree.worldLocation) == null
                return@sleepUntilTrue doneChopping || treeDisappeared
            }, 100, 1000 * 90)
        }
    }
}