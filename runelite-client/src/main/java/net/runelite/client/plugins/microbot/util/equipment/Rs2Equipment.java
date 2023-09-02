package net.runelite.client.plugins.microbot.util.equipment;

import net.runelite.api.*;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.menu.Rs2Menu;
import net.runelite.client.plugins.microbot.util.tabs.Tab;

import static net.runelite.client.plugins.microbot.util.Global.sleep;

public class Rs2Equipment {

    public static int widgetId;
    public static int identifier;

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

    public static void useRingAction(JewelleryLocationEnum jewelleryLocationEnum) {
        if (!hasEquippedSlot(EquipmentInventorySlot.RING)) {
            Microbot.status = "Amulet is missing in the equipment slot";
            return;
        }
        widgetId = 25362456;
        identifier = jewelleryLocationEnum.getIdentifier();
        Microbot.getMouse().click(Random.random(0, Microbot.getClient().getCanvasWidth()), Random.random(0, Microbot.getClient().getCanvasHeight()));
        sleep(100);
        widgetId = 0;
        identifier = 0;
    }

    public static void useAmuletAction(JewelleryLocationEnum jewelleryLocationEnum) {
        if (!hasEquippedSlot(EquipmentInventorySlot.AMULET)) {
            Microbot.status = "Amulet is missing in the equipment slot";
            return;
        }
        widgetId = 25362449;
        identifier = jewelleryLocationEnum.getIdentifier();
        Microbot.getMouse().click(Random.random(0, Microbot.getClient().getCanvasWidth()), Random.random(0, Microbot.getClient().getCanvasHeight()));
        sleep(100);
        widgetId = 0;
        identifier = 0;
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
            for (EquipmentInventorySlot value: EquipmentInventorySlot.values()) {
                ItemComposition item =  getEquippedItem(value);
                if (item == null) continue;
                if (item.getName().toLowerCase().equals(itemName.toLowerCase())) {
                    return true;
                }
            }
            return false;
        });
    }

    public static boolean hasEquippedContains(String itemName) {
        return Microbot.getClientThread().runOnClientThread(() -> {
            for (EquipmentInventorySlot value: EquipmentInventorySlot.values()) {
                ItemComposition item =  getEquippedItem(value);
                if (item == null) continue;
                if (item.getName().toLowerCase().contains(itemName.toLowerCase())) {
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

    public static boolean hasEquippedSlot(EquipmentInventorySlot slot) {
        return Microbot.getClientThread().runOnClientThread(() -> {
            final ItemContainer container = Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getItemContainer(InventoryID.EQUIPMENT));

            if (container == null) return false;

            Item itemSlot = container.getItem(slot.getSlotIdx());

            return itemSlot != null;
        });
    }

    public static void handleMenuSwapper(MenuEntry menuEntry) {
        if (widgetId == 0) return;
        menuEntry.setOption("Teleport");
        menuEntry.setIdentifier(identifier);
        menuEntry.setParam0(-1);
        menuEntry.setParam1(widgetId);
        menuEntry.setTarget("");
        menuEntry.setType(MenuAction.CC_OP);
    }

}
