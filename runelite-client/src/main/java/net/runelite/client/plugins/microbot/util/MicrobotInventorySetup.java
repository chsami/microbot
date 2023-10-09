package net.runelite.client.plugins.microbot.util;

import net.runelite.client.plugins.inventorysetups.InventorySetup;
import net.runelite.client.plugins.inventorysetups.InventorySetupsItem;
import net.runelite.client.plugins.inventorysetups.InventorySetupsPlugin;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;

import java.util.Objects;

import static net.runelite.client.plugins.microbot.util.Global.sleep;

public class MicrobotInventorySetup {
    public static void loadInventory(String name) {
        Rs2Bank.openBank();
        if (Rs2Bank.isOpen()) {
            InventorySetup inventorySetup = InventorySetupsPlugin.getInventorySetups().stream().filter(Objects::nonNull).filter(x -> x.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
            if (inventorySetup == null) return;
            for (int i = 0; i < inventorySetup.getInventory().size(); i++) {
                InventorySetupsItem inventorySetupsItem = inventorySetup.getInventory().get(i);
                if (Inventory.hasItemAmount(inventorySetupsItem.getId(), (int) inventorySetup.getInventory().stream().filter(x -> x.getId() == inventorySetupsItem.getId()).count()))
                    continue;
                Rs2Bank.withdrawX(inventorySetupsItem.getId(), inventorySetupsItem.getQuantity());
                sleep(300, 600);
            }
        }
    }

    public static void loadEquipment(String name) {
        Rs2Bank.openBank();
        if (Rs2Bank.isOpen()) {
            InventorySetup inventorySetup = InventorySetupsPlugin.getInventorySetups().stream().filter(Objects::nonNull).filter(x -> x.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
            if (inventorySetup == null) return;
            for (InventorySetupsItem inventorySetupsItem : inventorySetup.getEquipment()) {
                if (inventorySetupsItem.getId() == -1) continue;
                if (Inventory.hasItem(inventorySetupsItem.getId())) {
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
    }
}
