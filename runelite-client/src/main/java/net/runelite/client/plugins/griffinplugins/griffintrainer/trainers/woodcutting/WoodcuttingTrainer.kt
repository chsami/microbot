package net.runelite.client.plugins.griffinplugins.griffintrainer.trainers.woodcutting

import net.runelite.api.ItemID
import net.runelite.api.Skill
import net.runelite.api.coords.WorldArea
import net.runelite.api.coords.WorldPoint
import net.runelite.client.plugins.griffinplugins.griffintrainer.GriffinTrainerConfig
import net.runelite.client.plugins.griffinplugins.griffintrainer.TrainerInterruptor
import net.runelite.client.plugins.griffinplugins.griffintrainer.TrainerThread
import net.runelite.client.plugins.griffinplugins.griffintrainer.WorldDestinations
import net.runelite.client.plugins.griffinplugins.griffintrainer.itemsets.GeneralItemSets
import net.runelite.client.plugins.griffinplugins.griffintrainer.models.inventory.InventoryRequirements
import net.runelite.client.plugins.griffinplugins.griffintrainer.trainers.BaseTrainer
import net.runelite.client.plugins.griffinplugins.util.helpers.WoodcuttingHelper
import net.runelite.client.plugins.microbot.Microbot
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker

class WoodcuttingTrainer(private val config: GriffinTrainerConfig) : BaseTrainer(config) {
    private val varrockWestTreeWorldArea = WorldArea(3159, 3388, 13, 25, 0)
    private val varrockWestTreeWorldPoint = WorldPoint(3164, 3402, 0)
    private val varrockWestOakTreeWorldArea = WorldArea(3132, 3393, 17, 23, 0)
    private val varrockWestOakTreeWorldPoint = WorldPoint(3137, 3403, 0)
    private val seersWillowTreeWorldArea = WorldArea(2701, 3502, 20, 14, 0)
    private val seersWillowTreeWorldPoint = WorldPoint(2709, 3506, 0)

    private enum class ScriptState {
        SETUP, CHECKING_AREA, CHOPPING, BANKING
    }

    private var scriptState: ScriptState = ScriptState.SETUP

    override fun getBankLocation(): WorldPoint {
        return if (getMinimumSkillLevel() < 30) {
            WorldDestinations.VARROCK_WEST_BANK.worldPoint
        } else {
            WorldDestinations.SEERS_VILLAGE_BANK.worldPoint
        }
    }

    override fun getInventoryRequirements(): InventoryRequirements {
        val inventoryRequirements = InventoryRequirements()
        val axes = GeneralItemSets.getAxesItemSet()
        val helmets = GeneralItemSets.getHelmetItemSet()
        val bodies = GeneralItemSets.getBodiesItemSet()
        val legs = GeneralItemSets.getLegsItemSet()
        val boots = GeneralItemSets.getBootsItemSet()
        val shields = GeneralItemSets.getShieldsItemSet()

        inventoryRequirements.addItemSet(axes)
        inventoryRequirements.addItemSet(helmets)
        inventoryRequirements.addItemSet(bodies)
        inventoryRequirements.addItemSet(legs)
        inventoryRequirements.addItemSet(boots)
        inventoryRequirements.addItemSet(shields)
        return inventoryRequirements
    }

    override fun getMinimumSkillLevel(): Int {
        return Microbot.getClientForKotlin().getRealSkillLevel(Skill.WOODCUTTING)
    }

    override fun shouldTrain(): Boolean {
        return getMinimumSkillLevel() < config.woodcuttingLevel()
    }

    override fun process(): Boolean {
        val minimumSkillLevel = getMinimumSkillLevel()

        if (minimumSkillLevel < 15) {
            updateCounts("Chopping Trees", "Logs Collected")
            processState(varrockWestTreeWorldArea, varrockWestTreeWorldPoint, "tree", ItemID.LOGS)

        } else if (minimumSkillLevel < 30) {
            updateCounts("Chopping Oak Trees", "Oak Logs Collected")
            processState(varrockWestOakTreeWorldArea, varrockWestOakTreeWorldPoint, "oak tree", ItemID.OAK_LOGS)

        } else if (minimumSkillLevel < 99) {
            updateCounts("Chopping Willow Trees", "Willow Logs Collected")
            processState(seersWillowTreeWorldArea, seersWillowTreeWorldPoint, "willow tree", ItemID.WILLOW_LOGS)

        } else {
            return true
        }

        return false
    }

    private fun processState(worldArea: WorldArea, worldPoint: WorldPoint, treeName: String, itemId: Int) {
        when (scriptState) {
            ScriptState.SETUP -> runSetupState()
            ScriptState.CHECKING_AREA -> runCheckingAreaState(worldArea, worldPoint)
            ScriptState.CHOPPING -> runChoppingState(treeName, itemId)
            ScriptState.BANKING -> runBankingState()
        }
    }

    private fun runSetupState() {
        fetchItemRequirements()
        scriptState = ScriptState.CHECKING_AREA
    }

    private fun runCheckingAreaState(worldArea: WorldArea, worldPoint: WorldPoint) {
        val player = Microbot.getClientForKotlin().localPlayer
        if (!worldArea.contains(player.worldLocation)) {
            Rs2Walker.walkTo(worldPoint)
        }

        scriptState = ScriptState.CHOPPING
    }

    private fun runChoppingState(treeName: String, itemId: Int) {
        val countBefore = Rs2Inventory.count(itemId)

        if (WoodcuttingHelper.findAndChopTree(treeName)) {
            val countAfter = Rs2Inventory.count(itemId)
            TrainerThread.count += countAfter - countBefore
        }

        scriptState = ScriptState.BANKING
    }

    private fun runBankingState() {
        if (config.collectItems()) {
            if (Rs2Inventory.isFull()) {
                Rs2Walker.walkTo(getBankLocation())
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
            Rs2Inventory.dropAll()
        }

        scriptState = ScriptState.CHECKING_AREA
    }
}