package net.runelite.client.plugins.griffinplugins.griffintrainer.helpers

import net.runelite.client.plugins.griffinplugins.griffintrainer.TrainerInterruptor
import net.runelite.client.plugins.microbot.Microbot
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc

class NPCHelper {

    companion object {
        fun findAndAttack(npcName: String, waitUntilDespawned: Boolean): Boolean {
            val nearestNpc = Microbot.getClientThreadForKotlin().runOnClientThread { Rs2Npc.getAttackableNpcs(npcName).firstOrNull() }
            val player = Microbot.getClientForKotlin().localPlayer

            nearestNpc ?: return false

            if (nearestNpc.worldLocation.distanceTo(player.worldLocation) >= 3) {
                Microbot.getWalkerForKotlin().hybridWalkTo(nearestNpc.worldLocation)
            }

            if (TrainerInterruptor.isInterrupted) {
                return false
            }

            if (!Rs2Camera.isTileOnScreen(nearestNpc.getLocalLocation())) {
                Rs2Camera.turnTo(nearestNpc.getLocalLocation())
            }

            if (Rs2Npc.interact(nearestNpc, "attack")) {
                if (!TrainerInterruptor.sleepUntilTrue({ player.isInteracting }, 100, 3000)) {
                    return false
                }

                Microbot.status = "Waiting to finish attacking ${npcName}"
//                HelperInterruptor.sleepUntilTrue({ !Rs2Player.isWalking() && !Rs2Player.isAnimating() }, 100, 1000 * 10)
//                HelperInterruptor.sleepUntilTrue({ nearestNpc.isDead || player.isDead }, 200, 1000 * 90)


//                val result = TrainerInterruptor.sleepUntilTrue({
//                    if (player.interacting != nearestNpc) {
//                        return@sleepUntilTrue false
//                    }
//                    return@sleepUntilTrue nearestNpc.isDead || player.isDead
//                }, 100, 1000 * 90)
                var result = false
                var count = 0
                while (true) {
                    if (player.interacting != nearestNpc) {
                        break
                    }

                    result = TrainerInterruptor.sleepUntilTrue({ return@sleepUntilTrue nearestNpc.isDead || player.isDead }, 100, 1000 * 3)
                    count += 3

                    if (count >= 90) {
                        break
                    }
                }

                if (!result) {
                    return false
                }

                if (waitUntilDespawned) {
                    TrainerInterruptor.sleepUntilTrue({ nearestNpc.composition == null }, 100, 1000 * 10)
                }

                return true
            }

            return false
        }
    }
}