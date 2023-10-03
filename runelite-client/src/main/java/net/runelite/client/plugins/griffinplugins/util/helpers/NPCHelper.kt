package net.runelite.client.plugins.griffinplugins.griffintrainer.helpers

import net.runelite.api.AnimationID
import net.runelite.client.plugins.microbot.Microbot
import net.runelite.client.plugins.microbot.util.Global
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

            if (!Rs2Camera.isTileOnScreen(nearestNpc.getLocalLocation())) {
                Rs2Camera.turnTo(nearestNpc.getLocalLocation())
            }

            if (Rs2Npc.interact(nearestNpc, "attack")) {
                if (!Global.sleepUntilTrue({ player.isInteracting }, 100, 3000)) {
                    return false
                }

                Microbot.status = "Waiting to finish attacking ${npcName}"
//                Global.sleepUntilTrue({ !Rs2Player.isWalking() && !Rs2Player.isAnimating() }, 100, 1000 * 10)
//                Global.sleepUntilTrue({ nearestNpc.isDead || player.isDead }, 200, 1000 * 90)


                val result = Global.sleepUntilTrue({
                    if (player.interacting != nearestNpc) {
                        return@sleepUntilTrue false
                    }
                    return@sleepUntilTrue nearestNpc.isDead || player.isDead
                }, 100, 1000 * 90)

                if (!result) {
                    return false
                }

                if (waitUntilDespawned) {
                    Global.sleepUntilTrue({ nearestNpc.composition == null }, 100, 1000 * 10)
                }

                return true
            }

            return false
        }
    }
}