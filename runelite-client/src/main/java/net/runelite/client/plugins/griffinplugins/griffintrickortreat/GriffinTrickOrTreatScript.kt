package net.runelite.client.plugins.griffinplugins.griffintrickortreat

import java.util.concurrent.TimeUnit
import net.runelite.api.NPC
import net.runelite.api.coords.WorldArea
import net.runelite.api.coords.WorldPoint
import net.runelite.client.plugins.microbot.Microbot
import net.runelite.client.plugins.microbot.Script
import net.runelite.client.plugins.microbot.util.Global
import net.runelite.client.plugins.microbot.util.dialogues.Dialogue
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc
import net.runelite.client.plugins.microbot.util.player.Rs2Player

class GriffinTrickOrTreatScript : Script() {
    companion object {
        val trackedNpcs: MutableList<Int> = mutableListOf()
        var currentNpcId: Int = 0
        var killScript = false
    }

    private val varrockCastleArea = WorldArea(3203, 3460, 20, 12, 0)
    private val varrockCastleWorldPoint = WorldPoint(3212, 3465, 0)

    private enum class ScriptState {
        CHECKING, FINDING, TRICK_OR_TREATING
    }

    private var scriptState: ScriptState = ScriptState.CHECKING

    override fun run(): Boolean {
        scheduledExecutorService.scheduleWithFixedDelay({
            if (!super.run()) return@scheduleWithFixedDelay
            if (killScript){
                shutdown()
                return@scheduleWithFixedDelay
            }

            try {
                val player = Microbot.getClientForKotlin().localPlayer

                when (scriptState) {
                    ScriptState.CHECKING -> {
                        if (!varrockCastleArea.contains(player.worldLocation)) {
                            Microbot.getWalkerForKotlin().staticWalkTo(varrockCastleWorldPoint)
                        }
                        scriptState = ScriptState.FINDING
                    }

                    ScriptState.FINDING -> {
                        val npcs = Rs2Npc.getNpcs("Guard")
                            .filterNotNull()
                            .filter { npc: NPC -> varrockCastleArea.contains(npc.worldLocation) }
                            .filter { npc: NPC -> !trackedNpcs.contains(npc.id) }

                        npcs.forEach { npc: NPC ->
                            trackedNpcs.add(npc.id)
                        }

                        scriptState = ScriptState.TRICK_OR_TREATING
                    }

                    ScriptState.TRICK_OR_TREATING -> {
                        val npcId = trackedNpcs.removeAt(0)
                        currentNpcId = npcId

                        val npc = Rs2Npc.getNpc(npcId)
                        if (npc != null) {
                            Rs2Npc.interact(npc, "Trick-or-Treat")

                            Global.sleepUntilTrue({ Rs2Player.isWalking() }, 100, 1000)
                            Global.sleepUntilTrue({ !Rs2Player.isWalking() }, 100, 5000)
                            Global.sleep(1000)

                            if (Rs2Player.isAnimating()) {
                                println("Waiting for animation to finish")
                                Global.sleepUntilTrue({ !Rs2Player.isAnimating() }, 100, 10000)
                            } else {
                                println("Waiting for dialogue to finish")
                                Global.sleepUntilTrue({ Dialogue.isInDialogue() }, 100, 3000)
                                while (Dialogue.isInDialogue()) {
                                    Dialogue.clickContinue()
                                    Global.sleep(1000)
                                }
                            }
                        }
                        scriptState = ScriptState.CHECKING
                    }
                }


            } catch (ex: Exception) {
                ex.printStackTrace()
            }


        }, 0, 500, TimeUnit.MILLISECONDS)

        return true
    }

}