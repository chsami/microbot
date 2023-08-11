package net.runelite.client.plugins.microbot.util.equipment;

import net.runelite.api.*;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.menu.Rs2Menu;
import net.runelite.client.plugins.microbot.util.tabs.Tab;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

public class Rs2Equipment {

    public static void useRingAction(String actionName) {
        Widget ringSlot = Rs2Widget.getWidget(25362456);
        if(ringSlot != null) {
            Microbot.status = "found ring slot";
            Rs2Menu.doAction(actionName, new Point((int) ringSlot.getBounds().getCenterX(), (int) ringSlot.getBounds().getCenterY()));

            Microbot.status = "attempted action";
        }
    }

    public static boolean equipItemFast(int id) {
        Tab.switchToInventoryTab();
        Widget item = Inventory.findItem(id);
        if (item == null) return false;
        Microbot.getMouse().click(new Point((int) item.getBounds().getCenterX(), (int) item.getBounds().getCenterY()));
        return true;
    }

    public static boolean equipItemFast(String name) {
        Tab.switchToInventoryTab();
        Widget item = Inventory.findItem(name);
        if (item == null) return false;
        Microbot.getMouse().click(new Point((int) item.getBounds().getCenterX(), (int) item.getBounds().getCenterY()));
        return true;
    }


    public static void equipItem(int id) {
        assert !Microbot.getClient().isClientThread();
        Tab.switchToInventoryTab();
        Widget item = Inventory.findItem(id);
        if (item == null) return;
        while (!hasEquipped(id)) {
            Rs2Menu.doAction(new String[]{"wield", "wear"}, new Point((int) item.getBounds().getCenterX(), (int) item.getBounds().getCenterY()));
        }
    }

    public static void equipItem(String itemName) {
        assert !Microbot.getClient().isClientThread();
        Tab.switchToInventoryTab();
        Widget item = Inventory.findItem(itemName);
        if (item == null) return;
        while (!hasEquipped(itemName)) {
            Rs2Menu.doAction(new String[]{"wield", "wear"}, new Point((int) item.getBounds().getCenterX(), (int) item.getBounds().getCenterY()));
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
