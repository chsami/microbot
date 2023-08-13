package net.runelite.client.plugins.jrPlugins.autoChin

import com.google.inject.Provides
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject
import net.runelite.client.plugins.microbot.util.grounditem.GroundItem
import javax.inject.Inject
import net.runelite.api.Client
import net.runelite.api.ItemID
import net.runelite.api.ObjectID
import net.runelite.client.config.ConfigManager
import net.runelite.client.plugins.jrPlugins.autoVorkath.AutoVorkathConfig
import net.runelite.client.plugins.microbot.Microbot
import net.runelite.client.plugins.microbot.util.Global.sleep

@PluginDescriptor(
    name = "Auto Chinchompa",
    description = "JR - Automatically catches chinchompas",
    tags = ["chinchompas", "hunter", "auto", "catching", "jr", "JR", "microbot"],
    enabledByDefault = false
)
class AutoChinchompasPlugin : Plugin() {

    @Inject
    private lateinit var client: Client

    @Provides
    fun getConfig(configManager: ConfigManager): AutoChinConfig {
        return configManager.getConfig(AutoChinConfig::class.java)
    }

    private var running = false

    private enum class State {
        IDLE,
        CATCHING
    }

    private var currentState = State.IDLE

    override fun startUp() {
        currentState = State.IDLE
        if (client.getLocalPlayer() != null) {
            running = true
            GlobalScope.launch { run() }
        }
    }

    private fun run(){
        while (running) {
            when (currentState) {
                State.IDLE -> handleIdleState()
                State.CATCHING -> handleCatchingState()
            }
        }
    }

    override fun shutDown() {
        running = false
        currentState = State.IDLE
    }

    private fun handleIdleState() {
        try {
            // If there are box traps on the floor, interact with them first
            if (GroundItem.interact(ItemID.BOX_TRAP, "lay", 4)) {
                currentState = State.CATCHING
                return
            }

            // If there are shaking boxes, interact with them
            if (Rs2GameObject.interact(ObjectID.SHAKING_BOX_9383, "reset", 4)) {
                currentState = State.CATCHING
                return
            }

            // Interact with traps that have not caught anything
            if (Rs2GameObject.interact(ObjectID.BOX_TRAP_9385, "reset", 4)) {
                currentState = State.CATCHING
                return
            }
        } catch (e: Exception) {
            //e.printStackTrace()
        }
    }

    private fun handleCatchingState() {
        sleep(8000,8100)
        currentState = State.IDLE
    }
}
