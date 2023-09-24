package net.runelite.client.plugins.kstarplugins.sandMiner

import com.google.inject.Provides
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.runelite.api.Client
import net.runelite.api.Skill
import net.runelite.api.coords.WorldPoint
import net.runelite.api.events.GameTick
import net.runelite.client.config.ConfigManager
import net.runelite.client.eventbus.Subscribe
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.plugins.PluginDescriptor.Kstar
import net.runelite.client.plugins.microbot.Microbot
import net.runelite.client.plugins.microbot.util.Global
import net.runelite.client.plugins.microbot.util.dialogues.Dialogue
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject.findObjectByLocation
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory
import net.runelite.client.ui.overlay.OverlayManager
import java.util.function.BooleanSupplier
import javax.inject.Inject

@PluginDescriptor(
    name = Kstar + "Sand Miner",
    description = "Does not handle equipment or waterskins, users should be ready at the quarry",
    tags = ["sand", "mining", "ironman", "Kstar", "microbot"],
    enabledByDefault = false
)
class SandMiner : Plugin() {
    @Inject
    private lateinit var client: Client

    @Inject
    private lateinit var overlayManager: OverlayManager

    @Inject
    private lateinit var sandMinerOverlay: SandMinerOverlay

    @Inject
    private lateinit var config: SandMinerConfig

    @Provides
    fun getConfig(configManager: ConfigManager): SandMinerConfig {
        return configManager.getConfig(SandMinerConfig::class.java)
    }

    @Subscribe
    fun onGameTick(gameTick: GameTick?) {
        overlayManager.add(sandMinerOverlay)
        overlayActive = true
        time = getElapsedTime()
        xpGained = client.getSkillExperience(Skill.MINING) - startingXp.toLong()
        xpHr = ((xpGained * 3600000.0 / (System.currentTimeMillis() - startTime))).toInt().toString()
    }

    companion object {
        @JvmField
        var xpGained: Long = 0

        @JvmField
        var caught: Long = 0

        lateinit var version: String
        lateinit var currentAction: Action
        lateinit var time: String
        lateinit var xpHr: String
    }

    private var running = false
    private var startTime: Long = 0L
    private var startingXp: Int = 0
    private var currentRock: Int = 0

    private var overlayActive = false

    enum class Action {
        MINING,
        DEPOSITING
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun startUp() {
        currentAction = Action.MINING
        version = "1.0.1"
        startTime = System.currentTimeMillis()
        startingXp = client.getSkillExperience(Skill.MINING)
        currentRock = 0

        if (client.getLocalPlayer() != null) {
            running = true
            GlobalScope.launch { run() }
        }
    }

    private fun run() {
        while (running) {
            when (currentAction) {
                Action.MINING -> handleMining()
                Action.DEPOSITING -> handleDepositing()
            }
        }
    }

    override fun shutDown() {
        running = false
        overlayManager.remove(sandMinerOverlay)
        currentAction = Action.MINING
        overlayActive = false
    }

    private fun handleMining() {
        val firstRock = findObjectByLocation(WorldPoint(3166, 2913, 0))
        val secondRock = findObjectByLocation(WorldPoint(3164, 2914, 0))
        val thirdRock = findObjectByLocation(WorldPoint(3164, 2915, 0))
        val rocks = listOf(firstRock, secondRock, thirdRock)
        val startingPoint = WorldPoint(3166, 2914, 0)

        if(config.dropClues() && Rs2Inventory.findItem("geode") != null) {
            var geode = Rs2Inventory.findItem("geode").name.substringAfter(">").substringBefore('<')
            Rs2Inventory.drop(geode)
            println(geode)
        }

        if(Rs2Inventory.isFull()) {
            currentAction = Action.DEPOSITING
            currentRock = 0
            return
        }

        while (client.localPlayer.worldLocation.distanceTo(startingPoint) >= 3 && running) {
            Microbot.getWalkerForKotlin().walkTo(startingPoint)
            Global.sleep(600, 1200)
        }

        Rs2GameObject.interact(rocks[currentRock])
        Global.sleepUntil(BooleanSupplier { Microbot.isAnimating() })
        Global.sleepUntil(BooleanSupplier { !Microbot.isAnimating() })

        currentRock++
        if (currentRock > 2) currentRock = 0
    }

    private fun handleDepositing() {
        val grinderWorldPoint = WorldPoint(3165, 2914, 0)
        while (client.localPlayer.worldLocation.distanceTo(grinderWorldPoint) >= 3 && running) {
            Microbot.getWalkerForKotlin().walkTo(grinderWorldPoint)
            Global.sleep(600, 1200)
        }
        Rs2GameObject.interact(26199)
        Global.sleepUntil(BooleanSupplier { Dialogue.isInDialogue() })
        Global.sleep(1200, 1800)
        currentAction = Action.MINING
    }

    fun getElapsedTime(): String {
        val elapsed = System.currentTimeMillis() - startTime
        val hours = elapsed / (1000 * 60 * 60)
        val minutes = (elapsed % (1000 * 60 * 60)) / (1000 * 60)
        val seconds = (elapsed % (1000 * 60)) / 1000
        return "%02d:%02d:%02d".format(hours, minutes, seconds)
    }

}
