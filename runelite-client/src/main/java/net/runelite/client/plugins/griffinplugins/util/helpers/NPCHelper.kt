package net.runelite.client.plugins.griffinplugins.griffintrainer.helpers

import net.runelite.client.plugins.microbot.Microbot
import net.runelite.client.plugins.microbot.util.Global
import net.runelite.client.plugins.microbot.util.camera.Camera
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc
import net.runelite.client.plugins.microbot.util.player.Rs2Player

class NPCHelper {

    companion object {
        fun findAndAttack(npcName: String): Boolean {
            val nearestNpc = Microbot.getClientThreadForKotlin().runOnClientThread { Rs2Npc.getAttackableNpcs(npcName).firstOrNull() }
            val player = Microbot.getClientForKotlin().localPlayer

            nearestNpc ?: return false

            if (nearestNpc.worldLocation.distanceTo(player.worldLocation) >= 3) {
                Microbot.getWalkerForKotlin().hybridWalkTo(nearestNpc.worldLocation)
            }

            if (!Camera.isTileOnScreen(nearestNpc.getLocalLocation())) {
                Camera.turnTo(nearestNpc.getLocalLocation())
            }

            if (Rs2Npc.interact(nearestNpc, "attack")) {
                if (!Global.sleepUntilTrue({ player.isInteracting }, 100, 3000)) {
                    return false
                }

                Microbot.status = "Waiting to finish attacking ${npcName}"
                Global.sleepUntilTrue({ !Rs2Player.isWalking() && !Rs2Player.isAnimating() }, 200, 1000 * 10)
                Global.sleepUntilTrue({ nearestNpc.isDead || player.isDead }, 200, 1000 * 90)
                Global.sleep(3000, 3500)
                return true
            }

            return false
        }
    }
}