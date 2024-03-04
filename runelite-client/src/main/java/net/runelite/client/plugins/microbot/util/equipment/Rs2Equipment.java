package net.runelite.client.plugins.microbot.util.equipment;

import net.runelite.api.*;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.reflection.Rs2Reflection;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class Rs2Equipment {
    public static ItemContainer equipment() {
        return Microbot.getClient().getItemContainer(InventoryID.EQUIPMENT);
    }
    public static CopyOnWriteArrayList<Rs2Item> equipmentItems = new CopyOnWriteArrayList<>();

    public static void storeEquipmentItemsInMemory(ItemContainerChanged e) {
        if (e.getContainerId() == InventoryID.EQUIPMENT.getId() && e.getItemContainer() != null) {
            equipmentItems = new CopyOnWriteArrayList<>();
            for (int i = 0; i < e.getItemContainer().getItems().length; i++) {
                Item item = equipment().getItems()[i];
                if (item.getId() == -1) continue;
                ItemComposition itemComposition = Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getItemDefinition(item.getId()));
                int finalI = i;
                Optional<EquipmentInventorySlot> equipmentSlot = Arrays.stream(EquipmentInventorySlot.values()).filter(x -> x.getSlotIdx() == finalI).findFirst();
                if (equipmentSlot.isEmpty()) continue;
                int slot = equipmentSlot.get().getSlotIdx();
                equipmentItems.add(new Rs2Item(item, itemComposition, slot));
            }
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


    public static Rs2Item getEquippedItem(EquipmentInventorySlot slot) {
        return equipmentItems.stream().filter(x -> x.slot == slot.getSlotIdx()).findFirst().orElse(null);
    }

    @Deprecated(since="Use isWearing", forRemoval = true)
    public static boolean hasEquipped(String itemName) {
        return Microbot.getClientThread().runOnClientThread(() -> {
            for (EquipmentInventorySlot value : EquipmentInventorySlot.values()) {
                Rs2Item item = getEquippedItem(value);
                if (item == null) continue;
                if (item.name.equalsIgnoreCase(itemName)) {
                    return true;
                }
            }
            return false;
        });
    }

    public static boolean hasEquippedContains(String itemName) {
        return Microbot.getClientThread().runOnClientThread(() -> {
            for (EquipmentInventorySlot value : EquipmentInventorySlot.values()) {
                Rs2Item item = getEquippedItem(value);
                if (item == null) continue;
                if (item.name.toLowerCase().contains(itemName.toLowerCase())) {
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

    public static boolean isEquipped(int id, EquipmentInventorySlot slot) {
        final Rs2Item item = getEquippedItem(slot);

        return item != null && item.id == id;
    }

    public static boolean isEquipped(String name, EquipmentInventorySlot slot, boolean exact) {
        final Rs2Item item = getEquippedItem(slot);
        if (exact) {
            return item != null && item.name.equalsIgnoreCase(name);
        } else {
            return item != null && item.name.toLowerCase().contains(name.toLowerCase());
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

    public static boolean isWearing(int id) {
        for (EquipmentInventorySlot slot : EquipmentInventorySlot.values()
        ) {
            if (isEquipped(id, slot)) {
                return true;
            }
        }
        return false;
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
