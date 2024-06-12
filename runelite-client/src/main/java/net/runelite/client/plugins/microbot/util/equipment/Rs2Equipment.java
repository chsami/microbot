package net.runelite.client.plugins.microbot.util.equipment;

import net.runelite.api.*;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.menu.NewMenuEntry;
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Rs2Equipment {
    public static ItemContainer equipment() {
        return Microbot.getClient().getItemContainer(InventoryID.EQUIPMENT);
    }

    public static List<Rs2Item> equipmentItems = new ArrayList<>();

    public static void storeEquipmentItemsInMemory(ItemContainerChanged e) {
        if (e.getContainerId() == InventoryID.EQUIPMENT.getId() && e.getItemContainer() != null) {
            List<Rs2Item> _equipmentItems = new ArrayList<>();
            for (int i = 0; i < e.getItemContainer().getItems().length; i++) {
                Item item = equipment().getItems()[i];
                if (item.getId() == -1) continue;
                int finalI = i;
                Optional<EquipmentInventorySlot> equipmentSlot = Arrays.stream(EquipmentInventorySlot.values()).filter(x -> x.getSlotIdx() == finalI).findFirst();
                if (equipmentSlot.isEmpty()) continue;
                int slot = equipmentSlot.get().getSlotIdx();
                ItemComposition itemComposition = Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getItemDefinition(item.getId()));
                _equipmentItems.add(new Rs2Item(item, itemComposition, slot));
            }
            equipmentItems = _equipmentItems;
        }
    }

    public static void useRingAction(JewelleryLocationEnum jewelleryLocationEnum) {
        if (!hasEquippedSlot(EquipmentInventorySlot.RING)) {
            Microbot.status = "Amulet is missing in the equipment slot";
            return;
        }
        Microbot.doInvoke(new NewMenuEntry(-1, 25362456, MenuAction.CC_OP.getId(), jewelleryLocationEnum.getIdentifier(), -1, "Equip"),
                new Rectangle(1, 1, Microbot.getClient().getCanvasWidth(), Microbot.getClient().getCanvasHeight()));

        //Rs2Reflection.invokeMenu(-1, 25362456, MenuAction.CC_OP.getId(), jewelleryLocationEnum.getIdentifier(), -1, "Equip", "", -1, -1);
    }

    public static void useAmuletAction(JewelleryLocationEnum jewelleryLocationEnum) {
        if (!hasEquippedSlot(EquipmentInventorySlot.AMULET) || !hasEquippedContains(jewelleryLocationEnum.getTooltip())) {
            Microbot.status = "Amulet is missing in the equipment slot";
            return;
        }
        Microbot.doInvoke(new NewMenuEntry(-1, 25362449, MenuAction.CC_OP.getId(), jewelleryLocationEnum.getIdentifier(), -1, "Equip"),
                new Rectangle(1, 1, Microbot.getClient().getCanvasWidth(), Microbot.getClient().getCanvasHeight()));
        //Rs2Reflection.invokeMenu(-1, 25362449, MenuAction.CC_OP.getId(), jewelleryLocationEnum.getIdentifier(), -1, "Equip", "", -1, -1);
    }


    public static Rs2Item get(EquipmentInventorySlot slot) {
        return equipmentItems.stream().filter(x -> x.slot == slot.getSlotIdx()).findFirst().orElse(null);
    }

    public static Rs2Item get(int id) {
        return equipmentItems.stream().filter(x -> x.id == id).findFirst().orElse(null);
    }

    public static Rs2Item get(String name, boolean exact) {
        if (exact) {
            return equipmentItems.stream().filter(x -> x.name.equalsIgnoreCase(name))
                    .findFirst()
                    .orElse(null);
        }
        return equipmentItems.stream().filter(x -> x.name.toLowerCase().contains(name.toLowerCase()))
                .findFirst()
                .orElse(null);

    }

    public static Rs2Item get(String name) {
        return get(name, false);
    }


    @Deprecated(since = "Use isWearing", forRemoval = true)
    public static boolean hasEquipped(String itemName) {
        return Microbot.getClientThread().runOnClientThread(() -> {
            for (EquipmentInventorySlot value : EquipmentInventorySlot.values()) {
                Rs2Item item = get(value);
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
                Rs2Item item = get(value);
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
        final Rs2Item item = get(slot);

        return item != null && item.id == id;
    }

    public static boolean isEquipped(String name, EquipmentInventorySlot slot, boolean exact) {
        final Rs2Item item = get(slot);
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

    public static boolean isWearing(List<String> names, boolean exact, List<EquipmentInventorySlot> ignoreSlots) {
        for (String name : names) {
            for (EquipmentInventorySlot slot : EquipmentInventorySlot.values()) {
                if (ignoreSlots.stream().anyMatch(x -> x.getSlotIdx() == slot.getSlotIdx()))
                    continue;
                if (!isEquipped(name, slot, exact)) {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean interact(int id, String action) {
        Rs2Item item = get(id);
        if (item != null) {
            invokeMenu(item, action);
            return true;
        }
        return false;
    }

    public static boolean interact(String name, String action) {
        Rs2Item item = get(name);
        if (item != null) {
            invokeMenu(item, action);
            return true;
        }
        return false;
    }

    /**
     * @param name
     * @param action
     * @param exact  name of the item
     * @return
     */
    public static boolean interact(String name, String action, boolean exact) {
        Rs2Item item = get(name, exact);
        if (item != null) {
            invokeMenu(item, action);
            return true;
        }
        return false;
    }

    public static boolean isWearingShield() {
        return equipmentItems.stream().anyMatch(x -> x.getSlot() == EquipmentInventorySlot.SHIELD.getSlotIdx());
    }

    private static void invokeMenu(Rs2Item rs2Item, String action) {
        if (rs2Item == null) return;

        Rs2Tab.switchToEquipmentTab();
        Microbot.status = action + " " + rs2Item.name;

        int param0 = -1;
        int param1 = -1;
        int identifier = 0;
        MenuAction menuAction = MenuAction.CC_OP;
        if (!action.isEmpty()) {
            List<String> actions = rs2Item.getEquipmentActions();

            for (int i = 0; i < actions.size(); i++) {
                if (action.equalsIgnoreCase(actions.get(i))) {
                    identifier = i + 2;
                    break;
                }
            }
        }

        if (rs2Item.getSlot() == EquipmentInventorySlot.CAPE.getSlotIdx()) {
            param1 = 25362448;
        } else if (rs2Item.getSlot() == EquipmentInventorySlot.HEAD.getSlotIdx()) {
            param1 = 25362447;
        } else if (rs2Item.getSlot() == EquipmentInventorySlot.AMMO.getSlotIdx()) {
            param1 = 25362457;
        } else if (rs2Item.getSlot() == EquipmentInventorySlot.AMULET.getSlotIdx()) {
            param1 = 25362449;
        } else if (rs2Item.getSlot() == EquipmentInventorySlot.WEAPON.getSlotIdx()) {
            param1 = 25362450;
        } else if (rs2Item.getSlot() == EquipmentInventorySlot.BODY.getSlotIdx()) {
            param1 = 25362451;
        } else if (rs2Item.getSlot() == EquipmentInventorySlot.SHIELD.getSlotIdx()) {
            param1 = 25362452;
        } else if (rs2Item.getSlot() == EquipmentInventorySlot.LEGS.getSlotIdx()) {
            param1 = 25362453;
        } else if (rs2Item.getSlot() == EquipmentInventorySlot.GLOVES.getSlotIdx()) {
            param1 = 25362454;
        } else if (rs2Item.getSlot() == EquipmentInventorySlot.BOOTS.getSlotIdx()) {
            param1 = 25362455;
        } else if (rs2Item.getSlot() == EquipmentInventorySlot.RING.getSlotIdx()) {
            param1 = 25362456;
        }


        Microbot.doInvoke(new NewMenuEntry(param0, param1, menuAction.getId(), identifier, -1, rs2Item.name), new Rectangle(0, 0, 1, 1));
        //Rs2Reflection.invokeMenu(param0, param1, menuAction.getId(), identifier, rs2Item.id, action, target, -1, -1);
    }
}
