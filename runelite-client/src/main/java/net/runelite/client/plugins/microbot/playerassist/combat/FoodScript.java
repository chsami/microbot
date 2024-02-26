package net.runelite.client.plugins.microbot.playerassist.combat;

import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.ItemComposition;
import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.playerassist.PlayerAssistConfig;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment.getEquippedItem;

public class FoodScript extends Script {

    String weaponname = "";
    String bodyName = "";
    String legsName = "";
    String helmName = "";

    String shieldName = "";

    public boolean run(PlayerAssistConfig config) {
        weaponname = "";
        bodyName = "";
        legsName = "";
        helmName = "";
        shieldName = "";
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!super.run()) return;
                if (!config.toggleFood()) return;
                if (Rs2Inventory.hasItem("empty vial"))
                    Rs2Inventory.drop("empty vial");
                double treshHold = (double) (Microbot.getClient().getRealSkillLevel(Skill.HITPOINTS) * Microbot.getClient().getBoostedSkillLevel(Skill.HITPOINTS)) / 100;
                if (Rs2Equipment.isWearingFullGuthan()) {
                    if (treshHold > 80) //only unequip guthans if we have more than 80% hp
                        unEquipGuthans();
                    return;
                } else {
                    if (treshHold > 51) //return as long as we have more than 51% health and not guthan equipped
                        return;
                }
                List<Rs2Item> foods = Microbot.getClientThread().runOnClientThread(Rs2Inventory::getInventoryFood);
                if (foods == null || foods.isEmpty()) {
                    if (!equipFullGuthans()) {
                        Microbot.getNotifier().notify("No more food left & no guthans available. Please teleport");
                    }
                    return;
                }
                for (Rs2Item food : foods) {
                    Rs2Inventory.interact(food, "eat");
                    sleep(1200, 2000);
                    break;
                }
            } catch(Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 600, TimeUnit.MILLISECONDS);
        return true;
    }

    private void unEquipGuthans() {
        if (Rs2Equipment.hasGuthanWeaponEquiped()  && !weaponname.isEmpty()) {
            Rs2Inventory.equip(weaponname);
            if (shieldName != null)
                Rs2Inventory.equip(shieldName);
        }
        if (Rs2Equipment.hasGuthanBodyEquiped() && !bodyName.isEmpty()) {
            Rs2Inventory.equip(bodyName);
        }
        if (Rs2Equipment.hasGuthanLegsEquiped() && !legsName.isEmpty()) {
            Rs2Inventory.equip(legsName);
        }
        if (Rs2Equipment.hasGuthanHelmEquiped() && !helmName.isEmpty()) {
            Rs2Inventory.equip(helmName);
        }
    }

    private boolean equipFullGuthans() {
        ItemComposition shield = getEquippedItem(EquipmentInventorySlot.SHIELD);
        if (shield != null)
            shieldName = shield.getName();

        if (!Rs2Equipment.hasGuthanWeaponEquiped()) {
            Rs2Item spearWidget = Microbot.getClientThread().runOnClientThread(() -> Rs2Inventory.get("guthan's warspear"));
            if (spearWidget == null) return false;
            ItemComposition weapon = Rs2Equipment.getEquippedItem(EquipmentInventorySlot.WEAPON);
            weaponname = weapon != null ? weapon.getName() : "";
            Rs2Inventory.equip(spearWidget.name);
        }
        if (!Rs2Equipment.hasGuthanBodyEquiped()) {
            Rs2Item bodyWidget = Microbot.getClientThread().runOnClientThread(() -> Rs2Inventory.get("guthan's platebody"));
            if (bodyWidget == null) return false;
            ItemComposition body = Rs2Equipment.getEquippedItem(EquipmentInventorySlot.BODY);
            bodyName = body != null ? body.getName() : "";
            Rs2Inventory.equip(bodyWidget.name);
        }
        if (!Rs2Equipment.hasGuthanLegsEquiped()) {
            Rs2Item legsWidget = Microbot.getClientThread().runOnClientThread(() -> Rs2Inventory.get("guthan's chainskirt"));
            if (legsWidget == null) return false;
            ItemComposition legs = Rs2Equipment.getEquippedItem(EquipmentInventorySlot.LEGS);
            legsName = legs != null ? legs.getName() : "";
            Rs2Inventory.equip(legsWidget.name);
        }
        if (!Rs2Equipment.hasGuthanHelmEquiped()) {
            Rs2Item helmWidget = Microbot.getClientThread().runOnClientThread(() -> Rs2Inventory.get("guthan's helm"));
            if (helmWidget == null) return false;
            ItemComposition helm = Rs2Equipment.getEquippedItem(EquipmentInventorySlot.HEAD);
            helmName = helm != null ? helm.getName() : "";
            Rs2Inventory.equip(helmWidget.name);
        }
        return true;
    }
}
