package net.runelite.client.plugins.microbot.util;

import net.runelite.client.plugins.inventorysetups.InventorySetup;
import net.runelite.client.plugins.inventorysetups.InventorySetupsItem;
import net.runelite.client.plugins.inventorysetups.InventorySetupsPlugin;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static net.runelite.client.plugins.microbot.util.Global.sleep;
import static net.runelite.client.plugins.microbot.util.Global.sleepUntil;

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
        // depositNonMatchingItems();
        if (Rs2Bank.isOpen()) {
            if (inventorySetup == null) return false;
            for (int i = 0; i < inventorySetup.getInventory().size(); i++) {
                InventorySetupsItem inventorySetupsItem = inventorySetup.getInventory().get(i);
                int itemId = hasItemInBank(inventorySetupsItem);
                if (itemId == -1)
                    continue;
                if (inventorySetupsItem.isFuzzy()) {
                    Rs2Bank.withdrawX(itemId, inventorySetupsItem.getQuantity());
                } else {
                    Rs2Bank.withdrawX(inventorySetupsItem.getName(), inventorySetupsItem.getQuantity());
                }
            }
        }
        sleep(1000);

        return doesInventoryMatch(name);
    }

    public static boolean loadEquipment(String name) {
        Rs2Bank.openBank();
        if (Rs2Bank.isOpen()) {
            if (!getInventorySetup(name))
            {
                return false;
            }
            if (inventorySetup == null) return false;
            for (InventorySetupsItem inventorySetupsItem : inventorySetup.getEquipment()) {
                if (inventorySetupsItem.isFuzzy()) {
                    int itemId = hasItemInBank(inventorySetupsItem);
                    if (itemId == -1)
                        continue;
                    if (Rs2Inventory.hasItemAmount(itemId, (int) inventorySetup.getInventory().stream().filter(x -> x.getId() == inventorySetupsItem.getId()).count()))
                        continue;
                    if (inventorySetupsItem.getQuantity() > 1) {
                        Rs2Bank.withdrawAllAndEquip(itemId);
                    } else {
                        Rs2Bank.withdrawAndEquip(itemId);
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
                    } else {
                        Rs2Bank.withdrawAndEquip(inventorySetupsItem.getName());
                    }
                }
            }
        }
        sleep(1000);

        return doesEquipmentMatch(name);
    }

    private static void depositNonMatchingItems() {
        for (Rs2Item rs2Item : Rs2Inventory.items()) {
            boolean match = false;
            for (int i = 0; i < inventorySetup.getInventory().size(); i++) {
                if (match)
                    break;
                InventorySetupsItem inventorySetupsItem = inventorySetup.getInventory().get(i);
                if (inventorySetupsItem.isFuzzy()) {
                    Collection<Integer> possibleIds = inventorySetupsItem.getVariations();
                    match = possibleIds.stream().anyMatch(id -> id == rs2Item.id);
                } else {
                    match = inventorySetupsItem.getId() == rs2Item.id;
                }
            }
            if (!match) {
                Rs2Bank.depositAll(rs2Item.id);
            }
        }
    }

    private static int hasItemInBank(InventorySetupsItem inventorySetupsItem) {
        if (inventorySetupsItem.getId() == -1) return -1;
        for (int mappedId : inventorySetupsItem.getVariations()) {
            if (Rs2Bank.hasItem(mappedId)) {
                return mappedId;
            }
        }
        Microbot.pauseAllScripts = true;
        Microbot.showMessage("Bank is missing the following item " + inventorySetupsItem.getName());
        return -1;
    }

    private static int itemExistsInInventory(InventorySetupsItem inventorySetupsItem) {
        for (int mappedId : inventorySetupsItem.getVariations()) {
            if (Rs2Inventory.hasItemAmount(mappedId, (int) inventorySetup.getInventory().stream().filter(x -> x.getId() == inventorySetupsItem.getId()).count())) {
                return mappedId;
            }
        }
        return -1;
    }

    public static boolean doesInventoryMatch(String name) {
        inventorySetup = InventorySetupsPlugin.getInventorySetups().stream().filter(Objects::nonNull).filter(x -> x.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        if (inventorySetup == null) return false;

        for (int i = 0; i < inventorySetup.getInventory().size(); i++) {
            InventorySetupsItem inventorySetupsItem = inventorySetup.getInventory().get(i);
            Rs2Item rsItem = Rs2Inventory.getItemInSlot(i);
            if (rsItem == null) return false;
            if (inventorySetupsItem.isFuzzy()) {
                Collection<Integer> variations = inventorySetupsItem.getVariations();
                if (!variations.contains(rsItem.id)) {
                    return false;
                }
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
