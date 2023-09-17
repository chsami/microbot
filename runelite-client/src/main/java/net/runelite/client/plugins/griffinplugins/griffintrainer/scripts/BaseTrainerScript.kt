package net.runelite.client.plugins.griffinplugins.griffintrainer.scripts

import net.runelite.api.coords.WorldPoint
import net.runelite.client.plugins.griffinplugins.griffintrainer.models.inventory.InventoryRequirements
import net.runelite.client.plugins.microbot.Script

abstract class BaseTrainerScript : Script() {
    abstract fun getBankLocation(): WorldPoint
    abstract fun getInventoryRequirements(): InventoryRequirements
    abstract fun shouldTrain(): Boolean

    fun fetchRequiredItems() {
//        val player = Alfred.api.players.localPlayer
//
//        // if we are too far away from the bank we don't have it open
//        if (player.worldLocation.distanceTo(getBankLocation()) > 5) {
//            Alfred.api.walk.walkTo(getBankLocation())
//        }
//
//        // if the bank is not open then open it
//        Alfred.tasks.banking.openBank()
//
//        Alfred.api.banks.depositInventory()
//        Alfred.api.banks.depositEquipment()
//        Alfred.tasks.inventory.fetchInventoryRequirements(getInventoryRequirements())
//
//        Alfred.sleep(250, 750)
//        Alfred.api.banks.close()
//        Alfred.sleep(250, 750)
    }
}