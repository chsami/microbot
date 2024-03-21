/*
 * Copyright (c) 2024. By Jrod7938
 *
 */
package net.runelite.client.plugins.jrPlugins.autoVorkath

import com.google.inject.Provides
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.runelite.api.*
import net.runelite.api.coords.LocalPoint
import net.runelite.api.coords.WorldArea
import net.runelite.api.coords.WorldPoint
import net.runelite.api.events.*
import net.runelite.client.callback.ClientThread
import net.runelite.client.config.ConfigManager
import net.runelite.client.eventbus.Subscribe
import net.runelite.client.events.NpcLootReceived
import net.runelite.client.game.ItemManager
import net.runelite.client.game.ItemStack
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.plugins.PluginManager
import net.runelite.client.plugins.microbot.Microbot
import net.runelite.client.plugins.microbot.util.Global.sleepUntil
import net.runelite.client.plugins.PluginDescriptor.JR
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject
import net.runelite.client.plugins.microbot.util.grandexchange.Rs2GrandExchange
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc
import net.runelite.client.plugins.microbot.util.player.Rs2Player
import net.runelite.client.plugins.microbot.util.prayer.Rs2Prayer
import net.runelite.client.plugins.microbot.util.walker.Walker
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget
import net.runelite.client.plugins.microbot.util.prayer.Rs2PrayerEnum
import net.runelite.client.ui.overlay.OverlayManager
import java.awt.event.KeyEvent
import java.lang.Thread.sleep
import javax.inject.Inject
import kotlin.math.abs


@PluginDescriptor(
    name = JR + "Auto Vorkath",
    description = "JR - Auto Vorkath",
    tags = ["vorkath", "auto", "auto prayer", "mule", "trade"],
    enabledByDefault = false
)
class AutoVorkathPlugin : Plugin() {
    @Inject
    private lateinit var client: Client

    @Inject
    private lateinit var clientThread: ClientThread

    @Inject
    private lateinit var pluginManager: PluginManager

    @Inject
    private lateinit var overlayManager: OverlayManager

    @Inject
    private lateinit var autoVorkathOverlay: AutoVorkathOverlay

    @Inject
    private lateinit var config: AutoVorkathConfig

    @Inject
    private lateinit var itemManager: ItemManager

    @Provides
    fun getConfig(configManager: ConfigManager): AutoVorkathConfig {
        return configManager.getConfig(AutoVorkathConfig::class.java)
    }

    var botState: State = State.NONE
    var killCount: Int = 0
    private var running = false
    private val redProjectileId = 1481
    private val acidProjectileId = 1483
    private val acidRedProjectileId = 1482
    private val whiteProjectileId = 395

    private var isPrepared = false
    private var drankAntiFire = false
    private var drankRangePotion = false
    private var lastDrankAntiFire: Long = 0
    private var lastDrankRangePotion: Long = 0

    private val lootQueue: MutableList<ItemStack> = mutableListOf()
    private var lootNames: MutableSet<String> = mutableSetOf()
    private var acidPools: HashSet<WorldPoint> = hashSetOf()

    private var initialAcidMove = false

    private var redBallLocation: WorldPoint = WorldPoint(0, 0, 0)

    private val bankArea: WorldArea = WorldArea(2096, 3911, 20, 11, 0)
    private val bankLocation: WorldPoint = WorldPoint(2099, 3919, 0)
    private val fremennikArea: WorldArea = WorldArea(2627, 3672, 24, 30, 0)

    enum class State {
        WALKING_TO_BANK,
        BANKING,
        WALKING_TO_VORKATH,
        PREPARE,
        POKE,
        FIGHTING,
        ACID,
        SPAWN,
        RED_BALL,
        LOOTING,
        THINKING,
        NONE
    }

    override fun startUp() {
        println("Auto Vorkath Plugin Activated")
        botState = State.THINKING
        running = client.gameState == GameState.LOGGED_IN
        lootNames = mutableSetOf()
        overlayManager.add(autoVorkathOverlay)
        GlobalScope.launch { autoVorkath() }
    }

    override fun shutDown() {
        println("Auto Vorkath Plugin Deactivated")
        running = false
        botState = State.NONE
        drankAntiFire = false
        drankRangePotion = false
        lastDrankAntiFire = 0
        lastDrankRangePotion = 0
        killCount = 0
        lootQueue.clear()
        lootNames.clear()
        acidPools.clear()
        overlayManager.remove(autoVorkathOverlay)
    }

    @Subscribe
    fun onChatMessage(e: ChatMessage) {
        if (e.message.contains("Oh dear, you are dead!")) {
            drankAntiFire = false
            drankRangePotion = false
            isPrepared = false
            activatePrayers(false)
            pluginManager.stopPlugin(this)
        }
        if (e.message.contains("Your Vorkath kill count is:")) {
            activatePrayers(false)
            drankAntiFire = false
            drankRangePotion = false
            isPrepared = false
        }
        if (e.message.contains("There is no ammo left in your quiver.")) {
            teleToHouse()
            Microbot.showMessage("No ammo, stopping plugin.")
            drankAntiFire = false
            drankRangePotion = false
            isPrepared = false
            activatePrayers(false)
            pluginManager.stopPlugin(this)
        }
    }

    @Subscribe
    fun onNpcLootReceived(event: NpcLootReceived) {
        if (!running) return
        val items = event.items
        items.stream().forEach { item ->
            if (item != null) {
                lootQueue.add(item)
                val comp: ItemComposition = itemManager.getItemComposition(item.id)
                lootNames.add(comp.name)
            }
        }
        killCount++
        changeStateTo(State.LOOTING, 2)
    }

    @Subscribe
    fun onNpcDespawned(e: NpcDespawned) {
        if (e.npc.name == "Zombified Spawn") {
            changeStateTo(State.FIGHTING)
        }
    }

    @Subscribe
    fun onProjectileMoved(e: ProjectileMoved) {
        when (e.projectile.id) {
            acidProjectileId -> {
                clientThread.runOnClientThread { acidPools.add(WorldPoint.fromLocal(client, e.position)) }
                changeStateTo(State.ACID)
            }

            whiteProjectileId -> changeStateTo(State.SPAWN)
            acidRedProjectileId -> changeStateTo(State.ACID)
            redProjectileId -> {
                redBallLocation = clientThread.runOnClientThread { WorldPoint.fromLocal(client, e.position) }
                changeStateTo(State.RED_BALL)
            }
        }
    }

    @Subscribe
    fun onGameObjectDespawned(e: GameObjectDespawned) {
        if (e.gameObject.id == 32000) {
            acidPools.clear()
            changeStateTo(State.FIGHTING)
        }
    }


    private fun autoVorkath() {
        while (running) {
            when (botState) {
                State.WALKING_TO_BANK -> walkingToBankState()
                State.BANKING -> bankingState()
                State.WALKING_TO_VORKATH -> walkingToVorkathState()
                State.PREPARE -> prepareState()
                State.POKE -> pokeState()
                State.FIGHTING -> fightingState()
                State.ACID -> acidState()
                State.SPAWN -> spawnState()
                State.RED_BALL -> redBallState()
                State.LOOTING -> lootingState()
                State.THINKING -> thinkingState()
                State.NONE -> println("None State")
            }
        }
    }

    private fun lootingState() {
        if (lootQueue.isEmpty()) {
            //println("Queue empty")
            changeStateTo(State.WALKING_TO_BANK)
            return
        }
        activatePrayers(false)
        Rs2Inventory.wield(config.CROSSBOW().toString())
        lootQueue.firstOrNull()?.let {
            //println("Queue first")
            if (clientThread.runOnClientThread { !Rs2Player.isMoving() }) {
                if (clientThread.runOnClientThread { !Rs2Inventory.isFull() }) {
                    Rs2GroundItem.loot(it.id)
                    //println("Looting")
                    lootQueue.removeAt(lootQueue.indexOf(it))
                    //println("Removed")
                    return
                } else {
                    Microbot.showMessage("Inventory full, going to bank.")
                    lootQueue.clear()
                    changeStateTo(State.WALKING_TO_BANK)
                    return
                }
            }
        } ?: run { changeStateTo(State.WALKING_TO_BANK) }
    }

    private fun acidState() {
        if (runIs(true)) {
            enableRun(false)
            return
        }
        activatePrayers(false)
        if (!inVorkathArea()) {
            acidPools.clear()
            changeStateTo(State.THINKING)
            return
        }

        if (acidPools.isNotEmpty()){
            val vorkath = Rs2Npc.getNpc("Vorkath")
            val swPoint = WorldPoint(vorkath.worldLocation.x + 1, vorkath.worldLocation.y - 8, 0)

            fun findSafeTiles(): WorldPoint? {
                val wooxWalkArea = WorldArea(swPoint, 5, 1)
                //println("Woox Walk Area: ${wooxWalkArea.toWorldPointList()}")

                fun isTileSafe(tile: WorldPoint): Boolean = tile !in acidPools
                        && WorldPoint(tile.x, tile.y + 1, tile.plane) !in acidPools
                        && WorldPoint(tile.x, tile.y + 2, tile.plane) !in acidPools
                        && WorldPoint(tile.x, tile.y + 3, tile.plane) !in acidPools


                val safeTiles = wooxWalkArea.toWorldPointList().filter { isTileSafe(it) }

                // Find the closest safe tile by x-coordinate to the player
                return clientThread.runOnClientThread { safeTiles.minByOrNull { abs(it.x - client.localPlayer.worldLocation.x) } }
            }

            val safeTile: WorldPoint? = findSafeTiles()
            //println("Acid pools: $acidPools")
            //println("Left Tile: $swPoint")
            //println("Safe tile: $safeTile")

            safeTile?.let {
                if (clientThread.runOnClientThread { client.localPlayer.worldLocation == safeTile }) {
                    // Attack Vorkath if the player close to the safe tile
                    Rs2Npc.interact(vorkath, "Attack")
                    println("Attacked")
                    sleepUntil { Rs2Player.isInteracting() || !isMoving() }
                } else {
                    // Move to the safe tile if the player is not close enough
                    //println("Moving to safe tile: $safeTile")
                    //println("Player location: $playerLocation")
                    Microbot.getWalkerForKotlin().walkFastLocal(LocalPoint.fromWorld(client, it))
                    println("Walked back")
                    sleepUntil { clientThread.runOnClientThread { client.localPlayer.worldLocation == safeTile } }
                }
            } ?: run {
                Microbot.showMessage("NO SAFE TILES! TELEPORTING TF OUT!")
                teleToHouse()
                changeStateTo(State.WALKING_TO_BANK)
            }
        } else {
            changeStateTo(State.FIGHTING)
            return
        }

    }

    private fun redBallState() {
        val safeTile = WorldPoint(redBallLocation.x + 2, redBallLocation.y, redBallLocation.plane)
        Microbot.getWalkerForKotlin().walkTo(safeTile)
        drinkPrayer()
        eat(config.EATAT())
        sleepUntil {
            clientThread.runOnClientThread { client.localPlayer.worldLocation == safeTile } && !doesProjectileExistById(
                redProjectileId
            )
        }
        changeStateTo(State.FIGHTING)
    }

    private fun spawnState() {
        if (!inVorkathArea()) {
            changeStateTo(State.THINKING)
            return
        }
        activatePrayers(false)
        drinkPrayer()
        if (!Rs2Equipment.hasEquippedContains(config.SLAYERSTAFF().toString())) {
            Rs2Inventory.wield(config.SLAYERSTAFF().toString())
            sleepUntil { Rs2Equipment.hasEquippedContains(config.SLAYERSTAFF().toString()) }
            return
        } else {
            if (Rs2Npc.getNpc("Zombified Spawn") != null) {
                Rs2Npc.interact("Zombified Spawn", "Attack")
                sleepUntil { Rs2Npc.getNpc("Zombified Spawn") == null }
                Rs2Inventory.wield(config.CROSSBOW().toString())
                sleepUntil { Rs2Equipment.hasEquippedContains(config.CROSSBOW().toString()) }
                changeStateTo(State.FIGHTING)
                return
            }
        }
    }

    private fun fightingState() {
        if (doesProjectileExistById(redProjectileId)) {
            changeStateTo(State.RED_BALL)
            return
        }
        if (doesProjectileExistById(acidProjectileId) || doesProjectileExistById(acidRedProjectileId)){
            changeStateTo(State.ACID)
            return
        }
        if (runIs()) enableRun(true)
        activatePrayers(true)
        if (!inVorkathArea()) {
            changeStateTo(State.THINKING)
            return
        } else {
            if (isVorkathAsleep()) {
                changeStateTo(State.WALKING_TO_BANK)
                return
            }
            val vorkath = Rs2Npc.getNpc(8061) ?: return
            val middle = WorldPoint(vorkath.worldLocation.x + 3, vorkath.worldLocation.y - 5, 0)
            if (clientThread.runOnClientThread { client.localPlayer.interacting == null }) {
                Rs2Npc.interact(vorkath, "Attack")
                sleep(300)
                return
            }
            if (clientThread.runOnClientThread { client.localPlayer.worldLocation != middle }) {
                if (!isMoving()) {
                    Microbot.getWalkerForKotlin().walkFastLocal(LocalPoint.fromWorld(client, middle))
                }
            }
            eat(config.EATAT())
            if (hasItem(config.CROSSBOW().toString())) {
                Rs2Inventory.wield(config.CROSSBOW().toString())
                sleepUntil { Rs2Equipment.hasEquippedContains(config.CROSSBOW().toString()) }
            }
        }
    }

    private fun pokeState() {
        if (isVorkathAsleep()) {
            acidPools.clear()
            lootQueue.clear()
            if (!isMoving()) {
                Rs2Npc.interact("Vorkath", "Poke")
                sleepUntil { !isVorkathAsleep() }
                return
            }
        } else {
            val vorkath = Rs2Npc.getNpc("Vorkath")
            val middle = WorldPoint(vorkath.worldLocation.x + 3, vorkath.worldLocation.y - 5, 0)
            Microbot.getWalkerForKotlin().walkFastLocal(LocalPoint.fromWorld(client, middle))
            sleepUntil { clientThread.runOnClientThread { client.localPlayer.worldLocation == middle } }
            changeStateTo(State.FIGHTING)
            return
        }
    }

    private fun walkingToVorkathState() {
        if (runIs()) enableRun(true)
        activatePrayers(false)
        if (!isMoving()) {
            if (clientThread.runOnClientThread { bankArea.contains(client.localPlayer.worldLocation) }) {
                if (Rs2Widget.hasWidget("Click here to continue")) {
                    sendKey(KeyEvent.VK_SPACE)
                    sleep(1000)
                    return
                }
                if (clientThread.runOnClientThread { client.localPlayer.worldLocation != bankLocation }) {
                    Walker().walkTo(bankLocation)
                    return
                } else {
                    if (!Rs2Widget.hasWidget("Click here to continue")) {
                        Rs2Npc.interact("Sirsal Banker", "Talk-to")
                        sleepUntil { Rs2Widget.hasWidget("Click here to continue") }
                        return
                    }
                }
            } else {
                if (inVorkathArea()) {
                    changeStateTo(State.THINKING, 3)
                    return
                }
                if (clientThread.runOnClientThread { fremennikArea.contains(client.localPlayer.worldLocation) }) {
                    Rs2GameObject.interact(29917, "Travel")
                    sleepUntil { !Microbot.isMoving() }
                    return
                } else {
                    if (Rs2GameObject.exists(31990)) {
                        Rs2GameObject.interact(31990, "Climb-over")
                        sleepUntil { inVorkathArea() }
                        return
                    } else {
                        changeStateTo(State.WALKING_TO_BANK)
                        return
                    }
                }
            }
        }
    }

    private fun bankingState() {
        activatePrayers(false)
        if (clientThread.runOnClientThread { bankArea.contains(client.localPlayer.worldLocation) }) {
            if (!isMoving()) {
                if (!Rs2Bank.isOpen()) {
                    if (clientThread.runOnClientThread { client.localPlayer.worldLocation != bankLocation }) {
                        Walker().walkTo(bankLocation)
                        return
                    } else {
                        val bank = Rs2GameObject.findObject(16700, WorldPoint(2099, 3920, 0))
                        Rs2Bank.openBank(bank)
                    }
                } else {
                    bank()
                    return
                }
            }
        } else {
            changeStateTo(State.THINKING)
            return
        }
    }

    private fun walkingToBankState() {
        if (runIs()) enableRun(true)
        activatePrayers(false)
        if (!isMoving()) {
            if (clientThread.runOnClientThread { bankArea.contains(client.localPlayer.worldLocation) }) {
                changeStateTo(State.THINKING)
                return
            }
            if (!inHouse()) {
                teleToHouse()
                return
            }
            if (clientThread.runOnClientThread {
                    client.getBoostedSkillLevel(Skill.HITPOINTS) < config.POOLDRINK().width || client.getBoostedSkillLevel(
                        Skill.PRAYER
                    ) < config.POOLDRINK().height
                }) {
                Rs2GameObject.interact("Ornate pool of Rejuvenation", "Drink")
                return
            }
            if (inHouse()) {
                Rs2GameObject.interact(config.PORTAL().toString(), config.PORTAL().action())
                return
            }
        }
    }

    private fun thinkingState() {
        if (readyToFight()) { // Check if player has all potions and food
            if (inVorkathArea()) { // Check if player in Vorkath area
                if (isPrepared) { // Has drank potions
                    changeStateTo(State.POKE)
                    return
                } else { // Hasn't drank potions
                    changeStateTo(State.PREPARE) // Drink potions
                    return
                }
            } else { // walk to vorkath
                changeStateTo(State.WALKING_TO_VORKATH)
                return
            }
        } else { // If player doesn't have all potions and food
            drankRangePotion = false
            drankAntiFire = false
            isPrepared = false
            if (clientThread.runOnClientThread { bankArea.contains(client.localPlayer.worldLocation) }) { // Player is in bank area
                changeStateTo(State.BANKING)
                return
            } else { // Player is not in bank area
                changeStateTo(State.WALKING_TO_BANK)
                return
            }
        }
    }

    private fun prepareState() {
        val currentTime = System.currentTimeMillis()

        if (!drankRangePotion && currentTime - lastDrankRangePotion > config.RANGEPOTION().time()) {
            Rs2Inventory.interact(config.RANGEPOTION().toString(), "Drink")
            lastDrankRangePotion = System.currentTimeMillis()
            drankRangePotion = true
            sleep(2000)
            return
        }
        if (!drankAntiFire && currentTime - lastDrankAntiFire > config.ANTIFIRE().time()) {
            Rs2Inventory.interact(config.ANTIFIRE().toString(), "Drink")
            lastDrankAntiFire = System.currentTimeMillis()
            drankAntiFire = true
            sleep(2000)
            return
        }

        drinkPrayer()

        if (!Rs2Equipment.isWearing("Serpentine helm")) {
            Rs2Inventory.interact("Anti-venom", "Drink")
            sleep(2000)
        }
        isPrepared = drankAntiFire && drankRangePotion && !inventoryHasLoot()
        if (isPrepared) {
            changeStateTo(State.THINKING)
            return
        } else {
            changeStateTo(State.WALKING_TO_BANK)
            return
        }
    }

    private fun drinkPrayer() {
        if (needsToDrinkPrayer()) {
            if (Rs2Inventory.hasItem(config.PRAYERPOTION().toString())) {
                Rs2Inventory.interact(config.PRAYERPOTION().toString(), "Drink")
                //println("Drink Sleep")
                sleep(200)
                return
            } else {
                isPrepared = false
                drankRangePotion = false
                drankAntiFire = false
                teleToHouse()
                changeStateTo(State.WALKING_TO_BANK)
                return
            }
        }
    }

    private fun bank() {
        lootNames.forEach { item ->
            Rs2Bank.depositAll(item)
        }
        if (!hasItem(config.TELEPORT().toString())) {
            //("Withdraw teleport")
            Rs2Bank.withdrawAll(config.TELEPORT().toString())
        }
        if (!Rs2Inventory.hasItemAmount(config.RANGEPOTION().toString(), 2)) {
            //println("Withdraw Range potion")
            Rs2Bank.withdrawItem(config.RANGEPOTION().toString())
        }
        if (!Rs2Inventory.hasItem(config.SLAYERSTAFF().toString())) {
            //println("Withdraw staff")
            Rs2Bank.withdrawItem(config.SLAYERSTAFF().toString())
        }
        if (!Rs2Inventory.hasItemAmount(config.PRAYERPOTION().toString(), 2)) {
            //println("Withdraw Prayer potion")
            Rs2Bank.withdrawItem(config.PRAYERPOTION().toString())
        }
        if (!Rs2Inventory.hasItem("Rune pouch")) {
            //println("Withdraw rune pouch")
            Rs2Bank.withdrawItem("Rune pouch")
        }
        if (!Rs2Inventory.hasItemAmount(config.ANTIFIRE().toString(), 2)) {
            //println("Withdraw anti-fire")
            Rs2Bank.withdrawItem(config.ANTIFIRE().toString())
        }
        if (!Rs2Equipment.isWearing("Serpentine helm")) {
            if (!Rs2Inventory.hasItemAmount("Anti-venom", 2)) {
                //println("Withdraw Anti-venom")
                Rs2Bank.withdrawItem("Anti-venom")
            }
        }
        if (!Rs2Inventory.hasItemAmount(config.FOOD().toString(), config.FOODAMOUNT().width)) {
            for (i in 1..config.FOODAMOUNT().width - Rs2Inventory.count(config.FOOD())) {
                //println("Withdrawing food")
                Rs2Bank.withdrawItem(config.FOOD())
            }
        }
        if (Rs2Inventory.hasItemAmount(config.FOOD().toString(), config.FOODAMOUNT().width)) changeStateTo(State.THINKING)
    }

    private fun inVorkathArea(): Boolean =
        Rs2Npc.getNpc("Vorkath") != null && clientThread.runOnClientThread { client.isInInstancedRegion }

    private fun isVorkathAsleep(): Boolean = Rs2Npc.getNpc(8059) != null
    private fun inHouse(): Boolean = Rs2GameObject.exists(4525)

    private fun inGE(): Boolean = Rs2Npc.getNpc("Grand Exchange Clerk") != null

    private fun geIsOpen(): Boolean = Rs2GrandExchange.isOpen()

    private fun isMoving(): Boolean = Rs2Player.isMoving() || clientThread.runOnClientThread { client.localPlayer.animation != -1 }
    private fun needsToDrinkPrayer(): Boolean = clientThread.runOnClientThread { client.getBoostedSkillLevel(Skill.PRAYER) <= 70 }

    private fun readyToFight(): Boolean = Rs2Inventory.getInventoryFood().size >= config.FOODAMOUNT().height
            && Rs2Inventory.hasItem(config.ANTIFIRE().toString())
            && Rs2Inventory.hasItem(config.RANGEPOTION().toString())
            && Rs2Inventory.hasItem(config.SLAYERSTAFF().toString())
            && Rs2Inventory.hasItem(config.TELEPORT().toString())
            && Rs2Inventory.hasItem("Rune pouch")
            && Rs2Inventory.hasItem(config.PRAYERPOTION().toString())
            && !inventoryHasLoot()

    private fun needsToEat(at: Int): Boolean =
        clientThread.runOnClientThread { client.getBoostedSkillLevel(Skill.HITPOINTS) <= at }

    private fun eat(at: Int) {
        if (needsToEat(at)) {
            if (Rs2Inventory.getInventoryFood().isNotEmpty()) {
                val food = Rs2Inventory.getInventoryFood().first()
                Rs2Inventory.interact(food);
                return
            } else {
                isPrepared = false
                drankRangePotion = false
                drankAntiFire = false
                initialAcidMove = false
                teleToHouse()
                changeStateTo(State.WALKING_TO_BANK)
                return
            }
        }
    }

    private fun inventoryHasLoot(): Boolean {
        lootNames.forEach { item ->
            if (hasItem(item)) {
                return true
            }
        }
        return false
    }


    private fun sendKey(key: Int) {
        keyEvent(KeyEvent.KEY_PRESSED, key)
        keyEvent(KeyEvent.KEY_RELEASED, key)
    }

    private fun keyEvent(id: Int, key: Int) {
        val e = KeyEvent(
            client.canvas,
            id,
            System.currentTimeMillis(),
            0,
            key,
            KeyEvent.CHAR_UNDEFINED
        )
        client.canvas.dispatchEvent(e)
    }

    private fun hasItem(name: String): Boolean = Rs2Inventory.hasItem(name)

    private fun runIs(on: Boolean = false): Boolean =
        clientThread.runOnClientThread { client.getVarpValue(173) == if (on) 1 else 0 }

    private fun enableRun(on: Boolean) {
        Rs2Player.toggleRunEnergy(on)
        sleep(30)
    }

    private fun activatePrayers(on: Boolean) {
        if (config.ACTIVATERIGOUR()) {
            Rs2Prayer.toggle(Rs2PrayerEnum.RIGOUR, on)
        }
        Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_MAGIC, on)
        return
    }

    private fun teleToHouse() {
        Rs2Inventory.interact(config.TELEPORT().toString())
        sleepUntil { !isMoving() }
    }

    private fun doesProjectileExistById(id: Int): Boolean {
        for (projectile in clientThread.runOnClientThread { client.projectiles }) {
            if (projectile.id == id && projectile != null) {
                //println("Projectile $id found")
                return true
            }
        }
        return false
    }


    private fun changeStateTo(stateName: State, ticksToDelay: Int = 0) {
        botState = stateName
        sleep((ticksToDelay * 600).toLong())
        // println("State : $stateName")
    }
}
