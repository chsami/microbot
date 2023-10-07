package net.runelite.client.plugins.griffinplugins.griffintrainer.trainers.fishing

import net.runelite.api.ItemID
import net.runelite.api.Skill
import net.runelite.api.VarPlayer
import net.runelite.api.coords.WorldArea
import net.runelite.api.coords.WorldPoint
import net.runelite.api.widgets.WidgetInfo
import net.runelite.client.plugins.griffinplugins.griffintrainer.*
import net.runelite.client.plugins.griffinplugins.griffintrainer.helpers.BankHelper
import net.runelite.client.plugins.griffinplugins.griffintrainer.helpers.ItemHelper
import net.runelite.client.plugins.griffinplugins.griffintrainer.helpers.NPCHelper
import net.runelite.client.plugins.griffinplugins.griffintrainer.itemsets.GeneralItemSets
import net.runelite.client.plugins.griffinplugins.griffintrainer.models.inventory.InventoryRequirements
import net.runelite.client.plugins.griffinplugins.griffintrainer.trainers.BaseTrainer
import net.runelite.client.plugins.griffinplugins.util.helpers.FishingHelper
import net.runelite.client.plugins.microbot.Microbot
import net.runelite.client.plugins.microbot.staticwalker.WorldDestinations
import net.runelite.client.plugins.microbot.util.Global
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank
import net.runelite.client.plugins.microbot.util.inventory.Inventory
import net.runelite.client.plugins.microbot.util.player.Rs2Player
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget

class FishingTrainer(private val config: GriffinTrainerConfig) : BaseTrainer() {
    private val draynorFishingArea = WorldArea(3084, 3223, 7, 11, 0)
    private val draynorFishingPoint = WorldPoint(3087, 3229, 0)
    private val barbarianFishingArea = WorldArea(3101, 3423, 10, 13, 0)
    private val barbarianFishingPoint = WorldPoint(3105, 3430, 0)

    private enum class ScriptState {
        SETUP, CHECKING_AREA, FISHING, BANKING
    }

    private var scriptState: ScriptState = ScriptState.SETUP

    override fun getBankLocation(): WorldPoint {
        return if (getMinimumSkillLevel() < 20) {
            WorldDestinations.DRAYNOR_VILLAGE_BANK.worldPoint
        } else {
            WorldDestinations.VARROCK_WEST_BANK.worldPoint
        }
    }

    override fun getInventoryRequirements(): InventoryRequirements {
        val inventoryRequirements = InventoryRequirements()
        val weapons = GeneralItemSets.getWeaponItemSet()
        val helmets = GeneralItemSets.getHelmetItemSet()
        val bodies = GeneralItemSets.getBodiesItemSet()
        val legs = GeneralItemSets.getLegsItemSet()
        val boots = GeneralItemSets.getBootsItemSet()
        val shields = GeneralItemSets.getShieldsItemSet()

        inventoryRequirements.addItemSet(weapons)
        inventoryRequirements.addItemSet(helmets)
        inventoryRequirements.addItemSet(bodies)
        inventoryRequirements.addItemSet(legs)
        inventoryRequirements.addItemSet(boots)
        inventoryRequirements.addItemSet(shields)

        return inventoryRequirements
    }

    override fun getMinimumSkillLevel(): Int {
        return Microbot.getClientForKotlin().getRealSkillLevel(Skill.FISHING)
    }

    override fun shouldTrain(): Boolean {
        return getMinimumSkillLevel() < config.fishingLevel()
    }

    override fun process(): Boolean {
        val minimumSkillLevel = getMinimumSkillLevel()

        if (minimumSkillLevel < 15) {
            updateCounts("Fishing Shrimp", "Shrimp Caught")
            processState(draynorFishingArea, draynorFishingPoint, "fishing spot", "small net", listOf(ItemID.RAW_SHRIMPS))

        } else if (minimumSkillLevel < 20) {
            updateCounts("Fishing Shrimp & Anchovies", "Shrimp & Anchovies Caught")
            processState(draynorFishingArea, draynorFishingPoint, "fishing spot", "small net", listOf(ItemID.RAW_SHRIMPS, ItemID.RAW_ANCHOVIES))

        } else if (minimumSkillLevel < 99) {
            updateCounts("Fishing Trout & Salmon", "Trout & Salmon Caught")
            processState(barbarianFishingArea, barbarianFishingPoint, "rod fishing spot", "lure", listOf(ItemID.RAW_SALMON, ItemID.RAW_TROUT))

        } else {
            return true
        }

        return false
    }

    private fun processState(worldArea: WorldArea, worldPoint: WorldPoint, npcName: String, actionName: String, itemIds: List<Int>) {
        when (scriptState) {
            ScriptState.SETUP -> runSetupState()
            ScriptState.CHECKING_AREA -> runCheckingAreaState(worldArea, worldPoint)
            ScriptState.FISHING -> runFishingState(npcName, actionName, itemIds)
            ScriptState.BANKING -> runBankingState()
        }
    }

    private fun runSetupState() {
        if (config.equipGear()) {
            Microbot.getWalkerForKotlin().staticWalkTo(getBankLocation())
            if (!Rs2Bank.isOpen()) {
                Rs2Bank.openBank()
            }

            Rs2Bank.depositAll()
            TrainerInterruptor.sleep(300, 600)
            Rs2Bank.depositEquipment()
            TrainerInterruptor.sleep(600, 900)

            val foundItemIds = BankHelper.fetchInventoryRequirements(getInventoryRequirements())
            Rs2Bank.closeBank()
            TrainerInterruptor.sleepUntilTrue({ !Rs2Bank.isOpen() }, 100, 3000)

            ItemHelper.equipItemIds(foundItemIds)
        }

        scriptState = ScriptState.CHECKING_AREA
    }

    private fun runCheckingAreaState(worldArea: WorldArea, worldPoint: WorldPoint) {
        val player = Microbot.getClientForKotlin().localPlayer
        if (!worldArea.contains(player.worldLocation)) {
            Microbot.getWalkerForKotlin().staticWalkTo(worldPoint)
        }

        scriptState = ScriptState.FISHING
    }

    private fun runFishingState(npcName: String, actionName: String, itemIds: List<Int>) {
        var countBefore = 0
        itemIds.forEach { itemId: Int ->
            countBefore += Inventory.getInventoryItems().count { it.id == itemId }
        }

        if (FishingHelper.findAndInteract(npcName, actionName)) {
            var countAfter = 0
            itemIds.forEach { itemId: Int ->
                countAfter += Inventory.getInventoryItems().count { it.id == itemId }
            }

            TrainerThread.count += countAfter - countBefore
        }

        scriptState = ScriptState.BANKING
    }

    private fun runBankingState() {
        if (config.collectItems()) {
            if (Inventory.isFull()) {
                Microbot.getWalkerForKotlin().staticWalkTo(getBankLocation(), 0)
                TrainerInterruptor.sleep(400, 600)

                Rs2Bank.getNearestBank()

                Rs2Bank.openBank()
                if (Rs2Bank.isOpen()) {
                    Rs2Bank.depositAll()
                    Rs2Bank.closeBank()
                }
                TrainerInterruptor.sleep(200)
            }
        } else {
            Inventory.dropAll()
        }

        scriptState = ScriptState.CHECKING_AREA
    }
}