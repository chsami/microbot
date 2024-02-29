package net.runelite.client.plugins.microbot.util;

import net.runelite.client.plugins.inventorysetups.InventorySetup;
import net.runelite.client.plugins.inventorysetups.InventorySetupsItem;
import net.runelite.client.plugins.inventorysetups.InventorySetupsPlugin;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;

import java.util.Objects;

public class MicrobotInventorySetup {
    public static boolean loadInventory(String name) {
        Rs2Bank.openBank();
        if (Rs2Bank.isOpen()) {
            InventorySetup inventorySetup = InventorySetupsPlugin.getInventorySetups().stream().filter(Objects::nonNull).filter(x -> x.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
            if (inventorySetup == null) return false;
            for (int i = 0; i < inventorySetup.getInventory().size(); i++) {
                InventorySetupsItem inventorySetupsItem = inventorySetup.getInventory().get(i);
                if (Rs2Inventory.hasItemAmount(inventorySetupsItem.getId(), (int) inventorySetup.getInventory().stream().filter(x -> x.getId() == inventorySetupsItem.getId()).count()))
                    continue;
                Rs2Bank.withdrawX(inventorySetupsItem.getId(), inventorySetupsItem.getQuantity());
            }
        }
        return doesInventoryMatch(name);
    }

    public static boolean loadEquipment(String name) {
        Rs2Bank.openBank();
        if (Rs2Bank.isOpen()) {
            InventorySetup inventorySetup = InventorySetupsPlugin.getInventorySetups().stream().filter(Objects::nonNull).filter(x -> x.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
            if (inventorySetup == null) return false;
            for (InventorySetupsItem inventorySetupsItem : inventorySetup.getEquipment()) {
                if (inventorySetupsItem.getId() == -1 || !Rs2Bank.hasItem(inventorySetupsItem.getId())) continue;
                if (Rs2Inventory.hasItem(inventorySetupsItem.getId())) {
                    Rs2Bank.wearItem(inventorySetupsItem.getId());
                    continue;
                }
                if (inventorySetupsItem.getQuantity() > 1) {
                    Rs2Bank.withdrawAllAndEquip(inventorySetupsItem.getId());
                } else {
                    Rs2Bank.withdrawAndEquip(inventorySetupsItem.getId());
                }
            }
        }
        return doesEquipmentMatch(name);
    }

    private static boolean doesInventoryMatch(String name) {
        InventorySetup inventorySetup = InventorySetupsPlugin.getInventorySetups().stream().filter(Objects::nonNull).filter(x -> x.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        if (inventorySetup == null) return false;
        for (InventorySetupsItem inventorySetupsItem : inventorySetup.getEquipment()) {
            if (!Rs2Inventory.hasItem(inventorySetupsItem.getName(), true))
                return false;
        }
        return true;
    }

    private static boolean doesEquipmentMatch(String name) {
        InventorySetup inventorySetup = InventorySetupsPlugin.getInventorySetups().stream().filter(Objects::nonNull).filter(x -> x.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        if (inventorySetup == null) return false;
        for (InventorySetupsItem inventorySetupsItem : inventorySetup.getEquipment()) {
            if (!Rs2Equipment.isWearing(inventorySetupsItem.getName(), true)) {
                Microbot.showMessage("Missing item " + inventorySetupsItem.getName());
                return false;
            }
        }
        return true;
    }
}
