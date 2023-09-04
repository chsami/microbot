package net.runelite.client.plugins.jrPlugins.autoChin

import com.google.inject.Provides
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.runelite.api.Client
import net.runelite.api.ItemID
import net.runelite.api.ObjectID
import net.runelite.api.Skill
import net.runelite.api.events.GameTick
import net.runelite.client.config.ConfigManager
import net.runelite.client.eventbus.Subscribe
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.plugins.PluginDescriptor.JR
import net.runelite.client.plugins.microbot.util.Global.sleep
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem
import net.runelite.client.ui.overlay.OverlayManager
import javax.inject.Inject

@PluginDescriptor(
    name = JR + "Auto Chinchompa",
    description = "JR - Automatically catches chinchompas",
    tags = ["chinchompas", "hunter", "auto", "catching", "jr", "JR", "microbot"],
    enabledByDefault = false
)
class AutoChin : Plugin() {
    @Inject
    private lateinit var client: Client

    @Inject
    private lateinit var overlayManager: OverlayManager

    @Inject
    private lateinit var autoChinOverlay: AutoChinOverlay

    @Inject
    private lateinit var config: AutoChinConfig

    @Provides
    fun getConfig(configManager: ConfigManager): AutoChinConfig {
        return configManager.getConfig(AutoChinConfig::class.java)
    }

    @Subscribe
    fun onGameTick(gameTick: GameTick?) {
        if (config.overlay() && !overlayActive) {
            overlayManager.add(autoChinOverlay)
            overlayActive = true
        }
        if (!config.overlay() && overlayActive) {
            overlayManager.remove(autoChinOverlay)
            overlayActive = false
        }
        time = getElapsedTime()
        xpGained = client.getSkillExperience(Skill.HUNTER) - startingXp.toLong()
        xpHr = ((xpGained * 3600000.0 / (System.currentTimeMillis() - startTime))).toInt().toString()
        caught = xpGained / 265
        lvlsGained = client.getRealSkillLevel(Skill.HUNTER) - startingLvl.toLong()
    }

    companion object {
        @JvmField
        var xpGained: Long = 0

        @JvmField
        var caught: Long = 0

        @JvmField
        var lvlsGained: Long = 0
        lateinit var version: String
        lateinit var currentState: State
        lateinit var time: String
        lateinit var xpHr: String
    }

    private var running = false
    private var startTime: Long = 0L
    private var startingXp: Int = 0
    private var startingLvl: Int = 0

    private var overlayActive = false


    enum class State {
        IDLE,
        CATCHING,
        LAYING
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun startUp() {
        currentState = State.IDLE
        version = "1.0.2"
        startTime = System.currentTimeMillis()
        startingXp = client.getSkillExperience(Skill.HUNTER)
        startingLvl = client.getRealSkillLevel(Skill.HUNTER)

        if (client.getLocalPlayer() != null) {
            running = true
            if (overlayManager != null && config.overlay() && !overlayActive) {
                overlayManager.add(autoChinOverlay)
                overlayActive = true
            }
            GlobalScope.launch { run() }
        }
    }

    private fun run() {
        while (running) {
            when (currentState) {
                State.IDLE -> handleIdleState()
                State.CATCHING -> handleCatchingState()
                State.LAYING -> handleLayingState()
            }
        }
    }

    override fun shutDown() {
        running = false
        overlayManager.remove(autoChinOverlay)
        currentState = State.IDLE
        overlayActive = false
    }

    private fun handleIdleState() {
        try {
            // If there are box traps on the floor, interact with them first
            if (Rs2GroundItem.interact(ItemID.BOX_TRAP, "lay", 4)) {
                currentState = State.LAYING
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
            currentState = State.CATCHING
        }
    }

    private fun handleCatchingState() {
        sleep(8300, 8400)
        currentState = State.IDLE
    }

    private fun handleLayingState() {
        sleep(5500, 5700)
        currentState = State.IDLE
    }

    fun getElapsedTime(): String {
        val elapsed = System.currentTimeMillis() - startTime
        val hours = elapsed / (1000 * 60 * 60)
        val minutes = (elapsed % (1000 * 60 * 60)) / (1000 * 60)
        val seconds = (elapsed % (1000 * 60)) / 1000
        return "%02d:%02d:%02d".format(hours, minutes, seconds)
    }

}


