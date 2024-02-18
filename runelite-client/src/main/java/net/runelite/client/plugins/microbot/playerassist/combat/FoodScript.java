package net.runelite.client.plugins.microbot.playerassist.combat;

import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.ItemComposition;
import net.runelite.api.Skill;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.playerassist.PlayerAssistConfig;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;

import java.util.concurrent.TimeUnit;

import static net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment.getEquippedItem;

public class FoodScript extends Script {

    String weaponname = "";
    String bodyName = "";
    String legsName = "";
    String helmName = "";

    String shieldName = "";

    public boolean run(PlayerAssistConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!super.run()) return;
                if (!config.toggleFood()) return;
                if (Inventory.hasItem("empty vial"))
                    Inventory.drop("empty vial");

                if ((Microbot.getClient().getRealSkillLevel(Skill.HITPOINTS) - Microbot.getClient().getBoostedSkillLevel(Skill.HITPOINTS)) < 20) {
                    unEquipGuthans();
                    return;
                }
                Widget[] foods = Microbot.getClientThread().runOnClientThread(Inventory::getInventoryFood);
                if (foods == null || foods.length == 0) {
                    if (!equipFullGuthans()) {
                        Microbot.getNotifier().notify("No more food left & no guthans available. Please teleport");
                    }
                    return;
                }
                for (Widget food : foods) {
                    Microbot.getMouse().click(food.getBounds());
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
        if (Rs2Equipment.hasGuthanWeaponEquiped()) {
            Rs2Equipment.equipItemFast(weaponname);
            if (shieldName != null)
                Rs2Equipment.equipItemFast(shieldName);
        }
        if (Rs2Equipment.hasGuthanBodyEquiped()) {
            Rs2Equipment.equipItemFast(bodyName);
        }
        if (Rs2Equipment.hasGuthanLegsEquiped()) {
            Rs2Equipment.equipItemFast(legsName);
        }
        if (Rs2Equipment.hasGuthanHelmEquiped()) {
            Rs2Equipment.equipItemFast(helmName);
        }
    }

    private boolean equipFullGuthans() {
        ItemComposition shield = getEquippedItem(EquipmentInventorySlot.SHIELD);
        if (shield != null)
            shieldName = shield.getName();
        Inventory.open();

        if (!Rs2Equipment.hasGuthanWeaponEquiped()) {
            Widget spearWidget = Microbot.getClientThread().runOnClientThread(() -> Inventory.getInventoryItem("guthan's warspear"));
            if (spearWidget == null) return false;
            ItemComposition weapon = Rs2Equipment.getEquippedItem(EquipmentInventorySlot.WEAPON);
            weaponname = weapon != null ? weapon.getName() : "";
            Rs2Equipment.equipItemFast(spearWidget.getName().split(">")[1].split("</")[0]);
        }
        if (!Rs2Equipment.hasGuthanBodyEquiped()) {
            Widget bodyWidget = Microbot.getClientThread().runOnClientThread(() -> Inventory.getInventoryItem("guthan's platebody"));
            if (bodyWidget == null) return false;
            ItemComposition body = Rs2Equipment.getEquippedItem(EquipmentInventorySlot.BODY);
            bodyName = body != null ? body.getName() : "";
            Rs2Equipment.equipItemFast(bodyWidget.getName().split(">")[1].split("</")[0]);
        }
        if (!Rs2Equipment.hasGuthanLegsEquiped()) {
            Widget legsWidget = Microbot.getClientThread().runOnClientThread(() -> Inventory.getInventoryItem("guthan's chainskirt"));
            if (legsWidget == null) return false;
            ItemComposition legs = Rs2Equipment.getEquippedItem(EquipmentInventorySlot.LEGS);
            legsName = legs != null ? legs.getName() : "";
            Rs2Equipment.equipItemFast(legsWidget.getName().split(">")[1].split("</")[0]);
        }
        if (!Rs2Equipment.hasGuthanHelmEquiped()) {
            Widget helmWidget = Microbot.getClientThread().runOnClientThread(() -> Inventory.getInventoryItem("guthan's helm"));
            if (helmWidget == null) return false;
            ItemComposition helm = Rs2Equipment.getEquippedItem(EquipmentInventorySlot.HEAD);
            helmName = helm != null ? helm.getName() : "";
            Rs2Equipment.equipItemFast(helmWidget.getName().split(">")[1].split("</")[0]);
        }
        return true;
    }
}
