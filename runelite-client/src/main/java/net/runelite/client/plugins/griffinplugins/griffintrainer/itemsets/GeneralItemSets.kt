package net.runelite.client.plugins.griffinplugins.griffintrainer.itemsets

import net.runelite.api.ItemID
import net.runelite.api.Skill
import net.runelite.client.plugins.griffinplugins.griffintrainer.models.DynamicItemSet
import net.runelite.client.plugins.microbot.Microbot

class GeneralItemSets {

    companion object {
        fun getWeaponItemSet(): DynamicItemSet {
            val attackLevel = Microbot.getClientForKotlin().getRealSkillLevel(Skill.ATTACK)
            val weapons = DynamicItemSet()

            if (attackLevel >= 1) {
                weapons.add(ItemID.BRONZE_2H_SWORD, 1, true)
                weapons.add(ItemID.BRONZE_DAGGER, 1, true)
                weapons.add(ItemID.BRONZE_SWORD, 1, true)
                weapons.add(ItemID.BRONZE_LONGSWORD, 1, true)
                weapons.add(ItemID.BRONZE_SCIMITAR, 1, true)
                weapons.add(ItemID.IRON_2H_SWORD, 1, true)
                weapons.add(ItemID.IRON_DAGGER, 1, true)
                weapons.add(ItemID.IRON_SWORD, 1, true)
                weapons.add(ItemID.IRON_LONGSWORD, 1, true)
                weapons.add(ItemID.IRON_SCIMITAR, 1, true)
            }
            if (attackLevel >= 5) {
                weapons.add(ItemID.STEEL_2H_SWORD, 1, true)
                weapons.add(ItemID.STEEL_DAGGER, 1, true)
                weapons.add(ItemID.STEEL_SWORD, 1, true)
                weapons.add(ItemID.STEEL_LONGSWORD, 1, true)
                weapons.add(ItemID.STEEL_SCIMITAR, 1, true)
            }
            if (attackLevel >= 10) {
                weapons.add(ItemID.BLACK_2H_SWORD, 1, true)
                weapons.add(ItemID.BLACK_DAGGER, 1, true)
                weapons.add(ItemID.BLACK_SWORD, 1, true)
                weapons.add(ItemID.BLACK_LONGSWORD, 1, true)
                weapons.add(ItemID.BLACK_SCIMITAR, 1, true)
            }
            if (attackLevel >= 20) {
                weapons.add(ItemID.MITHRIL_2H_SWORD, 1, true)
                weapons.add(ItemID.MITHRIL_DAGGER, 1, true)
                weapons.add(ItemID.MITHRIL_SWORD, 1, true)
                weapons.add(ItemID.MITHRIL_LONGSWORD, 1, true)
                weapons.add(ItemID.MITHRIL_SCIMITAR, 1, true)
            }
            if (attackLevel >= 30) {
                weapons.add(ItemID.ADAMANT_2H_SWORD, 1, true)
                weapons.add(ItemID.ADAMANT_DAGGER, 1, true)
                weapons.add(ItemID.ADAMANT_SWORD, 1, true)
                weapons.add(ItemID.ADAMANT_LONGSWORD, 1, true)
                weapons.add(ItemID.ADAMANT_SCIMITAR, 1, true)
            }
            if (attackLevel >= 40) {
                weapons.add(ItemID.RUNE_2H_SWORD, 1, true)
                weapons.add(ItemID.RUNE_DAGGER, 1, true)
                weapons.add(ItemID.RUNE_SWORD, 1, true)
                weapons.add(ItemID.RUNE_LONGSWORD, 1, true)
                weapons.add(ItemID.RUNE_SCIMITAR, 1, true)
            }

            return weapons
        }

        fun getHelmetItemSet(): DynamicItemSet {
            val defenceLevel = Microbot.getClientForKotlin().getRealSkillLevel(Skill.DEFENCE)
            val helmets = DynamicItemSet()

            if (defenceLevel >= 1) {
                helmets.add(ItemID.BRONZE_MED_HELM, 1, true)
                helmets.add(ItemID.BRONZE_FULL_HELM, 1, true)
                helmets.add(ItemID.IRON_MED_HELM, 1, true)
                helmets.add(ItemID.IRON_FULL_HELM, 1, true)
            }
            if (defenceLevel >= 5) {
                helmets.add(ItemID.STEEL_MED_HELM, 1, true)
                helmets.add(ItemID.STEEL_FULL_HELM, 1, true)
            }
            if (defenceLevel >= 10) {
                helmets.add(ItemID.BLACK_MED_HELM, 1, true)
                helmets.add(ItemID.BLACK_FULL_HELM, 1, true)
            }
            if (defenceLevel >= 20) {
                helmets.add(ItemID.MITHRIL_MED_HELM, 1, true)
                helmets.add(ItemID.MITHRIL_FULL_HELM, 1, true)
            }
            if (defenceLevel >= 30) {
                helmets.add(ItemID.ADAMANT_MED_HELM, 1, true)
                helmets.add(ItemID.ADAMANT_FULL_HELM, 1, true)
            }
            if (defenceLevel >= 40) {
                helmets.add(ItemID.RUNE_MED_HELM, 1, true)
                helmets.add(ItemID.RUNE_FULL_HELM, 1, true)
            }

            return helmets
        }

        fun getBodiesItemSet(): DynamicItemSet {
            val defenceLevel = Microbot.getClientForKotlin().getRealSkillLevel(Skill.DEFENCE)
            val bodies = DynamicItemSet()

            if (defenceLevel >= 1) {
                bodies.add(ItemID.BRONZE_CHAINBODY, 1, true)
                bodies.add(ItemID.BRONZE_PLATEBODY, 1, true)
                bodies.add(ItemID.IRON_CHAINBODY, 1, true)
                bodies.add(ItemID.IRON_PLATEBODY, 1, true)
            }
            if (defenceLevel >= 5) {
                bodies.add(ItemID.STEEL_CHAINBODY, 1, true)
                bodies.add(ItemID.STEEL_PLATEBODY, 1, true)
            }
            if (defenceLevel >= 10) {
                bodies.add(ItemID.BLACK_CHAINBODY, 1, true)
                bodies.add(ItemID.BLACK_PLATEBODY, 1, true)
            }
            if (defenceLevel >= 20) {
                bodies.add(ItemID.MITHRIL_CHAINBODY, 1, true)
                bodies.add(ItemID.MITHRIL_PLATEBODY, 1, true)
            }
            if (defenceLevel >= 30) {
                bodies.add(ItemID.ADAMANT_CHAINBODY, 1, true)
                bodies.add(ItemID.ADAMANT_PLATEBODY, 1, true)
            }
            if (defenceLevel >= 40) {
                bodies.add(ItemID.RUNE_CHAINBODY, 1, true)
                bodies.add(ItemID.RUNE_PLATEBODY, 1, true)
            }

            return bodies
        }

        fun getLegsItemSet(): DynamicItemSet {
            val defenceLevel = Microbot.getClientForKotlin().getRealSkillLevel(Skill.DEFENCE)
            val legs = DynamicItemSet()

            if (defenceLevel >= 1) {
                legs.add(ItemID.BRONZE_PLATESKIRT, 1, true)
                legs.add(ItemID.BRONZE_PLATELEGS, 1, true)
                legs.add(ItemID.IRON_PLATESKIRT, 1, true)
                legs.add(ItemID.IRON_PLATELEGS, 1, true)
            }
            if (defenceLevel >= 5) {
                legs.add(ItemID.STEEL_PLATESKIRT, 1, true)
                legs.add(ItemID.STEEL_PLATELEGS, 1, true)
            }
            if (defenceLevel >= 10) {
                legs.add(ItemID.BLACK_PLATESKIRT, 1, true)
                legs.add(ItemID.BLACK_PLATELEGS, 1, true)
            }
            if (defenceLevel >= 20) {
                legs.add(ItemID.MITHRIL_PLATESKIRT, 1, true)
                legs.add(ItemID.MITHRIL_PLATELEGS, 1, true)
            }
            if (defenceLevel >= 30) {
                legs.add(ItemID.ADAMANT_PLATESKIRT, 1, true)
                legs.add(ItemID.ADAMANT_PLATELEGS, 1, true)
            }
            if (defenceLevel >= 40) {
                legs.add(ItemID.RUNE_PLATESKIRT, 1, true)
                legs.add(ItemID.RUNE_PLATELEGS, 1, true)
            }

            return legs
        }

        fun getBootsItemSet(): DynamicItemSet {
            val defenceLevel = Microbot.getClientForKotlin().getRealSkillLevel(Skill.DEFENCE)
            val boots = DynamicItemSet()

            if (defenceLevel >= 1) {
                boots.add(ItemID.BRONZE_BOOTS, 1, true)
                boots.add(ItemID.IRON_BOOTS, 1, true)
            }
            if (defenceLevel >= 5) {
                boots.add(ItemID.STEEL_BOOTS, 1, true)
            }
            if (defenceLevel >= 10) {
                boots.add(ItemID.BLACK_BOOTS, 1, true)
            }
            if (defenceLevel >= 20) {
                boots.add(ItemID.MITHRIL_BOOTS, 1, true)
            }
            if (defenceLevel >= 30) {
                boots.add(ItemID.ADAMANT_BOOTS, 1, true)
            }
            if (defenceLevel >= 40) {
                boots.add(ItemID.RUNE_BOOTS, 1, true)
            }

            return boots
        }

        fun getShieldsItemSet(): DynamicItemSet {
            val defenceLevel = Microbot.getClientForKotlin().getRealSkillLevel(Skill.DEFENCE)
            val shields = DynamicItemSet()

            if (defenceLevel >= 1) {
                shields.add(ItemID.WOODEN_SHIELD, 1, true)
                shields.add(ItemID.BRONZE_SQ_SHIELD, 1, true)
                shields.add(ItemID.BRONZE_KITESHIELD, 1, true)
                shields.add(ItemID.IRON_SQ_SHIELD, 1, true)
                shields.add(ItemID.IRON_KITESHIELD, 1, true)
            }
            if (defenceLevel >= 5) {
                shields.add(ItemID.STEEL_SQ_SHIELD, 1, true)
                shields.add(ItemID.STEEL_KITESHIELD, 1, true)
            }
            if (defenceLevel >= 10) {
                shields.add(ItemID.BLACK_SQ_SHIELD, 1, true)
                shields.add(ItemID.BLACK_KITESHIELD, 1, true)
            }
            if (defenceLevel >= 20) {
                shields.add(ItemID.MITHRIL_SQ_SHIELD, 1, true)
                shields.add(ItemID.MITHRIL_KITESHIELD, 1, true)
            }
            if (defenceLevel >= 30) {
                shields.add(ItemID.ADAMANT_SQ_SHIELD, 1, true)
                shields.add(ItemID.ADAMANT_KITESHIELD, 1, true)
            }
            if (defenceLevel >= 40) {
                shields.add(ItemID.RUNE_SQ_SHIELD, 1, true)
                shields.add(ItemID.RUNE_KITESHIELD, 1, true)
            }

            return shields
        }

        fun getPickaxesItemSet(): DynamicItemSet {
            val miningLevel = Microbot.getClientForKotlin().getRealSkillLevel(Skill.ATTACK)
            val pickaxes = DynamicItemSet()

            if (miningLevel >= 1) {
                pickaxes.add(ItemID.BRONZE_PICKAXE, 1, true)
                pickaxes.add(ItemID.IRON_PICKAXE, 1, true)
            }
            if (miningLevel >= 6) {
                pickaxes.add(ItemID.STEEL_PICKAXE, 1, true)
            }
            if (miningLevel >= 11) {
                pickaxes.add(ItemID.BLACK_PICKAXE, 1, true)
            }
            if (miningLevel >= 21) {
                pickaxes.add(ItemID.MITHRIL_PICKAXE, 1, true)
            }
            if (miningLevel >= 31) {
                pickaxes.add(ItemID.ADAMANT_PICKAXE, 1, true)
            }
            if (miningLevel >= 41) {
                pickaxes.add(ItemID.RUNE_PICKAXE, 1, true)
            }

            return pickaxes
        }

        fun getAxesItemSet(): DynamicItemSet {
            val miningLevel = Microbot.getClientForKotlin().getRealSkillLevel(Skill.ATTACK)
            val pickaxes = DynamicItemSet()

            if (miningLevel >= 1) {
                pickaxes.add(ItemID.BRONZE_AXE, 1, true)
                pickaxes.add(ItemID.IRON_AXE, 1, true)
            }
            if (miningLevel >= 6) {
                pickaxes.add(ItemID.STEEL_AXE, 1, true)
            }
            if (miningLevel >= 11) {
                pickaxes.add(ItemID.BLACK_AXE, 1, true)
            }
            if (miningLevel >= 21) {
                pickaxes.add(ItemID.MITHRIL_AXE, 1, true)
            }
            if (miningLevel >= 31) {
                pickaxes.add(ItemID.ADAMANT_AXE, 1, true)
            }
            if (miningLevel >= 41) {
                pickaxes.add(ItemID.RUNE_AXE, 1, true)
            }

            return pickaxes
        }
    }
}