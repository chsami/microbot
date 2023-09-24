package net.runelite.client.plugins.griffinplugins.griffintrainer.models.inventory

import net.runelite.client.plugins.griffinplugins.griffintrainer.models.DynamicItemSet
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory

class InventoryRequirements {

    private val itemSets: MutableList<DynamicItemSet> = mutableListOf()

    fun getItemSets(): List<DynamicItemSet> {
        return itemSets.reversed()
    }

    fun addItemSet(itemSet: DynamicItemSet) {
        itemSets.add(itemSet)
    }

    fun checkMeetsRequirements(): Boolean {
        Rs2Inventory.open()
        itemSets.forEach { dynamicItemSet: DynamicItemSet ->
            var meetsRequirement = false
            dynamicItemSet.getItems().forEach { itemAndQuantityPair: Pair<Int, Int> ->
                if (Rs2Inventory.hasItem(itemAndQuantityPair.first)) {
                    if (Rs2Inventory.getItemAmount(itemAndQuantityPair.first) >= itemAndQuantityPair.second) {
                        meetsRequirement = true
                    }
                }
            }

            if (!meetsRequirement) {
                return false
            }
        }
        return true
    }
}