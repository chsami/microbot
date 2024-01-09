/*
 * Copyright (c) 2024. By Jrod7938
 *
 */
package net.runelite.client.plugins.jrPlugins.autoVorkath

import com.google.inject.Provides
import net.runelite.api.*
import net.runelite.api.coords.WorldArea
import net.runelite.api.coords.WorldPoint
import net.runelite.api.events.*
import net.runelite.client.config.ConfigManager
import net.runelite.client.eventbus.Subscribe
import net.runelite.client.events.NpcLootReceived
import net.runelite.client.game.ItemManager
import net.runelite.client.game.ItemStack
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.plugins.PluginManager
import net.runelite.client.plugins.microbot.Microbot
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject
import net.runelite.client.plugins.microbot.util.grandexchange.GrandExchange
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem
import net.runelite.client.plugins.microbot.util.inventory.Inventory
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc
import net.runelite.client.plugins.microbot.util.player.Rs2Player
import net.runelite.client.plugins.microbot.util.prayer.Rs2Prayer
import net.runelite.client.plugins.microbot.util.walker.Walker
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget
import net.runelite.client.plugins.microbot.util.prayer.*
import net.runelite.client.plugins.microbot.util.prayer.Prayer
import net.runelite.client.ui.overlay.OverlayManager
import java.awt.event.KeyEvent
import javax.inject.Inject
import kotlin.math.abs


@PluginDescriptor(
    name = "<html><font color=\"#9ddbff\">[JR]</font> Auto Vorkath </html>",
    description = "JR - Auto Vorkath",
    tags = ["vorkath", "auto", "auto prayer", "mule", "trade"],
    enabledByDefault = false
)
class AutoVorkathPlugin : Plugin() {
    @Inject
    private lateinit var client: Client

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

    var botState: State? = null
    var tickDelay: Int = 0
    var killCount: Int = 0
    private var running = false
    private val rangeProjectileId = 1477
    private val magicProjectileId = 393
    private val purpleProjectileId = 1471
    private val blueProjectileId = 1479
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
        WALKING_TO_GE,
        GETTING_ITEM,
        SELLING,
        THINKING,
        NONE
    }

    override fun startUp() {
        println("Auto Vorkath Plugin Activated")
        botState = State.THINKING
        running = client.gameState == GameState.LOGGED_IN
        lootNames = mutableSetOf()
        overlayManager.add(autoVorkathOverlay)
    }

    override fun shutDown() {
        println("Auto Vorkath Plugin Deactivated")
        running = false
        botState = null
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
                if (comp.isTradeable) lootNames.add(comp.name)
            }
        }
        killCount++
        changeStateTo(State.LOOTING)
    }

    @Subscribe
    fun onNpcDespawned(e: NpcDespawned) {
        if (e.npc.name == "Zombified Spawn") {
            if (Inventory.contains(config.CROSSBOW().toString())) {
                Inventory.useItemAction(config.CROSSBOW().toString(), "Wield")
            }
            changeStateTo(State.FIGHTING)
        }
    }

    @Subscribe
    fun onProjectileMoved(e: ProjectileMoved) {
        when (e.projectile.id) {
            acidProjectileId -> {
                acidPools.add(WorldPoint.fromLocal(client, e.position))
                changeStateTo(State.ACID)
            }

            whiteProjectileId -> changeStateTo(State.SPAWN)
            acidRedProjectileId -> changeStateTo(State.ACID)
            rangeProjectileId, magicProjectileId, purpleProjectileId, blueProjectileId -> activatePrayers(true)
            redProjectileId -> {
                redBallLocation = WorldPoint.fromLocal(client, e.position)
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


    @Subscribe
    fun onGameTick(e: GameTick) {
        if (running) {
            if (tickDelay > 0) { // Tick delay
                tickDelay--
                return
            }

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
                State.WALKING_TO_GE -> walkingToGEState()
                State.GETTING_ITEM -> gettingItemState()
                State.SELLING -> sellingItemState()
                State.NONE -> println("None State")
                null -> println("Null State")
            }
        }
    }

    private fun sellingItemState() {
        if (!isMoving()) {
            if (geIsOpen()) {
                if (inventoryHasLoot() && Rs2Widget.hasWidget("Select an offer slot to set up")) {
                    val itemToSell = Inventory.getInventoryItems().firstOrNull {
                        !it.name.contains(config.TELEPORT().toString())
                    }
                    val itemAmount = Inventory.getItemAmount(itemToSell?.name).toInt()
                    itemToSell?.let {
                        GrandExchange.sellItem(itemToSell.name, itemAmount, 100)
                        //println("Offered Item")
                        tickDelay = 1
                        return
                    } ?: run {
                        changeStateTo(State.GETTING_ITEM, 1)
                        return
                    }
                }
                if (Rs2Widget.hasWidget("Confirm")) {
                    Rs2Widget.clickWidget("-5%")
                    Rs2Widget.clickWidget("-5%")
                    Rs2Widget.clickWidget("Confirm")
                    lootNames.remove(lootNames.toList()[0])
                    tickDelay = 1
                    return
                }
                if (Rs2Widget.hasWidget("Collect to inventory")) {
                    GrandExchange.collectToInventory()
                    Rs2Bank.openBank()
                    changeStateTo(State.GETTING_ITEM, 1)
                }
            }
        }
    }

    private fun gettingItemState() {
        if (lootNames.isEmpty()) {
            if (Rs2Bank.isOpen()) {
                Rs2Bank.depositAll("Coins")
                Rs2Bank.closeBank()
            }
            changeStateTo(State.WALKING_TO_BANK)
            return
        }
        if (!isMoving()) {
            if (Rs2Bank.isOpen()) {
                if (Inventory.hasItem("Coins")) {
                    Rs2Bank.depositAll("Coins")
                    tickDelay = 1
                    return
                }
                if (inventoryHasLoot()) {
                    GrandExchange.openExchange()
                    changeStateTo(State.SELLING, 1)
                    return
                } else {
                    if (client.getVarbitValue(3958) == 0) {
                        Rs2Widget.clickWidget("Note")
                        tickDelay = 1
                        return
                    }
                    Rs2Bank.withdrawAll(lootNames.toList()[0])
                    tickDelay = 1
                    return
                }
            } else {
                Rs2Bank.openBank()
                return
            }
        }
    }

    private fun walkingToGEState() {
        if (!isMoving()) {
            if (inVorkathArea() || !inGE() && !inHouse()) {
                teleToHouse()
                return
            }
            if (inHouse()) {
                Rs2GameObject.interact("Ornate Jewellery Box", "Grand Exchange")
                return
            }
            if (Rs2Bank.isOpen()) {
                val item = Inventory.getInventoryItems().firstOrNull { !it.name.contains(config.TELEPORT().toString()) }
                Rs2Bank.depositAll(item?.name)
                if (Inventory.getInventoryItems().size == 1) { // only has teleport in inventory
                    //println("LootId Before Check: $lootNames")
                    lootNames = lootNames.filter {
                        Rs2Bank.hasItem(it)
                    }.toMutableSet()
                    //println("LootId After Check: $lootNames")
                    changeStateTo(State.GETTING_ITEM, 1)
                    return
                }
            } else {
                Rs2Bank.openBank()
                return
            }
        }
    }

    private fun lootingState() {
        if (lootQueue.isEmpty()) {
            if (killCount % config.SELLAT() == 0 && killCount != 0) changeStateTo(State.WALKING_TO_GE)
            else changeStateTo(State.WALKING_TO_BANK, 1)
            return
        }
        Inventory.useItemAction(config.CROSSBOW().toString(), "Wield")
        lootQueue.firstOrNull()?.let {
            if (!Rs2Player.isMoving()) {
                if (!Inventory.isFull()) {
                    Rs2GroundItem.loot(it.id)
                    lootQueue.removeAt(lootQueue.indexOf(it))
                    tickDelay = if (isMoving()) 3 else 1
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
        if (!runIsOff()) enableRun(false)
        activatePrayers(false)
        if (!inVorkathArea()) {
            acidPools.clear()
            changeStateTo(State.THINKING)
            return
        }

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
            return safeTiles.minByOrNull { abs(it.x - client.localPlayer.worldLocation.x) }
        }

        val safeTile: WorldPoint? = findSafeTiles()
        //println("Acid pools: $acidPools")
        //println("Left Tile: $swPoint")
        //println("Safe tile: $safeTile")

        val playerLocation = client.localPlayer.worldLocation

        safeTile?.let {
            if (playerLocation == safeTile) {
                // Attack Vorkath if the player close to the safe tile
                Rs2Npc.interact(vorkath, "Attack")
            } else {
                eat(config.EATAT())
                // Move to the safe tile if the player is not close enough
                //println("Moving to safe tile: $safeTile")
                //println("Player location: $playerLocation")
                Walker().walkTo(safeTile)
            }
        } ?: run {
            Microbot.showMessage("NO SAFE TILES! TELEPORTING TF OUT!")
            teleToHouse()
            changeStateTo(State.WALKING_TO_BANK)
        }
    }

    private fun redBallState() {
        drinkPrayer()
        eat(config.EATAT())
        Walker().walkTo(WorldPoint(redBallLocation.x + 2, redBallLocation.y, redBallLocation.plane))
        changeStateTo(State.FIGHTING, 2)
    }

    private fun spawnState() {
        if (!inVorkathArea()) {
            changeStateTo(State.THINKING)
            return
        }
        activatePrayers(false)
        drinkPrayer()
        if (!Rs2Equipment.hasEquipped(config.SLAYERSTAFF().toString())) {
            Inventory.useItemAction(config.SLAYERSTAFF().toString(), "Wield")
            return
        } else {
            Rs2Npc.interact("Zombie spawn", "Attack")
        }
    }

    private fun fightingState() {
        if (runIsOff()) enableRun(true)
        acidPools.clear()
        if (!inVorkathArea()) {
            changeStateTo(State.THINKING)
            return
        } else {
            val vorkath = Rs2Npc.getNpc("Vorkath")
            val middle = WorldPoint(vorkath.worldLocation.x + 3, vorkath.worldLocation.y - 5, 0)
            if (isVorkathAsleep()) {
                changeStateTo(State.WALKING_TO_BANK)
                return
            }
            if (client.localPlayer.interacting == null) {
                Rs2Npc.interact(vorkath, "Attack")
                return
            }
            if (client.localPlayer.worldLocation != middle) {
                if (!isMoving()) {
                    Walker().walkTo(middle)
                }
            }
            eat(config.EATAT())
            if (Inventory.hasItem(config.CROSSBOW().toString())) {
                Inventory.useItemAction(config.CROSSBOW().toString(), "Wield")
            }

        }
    }

    private fun pokeState() {
        if (isVorkathAsleep()) {
            acidPools.clear()
            lootQueue.clear()
            lootNames.clear()
            if (!isMoving()) {
                Rs2Npc.interact("Vorkath", "Poke")
                return
            }
        } else {
            val vorkath = Rs2Npc.getNpc("Vorkath")
            val middle = WorldPoint(vorkath.worldLocation.x + 3, vorkath.worldLocation.y - 5, 0)
            Walker().walkTo(middle)
            changeStateTo(State.FIGHTING)
            return
        }
    }

    private fun walkingToVorkathState() {
        if (runIsOff()) enableRun(true)
        activatePrayers(false)
        if (!isMoving()) {
            if (bankArea.contains(client.localPlayer.worldLocation)) {
                if (Rs2Widget.hasWidget("Click here to continue")) {
                    sendKey(KeyEvent.VK_SPACE)
                    return
                }
                if (client.localPlayer.worldLocation != bankLocation) {
                    Walker().walkTo(bankLocation)
                    return
                } else {
                    Rs2Npc.interact("Sirsal Banker", "Talk-to")
                    return
                }
            } else {
                if (inVorkathArea()) {
                    changeStateTo(State.THINKING, 3)
                    return
                }
                if (fremennikArea.contains(client.localPlayer.worldLocation)) {
                    Rs2GameObject.interact(29917, "Travel")
                    return
                } else {
                    if (Rs2GameObject.exists(31990)) {
                        Rs2GameObject.interact(31990, "Climb-over")
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
        if (bankArea.contains(client.localPlayer.worldLocation)) {
            if (!isMoving()) {
                if (!Rs2Bank.isOpen()) {
                    if (client.localPlayer.worldLocation != bankLocation) {
                        Walker().walkTo(bankLocation)
                        return
                    } else {
                        val jack = Rs2Npc.getNpc("'Birds-Eye' Jack")
                        Rs2Npc.interact(jack, "Bank")
                        tickDelay = 1
                        return
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
        if (runIsOff()) enableRun(true)
        activatePrayers(false)
        if (!isMoving()) {
            if (bankArea.contains(client.localPlayer.worldLocation)) {
                changeStateTo(State.THINKING)
                return
            }
            if (!inHouse()) {
                teleToHouse()
                return
            }
            if (client.getBoostedSkillLevel(Skill.HITPOINTS) < config.POOLDRINK().width || client.getBoostedSkillLevel(
                    Skill.PRAYER
                ) < config.POOLDRINK().height
            ) {
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
            if (bankArea.contains(client.localPlayer.worldLocation)) { // Player is in bank area
                changeStateTo(State.BANKING)
                return
            } else { // Player is not in bank area
                if (killCount == 0) {
                    changeStateTo(State.WALKING_TO_BANK)
                    return
                }
                if (killCount % config.SELLAT() == 0) changeStateTo(State.WALKING_TO_GE) else changeStateTo(State.WALKING_TO_BANK)
                return
            }
        }
    }

    private fun prepareState() {
        val currentTime = System.currentTimeMillis()

        if (!drankRangePotion && currentTime - lastDrankRangePotion > config.RANGEPOTION().time()) {
            Inventory.interact(config.RANGEPOTION().toString(), "Drink")
            lastDrankRangePotion = System.currentTimeMillis()
            drankRangePotion = true
            tickDelay = 2
            return
        }
        if (!drankAntiFire && currentTime - lastDrankAntiFire > config.ANTIFIRE().time()) {
            Inventory.interact(config.ANTIFIRE().toString(), "Drink")
            lastDrankAntiFire = System.currentTimeMillis()
            drankAntiFire = true
            tickDelay = 2
            return
        }

        drinkPrayer()

        if (!Rs2Equipment.hasEquipped("Serpentine helm")) {
            Inventory.interact("Anti-venom", "Drink")
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
            if (Inventory.hasItem(config.PRAYERPOTION().toString())) {
                Inventory.useItemAction(config.PRAYERPOTION().toString(), "Drink")
                tickDelay = 2
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
            if (Inventory.hasItem(item)) {
                Rs2Bank.depositAll(item)
            } else {
                lootNames.remove(item)
            }
        }
        if (!hasItem(config.TELEPORT().toString())) {
            Rs2Bank.withdrawItem(config.TELEPORT().toString())
        }
        if (!Inventory.hasItemAmount(config.RANGEPOTION().toString(), 1)) {
            Rs2Bank.withdrawItem(config.RANGEPOTION().toString())
        }
        if (!Inventory.hasItem(config.SLAYERSTAFF().toString())) {
            Rs2Bank.withdrawItem(config.SLAYERSTAFF().toString())
        }
        if (!Inventory.hasItemAmount(config.PRAYERPOTION().toString(), 1)) {
            Rs2Bank.withdrawItem(config.PRAYERPOTION().toString())
        }
        if (!Inventory.hasItem("Rune pouch")) {
            Rs2Bank.withdrawItem("Rune pouch")
        }
        if (!Inventory.hasItemAmount(config.ANTIFIRE().toString(), 1)) {
            Rs2Bank.withdrawItem(config.ANTIFIRE().toString())
        }
        if (!Rs2Equipment.hasEquipped("Serpentine helm")) {
            if (Inventory.hasItemAmount("Anti-venom", 1)) {
                Rs2Bank.withdrawItem("Anti-venom")
            }
        }
        if (!Inventory.isFull()) {
            for (i in 1..config.FOODAMOUNT().width - Inventory.getItemAmount(config.FOOD())) {
                Rs2Bank.withdrawItem(config.FOOD())
            }
        }
        changeStateTo(State.THINKING)
    }

    private fun inVorkathArea(): Boolean =
        Rs2Npc.getNpc("Vorkath") != null && client.isInInstancedRegion

    private fun isVorkathAsleep(): Boolean = Rs2Npc.getNpc(8059) != null
    private fun inHouse(): Boolean = Rs2GameObject.exists(4525)

    private fun inGE(): Boolean = Rs2Npc.getNpc("Grand Exchange Clerk") != null

    private fun geIsOpen(): Boolean = GrandExchange.isOpen()

    private fun isMoving(): Boolean = Rs2Player.isMoving() || client.localPlayer.animation != -1
    private fun needsToDrinkPrayer(): Boolean = client.getBoostedSkillLevel(Skill.PRAYER) <= 70

    private fun readyToFight(): Boolean = Inventory.hasItemAmount(config.FOOD(), config.FOODAMOUNT().height)
                && Inventory.hasItem(config.ANTIFIRE().toString())
                && Inventory.hasItem(config.RANGEPOTION().toString())
                && Inventory.hasItem(config.SLAYERSTAFF().toString())
                && Inventory.hasItem(config.TELEPORT().toString())
                && Inventory.hasItem("Rune pouch")
                && Inventory.hasItem(config.PRAYERPOTION().toString())
                && !inventoryHasLoot()

    private fun needsToEat(at: Int): Boolean = client.getBoostedSkillLevel(Skill.HITPOINTS) <= at

    private fun eat(at: Int) {
        if (needsToEat(at)) {
            if (Inventory.getInventoryFood().isNotEmpty()) {
                Inventory.getInventoryFood().first().let {
                    Inventory.eat(it)
                }
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
            if (Inventory.hasItem(item)) {
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

    private fun hasItem(name: String): Boolean = Inventory.hasItem(name)

    private fun runIsOff(): Boolean = client.getVarpValue(173) == 0

    private fun enableRun(on: Boolean) {
        Rs2Player.toggleRunEnergy(on)
    }

    private fun activatePrayers(on: Boolean) {
        if (config.ACTIVATERIGOUR()) {
            Rs2Prayer.fastPray(Prayer.RIGOUR, on)
        }
        Rs2Prayer.fastPray(Prayer.PROTECT_MAGIC, on)
    }

    private fun teleToHouse() {
        Inventory.useItemAction(config.TELEPORT().toString(), config.TELEPORT().action())
    }

    private fun changeStateTo(stateName: State, ticksToDelay: Int = 0) {
        botState = stateName
        tickDelay = ticksToDelay
        // println("State : $stateName")
    }
}
