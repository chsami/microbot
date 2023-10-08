package net.runelite.client.plugins.griffinplugins.griffintrainer.trainers.combat

import net.runelite.api.ItemID
import net.runelite.api.Player
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
import net.runelite.client.plugins.griffinplugins.griffintrainer.TrainerInterruptor
import net.runelite.client.plugins.griffinplugins.util.helpers.WorldHelper
import net.runelite.client.plugins.microbot.Microbot
import net.runelite.client.plugins.microbot.staticwalker.WorldDestinations
import net.runelite.client.plugins.microbot.util.Global
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank
import net.runelite.client.plugins.microbot.util.inventory.Inventory
import net.runelite.client.plugins.microbot.util.player.Rs2Player
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget

class CombatTrainer(private val config: GriffinTrainerConfig) : BaseTrainer() {
    private val lumbridgeChickensWorldArea = WorldArea(3225, 3287, 12, 15, 0)
    private val lumbridgeCowsWorldArea = WorldArea(3255, 3258, 9, 37, 0)

    private enum class ScriptState {
        SETUP, CHECKING_AREA, FIGHTING, LOOTING, BANKING
    }

    private var scriptState: ScriptState = ScriptState.SETUP

    override fun getBankLocation(): WorldPoint {
        return WorldDestinations.LUMBRIDGE_BANK.worldPoint
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
        val attackLevel = Microbot.getClientForKotlin().getRealSkillLevel(Skill.ATTACK)
        val strengthLevel = Microbot.getClientForKotlin().getRealSkillLevel(Skill.STRENGTH)
        val defenceLevel = Microbot.getClientForKotlin().getRealSkillLevel(Skill.DEFENCE)
        return listOf(attackLevel, strengthLevel, defenceLevel).min()
    }

    override fun shouldTrain(): Boolean {
        val minimumSkillLevel = getMinimumSkillLevel()
        return minimumSkillLevel < config.attackLevel() || minimumSkillLevel < config.strengthLevel() || minimumSkillLevel < config.defenceLevel()
    }

    override fun process(): Boolean {
        val minimumSkillLevel = getMinimumSkillLevel()

        if (minimumSkillLevel < 10) {
            updateCounts("Fighting Chickens", "Chickens Killed")
            processState(lumbridgeChickensWorldArea, WorldDestinations.LUMBRIDGE_CHICKENS.worldPoint, "chicken", listOf(ItemID.FEATHER, ItemID.BONES))

        } else if (minimumSkillLevel < 99) {
            updateCounts("Fighting Cows", "Cows Killed")
            processState(lumbridgeCowsWorldArea, WorldDestinations.LUMBRIDGE_COWS.worldPoint, "cow", listOf(ItemID.COWHIDE, ItemID.BONES))

        } else {
            return true
        }

        return false
    }

    private fun updateCounts(status: String, countLabel: String) {
        if (TrainerThread.countLabel != countLabel) {
            TrainerThread.count = 0
        }

        Microbot.status = status
        TrainerThread.countLabel = countLabel
    }

    private fun processState(worldArea: WorldArea, worldPoint: WorldPoint, npcName: String, itemLootIds: List<Int>) {
        when (scriptState) {
            ScriptState.SETUP -> runSetupState()
            ScriptState.CHECKING_AREA -> runCheckingAreaState(worldArea, worldPoint)
            ScriptState.FIGHTING -> runFightingState(npcName)
            ScriptState.LOOTING -> runLootingState(itemLootIds)
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

        if (config.hopWorlds()) {

            val players = Microbot.getClientForKotlin().players
            val playerCount = players
                .filterNotNull()
                .filter { otherPlayer: Player -> otherPlayer.id != player.id }
                .filter { otherPlayer: Player -> worldArea.contains(player.worldLocation) }
                .count()

            if (config.hopWorlds() && playerCount > config.maxPlayers()) {
                WorldHelper.hopToWorldWithoutPlayersInArea(
                    Rs2Player.isMember(),
                    worldArea,
                    config.maxPlayers(),
                    config.maxWorldsToTry()
                )
            }
        }

        scriptState = ScriptState.FIGHTING
    }

    private fun runFightingState(npcName: String) {
        setCombatStyle()
        if (NPCHelper.findAndAttack(npcName, true)) {
            TrainerThread.count++
        }

        scriptState = ScriptState.LOOTING
    }

    private fun runLootingState(itemLootIds: List<Int>) {
        val prayerLevel = Microbot.getClientForKotlin().getRealSkillLevel(Skill.PRAYER)
        if (config.collectItems()) {
            if (Inventory.isFull() && prayerLevel < config.prayerLevel()) {
                buryBones()
                TrainerInterruptor.sleep(200)
            }

            if (!Inventory.isFull()) {
                ItemHelper.findAndLootItems(itemLootIds, 2)
            }
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

    private fun buryBones() {
        while (Inventory.hasItem(ItemID.BONES)) {
            val inventoryCount = Inventory.count()
            Inventory.useItemAction(ItemID.BONES, "Bury")
            TrainerInterruptor.sleep(700)
            TrainerInterruptor.sleepUntilTrue({ Inventory.count() == inventoryCount - 1 && !Rs2Player.isInteracting() }, 100, 3000)
        }
    }

    private fun setCombatStyle() {
        val attackLevel = Microbot.getClientForKotlin().getRealSkillLevel(Skill.ATTACK)
        val strengthLevel = Microbot.getClientForKotlin().getRealSkillLevel(Skill.STRENGTH)
        val defenceLevel = Microbot.getClientForKotlin().getRealSkillLevel(Skill.DEFENCE)


        if (attackLevel < config.attackLevel()) {
            if (!isAccurateCombatStyleSelected()) {
                toggleAccurateCombatStyle()
            }

        } else if (strengthLevel < config.strengthLevel()) {
            if (!isAggressiveCombatStyleSelected()) {
                toggleAggressiveCombatStyle()
            }

        } else if (defenceLevel < config.defenceLevel()) {
            if (!isDefensiveCombatStyleSelected()) {
                toggleDefensiveCombatStyle()
            }
        }
    }

    private fun toggleCombatStyle(attackStyleWidgetInfo: WidgetInfo): Boolean {
        Rs2Tab.switchToCombatOptionsTab()
        Global.sleep(150, 300)
        return Rs2Widget.clickWidget(attackStyleWidgetInfo)
    }

    fun toggleAccurateCombatStyle(): Boolean {
        return toggleCombatStyle(WidgetInfo.COMBAT_STYLE_ONE)
    }

    fun toggleAggressiveCombatStyle(): Boolean {
        return toggleCombatStyle(WidgetInfo.COMBAT_STYLE_TWO)
    }

    fun toggleControlledCombatStyle(): Boolean {
        return toggleCombatStyle(WidgetInfo.COMBAT_STYLE_THREE)
    }

    fun toggleDefensiveCombatStyle(): Boolean {
        return toggleCombatStyle(WidgetInfo.COMBAT_STYLE_FOUR)
    }

    fun isAccurateCombatStyleSelected(): Boolean {
        return Microbot.getVarbitPlayerValue(VarPlayer.ATTACK_STYLE) == 0
    }

    fun isAggressiveCombatStyleSelected(): Boolean {
        return Microbot.getVarbitPlayerValue(VarPlayer.ATTACK_STYLE) == 1
    }

    fun isControlledCombatStyleSelected(): Boolean {
        return Microbot.getVarbitPlayerValue(VarPlayer.ATTACK_STYLE) == 2
    }

    fun isDefensiveCombatStyleSelected(): Boolean {
        return Microbot.getVarbitPlayerValue(VarPlayer.ATTACK_STYLE) == 3
    }
}