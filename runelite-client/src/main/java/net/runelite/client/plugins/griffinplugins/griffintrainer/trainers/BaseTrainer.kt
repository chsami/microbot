package net.runelite.client.plugins.griffinplugins.griffintrainer.trainers

import net.runelite.api.coords.WorldPoint
import net.runelite.client.plugins.griffinplugins.griffintrainer.GriffinTrainerConfig
import net.runelite.client.plugins.griffinplugins.griffintrainer.TrainerInterruptor
import net.runelite.client.plugins.griffinplugins.griffintrainer.TrainerThread
import net.runelite.client.plugins.griffinplugins.griffintrainer.helpers.BankHelper
import net.runelite.client.plugins.griffinplugins.griffintrainer.helpers.ItemHelper
import net.runelite.client.plugins.griffinplugins.griffintrainer.models.inventory.InventoryRequirements
import net.runelite.client.plugins.microbot.Microbot
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank
import net.runelite.client.plugins.microbot.util.antiban.Rs2AntiBan

abstract class BaseTrainer(private val config: GriffinTrainerConfig) {
    abstract fun getBankLocation(): WorldPoint
    abstract fun getInventoryRequirements(): InventoryRequirements
    abstract fun getMinimumSkillLevel(): Int
    abstract fun shouldTrain(): Boolean
    abstract fun process(): Boolean
    fun run(): Boolean {
        if (TrainerInterruptor.isInterrupted) {
            return true
        }

        if (Rs2AntiBan.tryFindAndDismissRandomEvent()) {
            TrainerThread.randomEventDismissedCount++
        }

        if (!shouldTrain()) {
            return true
        }

        if (TrainerInterruptor.isInterrupted) {
            return true
        }

        if (process()) {
            return true
        }

        return false
    }

    fun updateCounts(status: String, countLabel: String) {
        if (TrainerThread.countLabel != countLabel) {
            TrainerThread.count = 0
        }

        Microbot.status = status
        TrainerThread.countLabel = countLabel
    }

    fun fetchItemRequirements() {
        if (config.equipGear()) {
            Microbot.getWalkerForKotlin().staticWalkTo(getBankLocation())
            if (!Rs2Bank.isOpen()) {
                Rs2Bank.openBank()
            }

            Rs2Bank.depositAll()
            TrainerInterruptor.sleep(300, 600)
            Rs2Bank.depositEquipment()
            TrainerInterruptor.sleep(600, 900)

            val foundItems = BankHelper.fetchInventoryRequirements(getInventoryRequirements())
            val equipableItemIds = foundItems.filter { itemPair -> itemPair.second }

            Rs2Bank.closeBank()
            TrainerInterruptor.sleepUntilTrue({ !Rs2Bank.isOpen() }, 100, 3000)

            ItemHelper.equipItemIds(equipableItemIds)
        }
    }
}