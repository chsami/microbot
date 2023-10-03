package net.runelite.client.plugins.griffinplugins.griffintrainer.helpers

import net.runelite.api.coords.WorldPoint
import net.runelite.client.plugins.microbot.Microbot
import net.runelite.client.plugins.microbot.util.Global
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem
import net.runelite.client.plugins.microbot.util.inventory.Inventory
import net.runelite.client.plugins.microbot.util.models.RS2Item
import net.runelite.client.plugins.microbot.util.player.Rs2Player

class ItemHelper {

    companion object {

        fun hasItemAtWorldPoint(itemId: Int, worldPoint: WorldPoint): Boolean {
            return Rs2GroundItem.getAllAt(worldPoint.x, worldPoint.y).any { rs2GroundItem: RS2Item -> rs2GroundItem.item.id == itemId }
        }

        fun findAndLootItems(itemIds: List<Int>, radius: Int): Boolean {
            val player = Microbot.getClientForKotlin().localPlayer
            val foundItems = Rs2GroundItem.getAll(radius)

            if (foundItems.isEmpty()) {
                return false
            }

            val groupedItems: Map<WorldPoint, List<RS2Item>> = foundItems
                .filter { rs2GroundItem: RS2Item -> itemIds.contains(rs2GroundItem.item.id) }
                .groupBy { rs2GroundItem: RS2Item -> rs2GroundItem.tile.worldLocation }

            groupedItems.forEach { entry: Map.Entry<WorldPoint, List<RS2Item>> ->
                val standingOnItem = player.worldLocation.equals(entry.key)

                entry.value.forEach { rsGroundItem: RS2Item ->
                    if (Inventory.isFull()) {
                        return true
                    }

                    val stillExists = Rs2GroundItem.getAllAt(rsGroundItem.tile.worldLocation.x, rsGroundItem.tile.worldLocation.y).isNotEmpty()

                    if (stillExists) {
                        if (rsGroundItem.tile.worldLocation.distanceTo(player.worldLocation) >= 4) {
                            Microbot.getWalkerForKotlin().hybridWalkTo(rsGroundItem.tile.worldLocation)
                        }

                        val inventoryCount = Inventory.count()
                        if (Rs2GroundItem.interact(rsGroundItem)) {
                            if (!standingOnItem) {
                                Global.sleepUntilTrue({ !Rs2Player.isWalking() }, 50, 3000)
                            }

                            Global.sleepUntilTrue({ !Rs2Player.isWalking() && !Rs2Player.isInteracting() }, 100, 1000 * 10)
                            Global.sleepUntilTrue({ hasItemAtWorldPoint(rsGroundItem.item.id, rsGroundItem.tile.worldLocation) }, 100, 1000 * 5)
                            Global.sleepUntilTrue({ Inventory.count() == inventoryCount + 1 }, 100, 1000 * 5)
                        }
                    }
                }
            }

            return true
        }

        fun equipItemIds(itemIds: List<Int>) {
            itemIds.forEach { itemId: Int ->
                Microbot.status = "Equipping item $itemId"
                if (!Rs2Equipment.hasEquipped(itemId)) {
                    Inventory.getInventoryItem(itemId)?.let {
                        Microbot.getMouseForKotlin().click(it.bounds)
                        Global.sleepUntilTrue({ Rs2Equipment.hasEquipped(itemId) }, 100, 3000)
                    }
                }
            }
        }
    }
}