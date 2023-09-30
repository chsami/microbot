package net.runelite.client.plugins.griffinplugins.griffintrainer.helpers

import net.runelite.api.widgets.Widget
import net.runelite.client.plugins.griffinplugins.griffintrainer.models.DynamicItemSet
import net.runelite.client.plugins.griffinplugins.griffintrainer.models.inventory.InventoryRequirements
import net.runelite.client.plugins.microbot.util.Global
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank
import net.runelite.client.plugins.microbot.util.inventory.Inventory

class BankHelper {
    companion object {
        fun fetchInventoryRequirements(inventoryRequirements: InventoryRequirements): List<Int> {
            val foundItemIds = mutableListOf<Int>()
            inventoryRequirements.getItemSets().forEach { dynamicItemSet: DynamicItemSet ->
                for (itemAndQuantityPair: Pair<Int, Int> in dynamicItemSet.getItems()) {
                    if (itemAndQuantityPair.second == 0) {
                        continue
                    }

                    if (!Rs2Bank.hasBankItem(itemAndQuantityPair.first)) {
                        continue
                    }

                    val countBefore = Inventory.getInventoryItems().count { widget: Widget -> widget.itemId == itemAndQuantityPair.first }
                    if (itemAndQuantityPair.second == 1) {
                        Rs2Bank.withdrawItem(false, itemAndQuantityPair.first)

                    } else {
                         Rs2Bank.withdrawItemX(false, itemAndQuantityPair.first, itemAndQuantityPair.second)
                    }

                    var success = true

                    if (!success) {
                        throw Exception("Failed to withdraw item ${itemAndQuantityPair.first}.")
                    }

                    success = Global.sleepUntilTrue({
                        val meetsCountItems = Inventory.getInventoryItems().count { widget: Widget -> widget.itemId == itemAndQuantityPair.first } == countBefore + itemAndQuantityPair.second
                        val meetsCountQuantity = Inventory.getInventoryItems().firstOrNull { widget: Widget -> widget.itemId == itemAndQuantityPair.first }?.itemQuantity == itemAndQuantityPair.second
                        return@sleepUntilTrue meetsCountItems || meetsCountQuantity
                    }, 100, 2000)

                    if (!success) {
                        throw Exception("Failed to withdraw all required items ${itemAndQuantityPair.first}.")
                    } else {
                        foundItemIds.add(itemAndQuantityPair.first)
                        Global.sleep(400, 800)
                    }

                    break
                }
            }

            return foundItemIds
        }
    }
}