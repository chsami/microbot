package net.runelite.client.plugins.griffinplugins.griffintrainer.helpers

import net.runelite.api.widgets.Widget
import net.runelite.client.plugins.griffinplugins.griffintrainer.models.DynamicItemSet
import net.runelite.client.plugins.griffinplugins.griffintrainer.models.inventory.InventoryRequirements
import net.runelite.client.plugins.griffinplugins.griffintrainer.TrainerInterruptor
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank
import net.runelite.client.plugins.microbot.util.inventory.Inventory

class BankHelper {
    companion object {
        fun fetchInventoryRequirements(inventoryRequirements: InventoryRequirements): MutableList<Pair<Int, Boolean>> {
            val foundItemIds: MutableList<Pair<Int, Boolean>> = mutableListOf()
            inventoryRequirements.getItemSets().forEach { dynamicItemSet: DynamicItemSet ->
                if (TrainerInterruptor.isInterrupted) {
                    return foundItemIds
                }

                for (itemAndQuantityPair: Triple<Int, Int, Boolean> in dynamicItemSet.getItems()) {
                    if (TrainerInterruptor.isInterrupted) {
                        return foundItemIds
                    }

                    if (itemAndQuantityPair.second == 0) {
                        continue
                    }

                    if (!Rs2Bank.hasItem(itemAndQuantityPair.first)) {
                        continue
                    }

                    val countBefore = Inventory.getInventoryItems().count { widget: Widget -> widget.itemId == itemAndQuantityPair.first }
                    if (itemAndQuantityPair.second == 1) {
                        Rs2Bank.withdrawItem(false, itemAndQuantityPair.first)

                    } else {
                        Rs2Bank.withdrawX(false, itemAndQuantityPair.first, itemAndQuantityPair.second)
                    }

                    var success = true

                    if (!success) {
                        throw Exception("Failed to withdraw item ${itemAndQuantityPair.first}.")
                    }

                    success = TrainerInterruptor.sleepUntilTrue({
                        val meetsCountItems = Inventory.getInventoryItems().count { widget: Widget -> widget.itemId == itemAndQuantityPair.first } == countBefore + itemAndQuantityPair.second
                        val meetsCountQuantity = Inventory.getInventoryItems().firstOrNull { widget: Widget -> widget.itemId == itemAndQuantityPair.first }?.itemQuantity == itemAndQuantityPair.second
                        return@sleepUntilTrue meetsCountItems || meetsCountQuantity
                    }, 100, 2000)

                    if (!success) {
                        throw Exception("Failed to withdraw all required items ${itemAndQuantityPair.first}.")
                    } else {
                        foundItemIds.add(Pair(itemAndQuantityPair.first, itemAndQuantityPair.third))
                        TrainerInterruptor.sleep(400, 800)
                    }

                    break
                }
            }

            return foundItemIds
        }
    }
}