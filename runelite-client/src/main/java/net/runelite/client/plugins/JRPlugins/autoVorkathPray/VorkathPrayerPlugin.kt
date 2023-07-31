package net.runelite.client.plugins.autoVorkathPray

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.runelite.api.Client
import net.runelite.api.Varbits
import net.runelite.api.coords.WorldArea
import net.runelite.client.callback.ClientThread
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.plugins.microbot.util.Global.sleep
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc
import net.runelite.client.plugins.microbot.util.prayer.Rs2Prayer
import java.awt.event.KeyEvent
import javax.inject.Inject

@PluginDescriptor(
    name = "<html><font color=\"#9dfffd\">[JR]</font>Auto Vorkath Prayers",
    description = "JR - Auto prayers for vorkath",
    tags = ["vorkath", "prayers", "auto", "auto prayer"],
    enabledByDefault = false)
class VorkathPrayerPlugin : Plugin() {
    @Inject
    lateinit var client: Client

    @Inject
    lateinit var clientThread: ClientThread

    private var botState: State? = null
    private var previousBotState: State? = null
    private var running = false
    private val rangeProjectileId = 1477
    private val magicProjectileId = 393
    private val purpleProjectileId = 1471
    private val blueProjectileId = 1479

    private enum class State {
        RANGE,
        MAGIC,
        NONE
    }

    @Throws(Exception::class)
    override fun startUp() {
        botState = State.RANGE
        previousBotState = State.NONE
        running = true
        GlobalScope.launch {
            run()
        }
    }

    @Throws(Exception::class)
    override fun shutDown() {
        println("Done")
        running = false
    }

    private suspend fun run() {
        while (running){
            val vorkath = Rs2Npc.getNpc("vorkath")
            // Check if player is in Vorkath Area
            if (vorkath!=null && vorkath.isInteracting){

                // Check if Magic or Range projectile is coming
                if (doesProjectileExistById(magicProjectileId) || doesProjectileExistById(purpleProjectileId) || doesProjectileExistById(blueProjectileId)) {
                    botState = State.MAGIC
                } else if (doesProjectileExistById(rangeProjectileId)) {
                    botState = State.RANGE
                }

                // Handle prayer switching
                when (botState) {
                    // If Magic is coming and Magic prayer is not on, turn on Magic prayer
                    State.MAGIC -> if ((clientThread.runOnClientThread { client.getVarbitValue(Varbits.PRAYER_PROTECT_FROM_MAGIC) == 0 }) || previousBotState != State.MAGIC) {
                        previousBotState = State.MAGIC
                        openPrayerTab()
                        Rs2Prayer.turnOnMagePrayer()
                        openInventoryTab()
                    }
                    // If Range is coming and Range prayer is not on, turn on Range prayer
                    State.RANGE -> if ((clientThread.runOnClientThread { client.getVarbitValue(Varbits.PRAYER_PROTECT_FROM_MISSILES) == 0 }) || previousBotState != State.RANGE) {
                        previousBotState = State.RANGE
                        openPrayerTab()
                        Rs2Prayer.turnOnRangedPrayer()
                        openInventoryTab()
                    }

                    State.NONE -> println("TODO")
                    null -> botState = State.NONE
                }
            }
        }
    }

    // Open Prayer tab
    private fun openPrayerTab() {
        sleep(50, 100)
        VirtualKeyboard.keyPress(KeyEvent.VK_F3)
        sleep(50, 100)
    }

    // Open Inventory tab
    private fun openInventoryTab() {
        sleep(50, 100)
        VirtualKeyboard.keyPress(KeyEvent.VK_F2)
        sleep(50, 100)
    }

    // Check if projectile exists by ID
    private fun doesProjectileExistById(id: Int): Boolean {
        for (projectile in client!!.getProjectiles()) {
            if (projectile.getId() == id) {
                println("Projectile $id found")
                return true
            }
        }
        return false
    }
}
