package net.runelite.client.plugins.griffinplugins.griffintrainer.scripts

import net.runelite.api.EquipmentInventorySlot
import net.runelite.api.ItemID
import net.runelite.api.Skill
import net.runelite.api.coords.WorldArea
import net.runelite.api.coords.WorldPoint
import net.runelite.api.widgets.Widget
import net.runelite.client.plugins.griffinplugins.griffintrainer.GriffinTrainerConfig
import net.runelite.client.plugins.griffinplugins.griffintrainer.helpers.ItemHelper
import net.runelite.client.plugins.griffinplugins.griffintrainer.helpers.NPCHelper
import net.runelite.client.plugins.griffinplugins.griffintrainer.models.DynamicItemSet
import net.runelite.client.plugins.griffinplugins.griffintrainer.models.inventory.InventoryRequirements
import net.runelite.client.plugins.microbot.Microbot
import net.runelite.client.plugins.microbot.staticwalker.WorldDestinations
import net.runelite.client.plugins.microbot.util.Global
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank
import net.runelite.client.plugins.microbot.util.camera.Camera
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment
import net.runelite.client.plugins.microbot.util.inventory.Inventory
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc
import net.runelite.client.plugins.microbot.util.player.Rs2Player
import java.util.concurrent.TimeUnit

class CombatTrainerScript : BaseTrainerScript() {

    companion object {
        private val LUMBRIDGE_CHICKENS_WORLD_AREA = WorldArea(3225, 3287, 12, 15, 0)
        private val LUMBRIDGE_COWS_WORLD_AREA = WorldArea(3255, 3258, 9, 37, 0)
    }


    lateinit var config: GriffinTrainerConfig
    var state: State = State.WAITING

    enum class State {
        SETUP, WAITING, FIGHTING, LOOTING
    }


    fun run(config: GriffinTrainerConfig): Boolean {
        this.config = config

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay({
            if (!super.run()) return@scheduleWithFixedDelay
            try {
                val minimumSkillRequirement = minimumSkillRequirement

                if (minimumSkillRequirement < 10) {
                    Microbot.status = "Fighting Chickens"
//                    GerberThread.countLabel = "Chickens Killed"
                    fightNPC(LUMBRIDGE_CHICKENS_WORLD_AREA, WorldDestinations.LUMBRIDGE_CHICKENS.worldPoint, "chicken", listOf(ItemID.FEATHER, ItemID.BONES))

                } else if (minimumSkillRequirement < 20) {
                    Microbot.status = "Fighting Cows"
//                    GerberThread.countLabel = "Cows Killed"
                    fightNPC(LUMBRIDGE_COWS_WORLD_AREA, WorldDestinations.LUMBRIDGE_COWS.worldPoint, "cow", listOf(ItemID.COWHIDE, ItemID.BONES))

                } else {
                    shutdown()
                }

            } catch (ex: Exception) {
                println(ex.message)
            }
        }, 0, 600, TimeUnit.MILLISECONDS)
        return true
    }

    override fun getBankLocation(): WorldPoint {
        return WorldDestinations.LUMBRIDGE_BANK.worldPoint
    }

    override fun getInventoryRequirements(): InventoryRequirements {
        val inventoryRequirements = InventoryRequirements()
        val attackLevel = Microbot.getClientForKotlin().getRealSkillLevel(Skill.ATTACK)

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

//        val defenceLevel = player.getSkillLevel(Skill.DEFENCE)
//        if (defenceLevel >= 1) {
//            requiredItems.add(Pair(ItemID.BRONZE_PICKAXE, 1))
//            requiredItems.add(Pair(ItemID.IRON_PICKAXE, 1))
//        }
//        if (defenceLevel >= 5) {
//            requiredItems.add(Pair(ItemID.STEEL_PICKAXE, 1))
//        }
//        if (defenceLevel >= 10) {
//            requiredItems.add(Pair(ItemID.BLACK_PICKAXE, 1))
//        }
//        if (defenceLevel >= 20) {
//            requiredItems.add(Pair(ItemID.MITHRIL_PICKAXE, 1))
//        }
//        if (defenceLevel >= 30) {
//            requiredItems.add(Pair(ItemID.ADAMANT_PICKAXE, 1))
//        }
//        if (defenceLevel >= 40) {
//            requiredItems.add(Pair(ItemID.RUNE_PICKAXE, 1))
//        }
        return inventoryRequirements
    }

    override fun shouldTrain(): Boolean {
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
                if (!isWieldingRequiredWeapon) {
                    val weapon = getRequiredWeaponFromInventory
                    if (weapon != null) {
                        Rs2Equipment.equipItem(weapon.itemId)
//                        val inventoryCount = Inventory.count()
//                        weapon.leftClick()
//                        Alfred.sleepUntil({ Alfred.api.inventory.count() == inventoryCount - 1 }, 100, 3000)
                    }
                } else {
                    state = State.WAITING
                }
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
//                    GerberThread.count++
                    state = State.LOOTING
                }
            }

            State.LOOTING -> {
                if (config.collectItems()) {
                    if (Inventory.isFull()) {
//                        buryBones()
                        Global.sleep(200)
                    }

                    if (Inventory.isFull()) {
                        Microbot.getWalkerForKotlin().hybridWalkTo(getBankLocation())
                        Rs2Bank.openBank()
                        if (Rs2Bank.isOpen()) {
                            Rs2Bank.depositAll()
                            Rs2Bank.closeBank()
                        }
//                        Alfred.tasks.banking.depositInventory()
                        Global.sleep(200)
                    }

                    ItemHelper.findAndLootItems(itemLootNames, 2)
                }
                state = State.WAITING
            }
        }
    }

    private fun setCombatStyle() {
        val attackLevel = Microbot.getClientForKotlin().getRealSkillLevel(Skill.ATTACK)
        val strengthLevel = Microbot.getClientForKotlin().getRealSkillLevel(Skill.STRENGTH)
        val defenceLevel = Microbot.getClientForKotlin().getRealSkillLevel(Skill.DEFENCE)


        if (attackLevel < config.attackLevel()) {
            if (!Rs2Combat.isAccurateCombatStyleSelected()) {
                Rs2Combat.toggleAccurateCombatStyle()
            }

        } else if (strengthLevel < config.strengthLevel()) {
            if (!Rs2Combat.isAggressiveCombatStyleSelected()) {
                Rs2Combat.toggleAggressiveCombatStyle()
            }

        } else if (defenceLevel < config.defenseLevel()) {
            if (!Rs2Combat.isDefensiveCombatStyleSelected()) {
                Rs2Combat.toggleDefensiveCombatStyle()
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
}
