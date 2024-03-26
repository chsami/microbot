package net.runelite.client.plugins.microbot.util;

import net.runelite.client.plugins.inventorysetups.InventorySetup;
import net.runelite.client.plugins.inventorysetups.InventorySetupsItem;
import net.runelite.client.plugins.inventorysetups.InventorySetupsPlugin;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;

import java.util.Objects;

import static net.runelite.client.plugins.microbot.util.Global.sleep;

public class MicrobotInventorySetup {
    static InventorySetup inventorySetup;

    private static boolean getInventorySetup(String name) {
        inventorySetup = InventorySetupsPlugin.getInventorySetups().stream().filter(Objects::nonNull).filter(x -> x.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        if (inventorySetup == null) {
            Microbot.showMessage("Inventory load with name " + name + " not found!");
            Microbot.pauseAllScripts = true;
            return false;
        }
        return true;
    }

    public static boolean loadInventory(String name) {
        Rs2Bank.openBank();
        Rs2Bank.depositAll();
        if (Rs2Bank.isOpen()) {
            if (!getInventorySetup(name)) {
                return false;
            }
            for (int i = 0; i < inventorySetup.getInventory().size(); i++) {
                InventorySetupsItem inventorySetupsItem = inventorySetup.getInventory().get(i);
                if (inventorySetupsItem.getId() == -1) continue;
                if (!Rs2Bank.hasBankItem(inventorySetupsItem.getName(), inventorySetupsItem.getQuantity())) {
                    Microbot.pauseAllScripts = true;
                    Microbot.showMessage("Bank is missing the following item " + inventorySetupsItem.getName());
                    break;
                }
                if (inventorySetupsItem.isFuzzy()) {
                    Rs2Bank.withdrawX(inventorySetupsItem.getName(), inventorySetupsItem.getQuantity());
                } else {
                    if (inventorySetupsItem.getQuantity() > 1) {
                        Rs2Bank.withdrawX(inventorySetupsItem.getId(), inventorySetupsItem.getQuantity());
                        sleep(100, 250);
                    } else {
                        Rs2Bank.withdrawItem(inventorySetupsItem.getId());
                        sleep(100, 250);
                    }
                }
            }
        }
        sleep(1000);

        return doesInventoryMatch(name);
    }

    public static boolean loadEquipment(String name) {
        Rs2Bank.openBank();
        if (Rs2Bank.isOpen()) {
            if (!getInventorySetup(name)) {
                return false;
            }
            if (inventorySetup == null) return false;
            for (InventorySetupsItem inventorySetupsItem : inventorySetup.getEquipment()) {
                if (InventorySetupsItem.itemIsDummy(inventorySetupsItem)) continue;
                if (inventorySetupsItem.isFuzzy()) {
                    if (!Rs2Bank.hasBankItem(inventorySetupsItem.getName())) {
                        Microbot.pauseAllScripts = true;
                        Microbot.showMessage("Bank is missing the following item " + inventorySetupsItem.getName());
                        break;
                    }
                    if (Rs2Inventory.hasItemAmount(inventorySetupsItem.getName(), (int) inventorySetup.getInventory().stream().filter(x -> x.getId() == inventorySetupsItem.getId()).count()))
                        continue;
                    if (inventorySetupsItem.getQuantity() > 1) {
                        Rs2Bank.withdrawAllAndEquip(inventorySetupsItem.getName());
                        sleep(100, 250);
                    } else {
                        Rs2Bank.withdrawAndEquip(inventorySetupsItem.getName());
                        sleep(100, 250);
                    }
                } else {
                    if (inventorySetupsItem.getId() == -1 || !Rs2Bank.hasItem(inventorySetupsItem.getName()))
                        continue;
                    if (Rs2Inventory.hasItem(inventorySetupsItem.getName())) {
                        Rs2Bank.wearItem(inventorySetupsItem.getName());
                        continue;
                    }
                    if (inventorySetupsItem.getQuantity() > 1) {
                        Rs2Bank.withdrawAllAndEquip(inventorySetupsItem.getName());
                        sleep(100, 250);
                    } else {
                        Rs2Bank.withdrawAndEquip(inventorySetupsItem.getName());
                        sleep(100, 250);
                    }
                }
            }
        }
        sleep(1000);

        return doesEquipmentMatch(name);
    }

    public static boolean wearEquipment(String name) {
        if (!getInventorySetup(name)) {
            return false;
        }
        for (InventorySetupsItem inventorySetupsItem : inventorySetup.getEquipment()) {
            Rs2Inventory.wield(inventorySetupsItem.getId());
        }
        return doesEquipmentMatch(name);
    }

    public static boolean doesInventoryMatch(String name) {
        inventorySetup = InventorySetupsPlugin.getInventorySetups().stream().filter(Objects::nonNull).filter(x -> x.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        if (inventorySetup == null) return false;

        for (int i = 0; i < inventorySetup.getInventory().size(); i++) {
            InventorySetupsItem inventorySetupsItem = inventorySetup.getInventory().get(i);
            Rs2Item rsItem = Rs2Inventory.getItemInSlot(i);
            if (rsItem == null) return false;
            if (inventorySetupsItem.isFuzzy()) {
                if (!inventorySetupsItem.getName().contains(name))
                    return false;
            } else {
                if (rsItem.id != inventorySetupsItem.getId()) {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean doesEquipmentMatch(String name) {
        inventorySetup = InventorySetupsPlugin.getInventorySetups().stream().filter(Objects::nonNull).filter(x -> x.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        if (inventorySetup == null) return false;
        for (InventorySetupsItem inventorySetupsItem : inventorySetup.getEquipment()) {
            if (inventorySetupsItem.getId() == -1) continue;
            if (!Rs2Equipment.isWearing(inventorySetupsItem.getName(), true)) {
                return false;
            }
        }
        return true;
    }
}
