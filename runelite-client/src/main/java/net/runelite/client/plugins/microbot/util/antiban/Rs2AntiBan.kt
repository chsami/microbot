package net.runelite.client.plugins.microbot.util.antiban

import net.runelite.api.NPC
import net.runelite.api.NpcID
import net.runelite.api.Player
import net.runelite.api.widgets.Widget
import net.runelite.api.widgets.WidgetInfo
import net.runelite.client.plugins.microbot.Microbot
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget

class Rs2AntiBan {

    companion object {

        val randomEventNPCIDs = listOf(
            NpcID.BEE_KEEPER_6747,
            NpcID.CAPT_ARNAV,
            NpcID.DRUNKEN_DWARF,
            NpcID.SERGEANT_DAMIEN_6743,
            NpcID.FREAKY_FORESTER_6748,
            NpcID.FROG_5429,
            NpcID.GENIE,
            NpcID.GENIE_327,
            NpcID.GILES,
            NpcID.GILES_5441,
            NpcID.NILES,
            NpcID.NILES_5439,
            NpcID.MILES,
            NpcID.MILES_5440,
            NpcID.DR_JEKYLL,
            NpcID.DR_JEKYLL_314,
            NpcID.EVIL_BOB,
            NpcID.EVIL_BOB_6754,
            NpcID.LEO_6746,
            NpcID.MYSTERIOUS_OLD_MAN_6750,
            NpcID.MYSTERIOUS_OLD_MAN_6751,
            NpcID.MYSTERIOUS_OLD_MAN_6752,
            NpcID.MYSTERIOUS_OLD_MAN_6753,
            NpcID.PILLORY_GUARD,
            NpcID.POSTIE_PETE_6738,
            NpcID.QUIZ_MASTER_6755,
            NpcID.RICK_TURPENTINE,
            NpcID.RICK_TURPENTINE_376,
            NpcID.DUNCE_6749,
            NpcID.SANDWICH_LADY,
            NpcID.FLIPPA_6744,
        )

        fun tryFindAndDismissRandomEvent(): Boolean {
            val randomEventNpc = Rs2Npc.getNpcs()
                .filterNotNull()
                .filter { npc: NPC -> randomEventNPCIDs.contains(npc.id) }
                .firstOrNull() ?: return false

            val player = Microbot.getClientForKotlin().localPlayer
            if (randomEventNpc.interacting != player) {
                return false
            }

            if (!Rs2Camera.isTileOnScreen(randomEventNpc.localLocation)) {
                return false
            }

            return Rs2Npc.interact(randomEventNpc, "Dismiss")
        }

        fun getJagexMods(): List<Player> {
            val messages = Microbot.getClientThreadForKotlin().runOnClientThread {
                val chatboxWidget = Rs2Widget.getWidget(WidgetInfo.CHATBOX_MESSAGE_LINES)
                chatboxWidget ?: return@runOnClientThread emptyList<String>()

                return@runOnClientThread chatboxWidget.children
                    ?.filterNotNull()
                    ?.map { widget: Widget -> widget.text } ?: emptyList<String>()
            }

            messages ?: return emptyList()

            val regex = "^<img=[a-zA-Z0-9]*>".toRegex()
            val playerNames = messages
                .filter { message: String -> regex.containsMatchIn(message) }
                .map { message: String -> message.split(">")[1] }
                .map { message: String -> message.dropLast(1) }
                .distinct()

            val players = Microbot.getClientForKotlin().players
            return players.filterNotNull().filter { player: Player -> playerNames.contains(player.name) }
        }
    }
}