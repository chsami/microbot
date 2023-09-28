package net.runelite.client.plugins.griffinplugins.griffintrainer

import net.runelite.api.EquipmentInventorySlot
import net.runelite.api.ItemID
import net.runelite.api.Skill
import net.runelite.api.VarPlayer
import net.runelite.api.coords.WorldArea
import net.runelite.api.coords.WorldPoint
import net.runelite.api.widgets.Widget
import net.runelite.api.widgets.WidgetInfo
import net.runelite.client.plugins.griffinplugins.griffincombat.GriffinCombatConfig
import net.runelite.client.plugins.griffinplugins.griffintrainer.helpers.BankHelper
import net.runelite.client.plugins.griffinplugins.griffintrainer.helpers.ItemHelper
import net.runelite.client.plugins.griffinplugins.griffintrainer.helpers.NPCHelper
import net.runelite.client.plugins.griffinplugins.griffintrainer.models.DynamicItemSet
import net.runelite.client.plugins.griffinplugins.griffintrainer.models.inventory.InventoryRequirements
import net.runelite.client.plugins.microbot.Microbot
import net.runelite.client.plugins.microbot.Script
import net.runelite.client.plugins.microbot.staticwalker.WorldDestinations
import net.runelite.client.plugins.microbot.util.Global
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment
import net.runelite.client.plugins.microbot.util.inventory.Inventory
import net.runelite.client.plugins.microbot.util.player.Rs2Player
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget
import java.util.concurrent.TimeUnit

class GriffinCombatScript : Script() {
    companion object {
        private val LUMBRIDGE_CHICKENS_WORLD_AREA = WorldArea(3225, 3287, 12, 15, 0)
        private val LUMBRIDGE_COWS_WORLD_AREA = WorldArea(3255, 3258, 9, 37, 0)
        var state: State = State.SETUP
    }

    lateinit var config: GriffinCombatConfig

    enum class State {
        SETUP, WAITING, FIGHTING, LOOTING
    }

    fun run(config: GriffinCombatConfig): Boolean {
//        Rs2Camera.setAngle(45)
//        Rs2Camera.setPitch(1.0f)

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay({
            this.config = config

            if (!super.run()) return@scheduleWithFixedDelay

            try {
                if (!shouldTrain()) {
                    shutdown()
                    return@scheduleWithFixedDelay
                }

                val minimumSkillRequirement = minimumSkillRequirement

                if (minimumSkillRequirement < 10) {
                    updateCounts("Fighting Chickens", "Chickens Killed")
                    fightNPC(LUMBRIDGE_CHICKENS_WORLD_AREA, WorldDestinations.LUMBRIDGE_CHICKENS.worldPoint, "chicken", listOf(ItemID.FEATHER, ItemID.BONES))

                } else if (minimumSkillRequirement < 99) {
                    updateCounts("Fighting Cows", "Cows Killed")
                    fightNPC(LUMBRIDGE_COWS_WORLD_AREA, WorldDestinations.LUMBRIDGE_COWS.worldPoint, "cow", listOf(ItemID.COWHIDE, ItemID.BONES))

                } else {
                    shutdown()
                    return@scheduleWithFixedDelay
                }

            } catch (ex: Exception) {
                println(ex)
            }
        }, 0, 400, TimeUnit.MILLISECONDS)
        return true
    }

    fun updateCounts(status: String, countLabel: String) {
        if (GriffinCombatPlugin.countLabel != countLabel) {
            GriffinCombatPlugin.count = 0
        }

        Microbot.status = status
        GriffinCombatPlugin.countLabel = countLabel
    }

    fun getBankLocation(): WorldPoint {
        return WorldDestinations.LUMBRIDGE_BANK.worldPoint
    }

    fun getInventoryRequirements(): InventoryRequirements {
        val inventoryRequirements = InventoryRequirements()
        val attackLevel = Microbot.getClientForKotlin().getRealSkillLevel(Skill.ATTACK)
        val defenceLevel = Microbot.getClientForKotlin().getRealSkillLevel(Skill.DEFENCE)

        val weapons = DynamicItemSet()
        if (attackLevel >= 1) {
            weapons.add(ItemID.BRONZE_2H_SWORD, 1)
            weapons.add(ItemID.BRONZE_DAGGER, 1)
            weapons.add(ItemID.BRONZE_SWORD, 1)
            weapons.add(ItemID.BRONZE_LONGSWORD, 1)
            weapons.add(ItemID.BRONZE_SCIMITAR, 1)
            weapons.add(ItemID.IRON_2H_SWORD, 1)
            weapons.add(ItemID.IRON_DAGGER, 1)
            weapons.add(ItemID.IRON_SWORD, 1)
            weapons.add(ItemID.IRON_LONGSWORD, 1)
            weapons.add(ItemID.IRON_SCIMITAR, 1)
        }
        if (attackLevel >= 5) {
            weapons.add(ItemID.STEEL_2H_SWORD, 1)
            weapons.add(ItemID.STEEL_DAGGER, 1)
            weapons.add(ItemID.STEEL_SWORD, 1)
            weapons.add(ItemID.STEEL_LONGSWORD, 1)
            weapons.add(ItemID.STEEL_SCIMITAR, 1)
        }
        if (attackLevel >= 10) {
            weapons.add(ItemID.BLACK_2H_SWORD, 1)
            weapons.add(ItemID.BLACK_DAGGER, 1)
            weapons.add(ItemID.BLACK_SWORD, 1)
            weapons.add(ItemID.BLACK_LONGSWORD, 1)
            weapons.add(ItemID.BLACK_SCIMITAR, 1)
        }
        if (attackLevel >= 20) {
            weapons.add(ItemID.MITHRIL_2H_SWORD, 1)
            weapons.add(ItemID.MITHRIL_DAGGER, 1)
            weapons.add(ItemID.MITHRIL_SWORD, 1)
            weapons.add(ItemID.MITHRIL_LONGSWORD, 1)
            weapons.add(ItemID.MITHRIL_SCIMITAR, 1)
        }
        if (attackLevel >= 30) {
            weapons.add(ItemID.ADAMANT_2H_SWORD, 1)
            weapons.add(ItemID.ADAMANT_DAGGER, 1)
            weapons.add(ItemID.ADAMANT_SWORD, 1)
            weapons.add(ItemID.ADAMANT_LONGSWORD, 1)
            weapons.add(ItemID.ADAMANT_SCIMITAR, 1)
        }
        if (attackLevel >= 40) {
            weapons.add(ItemID.RUNE_2H_SWORD, 1)
            weapons.add(ItemID.RUNE_DAGGER, 1)
            weapons.add(ItemID.RUNE_SWORD, 1)
            weapons.add(ItemID.RUNE_LONGSWORD, 1)
            weapons.add(ItemID.RUNE_SCIMITAR, 1)
        }

        if (weapons.getItems().isNotEmpty()) {
            inventoryRequirements.addItemSet(weapons)
        }

        val shields = DynamicItemSet()

        if (defenceLevel >= 1) {
            shields.add(ItemID.WOODEN_SHIELD, 1)
        }

        if (shields.getItems().isNotEmpty()) {
            inventoryRequirements.addItemSet(shields)
        }

        val plateBodies = DynamicItemSet()
        if (defenceLevel >= 1) {
            plateBodies.add(ItemID.BRONZE_PLATEBODY, 1)
            plateBodies.add(ItemID.IRON_PLATEBODY, 1)
        }
        if (defenceLevel >= 5) {
            plateBodies.add(ItemID.STEEL_PLATEBODY, 1)
        }
        if (defenceLevel >= 10) {
            plateBodies.add(ItemID.BLACK_PLATEBODY, 1)
            plateBodies.add(ItemID.WHITE_PLATEBODY, 1)
        }
        if (defenceLevel >= 20) {
            plateBodies.add(ItemID.MITHRIL_PLATEBODY, 1)
        }

        if (plateBodies.getItems().isNotEmpty()) {
            inventoryRequirements.addItemSet(plateBodies)
        }

        val plateLegs = DynamicItemSet()
        if (defenceLevel >= 1) {
            plateLegs.add(ItemID.BRONZE_PLATELEGS, 1)
            plateLegs.add(ItemID.IRON_PLATELEGS, 1)
        }
        if (defenceLevel >= 5) {
            plateLegs.add(ItemID.STEEL_PLATELEGS, 1)
        }
        if (defenceLevel >= 10) {
            plateLegs.add(ItemID.BLACK_PLATELEGS, 1)
            plateLegs.add(ItemID.WHITE_PLATELEGS, 1)
        }
        if (defenceLevel >= 20) {
            plateLegs.add(ItemID.MITHRIL_PLATELEGS, 1)
        }

        if (plateLegs.getItems().isNotEmpty()) {
            inventoryRequirements.addItemSet(plateLegs)
        }

        return inventoryRequirements
    }

    fun shouldTrain(): Boolean {
        val attackLevel = Microbot.getClientForKotlin().getRealSkillLevel(Skill.ATTACK)
        val strengthLevel = Microbot.getClientForKotlin().getRealSkillLevel(Skill.STRENGTH)
        val defenceLevel = Microbot.getClientForKotlin().getRealSkillLevel(Skill.DEFENCE)

        if (attackLevel < config.attackLevel()) {
            return true
        } else if (strengthLevel < config.strengthLevel()) {
            return true
        } else if (defenceLevel < config.defenseLevel()) {
            return true
        }
        return false
    }

    private val minimumSkillRequirement: Int
        get() {
            val attackLevel = Microbot.getClientForKotlin().getRealSkillLevel(Skill.ATTACK)
            val strengthLevel = Microbot.getClientForKotlin().getRealSkillLevel(Skill.STRENGTH)
            val defenceLevel = Microbot.getClientForKotlin().getRealSkillLevel(Skill.DEFENCE)
            return listOf(attackLevel, strengthLevel, defenceLevel).min()
        }

    private fun fightNPC(worldArea: WorldArea, worldPoint: WorldPoint, npcName: String, itemLootNames: List<Int>) {
        if (Rs2Player.isWalking() || Rs2Player.isInteracting()) {
            return
        }

        when (state) {
            State.SETUP -> {
                Microbot.getWalkerForKotlin().staticWalkTo(getBankLocation())
                if (!Rs2Bank.isOpen()) {
                    Rs2Bank.openBank()
                }

                Rs2Bank.depositAll()
                Global.sleep(300, 600)
                Rs2Bank.depositEquipment()
                Global.sleep(600, 900)

                val foundItemIds = BankHelper.fetchInventoryRequirements(getInventoryRequirements())
                Rs2Bank.closeBank()
                Global.sleepUntilTrue({ !Rs2Bank.isOpen() }, 100, 3000)

                foundItemIds.forEach { itemId: Int ->
                    println("Equipping item $itemId")
                    if (!Rs2Equipment.hasEquipped(itemId)) {
                        Inventory.getInventoryItem(itemId)?.let {
                            Microbot.getMouseForKotlin().click(it.bounds)
                            Global.sleepUntilTrue({ Rs2Equipment.hasEquipped(itemId) }, 100, 3000)
                        }
                    }
                }
                state = State.WAITING
            }

            State.WAITING -> {
                if (!worldArea.contains(Rs2Player.getWorldLocation())) {
                    Microbot.getWalkerForKotlin().hybridWalkTo(worldPoint)
                }
                state = State.FIGHTING
            }

            State.FIGHTING -> {
                setCombatStyle()
                if (NPCHelper.findAndAttack(npcName)) {
                    GriffinCombatPlugin.count++
                    state = State.LOOTING
                }
            }

            State.LOOTING -> {
                if (config.collectItems()) {
                    if (Inventory.isFull()) {
                        buryBones()
                        Global.sleep(200)
                    }

                    if (Inventory.isFull()) {
                        Microbot.getWalkerForKotlin().hybridWalkTo(getBankLocation())
                        Rs2Bank.openBank()
                        if (Rs2Bank.isOpen()) {
                            Rs2Bank.depositAll()
                            Rs2Bank.closeBank()
                        }
                        Global.sleep(200)
                    }

                    ItemHelper.findAndLootItems(itemLootNames, 2)
                }
                state = State.WAITING
            }
        }
    }

    private fun buryBones() {
        while (Inventory.hasItem(ItemID.BONES)) {
            val inventoryCount = Inventory.count()
            Inventory.useItemAction(ItemID.BONES, "Bury")
            Global.sleep(600)
            Global.sleepUntilTrue({ Inventory.count() == inventoryCount - 1 && !Rs2Player.isInteracting() }, 100, 3000)
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

        } else if (defenceLevel < config.defenseLevel()) {
            if (!isDefensiveCombatStyleSelected()) {
                toggleDefensiveCombatStyle()
            }
        }
    }

    private val isWieldingRequiredWeapon: Boolean
        get() {
            if (Rs2Equipment.hasEquippedSlot(EquipmentInventorySlot.WEAPON)) {
                return false
            }
            return getInventoryRequirements().getItemSets()
                .map { dynamicItemSet: DynamicItemSet -> dynamicItemSet.getItems() }.flatten()
                .map { thing: Pair<Int, Int> -> thing.first }
                .contains(Rs2Equipment.getEquippedItem(EquipmentInventorySlot.WEAPON).id)
        }

    private val getRequiredWeaponFromInventory: Widget?
        get() {
            val inventoryItems = Inventory.getInventoryItems()
            getInventoryRequirements().getItemSets()
                .map { dynamicItemSet: DynamicItemSet -> dynamicItemSet.getItems() }
                .flatten()
                .map { thing: Pair<Int, Int> -> thing.first }
                .forEach { requiredItemId: Int ->
                    inventoryItems.forEach { inventoryItemWidget: Widget ->
                        if (inventoryItemWidget.itemId == requiredItemId) {
                            return inventoryItemWidget
                        }
                    }
                }
            return null
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
