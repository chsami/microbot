package net.runelite.client.plugins.griffinplugins.griffintrainer.tasks

import net.runelite.api.coords.WorldPoint
import net.runelite.client.plugins.griffinplugins.griffintrainer.GriffinTrainerConfig
import net.runelite.client.plugins.griffinplugins.griffintrainer.GriffinTrainerScript
import net.runelite.client.plugins.griffinplugins.griffintrainer.helpers.BankHelper
import net.runelite.client.plugins.griffinplugins.griffintrainer.models.inventory.InventoryRequirements
import net.runelite.client.plugins.microbot.Microbot
import net.runelite.client.plugins.microbot.Script
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank

abstract class BaseTrainerTask(val config: GriffinTrainerConfig) {

    abstract fun getBankLocation(): WorldPoint
    abstract fun getInventoryRequirements(): InventoryRequirements
    abstract fun shouldTrain(): Boolean
    abstract fun process(): Boolean

    fun run(): Boolean {

//        setup()
        if (!GriffinTrainerScript.taskTimer.isTimerComplete && !GriffinTrainerScript.overallTimer.isTimerComplete && shouldTrain()) {
            return process()
        }

        return true
//        teardown()
    }

    fun fetchRequiredItems() {
        val player = Microbot.getClientForKotlin().localPlayer

        // if we are too far away from the bank we don't have it open
        if (player.worldLocation.distanceTo(getBankLocation()) > 5) {
            Microbot.getWalkerForKotlin().hybridWalkTo(getBankLocation())
        }

        Rs2Bank.openBank()
        if (Rs2Bank.isOpen()) {
            Rs2Bank.depositAll()
            Rs2Bank.depositEquipment()
            BankHelper.fetchInventoryRequirements(getInventoryRequirements())
            Rs2Bank.closeBank()
        }
    }
}