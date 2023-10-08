package net.runelite.client.plugins.griffinplugins.griffintrainer.trainers.fishing

import net.runelite.api.ItemID
import net.runelite.api.Skill
import net.runelite.api.coords.WorldArea
import net.runelite.api.coords.WorldPoint
import net.runelite.client.plugins.griffinplugins.griffintrainer.GriffinTrainerConfig
import net.runelite.client.plugins.griffinplugins.griffintrainer.TrainerInterruptor
import net.runelite.client.plugins.griffinplugins.griffintrainer.TrainerThread
import net.runelite.client.plugins.griffinplugins.griffintrainer.itemsets.GeneralItemSets
import net.runelite.client.plugins.griffinplugins.griffintrainer.models.DynamicItemSet
import net.runelite.client.plugins.griffinplugins.griffintrainer.models.inventory.InventoryRequirements
import net.runelite.client.plugins.griffinplugins.griffintrainer.trainers.BaseTrainer
import net.runelite.client.plugins.griffinplugins.util.helpers.FishingHelper
import net.runelite.client.plugins.microbot.Microbot
import net.runelite.client.plugins.microbot.staticwalker.WorldDestinations
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank
import net.runelite.client.plugins.microbot.util.inventory.Inventory

class FishingTrainer(private val config: GriffinTrainerConfig) : BaseTrainer(config) {
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
        val helmets = GeneralItemSets.getHelmetItemSet()
        val bodies = GeneralItemSets.getBodiesItemSet()
        val legs = GeneralItemSets.getLegsItemSet()
        val boots = GeneralItemSets.getBootsItemSet()
        val shields = GeneralItemSets.getShieldsItemSet()

        inventoryRequirements.addItemSet(helmets)
        inventoryRequirements.addItemSet(bodies)
        inventoryRequirements.addItemSet(legs)
        inventoryRequirements.addItemSet(boots)
        inventoryRequirements.addItemSet(shields)

        val tools = DynamicItemSet()
        val other = DynamicItemSet()

        val fishingLevel = Microbot.getClientForKotlin().getRealSkillLevel(Skill.FISHING)
        if (fishingLevel >= 1) {
            tools.add(ItemID.SMALL_FISHING_NET, 1, false)
        }

        if (fishingLevel >= 20) {
            tools.add(ItemID.FLY_FISHING_ROD, 1, false)
            other.add(ItemID.FEATHER, 300, false)
        }

        inventoryRequirements.addItemSet(tools)
        inventoryRequirements.addItemSet(other)

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
            ScriptState.BANKING -> runBankingState(itemIds)
        }
    }

    private fun runSetupState() {
        fetchItemRequirements()
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
            countBefore += Inventory.getInventoryItems().count { it.itemId == itemId }
        }

        if (FishingHelper.findAndInteract(npcName, actionName)) {
            var countAfter = 0
            itemIds.forEach { itemId: Int ->
                countAfter += Inventory.getInventoryItems().count { it.itemId == itemId }
            }

            TrainerThread.count += countAfter - countBefore
        }

        scriptState = ScriptState.BANKING
    }

    private fun runBankingState(itemIdsToDeposit: List<Int>) {
        if (config.collectItems()) {
            if (Inventory.isFull()) {
                Microbot.getWalkerForKotlin().staticWalkTo(getBankLocation(), 0)
                TrainerInterruptor.sleep(400, 600)

                Rs2Bank.getNearestBank()

                Rs2Bank.openBank()
                if (Rs2Bank.isOpen()) {

                    for (itemId in itemIdsToDeposit) {
                        Rs2Bank.depositAll(itemId)
                        TrainerInterruptor.sleepUntilTrue({ !Inventory.hasItem(itemId) }, 100, 3000)
                    }

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