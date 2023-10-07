package net.runelite.client.plugins.griffinplugins.griffintrainer.trainers

import net.runelite.api.coords.WorldPoint
import net.runelite.client.plugins.griffinplugins.griffintrainer.TrainerInterruptor
import net.runelite.client.plugins.griffinplugins.griffintrainer.TrainerThread
import net.runelite.client.plugins.griffinplugins.griffintrainer.models.inventory.InventoryRequirements
import net.runelite.client.plugins.microbot.Microbot

abstract class BaseTrainer {
    abstract fun getBankLocation(): WorldPoint
    abstract fun getInventoryRequirements(): InventoryRequirements
    abstract fun getMinimumSkillLevel(): Int
    abstract fun shouldTrain(): Boolean
    abstract fun process(): Boolean
    fun run(): Boolean {
        if (TrainerInterruptor.isInterrupted) {
            return true
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
}