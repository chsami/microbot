package net.runelite.client.plugins.griffinplugins.util.helpers

import net.runelite.api.NPC
import net.runelite.client.plugins.griffinplugins.griffintrainer.TrainerInterruptor
import net.runelite.client.plugins.microbot.Microbot
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc
import net.runelite.client.plugins.microbot.util.player.Rs2Player
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker

class FishingHelper {
    companion object {
        fun findAndInteract(fishingSpotName: String, action: String): Boolean {
            val player = Microbot.getClientForKotlin().localPlayer
            val nearestNpc = Microbot.getClientThreadForKotlin().runOnClientThread {
                Rs2Npc.getNpcs()
                    .filterNotNull()
                    .filter { npc: NPC -> npc.name.equals(fishingSpotName, ignoreCase = true) }
                    .minByOrNull { npc: NPC -> npc.worldLocation.distanceTo(player.worldLocation) }
            }

            nearestNpc ?: return false

            if (TrainerInterruptor.isInterrupted) {
                return false
            }

            if (!Rs2Camera.isTileOnScreen(nearestNpc.localLocation)) {
                Rs2Walker.walkTo(nearestNpc.worldLocation)
            }

            if (TrainerInterruptor.isInterrupted) {
                return false
            }

            val success = Rs2Npc.interact(nearestNpc, action)
            if (!success) {
                return false
            }

            TrainerInterruptor.sleepUntilTrue(Rs2Player::isAnimating, 100, 1000 * 10)
            Microbot.status = "Waiting to finish fishing"
            return TrainerInterruptor.sleepUntilTrue({ !Rs2Player.isMoving() && !Rs2Player.isAnimating() }, 100, 1000 * 60 * 5)
        }
    }
}