package net.runelite.client.plugins.griffinplugins.griffintrainer.helpers

import net.runelite.api.coords.WorldPoint
import net.runelite.client.plugins.griffinplugins.griffintrainer.TrainerInterruptor
import net.runelite.client.plugins.microbot.Microbot
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory
import net.runelite.client.plugins.microbot.util.models.RS2Item
import net.runelite.client.plugins.microbot.util.player.Rs2Player
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab

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

            if (TrainerInterruptor.isInterrupted) {
                return false
            }

            val groupedItems: Map<WorldPoint, List<RS2Item>> = foundItems
                .filter { rs2GroundItem: RS2Item -> itemIds.contains(rs2GroundItem.item.id) }
                .groupBy { rs2GroundItem: RS2Item -> rs2GroundItem.tile.worldLocation }

            groupedItems.forEach { entry: Map.Entry<WorldPoint, List<RS2Item>> ->
                if (TrainerInterruptor.isInterrupted) {
                    return false
                }

                val standingOnItem = player.worldLocation.equals(entry.key)

                entry.value.forEach { rsGroundItem: RS2Item ->
                    if (TrainerInterruptor.isInterrupted) {
                        return false
                    }

                    if (Rs2Inventory.isFull()) {
                        return true
                    }

                    val stillExists = Rs2GroundItem.getAllAt(rsGroundItem.tile.worldLocation.x, rsGroundItem.tile.worldLocation.y).isNotEmpty()

                    if (stillExists) {
                        if (rsGroundItem.tile.worldLocation.distanceTo(player.worldLocation) >= 4) {
                            Microbot.getWalkerForKotlin().hybridWalkTo(rsGroundItem.tile.worldLocation)
                        }

                        val inventoryCount = Rs2Inventory.count()
                        if (Rs2GroundItem.interact(rsGroundItem)) {
                            if (!standingOnItem) {
                                TrainerInterruptor.sleepUntilTrue({ !Rs2Player.isWalking() }, 50, 3000)
                            }

                            TrainerInterruptor.sleepUntilTrue({ !Rs2Player.isWalking() && !Rs2Player.isInteracting() }, 100, 1000 * 10)
                            TrainerInterruptor.sleepUntilTrue({ hasItemAtWorldPoint(rsGroundItem.item.id, rsGroundItem.tile.worldLocation) }, 100, 1000 * 5)
                            TrainerInterruptor.sleepUntilTrue({ Rs2Inventory.count() == inventoryCount + 1 }, 100, 1000 * 5)
                        }
                    }
                }
            }

            return true
        }

        fun equipItemIds(itemPairs: List<Pair<Int, Boolean>>) {
            Rs2Tab.switchToInventoryTab()

            for (itemPair in itemPairs) {
                if (TrainerInterruptor.isInterrupted) {
                    return
                }

                if (!itemPair.second) {
                    continue
                }

                Microbot.status = "Equipping item ${itemPair.first}"
                if (!Rs2Equipment.hasEquipped(itemPair.first)) {
                    Rs2Inventory.get(itemPair.first)?.let {
                        Rs2Inventory.interact(it)
                        TrainerInterruptor.sleepUntilTrue({ Rs2Equipment.hasEquipped(itemPair.first) }, 100, 3000)
                    }
                }
            }
        }
    }
}