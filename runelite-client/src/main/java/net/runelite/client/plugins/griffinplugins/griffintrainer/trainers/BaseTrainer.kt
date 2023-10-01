package net.runelite.client.plugins.griffinplugins.griffintrainer.trainers

import net.runelite.api.coords.WorldPoint
import net.runelite.client.plugins.griffinplugins.griffintrainer.models.inventory.InventoryRequirements

abstract class BaseTrainer {
    abstract fun getBankLocation(): WorldPoint
    abstract fun getInventoryRequirements(): InventoryRequirements
    abstract fun getMinimumSkillLevel(): Int
    abstract fun shouldTrain(): Boolean
    abstract fun process(): Boolean
    fun run(): Boolean {
        if (!shouldTrain()) {
            return true
        }

        if (process()) {
            return true
        }

        return false
    }
}