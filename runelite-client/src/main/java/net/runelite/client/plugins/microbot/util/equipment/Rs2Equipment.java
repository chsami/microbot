package net.runelite.client.plugins.microbot.util.equipment;

import net.runelite.api.*;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.tabs.Tab;

public class Rs2Equipment {

    public static boolean equipItem(int id) {
        Tab.switchToInventoryTab();
        Widget item = Inventory.findItem(id);
        if (item == null) return false;
        Microbot.getMouse().click(new Point((int) item.getBounds().getCenterX(), (int) item.getBounds().getCenterY()));
        return true;
    }

    public static void equipItem(String itemName) {
        assert !Microbot.getClient().isClientThread();
        Tab.switchToInventoryTab();
        if (!Inventory.hasItem(itemName)) return;
        while (!hasEquipped(itemName)) {
            Inventory.equipItem(itemName);
        }
    }
    public static ItemComposition getEquippedItem(EquipmentInventorySlot slot) {
        final ItemContainer container = Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getItemContainer(InventoryID.EQUIPMENT));
        if (container == null) return null;
        Item itemSlot = container.getItem(slot.getSlotIdx());
        if (itemSlot == null) return null;
        return Microbot.getClientThread().runOnClientThread(() -> Microbot.getItemManager().getItemComposition(itemSlot.getId()));
    }

    public static boolean hasEquipped(String itemName) {
        return Microbot.getClientThread().runOnClientThread(() -> {
            final ItemContainer container = Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getItemContainer(InventoryID.EQUIPMENT));
            if (container == null) return false;
            for (EquipmentInventorySlot value: EquipmentInventorySlot.values()) {
                Item itemSlot = container.getItem(value.getSlotIdx());
                if (itemSlot == null) continue;
                ItemComposition item =  Microbot.getItemManager().getItemComposition(itemSlot.getId());
                if (item.getName().toLowerCase().equals(itemName.toLowerCase())) {
                    return true;
                }
            }
            return false;
        });
    }

    public static boolean hasEquipped(int id) {
        return Microbot.getClientThread().runOnClientThread(() -> {
            final ItemContainer container = Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getItemContainer(InventoryID.EQUIPMENT));
            if (container == null) return false;
            for (EquipmentInventorySlot value: EquipmentInventorySlot.values()) {
                Item itemSlot = container.getItem(value.getSlotIdx());
                if (itemSlot == null) continue;
                if (itemSlot.getId() == id) {
                    return true;
                }
            }
            return false;
        });
    }
}
