package net.runelite.client.plugins.griffinplugins.griffintrainer.models.equipment

import net.runelite.client.plugins.griffinplugins.griffintrainer.models.DynamicEquipmentSet
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment

class EquipmentRequirements {

    val itemSets: MutableList<DynamicEquipmentSet> = mutableListOf()

    fun addItemSet(itemSet: DynamicEquipmentSet) {
        itemSets.add(itemSet)
    }

    fun checkMeetsRequirements(): Boolean {
        itemSets.forEach { dynamicItemSet: DynamicEquipmentSet ->
            var meetsRequirement = false
            dynamicItemSet.getItems().forEach { equipmentPair: Pair<Int, Int> ->
                if (Rs2Equipment.hasEquipped(equipmentPair.first)) {
                    meetsRequirement = true
                }
            }

            if (!meetsRequirement) {
                return false
            }
        }

        return true
    }
}