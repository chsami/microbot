package net.runelite.client.plugins.microbot.util;

import net.runelite.api.EquipmentInventorySlot;
import net.runelite.client.plugins.inventorysetups.InventorySetup;
import net.runelite.client.plugins.inventorysetups.InventorySetupsItem;
import net.runelite.client.plugins.inventorysetups.MInventorySetupsPlugin;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

import static net.runelite.client.plugins.microbot.util.Global.sleep;

public class MicrobotInventorySetup {
    static InventorySetup inventorySetup;

    private static boolean getInventorySetup(String name) {
        inventorySetup = MInventorySetupsPlugin.getInventorySetups().stream().filter(Objects::nonNull).filter(x -> x.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        if (inventorySetup == null) {
            Microbot.showMessage("Inventory load with name " + name + " not found!");
            Microbot.pauseAllScripts = true;
            return false;
        }
        return true;
    }

    public static boolean loadInventory(String name, ScheduledFuture<?> mainScheduler) {
        Rs2Bank.openBank();
        Rs2Bank.depositAll();
        if (Rs2Bank.isOpen()) {
            if (!getInventorySetup(name)) {
                return false;
            }
            Map<Integer, List<InventorySetupsItem>> groupedByItems = inventorySetup.getInventory().stream().collect(Collectors.groupingBy(InventorySetupsItem::getId));
            for (Integer key : groupedByItems.keySet()) {
                if (mainScheduler.isCancelled()) break;
                InventorySetupsItem inventorySetupsItem = groupedByItems.get(key).get(0);
                if (inventorySetupsItem.getId() == -1) continue;
                int withdrawQuantity;
                if (groupedByItems.get(key).size() == 1) {
                    withdrawQuantity = groupedByItems.get(key).get(0).getQuantity();
                } else {
                    withdrawQuantity = groupedByItems.get(key).size();
                }
                if (!Rs2Bank.hasBankItem(inventorySetupsItem.getName(), withdrawQuantity)) {
                    Microbot.pauseAllScripts = true;
                    Microbot.showMessage("Bank is missing the following item " + inventorySetupsItem.getName());
                    break;
                }
                if (inventorySetupsItem.isFuzzy()) {
                    Rs2Bank.withdrawX(inventorySetupsItem.getName(), withdrawQuantity);
                } else {
                    if (withdrawQuantity > 1) {
                        Rs2Bank.withdrawX(inventorySetupsItem.getId(), withdrawQuantity);
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

    public static boolean loadEquipment(String name, ScheduledFuture<?> mainScheduler) {
        Rs2Bank.openBank();
        if (Rs2Bank.isOpen()) {
            if (!getInventorySetup(name)) {
                return false;
            }
            if (inventorySetup == null) return false;
            for (InventorySetupsItem inventorySetupsItem : inventorySetup.getEquipment()) {
                if (mainScheduler.isCancelled()) break;
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
        inventorySetup = MInventorySetupsPlugin.getInventorySetups().stream().filter(Objects::nonNull).filter(x -> x.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        if (inventorySetup == null) return false;

        Map<Integer, List<InventorySetupsItem>> groupedByItems = inventorySetup.getInventory().stream().collect(Collectors.groupingBy(InventorySetupsItem::getId));
        boolean found = true;
        for (Integer key : groupedByItems.keySet()) {
            InventorySetupsItem inventorySetupsItem = groupedByItems.get(key).get(0);
            if (inventorySetupsItem.getId() == -1) continue;
            int withdrawQuantity = -1;
            boolean isStackable = false;
            if (groupedByItems.get(key).size() == 1) {
                withdrawQuantity = groupedByItems.get(key).get(0).getQuantity();
                isStackable = withdrawQuantity > 1;
            } else {
                withdrawQuantity = groupedByItems.get(key).size();
            }
            if (!Rs2Inventory.hasItemAmount(inventorySetupsItem.getName(), withdrawQuantity, isStackable))
                found = false;
        }

        return found;
    }

    public static boolean doesEquipmentMatch(String name) {
        inventorySetup = MInventorySetupsPlugin.getInventorySetups().stream().filter(Objects::nonNull).filter(x -> x.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        if (inventorySetup == null) {
            Microbot.showMessage("Inventory setup with name " + name + " has not found been found. Please make this inventory setup.");
            sleep(5000);
            return false;
        }
        for (InventorySetupsItem inventorySetupsItem : inventorySetup.getEquipment()) {
            if (inventorySetupsItem.getId() == -1) continue;
            if (!Rs2Equipment.isWearing(inventorySetupsItem.getName(), true)) {
                return false;
            }
        }
        return true;
    }

    public static boolean doesEquipmentMatch(String name, List<EquipmentInventorySlot> slots) {
        inventorySetup = MInventorySetupsPlugin.getInventorySetups().stream().filter(Objects::nonNull).filter(x -> x.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        if (inventorySetup == null) {
            Microbot.showMessage("Inventory setup with name " + name + " has not found been found. Please make this inventory setup.");
            sleep(5000);
            return false;
        }
        if (!Rs2Equipment.isWearing(inventorySetup.getEquipment().stream().filter(x -> x.getId() != -1).map(x -> x.getName()).collect(Collectors.toList()),
                true,
                slots)
        ) {
            return false;
        }
        return true;
    }
}
