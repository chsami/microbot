package net.runelite.client.plugins.microbot.util;

import net.runelite.api.Varbits;
import net.runelite.client.plugins.inventorysetups.InventorySetup;
import net.runelite.client.plugins.inventorysetups.InventorySetupsItem;
import net.runelite.client.plugins.inventorysetups.MInventorySetupsPlugin;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

import static net.runelite.client.plugins.microbot.util.Global.sleep;

public class Rs2InventorySetup {

    InventorySetup inventorySetup;

    ScheduledFuture<?> _mainScheduler;

    public Rs2InventorySetup(String name, ScheduledFuture<?> mainScheduler) {
        inventorySetup = MInventorySetupsPlugin.getInventorySetups().stream().filter(Objects::nonNull).filter(x -> x.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        _mainScheduler = mainScheduler;
        if (inventorySetup == null) {
            Microbot.showMessage("Inventory load with name " + name + " not found!");
            Microbot.pauseAllScripts = true;
        }
    }

    private boolean isMainSchedulerCancelled() {
        return _mainScheduler != null && _mainScheduler.isCancelled();
    }

    public boolean loadInventory() {
        Rs2Bank.openBank();
        if (!Rs2Bank.isOpen()) {
            return false;
        }
        Rs2Bank.depositAllExcept(itemsToNotDeposit());
        Map<Integer, List<InventorySetupsItem>> groupedByItems = inventorySetup.getInventory().stream().collect(Collectors.groupingBy(InventorySetupsItem::getId));

        for (Map.Entry<Integer, List<InventorySetupsItem>> entry : groupedByItems.entrySet()) {
            if (isMainSchedulerCancelled()) break;

            InventorySetupsItem inventorySetupsItem = entry.getValue().get(0);
            int key = entry.getKey();

            if (inventorySetupsItem.getId() == -1) continue;

            int withdrawQuantity = calculateWithdrawQuantity(entry.getValue(), inventorySetupsItem, key);
            if (withdrawQuantity == 0) continue;

            if (!Rs2Bank.hasBankItem(inventorySetupsItem.getName(), withdrawQuantity)) {
                Microbot.pauseAllScripts = true;
                Microbot.showMessage("Bank is missing the following item " + inventorySetupsItem.getName());
                break;
            }

            withdrawItem(inventorySetupsItem, withdrawQuantity);
        }

        sleep(1000);

        return doesInventoryMatch();
    }

    private int calculateWithdrawQuantity(List<InventorySetupsItem> items, InventorySetupsItem inventorySetupsItem, int key) {
        int withdrawQuantity;
        if (items.size() == 1) {
            Rs2Item rs2Item = Rs2Inventory.get(key);
            if (rs2Item != null && rs2Item.isStackable()) {
                withdrawQuantity = inventorySetupsItem.getQuantity() - rs2Item.quantity;
                if (Rs2Inventory.hasItemAmount(inventorySetupsItem.getName(), inventorySetupsItem.getQuantity())) {
                    return 0;
                }
            } else {
                withdrawQuantity = items.get(0).getQuantity();
                if (Rs2Inventory.hasItemAmount(inventorySetupsItem.getName(), withdrawQuantity)) {
                    return 0;
                }
            }
        } else {
            withdrawQuantity = items.size() - (int) Rs2Inventory.items().stream().filter(x -> x.getId() == key).count();
            if (Rs2Inventory.hasItemAmount(inventorySetupsItem.getName(), items.size())) {
                return 0;
            }
        }
        return withdrawQuantity;
    }

    private void withdrawItem(InventorySetupsItem item, int quantity) {
        if (item.isFuzzy()) {
            Rs2Bank.withdrawX(item.getName(), quantity);
        } else {
            if (quantity > 1) {
                Rs2Bank.withdrawX(item.getId(), quantity);
            } else {
                Rs2Bank.withdrawItem(item.getId());
            }
            sleep(100, 250);
        }
    }

    public boolean loadEquipment() {
        Rs2Bank.openBank();
        if (!Rs2Bank.isOpen()) {
            return false;
        }

        //Clear inventory if full
        if (Rs2Inventory.isFull()) {
            Rs2Bank.depositAll();
        } else {
            //only deposit the items we don't need
            Rs2Bank.depositAllExcept(itemsToNotDeposit());
        }

        for (InventorySetupsItem inventorySetupsItem : inventorySetup.getEquipment()) {
            if (isMainSchedulerCancelled()) break;
            if (InventorySetupsItem.itemIsDummy(inventorySetupsItem)) continue;

            if (inventorySetupsItem.isFuzzy()) {
                if (!Rs2Bank.hasBankItem(inventorySetupsItem.getName())) {
                    Microbot.pauseAllScripts = true;
                    Microbot.showMessage("Bank is missing the following item " + inventorySetupsItem.getName());
                    break;
                }

                if (Rs2Inventory.hasItemAmount(inventorySetupsItem.getName(), (int) inventorySetup.getInventory().stream().filter(x -> x.getId() == inventorySetupsItem.getId()).count()))
                    continue;
                if (Rs2Equipment.isWearing(inventorySetupsItem.getName()))
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

        sleep(1000);

        return doesEquipmentMatch();
    }

    public boolean wearEquipment() {
        for (InventorySetupsItem inventorySetupsItem : inventorySetup.getEquipment()) {
            Rs2Inventory.wield(inventorySetupsItem.getId());
        }
        return doesEquipmentMatch();
    }

    public boolean doesInventoryMatch() {
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

    public boolean doesEquipmentMatch() {
        for (InventorySetupsItem inventorySetupsItem : inventorySetup.getEquipment()) {
            if (inventorySetupsItem.getId() == -1) continue;
            if (!Rs2Equipment.isWearing(inventorySetupsItem.getName(), true)) {
                return false;
            }
        }
        return true;
    }

    public List<InventorySetupsItem> getInventoryItems() {
        return inventorySetup.getInventory().stream().filter(x -> x.getId() != -1).collect(Collectors.toList());
    }

    public List<InventorySetupsItem> getEquipmentItems() {
        return inventorySetup.getEquipment().stream().filter(x -> x.getId() != -1).collect(Collectors.toList());
    }

    public List<String> itemsToNotDeposit() {
        List<InventorySetupsItem> inventorySetupItems = getInventoryItems();
        List<InventorySetupsItem> equipmentSetupItems = getEquipmentItems();

        List<InventorySetupsItem> combined = new ArrayList<>();

        combined.addAll(inventorySetupItems);
        combined.addAll(equipmentSetupItems);

        return combined.stream().map(InventorySetupsItem::getName).collect(Collectors.toList());
    }

    public boolean hasSpellBook() {
        return inventorySetup.getSpellBook() == Microbot.getVarbitValue(Varbits.SPELLBOOK);
    }
}
