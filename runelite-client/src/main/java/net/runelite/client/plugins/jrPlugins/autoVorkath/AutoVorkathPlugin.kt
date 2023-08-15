package net.runelite.client.plugins.jrPlugins.autoVorkath

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.runelite.api.*
import net.runelite.api.coords.WorldPoint
import net.runelite.api.widgets.Widget
import net.runelite.client.callback.ClientThread
import net.runelite.client.config.ConfigManager
import com.google.inject.Provides
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.plugins.microbot.Microbot
import net.runelite.client.plugins.microbot.Script
import net.runelite.client.plugins.microbot.util.Global.sleep
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject
import net.runelite.client.plugins.microbot.util.inventory.Inventory
import net.runelite.client.plugins.microbot.util.mouse.VirtualMouse
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc
import net.runelite.client.plugins.microbot.util.prayer.Prayer
import net.runelite.client.plugins.microbot.util.prayer.Rs2Prayer
import net.runelite.client.plugins.microbot.util.walker.Walker
import javax.inject.Inject


@PluginDescriptor(
    name = "Auto Vorkath",
    description = "JR - Auto vorkath",
    tags = ["vorkath", "microbot", "auto", "auto prayer"],
    enabledByDefault = false
)
class AutoVorkathPlugin : Plugin() {
    @Inject
    private lateinit var client: Client

    @Inject
    private lateinit var clientThread: ClientThread

    @Inject
    private lateinit var config: AutoVorkathConfig

    @Provides
    fun getConfig(configManager: ConfigManager): AutoVorkathConfig {
        return configManager.getConfig(AutoVorkathConfig::class.java)
    }



    private var botState: State? = null
    private var previousBotState: State? = null
    private var running = false
    private val rangeProjectileId = 1477
    private val magicProjectileId = 393
    private val purpleProjectileId = 1471
    private val blueProjectileId = 1479
    private val whiteProjectileId = 395
    private val redProjectileId = 1481
    private val acidProjectileId = 1483
    private val acidRedProjectileId = 1482

    private lateinit var centerTile: WorldPoint
    private lateinit var rightTile: WorldPoint
    private lateinit var leftTile: WorldPoint

    private var foods: Array<Widget>? = null

    private enum class State {
        RANGE,
        ZOMBIFIED_SPAWN,
        RED_BALL,
        EAT,
        PRAYER,
        ACID,
        NONE
    }

    override fun startUp() {
        botState = State.RANGE
        previousBotState = State.NONE
        running = if(Microbot.isLoggedIn()) true else false
        GlobalScope.launch {
            run()
        }
    }

    override fun shutDown() {
        println("Done")
        running = false
        botState = null
        previousBotState = null
    }

    private fun run() {
        while (running) {
            if (Microbot.pauseAllScripts){ return }
            val vorkath = Rs2Npc.getNpc("vorkath")
            // Check if player is in Vorkath Area
            if (vorkath != null && vorkath.isInteracting) {
                Script.toggleRunEnergy(false)
                centerTile = WorldPoint(vorkath.worldLocation.x + 3, vorkath.worldLocation.y - 5, vorkath.worldLocation.plane)
                rightTile = WorldPoint(centerTile.x + 2, centerTile.y - 3, centerTile.plane)
                leftTile = WorldPoint(centerTile.x - 2, centerTile.y - 3, centerTile.plane)
                // Check what projectile is coming
                if (doesProjectileExistById(redProjectileId)) {
                    botState = State.RED_BALL
                }else if (doesProjectileExistById(acidProjectileId) || doesProjectileExistById(acidRedProjectileId)) {
                    botState = State.ACID
                    //println("Acid")
                } else if (doesProjectileExistById(rangeProjectileId) || doesProjectileExistById(magicProjectileId) || doesProjectileExistById(purpleProjectileId) || doesProjectileExistById(blueProjectileId)) {
                    botState = State.RANGE
                } else if (doesProjectileExistById(whiteProjectileId) || Rs2Npc.getNpc("Zombified Spawn") != null) {
                    botState = State.ZOMBIFIED_SPAWN
                } else if (doesProjectileExistById(redProjectileId)) {
                    botState = State.RED_BALL
                }

                // Check if player needs to eat
                if (clientThread.runOnClientThread { client.getBoostedSkillLevel(Skill.HITPOINTS) } < 40 && botState != State.ACID && botState != State.RED_BALL) {
                    foods = clientThread.runOnClientThread { Inventory.getInventoryFood() }
                    botState = State.EAT
                }

                // Check if player needs to drink prayer potion
                if (clientThread.runOnClientThread { client.getBoostedSkillLevel(Skill.PRAYER) } < 20) {
                    botState = State.PRAYER
                }

                // Handle bot state
                when (botState) {
                    State.RANGE -> if ((clientThread.runOnClientThread { client.getVarbitValue(Varbits.PRAYER_PROTECT_FROM_MISSILES) == 0 }) || previousBotState != State.RANGE) {
                        previousBotState = State.RANGE
                        Rs2Prayer.fastPray(Prayer.PROTECT_RANGE, true)
                        if (config.ACTIVATERIGOUR()){ Rs2Prayer.fastPray(Prayer.RIGOUR, true) }
                        walkToCenterLocation(isPlayerInCenterLocation())
                    }
                    State.ZOMBIFIED_SPAWN -> if (previousBotState != State.ZOMBIFIED_SPAWN) {
                        previousBotState = State.ZOMBIFIED_SPAWN
                        Rs2Prayer.fastPray(Prayer.PROTECT_RANGE, false)
                        if (config.ACTIVATERIGOUR()){ Rs2Prayer.fastPray(Prayer.RIGOUR, false) }
                        Inventory.useItem(config.SLAYERSTAFF().toString())
                        eatAt(75)
                        while (Rs2Npc.getNpc("Zombified Spawn") == null) {
                            sleep(100, 200)
                        }
                        Rs2Npc.attack("Zombified Spawn")
                        sleep(2300, 2500)
                        Inventory.useItem(config.CROSSBOW().toString())
                        eatAt(75)
                        sleep(600, 1000)
                        Rs2Npc.attack("Vorkath")
                    }
                    // If the player is not walking
                    State.RED_BALL -> if (client.localPlayer.idlePoseAnimation == 1 || doesProjectileExistById(redProjectileId)){
                        previousBotState = State.RED_BALL
                        redBallWalk()
                        sleep(1700, 1850)
                        Rs2Npc.attack("Vorkath")
                    }
                    State.ACID -> if (doesProjectileExistById(acidProjectileId) || doesProjectileExistById(acidRedProjectileId) || Rs2GameObject.findObject(ObjectID.ACID_POOL) != null) {
                        previousBotState = State.ACID
                        acidWalk()
                    }
                    State.EAT -> if (foods?.size!! > 0) {
                        VirtualMouse().click(foods!![0].getBounds())
                        botState = previousBotState
                    } else {
                        println("No food found")
                        // Teleport
                        Inventory.useItem(config.TELEPORT().toString())
                    }
                    State.PRAYER -> if (Inventory.findItemContains("prayer") != null) {
                        Inventory.useItemContains("prayer")
                        botState = previousBotState
                    } else {
                        println("No prayer potions found")
                        // Teleport
                        Inventory.useItem(config.TELEPORT().toString())
                    }
                    State.NONE -> println("TODO")
                    else -> botState = State.NONE
                }
            } else if(vorkath == null || vorkath.isDead){
                Rs2Prayer.fastPray(Prayer.PROTECT_RANGE, false)
                if (config.ACTIVATERIGOUR()){ Rs2Prayer.fastPray(Prayer.RIGOUR, false) }
                Script.toggleRunEnergy(true)
            }
        }
    }

    private fun acidWalk() {
        Rs2Prayer.fastPray(Prayer.PROTECT_RANGE, false)
        if (config.ACTIVATERIGOUR()){ Rs2Prayer.fastPray(Prayer.RIGOUR, false) }
        var clickedTile: WorldPoint
        var toggle = true
        while (botState == State.ACID && previousBotState == State.ACID && (doesProjectileExistById(acidProjectileId) || doesProjectileExistById(acidRedProjectileId))) {
            clickedTile = if (toggle) rightTile else leftTile

            // Check if player's location is equal to the clicked tile location or if it's within one tile of the clicked location.
            val currentPlayerLocation = client.localPlayer.worldLocation

            // Ensure player is at the clickedTile.y before toggling
            if(currentPlayerLocation.y != clickedTile.y) {
                // Walk player to clickedTile.y location
                Walker().walkFastCanvas(WorldPoint(currentPlayerLocation.x, clickedTile.y, currentPlayerLocation.plane))
                while (client.localPlayer.worldLocation.y != clickedTile.y) {
                    sleep(1)
                }
            } else {
                if (currentPlayerLocation.distanceTo(clickedTile) <= 1) {
                    toggle = !toggle
                    clickedTile = if (toggle) rightTile else leftTile
                }

                Walker().walkFastCanvas(clickedTile)
                while (client.localPlayer.worldLocation != clickedTile && client.localPlayer.worldLocation.distanceTo(clickedTile) > 1 && client.localPlayer.worldLocation.y == clickedTile.y && Microbot.isWalking()) {
                    sleep(1)
                }
                toggle = !toggle
            }
        }
    }

    private fun eatAt(health: Int){
        if (clientThread.runOnClientThread { client.getBoostedSkillLevel(Skill.HITPOINTS) } < health && Rs2Npc.getNpc("Vorkath") != null){
            foods = clientThread.runOnClientThread { Inventory.getInventoryFood() }
            val food = if(foods?.size!! > 0) foods!![0] else null
            if(food != null){
                VirtualMouse().click(food.getBounds())
            }else{
                //println("No food found")
                // Teleport
                Inventory.useItem(config.TELEPORT().toString())
            }
        }
    }

    // Check if projectile exists by ID
    private fun doesProjectileExistById(id: Int): Boolean {
        for (projectile in client.projectiles) {
            if (projectile.id == id) {
                //println("Projectile $id found")
                return true
            }
        }
        return false
    }

    // Click 2 tiles west of the player's current location
    private fun redBallWalk() {
        val currentPlayerLocation = client.localPlayer.worldLocation
        val twoTilesEastFromCurrentLocation = WorldPoint(currentPlayerLocation.x + 2, currentPlayerLocation.y, 0)
        Walker().walkFastCanvas(twoTilesEastFromCurrentLocation)
    }

    // player location is center location
    private fun isPlayerInCenterLocation(): Boolean {
        val currentPlayerLocation = client.localPlayer.worldLocation
        return currentPlayerLocation.x == centerTile.x && currentPlayerLocation.y == centerTile.y
    }

    // walk to center location
    private fun walkToCenterLocation(isPlayerInCenterLocation: Boolean) {
        if (!isPlayerInCenterLocation) {
            Walker().walkFastCanvas(centerTile)
            sleep(2000, 2100)
            Rs2Npc.attack("Vorkath")
        }
    }
}
