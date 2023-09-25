package net.runelite.client.plugins.griffinplugins.griffintrainer

import net.runelite.api.ItemID
import net.runelite.api.Skill
import net.runelite.api.coords.WorldArea
import net.runelite.api.coords.WorldPoint
import net.runelite.client.plugins.griffinplugins.griffinmining.GriffinMiningConfig
import net.runelite.client.plugins.griffinplugins.griffintrainer.helpers.BankHelper
import net.runelite.client.plugins.griffinplugins.griffintrainer.models.DynamicItemSet
import net.runelite.client.plugins.griffinplugins.griffintrainer.models.inventory.InventoryRequirements
import net.runelite.client.plugins.griffinplugins.util.helpers.MiningHelper
import net.runelite.client.plugins.microbot.Microbot
import net.runelite.client.plugins.microbot.Script
import net.runelite.client.plugins.microbot.staticwalker.WorldDestinations
import net.runelite.client.plugins.microbot.util.Global
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment
import net.runelite.client.plugins.microbot.util.inventory.Inventory
import net.runelite.client.plugins.microbot.util.player.Rs2Player
import java.util.concurrent.TimeUnit

class GriffinMiningScript : Script() {
    companion object {
        private val VARROCK_EAST_MINE_WORLD_AREA = WorldArea(3281, 3361, 9, 9, 0)
        private val VARROCK_EAST_MINE_WORLD_POINT = WorldPoint(3284, 3366, 0)
        var state: State = State.SETUP
    }

    lateinit var config: GriffinMiningConfig

    enum class State {
        SETUP, WAITING, MINING, BANKING
    }

    fun run(config: GriffinMiningConfig): Boolean {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay({
            this.config = config

            if (!super.run()) return@scheduleWithFixedDelay

            try {
                if (!shouldTrain()) {
                    shutdown()
                    return@scheduleWithFixedDelay
                }

                val minimumSkillRequirement = Microbot.getClientForKotlin().getRealSkillLevel(Skill.MINING)

                if (minimumSkillRequirement < 15) {
                    updateCounts("Mining Copper", "Copper Mined")
                    mineOre(VARROCK_EAST_MINE_WORLD_AREA, VARROCK_EAST_MINE_WORLD_POINT, "copper rocks", ItemID.COPPER_ORE)

                } else if (minimumSkillRequirement < 99) {
                    updateCounts("Mining Iron", "Iron Mined")
                    mineOre(VARROCK_EAST_MINE_WORLD_AREA, VARROCK_EAST_MINE_WORLD_POINT, "iron rocks", ItemID.IRON_ORE)

                } else {
                    shutdown()
                    return@scheduleWithFixedDelay
                }

            } catch (ex: Exception) {
                println(ex)
            }
        }, 0, 400, TimeUnit.MILLISECONDS)
        return true
    }

    fun updateCounts(status: String, countLabel: String) {
        if (GriffinMiningPlugin.countLabel != countLabel) {
            GriffinMiningPlugin.count = 0
        }

        Microbot.status = status
        GriffinMiningPlugin.countLabel = countLabel
    }

    fun getBankLocation(): WorldPoint {
        return WorldDestinations.VARROCK_EAST_BANK.worldPoint
    }

    fun getInventoryRequirements(): InventoryRequirements {
        val inventoryRequirements = InventoryRequirements()
        val miningLevel = Microbot.getClientForKotlin().getRealSkillLevel(Skill.MINING)

        val pickaxes = DynamicItemSet()
        if (miningLevel >= 1) {
            pickaxes.add(ItemID.BRONZE_PICKAXE, 1)
            pickaxes.add(ItemID.IRON_PICKAXE, 1)
        }
        if (miningLevel >= 6) {
            pickaxes.add(ItemID.STEEL_PICKAXE, 1)
        }
        if (miningLevel >= 11) {
            pickaxes.add(ItemID.BLACK_PICKAXE, 1)
        }
        if (miningLevel >= 21) {
            pickaxes.add(ItemID.MITHRIL_PICKAXE, 1)
        }
        if (miningLevel >= 31) {
            pickaxes.add(ItemID.ADAMANT_PICKAXE, 1)
        }
        if (miningLevel >= 41) {
            pickaxes.add(ItemID.RUNE_PICKAXE, 1)
        }

        if (pickaxes.getItems().isNotEmpty()) {
            inventoryRequirements.addItemSet(pickaxes)
        }
        return inventoryRequirements
    }

    fun shouldTrain(): Boolean {
        val miningLevel = Microbot.getClientForKotlin().getRealSkillLevel(Skill.MINING)
        return miningLevel < config.miningLevel()
    }

    private fun mineOre(oreArea: WorldArea, orePoint: WorldPoint, oreName: String, itemId: Int) {
        if (Rs2Player.isWalking() || Rs2Player.isInteracting()) {
            return
        }

        when (state) {
            State.SETUP -> {
                Microbot.getWalkerForKotlin().staticWalkTo(getBankLocation())
                if (!Rs2Bank.isOpen()) {
                    Rs2Bank.openBank()
                }

                Rs2Bank.depositAll()
                Global.sleep(300, 600)
                Rs2Bank.depositEquipment()
                Global.sleep(600, 900)

                val foundItemIds = BankHelper.fetchInventoryRequirements(getInventoryRequirements())
                Rs2Bank.closeBank()
                Global.sleepUntilTrue({ !Rs2Bank.isOpen() }, 100, 3000)

                foundItemIds.forEach { itemId: Int ->
                    println("Equipping item $itemId")
                    if (!Rs2Equipment.hasEquipped(itemId)) {
                        Inventory.getInventoryItem(itemId)?.let {
                            Microbot.getMouseForKotlin().click(it.bounds)
                            Global.sleepUntilTrue({ Rs2Equipment.hasEquipped(itemId) }, 100, 3000)
                        }
                    }
                }
                state = State.WAITING
            }

            State.WAITING -> {
                val player = Microbot.getClientForKotlin().localPlayer
                if (!oreArea.contains(player.worldLocation)) {
                    Microbot.getWalkerForKotlin().staticWalkTo(orePoint)
                }
                state = State.MINING
            }

            State.MINING -> {
                if (MiningHelper.findAndMineOre(oreName)) {
                    GriffinMiningPlugin.count++
                    state = State.BANKING
                }
            }

            State.BANKING -> {
                if (config.keepOre()) {
                    if (Inventory.isFull()) {
                        Microbot.getWalkerForKotlin().hybridWalkTo(getBankLocation())
                        Rs2Bank.openBank()
                        if (Rs2Bank.isOpen()) {
                            Rs2Bank.depositAll()
                            Rs2Bank.closeBank()
                        }
                        Global.sleep(200)
                    }
                } else {
                    Inventory.dropAll()
                }
                state = State.WAITING
            }
        }
    }
}