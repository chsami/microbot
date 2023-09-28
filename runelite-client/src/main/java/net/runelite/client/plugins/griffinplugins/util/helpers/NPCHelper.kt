package net.runelite.client.plugins.griffinplugins.griffintrainer.helpers

import net.runelite.api.NPC
import net.runelite.client.plugins.microbot.Microbot
import net.runelite.client.plugins.microbot.util.Global
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc
import net.runelite.client.plugins.microbot.util.player.Rs2Player

class NPCHelper {

    companion object {

        private fun waitUntilFininshedAttacking(npc: NPC): Boolean {
            val player = Microbot.getClientForKotlin().localPlayer
            var count = 0

            while (true) {
                if (count > 90) {
                    return false
                }

                if (!Global.sleepUntilTrue({ npc.isDead || player.isDead }, 200, 1000)) {
                    return true
                }

                count++
            }

        }

        fun findAndAttack(npcName: String): Boolean {
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

//                if (!Global.sleepUntilTrue({ player.interacting == nearestNpc }, 100, 1000 * 2)) {
//                    return false
//                }


                Microbot.status = "Waiting to finish attacking ${npcName}"
                Global.sleepUntilTrue({ !Rs2Player.isWalking() && !Rs2Player.isAnimating() }, 100, 1000 * 10)
                Global.sleepUntilTrue({ nearestNpc.isDead || player.isDead }, 200, 1000 * 90)
//                if (waitUntilFininshedAttacking(nearestNpc)) {
//                    Global.sleep(3000, 3500)
//                }

                return true
            }

            return false
        }
    }
}