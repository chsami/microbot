package net.runelite.client.plugins.microbot.playerassist.combat;

import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.ItemComposition;
import net.runelite.api.Skill;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.playerassist.PlayerAssistConfig;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;

import java.util.concurrent.TimeUnit;

public class FoodScript extends Script {

    int weaponIndex = 0;
    int bodyIndex = 0;
    int legsIndex = 0;
    int helmIndex = 0;

    public boolean run(PlayerAssistConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!super.run()) return;
                if (!config.toggleFood()) return;
                if (Microbot.getClient().getBoostedSkillLevel(Skill.HITPOINTS) > 60) {
                    unEquipGuthans();
                    return;
                }
                Widget[] foods = Microbot.getClientThread().runOnClientThread(() -> Inventory.getInventoryFood());
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
        if (weaponIndex >= 0) {
            Inventory.clickItem(weaponIndex);
            Inventory.useItem("Dragon defender");
            weaponIndex = -1;
        }
        if (bodyIndex >= 0) {
            Inventory.clickItem(bodyIndex);
            bodyIndex = -1;;
        }
        if (legsIndex >= 0) {
            Inventory.clickItem(legsIndex);
            legsIndex = -1;
        }
        if (helmIndex >= 0) {
            Inventory.clickItem(helmIndex);
            helmIndex = -1;
        }
    }

    private boolean equipFullGuthans() {
        final ItemComposition weapon = getEquippedItem(EquipmentInventorySlot.WEAPON);
        final ItemComposition body = getEquippedItem(EquipmentInventorySlot.BODY);
        final ItemComposition legs = getEquippedItem(EquipmentInventorySlot.LEGS);
        final ItemComposition helm = getEquippedItem(EquipmentInventorySlot.HEAD);

        boolean hasGuthanSpear = weapon.getName().toLowerCase().contains("guthan's warspear");
        boolean hasGuthanBody = body.getName().toLowerCase().contains("guthan's platebody");
        boolean hasGuthanLegs = legs.getName().toLowerCase().contains("guthan's chainskirt");
        boolean hasGuthanHelm = helm.getName().toLowerCase().contains("guthan's helm");

        Inventory.open();

        if (!hasGuthanSpear) {
            Widget spearWidget = Microbot.getClientThread().runOnClientThread(() -> Inventory.getInventoryItem("guthan's warspear"));
            if (spearWidget == null) return false;
            weaponIndex = spearWidget.getIndex();
            Microbot.getMouse().click(spearWidget.getBounds());
            sleep(1000);
        }
        if (!hasGuthanBody) {
            Widget bodyWidget = Microbot.getClientThread().runOnClientThread(() -> Inventory.getInventoryItem("guthan's platebody"));
            if (bodyWidget == null) return false;
            bodyIndex = bodyWidget.getIndex();
            Microbot.getMouse().click(bodyWidget.getBounds());
            sleep(1000);
        }
        if (!hasGuthanLegs) {
            Widget legsWidget = Microbot.getClientThread().runOnClientThread(() -> Inventory.getInventoryItem("guthan's chainskirt"));
            if (legsWidget == null) return false;
            legsIndex = legsWidget.getIndex();
            Microbot.getMouse().click(legsWidget.getBounds());
            sleep(1000);
        }
        if (!hasGuthanHelm) {
            Widget helmWidget = Microbot.getClientThread().runOnClientThread(() -> Inventory.getInventoryItem("guthan's helm"));
            if (helmWidget == null) return false;
            helmIndex = helmWidget.getIndex();
            Microbot.getMouse().click(helmWidget.getBounds());
            sleep(1000);
        }
        return true;
    }
}
