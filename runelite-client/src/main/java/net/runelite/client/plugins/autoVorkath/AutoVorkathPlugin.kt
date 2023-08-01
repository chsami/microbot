package net.runelite.client.plugins.autoVorkath

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.runelite.api.*
import net.runelite.api.coords.WorldPoint
import net.runelite.api.widgets.Widget
import net.runelite.client.callback.ClientThread
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.plugins.microbot.Microbot
import net.runelite.client.plugins.microbot.util.Global.sleep
import net.runelite.client.plugins.microbot.util.inventory.Inventory
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard
import net.runelite.client.plugins.microbot.util.mouse.Mouse
import net.runelite.client.plugins.microbot.util.mouse.VirtualMouse
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc
import net.runelite.client.plugins.microbot.util.prayer.Rs2Prayer
import net.runelite.client.plugins.microbot.util.walker.Walker
import java.awt.event.KeyEvent
import javax.inject.Inject


@PluginDescriptor(
    name = "AutoVorkath",
    description = "JR - Auto vorkath",
    tags = ["vorkath", "prayers", "auto", "auto prayer"],
    enabledByDefault = false
)
class AutoVorkathPlugin : Plugin() {
    @Inject
    private lateinit var client: Client

    @Inject
    private lateinit var clientThread: ClientThread

    private lateinit var walker: Walker

    private lateinit var mouse: Mouse

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
        MAGIC,
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
        running = true
        walker = Walker()
        mouse = VirtualMouse()
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
            val vorkath = Rs2Npc.getNpc("vorkath")
            // Check if player is in Vorkath Area
            if (vorkath != null && vorkath.isInteracting) {
                centerTile = WorldPoint(vorkath.worldLocation.x + 3, vorkath.worldLocation.y - 5, vorkath.worldLocation.plane)
                rightTile = WorldPoint(centerTile.x + 2, centerTile.y - 3, centerTile.plane)
                leftTile = WorldPoint(centerTile.x - 2, centerTile.y - 3, centerTile.plane)
                // Check what projectile is coming
                if (doesProjectileExistById(redProjectileId)) {
                    botState = State.RED_BALL
                }else if (doesProjectileExistById(acidProjectileId) || doesProjectileExistById(acidRedProjectileId)) {
                    botState = State.ACID
                    println("Acid")
                } else if (doesProjectileExistById(magicProjectileId) || doesProjectileExistById(purpleProjectileId) || doesProjectileExistById(blueProjectileId)) {
                    botState = State.MAGIC
                } else if (doesProjectileExistById(rangeProjectileId)) {
                    botState = State.RANGE
                } else if (doesProjectileExistById(whiteProjectileId) || Rs2Npc.getNpc("Zombified Spawn") != null) {
                    botState = State.ZOMBIFIED_SPAWN
                } else if (doesProjectileExistById(redProjectileId)) {
                    botState = State.RED_BALL
                }

                // Check if player needs to eat
                if (clientThread.runOnClientThread { client.getBoostedSkillLevel(Skill.HITPOINTS) } < 65) {
                    foods = clientThread.runOnClientThread { Inventory.getInventoryFood() }
                    botState = State.EAT
                }

                // Check if player needs to drink prayer potion
                if (clientThread.runOnClientThread { client.getBoostedSkillLevel(Skill.PRAYER) } < 20) {
                    botState = State.PRAYER
                }

                // Handle bot state
                when (botState) {
                    State.MAGIC -> if ((clientThread.runOnClientThread { client.getVarbitValue(Varbits.PRAYER_PROTECT_FROM_MAGIC) == 0 }) || previousBotState != State.MAGIC) {
                        previousBotState = State.MAGIC
                        openPrayerTab()
                        Rs2Prayer.turnOnMagePrayer()
                        openInventoryTab()
                        walkToCenterLocation(isPlayerInCenterLocation())
                    }
                    State.RANGE -> if ((clientThread.runOnClientThread { client.getVarbitValue(Varbits.PRAYER_PROTECT_FROM_MISSILES) == 0 }) || previousBotState != State.RANGE) {
                        previousBotState = State.RANGE
                        openPrayerTab()
                        Rs2Prayer.turnOnRangedPrayer()
                        openInventoryTab()
                        walkToCenterLocation(isPlayerInCenterLocation())
                    }
                    State.ZOMBIFIED_SPAWN -> if (previousBotState != State.ZOMBIFIED_SPAWN) {
                        previousBotState = State.ZOMBIFIED_SPAWN
                        Inventory.useItem(ItemID.SLAYERS_STAFF)
                        while (Rs2Npc.getNpc("Zombified Spawn") == null) {
                            sleep(100, 200)
                        }
                        Rs2Npc.attack("Zombified Spawn")
                        sleep(2300, 2500)
                        Inventory.useItem(ItemID.ARMADYL_CROSSBOW)
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
                    State.ACID -> if (doesProjectileExistById(acidProjectileId) || doesProjectileExistById(acidRedProjectileId)){
                        previousBotState = State.ACID
                        acidWalk()
                    }
                    State.EAT -> if (foods?.size!! > 0) {
                        for (food in foods!!) {
                            mouse.click(food.getBounds())
                            botState = previousBotState
                            break
                        }
                    } else {
                        println("No food found")
                        // Teleport
                        Inventory.useItem(ItemID.CONSTRUCT_CAPET)
                    }
                    State.PRAYER -> if (Inventory.findItemContains("prayer") != null) {
                        Inventory.useItemContains("prayer")
                    } else {
                        println("No prayer potions found")
                        // Teleport
                        Inventory.useItem(ItemID.CONSTRUCT_CAPET)
                    }
                    State.NONE -> println("TODO")
                    else -> botState = State.NONE
                }
            }
        }
    }

    private fun acidWalk() {
        var clickedTile: WorldPoint
        var toggle = true
        while ((doesProjectileExistById(acidProjectileId) || doesProjectileExistById(acidRedProjectileId))) {
            clickedTile = if (toggle) rightTile else leftTile
            println("Player location: ${client.localPlayer.worldLocation}")
            walker.walkFastCanvas(clickedTile)
            println("Walking to $clickedTile")
            while (client.localPlayer.worldLocation != clickedTile) {
                sleep(10, 15)
            }
            toggle = !toggle
        }
    }

    // Open Prayer tab
    private fun openPrayerTab() {
        VirtualKeyboard.keyPress(KeyEvent.VK_F3)
    }

    // Open Inventory tab
    private fun openInventoryTab() {
        VirtualKeyboard.keyPress(KeyEvent.VK_F2)
    }

    // Check if projectile exists by ID
    private fun doesProjectileExistById(id: Int): Boolean {
        for (projectile in client.projectiles) {
            if (projectile.id == id) {
                println("Projectile $id found")
                return true
            }
        }
        return false
    }

    // Click 3 tiles west of the player's current location
    private fun redBallWalk() {
        val currentPlayerLocation = client.localPlayer.worldLocation
        val twoTilesEastFromCurrentLocation = WorldPoint(currentPlayerLocation.x + 2, currentPlayerLocation.y, 0)
        walker.walkFastCanvas(twoTilesEastFromCurrentLocation)
    }

    // player location is center location
    private fun isPlayerInCenterLocation(): Boolean {
        val currentPlayerLocation = client.localPlayer.worldLocation
        return currentPlayerLocation.x == centerTile.x && currentPlayerLocation.y == centerTile.y
    }

    // walk to center location
    private fun walkToCenterLocation(isPlayerInCenterLocation: Boolean) {
        if (!isPlayerInCenterLocation) {
            walker.walkFastCanvas(centerTile)
            sleep(2000, 2100)
            Rs2Npc.attack("Vorkath")
        }
    }
}
