package net.runelite.client.plugins.microbot.util.equipment;

import net.runelite.api.*;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.menu.Rs2Menu;
import net.runelite.client.plugins.microbot.util.reflection.Rs2Reflection;
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab;

public class Rs2Equipment {

    public static boolean equipItemFast(int id) {
        Rs2Tab.switchToInventoryTab();
        Widget item = Inventory.findItem(id);
        if (item == null) return false;
        Microbot.getMouse().click(new Point((int) item.getBounds().getCenterX(), (int) item.getBounds().getCenterY()));
        return true;
    }

    public static boolean equipItemFast(String name) {
        Rs2Tab.switchToInventoryTab();
        Widget item = Inventory.findItem(name);
        if (item == null) return false;
        Microbot.getMouse().click(new Point((int) item.getBounds().getCenterX(), (int) item.getBounds().getCenterY()));
        return true;
    }


    public static void equipItem(int id) {
        assert !Microbot.getClient().isClientThread();
        Rs2Tab.switchToInventoryTab();
        Widget item = Inventory.findItem(id);
        if (item == null) return;
        while (!hasEquipped(id)) {
            Rs2Menu.doAction(new String[]{"wield", "wear"}, new Point((int) item.getBounds().getCenterX(), (int) item.getBounds().getCenterY()));
        }
    }

    public static void equipItem(String itemName) {
        assert !Microbot.getClient().isClientThread();
        Rs2Tab.switchToInventoryTab();
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
        Rs2Reflection.invokeMenu(-1, 25362456, MenuAction.CC_OP.getId(), jewelleryLocationEnum.getIdentifier(), -1, "Equip", "", -1, -1);
    }

    public static void useAmuletAction(JewelleryLocationEnum jewelleryLocationEnum) {
        if (!hasEquippedSlot(EquipmentInventorySlot.AMULET) || !hasEquippedContains(jewelleryLocationEnum.getTooltip())) {
            Microbot.status = "Amulet is missing in the equipment slot";
            return;
        }
        Rs2Reflection.invokeMenu(-1, 25362449, MenuAction.CC_OP.getId(), jewelleryLocationEnum.getIdentifier(), -1, "Equip", "", -1, -1);
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
            for (EquipmentInventorySlot value : EquipmentInventorySlot.values()) {
                ItemComposition item = getEquippedItem(value);
                if (item == null) continue;
                if (item.getName().equalsIgnoreCase(itemName)) {
                    return true;
                }
            }
            return false;
        });
    }

    public static boolean hasEquippedContains(String itemName) {
        return Microbot.getClientThread().runOnClientThread(() -> {
            for (EquipmentInventorySlot value : EquipmentInventorySlot.values()) {
                ItemComposition item = getEquippedItem(value);
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
            for (EquipmentInventorySlot value : EquipmentInventorySlot.values()) {
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

    public static boolean isEquipped(String name, EquipmentInventorySlot slot) {
        return isEquipped(name, slot, false);
    }

    public static boolean isEquipped(String name, EquipmentInventorySlot slot, boolean exact) {
        final ItemComposition item = getEquippedItem(slot);
        if (exact) {
            return item != null && item.getName().equalsIgnoreCase(name);
        } else {
            return item != null && item.getName().toLowerCase().contains(name.toLowerCase());
        }
    }


    public static boolean hasGuthanWeaponEquiped() {
        return isEquipped("guthan's warspear", EquipmentInventorySlot.WEAPON);
    }

    public static boolean hasGuthanBodyEquiped() {
        return isEquipped("guthan's platebody", EquipmentInventorySlot.BODY);
    }

    public static boolean hasGuthanLegsEquiped() {
        return isEquipped("guthan's chainskirt", EquipmentInventorySlot.LEGS);
    }

    public static boolean hasGuthanHelmEquiped() {
        return isEquipped("guthan's helm", EquipmentInventorySlot.HEAD);
    }

    public static boolean isWearingFullGuthan() {
        return hasGuthanBodyEquiped() && hasGuthanWeaponEquiped() &&
                hasGuthanHelmEquiped() && hasGuthanLegsEquiped();
    }

    public static boolean isWearing(String name) {
        return isWearing(name, false);
    }

    public static boolean isWearing(String name, boolean exact) {
        for (EquipmentInventorySlot slot : EquipmentInventorySlot.values()
        ) {
            if (isEquipped(name, slot, exact)) {
                return true;
            }
        }
        return false;
    }

}
