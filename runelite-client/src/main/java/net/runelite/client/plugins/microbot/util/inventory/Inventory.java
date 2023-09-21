package net.runelite.client.plugins.microbot.util.inventory;

import net.runelite.api.*;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.globval.enums.InterfaceTab;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.menu.Rs2Menu;
import net.runelite.client.plugins.microbot.util.reflection.Rs2Reflection;
import net.runelite.client.plugins.microbot.util.settings.Rs2Settings;
import net.runelite.client.plugins.microbot.util.tabs.Tab;

import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.runelite.client.plugins.microbot.util.Global.sleep;
import static net.runelite.client.plugins.microbot.util.Global.sleepUntilOnClientThread;


public class Inventory {

    public static Rs2Item item;
    public static String itemAction;

    public static void eat(Widget widget) {
        Microbot.getMouse().click(widget.getBounds());
        sleep(1200, 2000);
    }

    private static boolean itemExistsInInventory(Widget item) {
        return item != null && !item.getName().isEmpty() && !item.isHidden() && item.getOpacity() != 255 && !item.isSelfHidden();
    }

    public static void open() {
        Microbot.status = "Open inventory";
        Tab.switchToInventoryTab();
        sleep(300, 1200);
        sleepUntilOnClientThread(() -> Tab.getCurrentTab() == InterfaceTab.INVENTORY);
    }

    public static boolean clickItem(int slot) {
        Microbot.status = "Checking inventory slot " + slot;
        Widget inventoryWidget = getInventory();
        if (inventoryWidget == null) return false;
        return Microbot.getClientThread().runOnClientThread(() -> {
            Widget item = inventoryWidget.getChild(slot);
            if (item == null || item.isHidden()) return false;

            Microbot.getMouse().click(item.getBounds());

            sleep(300, 1000);

            return true;
        });
    }

    public static boolean isFull() {
        Microbot.status = "Checking if inventory is full";
        Tab.switchToInventoryTab();
        Widget inventoryWidget = getInventory();
        if (inventoryWidget == null) return false;
        return Microbot.getClientThread().runOnClientThread(() -> Arrays.stream(inventoryWidget.getDynamicChildren()).filter(x -> itemExistsInInventory(x)).count() == 28);
    }

    @Deprecated(since = "Use isFull method instead", forRemoval = true)
    public static boolean isInventoryFull() {
        Microbot.status = "Checking if inventory is full";
        Tab.switchToInventoryTab();
        Widget inventoryWidget = getInventory();
        if (inventoryWidget == null) return false;
        return Microbot.getClientThread().runOnClientThread(() -> Arrays.stream(inventoryWidget.getDynamicChildren()).filter(Inventory::itemExistsInInventory).count() == 28);
    }

    public static long count() {
        Microbot.status = "Counting inventory items";
        Widget inventoryWidget = getInventory();
        if (inventoryWidget == null) return 0;
        return Microbot.getClientThread().runOnClientThread(() -> Arrays.stream(inventoryWidget.getDynamicChildren()).filter(Inventory::itemExistsInInventory).count());
    }

    public static int getItemAmount(int id) {
        Microbot.status = "Looking for item: " + id;
        Widget inventoryWidget = getInventory();
        if (inventoryWidget == null) return 0;

        Widget item = Microbot.getClientThread().runOnClientThread(() -> Arrays.stream(inventoryWidget.getDynamicChildren()).filter(x -> itemExistsInInventory(x) && x.getItemId() == id)
                .findFirst().orElse(null));

        if (item != null)
            return item.getItemQuantity();

        return 0;
    }

    public static boolean isInventoryFull(String itemName) {
        return Microbot.getClientThread().runOnClientThread(() -> {
            Widget inventoryWidget = getInventory();
            if (inventoryWidget == null) return false;
            if (hasItemStackable(itemName)) return false;
            return Arrays.stream(inventoryWidget.getDynamicChildren()).filter(Inventory::itemExistsInInventory).count() == 28;
        });
    }

    public static boolean isInventoryFull(int itemId) {
        return Microbot.getClientThread().runOnClientThread(() -> {
            Widget inventoryWidget = getInventory();
            if (inventoryWidget == null) return false;
            if (hasItemStackable(itemId)) return false;
            return Arrays.stream(inventoryWidget.getDynamicChildren()).filter(Inventory::itemExistsInInventory).count() == 28;
        });
    }

    public static boolean isEmpty() {
        return hasAmountInventoryItems(0);
    }

    public static boolean hasAmountInventoryItems(int count) {
        Microbot.status = "Checking if player has " + count + " items in their inventory";
        Widget inventoryWidget = getInventory();
        if (inventoryWidget == null) return false;
        return Microbot.getClientThread().runOnClientThread(() -> Arrays.stream(inventoryWidget.getDynamicChildren()).filter(Inventory::itemExistsInInventory).count() == count);
    }

    public static boolean hasItemStackable(String itemName) {
        Microbot.status = "Checking inventory has stackable item " + itemName;
        Widget inventoryWidget = getInventory();
        if (inventoryWidget == null) return false;
        return Arrays.stream(inventoryWidget.getDynamicChildren())
                .anyMatch(x -> {
                    ItemComposition itemComp = Microbot.getItemManager().getItemComposition(x.getItemId());
                    if (itemComp.getNote() == 799 || itemComp.isStackable()) {
                        return itemExistsInInventory(x) && x.getName().split(">")[1].split("</")[0].equalsIgnoreCase(itemName);
                    } else {
                        return false;
                    }
                });
    }

    public static boolean hasItemStackable(int itemId) {
        Microbot.status = "Checking inventory has stackable item " + itemId;
        Widget inventoryWidget = getInventory();
        if (inventoryWidget == null) return false;
        return Arrays.stream(inventoryWidget.getDynamicChildren())
                .anyMatch(x -> {
                    ItemComposition itemComp = Microbot.getItemManager().getItemComposition(x.getItemId());
                    if (itemComp.getNote() == 799 || itemComp.isStackable()) {
                        return itemExistsInInventory(x) && x.getItemId() == itemId;
                    } else {
                        return false;
                    }
                });
    }

    public static boolean hasItem(String itemName) {
        Microbot.status = "Looking for item: " + itemName;
        Tab.switchToInventoryTab();
        Widget inventoryWidget = getInventory();
        if (inventoryWidget == null) return false;
        return Microbot.getClientThread().runOnClientThread(() -> Arrays.stream(inventoryWidget.getDynamicChildren())
                .anyMatch(x ->
                        itemExistsInInventory(x) && x.getName().split(">")[1].split("</")[0].equalsIgnoreCase(itemName)
                ));
    }

    public static boolean contains(String itemName) {
        return hasItem(itemName);
    }

    public static boolean hasItem(int id) {
        Microbot.status = "Looking for item: " + id;
        Widget inventoryWidget = getInventory();
        if (inventoryWidget == null) return false;
        return Microbot.getClientThread().runOnClientThread(() -> Arrays.stream(inventoryWidget.getDynamicChildren())
                .anyMatch(x ->
                        itemExistsInInventory(x) && x.getItemId() == id));
    }

    public static boolean hasItemContains(String itemName) {
        Microbot.status = "Looking for item: " + itemName;
        Widget inventoryWidget = getInventory();
        if (inventoryWidget == null) return false;
        return Microbot.getClientThread().runOnClientThread(() -> Arrays.stream(inventoryWidget.getDynamicChildren())
                .anyMatch(x ->
                        itemExistsInInventory(x) && x.getName().split(">")[1].split("</")[0].toLowerCase().contains(itemName.toLowerCase())
                ));
    }

    public static Widget getInventoryItem(String itemName) {
        Microbot.status = "Searching inventory item:" + itemName;
        Widget inventoryWidget = getInventory();
        if (inventoryWidget == null) return null;
        return Arrays.stream(inventoryWidget.getDynamicChildren()).filter(x -> itemExistsInInventory(x) && x.getName().toLowerCase().contains(itemName)).findFirst().get();
    }

    public static Widget[] getInventoryItems() {
        Microbot.status = "Fetching inventory items";
        Widget inventoryWidget = getInventory();
        if (inventoryWidget == null) return null;
        return Arrays.stream(inventoryWidget.getDynamicChildren()).filter(Inventory::itemExistsInInventory).toArray(Widget[]::new);
    }

    public static Widget[] getInventoryFood() {
        Microbot.status = "Fetching inventory food";
        Inventory.open();
        Widget inventoryWidget = getInventory();
        if (inventoryWidget == null) return null;
        Widget[] items = Arrays.stream(inventoryWidget.getDynamicChildren()).filter(x -> itemExistsInInventory(x)).toArray(Widget[]::new);
        items = Arrays.stream(items).filter(x -> Arrays.stream(x.getActions()).anyMatch(c -> c != null && c.toLowerCase().equals("eat"))).toArray(Widget[]::new);
        ;
        return items;
    }

    public static Widget[] getPotions() {
        Microbot.status = "Fetching inventory potions";
        Widget inventoryWidget = getInventory();
        if (inventoryWidget == null) return null;
        Widget[] items = Arrays.stream(inventoryWidget.getDynamicChildren()).filter(x -> itemExistsInInventory(x)).toArray(Widget[]::new);
        items = Arrays.stream(items).filter(x -> Arrays.stream(x.getActions()).anyMatch(c -> c != null && c.toLowerCase().equals("drink"))).toArray(Widget[]::new);
        return items;
    }

    public static Widget findItemSlot(int index) {
        Microbot.status = "Searching inventory slot " + index;
        Widget inventoryWidget = getInventory();
        if (inventoryWidget == null) return null;
        return inventoryWidget.getChild(index);
    }

    public static Widget findItem(int itemId) {
        Microbot.status = "Searching inventory item with id " + itemId;
        Widget inventoryWidget = getInventory();
        if (inventoryWidget == null) return null;
        for (Widget item : inventoryWidget.getDynamicChildren()) {
            if (item.getItemId() == itemId) {
                return item;
            }
        }
        return null;
    }

    public static boolean hasItemAmount(int itemId, int amount) {
        Microbot.status = "Check if inventory has item: " + itemId + " with amount: " + amount;
        Tab.switchToInventoryTab();
        Widget inventoryWidget = getInventory();
        if (inventoryWidget == null) return false;
        return Microbot.getClientThread().runOnClientThread(() -> Arrays.stream(inventoryWidget.getDynamicChildren())
                .filter(x ->
                        itemExistsInInventory(x) && x.getItemId() == itemId
                ).count() >= amount);
    }

    public static boolean hasItemAmount(String itemName, int amount) {
        Microbot.status = "Check if inventory has item: " + itemName + " with amount: " + amount;
        Tab.switchToInventoryTab();
        Widget inventoryWidget = getInventory();
        if (inventoryWidget == null) return false;
        return Microbot.getClientThread().runOnClientThread(() -> Arrays.stream(inventoryWidget.getDynamicChildren())
                .filter(x ->
                        itemExistsInInventory(x) && x.getName().split(">")[1].split("</")[0].equalsIgnoreCase(itemName)
                ).count() >= amount);
    }

    public static boolean hasItemAmountExact(String itemName, int amount) {
        Microbot.status = "Check if inventory has item: " + itemName + " with amount: " + amount;
        Tab.switchToInventoryTab();
        Widget inventoryWidget = getInventory();
        if (inventoryWidget == null) return false;
        return Microbot.getClientThread().runOnClientThread(() -> Arrays.stream(inventoryWidget.getDynamicChildren())
                .filter(x ->
                        itemExistsInInventory(x) && x.getName().split(">")[1].split("</")[0].equalsIgnoreCase(itemName)
                ).count() == amount);
    }


    public static boolean hasItemAmountStackable(String itemName, int amount) {
        Microbot.status = "Check if inventory has item: " + itemName + " with amount: " + amount;
        Tab.switchToInventoryTab();
        Widget inventoryWidget = getInventory();
        if (inventoryWidget == null) return false;
        return Microbot.getClientThread().runOnClientThread(() -> Arrays.stream(inventoryWidget.getDynamicChildren())
                .anyMatch(x ->
                        itemExistsInInventory(x) &&
                                x.getName()
                                        .split(">")[1]
                                        .split("</")[0]
                                        .equalsIgnoreCase(itemName) &&
                                x.getItemQuantity() >= amount
                ));
    }

    private static Widget getInventory() {
        return Microbot.getClientThread().runOnClientThread(() -> {
            Widget inventoryWidget = Microbot.getClient().getWidget(9764864);
            Widget bankInventoryWidget = Microbot.getClient().getWidget(983043);
            Widget bankPinInventoryWidget = Microbot.getClient().getWidget(17563648);
            if (inventoryWidget != null && inventoryWidget.getDynamicChildren() != null && !inventoryWidget.isHidden()) {
                return inventoryWidget;
            }
            if (bankInventoryWidget != null && bankInventoryWidget.getDynamicChildren() != null && !bankInventoryWidget.isHidden()) {
                return bankInventoryWidget;
            }
            if (bankPinInventoryWidget != null && bankPinInventoryWidget.getDynamicChildren() != null && !bankPinInventoryWidget.isHidden()) {
                return bankPinInventoryWidget;
            }
            return null;
        });
    }

    public static Widget findItem(String itemName, boolean exact) {
        Microbot.status = "Searching inventory for item: " + itemName;
        Tab.switchToInventoryTab();
        Widget inventoryWidget = getInventory();
        if (inventoryWidget == null) return null;
        return Microbot.getClientThread().runOnClientThread(() -> {
            for (Widget item : inventoryWidget.getDynamicChildren()) {
                if (!item.isSelfHidden()) {
                    if (itemExistsInInventory(item)) {
                        String itemNameInItem = item.getName().split(">")[1].split("</")[0].toLowerCase();
                        String targetItemName = itemName.toLowerCase();

                        if (exact ? itemNameInItem.equals(targetItemName) : itemNameInItem.contains(targetItemName)) {
                            return item;
                        }
                    }
                }
            }
            return null;
        });
    }

    public static Widget findItem(String itemName) {
        return findItem( itemName, false);
    }

    public static void useItemOnItemSlot(int slot1, int slot2) {
        useItemSlot(slot1);
        useItemSlot(slot2);
    }

    public static void useItemOnItemSlot(int slot1, int slot2, int minWait, int maxWait) {
        Microbot.status = "Use inventory slot " + slot1 + " with slot " + slot2;
        useItemSlot(slot1);
        useItemSlot(slot2);
        sleep(minWait, maxWait);
    }

    public static boolean useItemSlot(int slot) {
        if (Rs2Bank.isOpen()) return false;
        Microbot.status = "Use inventory slot " + slot;
        Widget item = findItemSlot(slot);
        if (item == null) return false;
        Microbot.getMouse().click(item.getBounds().getCenterX(), item.getBounds().getCenterY());
        return true;
    }

    public static boolean useItemContains(String itemName) {
        if (Rs2Bank.isOpen()) return false;
        Microbot.status = "Use inventory item containing " + itemName;
        Widget item = findItem(itemName);
        if (item == null) return false;
        Microbot.getMouse().click(item.getBounds().getCenterX(), item.getBounds().getCenterY());
        return true;
    }

    public static boolean useItem(String itemName) {
        if (Rs2Bank.isOpen()) return false;
        Microbot.status = "Use inventory item " + itemName;
        Widget item = findItem(itemName);
        if (item == null) return false;
        Microbot.getMouse().click(item.getBounds().getCenterX(), item.getBounds().getCenterY());
        sleep(600, 800);
        return true;
    }

    public static boolean useItemUnsafe(String itemName) {
        if (Rs2Bank.isOpen()) return false;
        Microbot.status = "Use inventory item " + itemName;
        Widget item = findItem(itemName);
        if (item == null) return false;
        Microbot.getMouse().click(item.getBounds().getCenterX(), item.getBounds().getCenterY());
        return true;
    }

    public static boolean useItem(int id) {
        if (Rs2Bank.isOpen()) return false;
        Microbot.status = "Use inventory item " + id;
        Widget item = findItem(id);
        if (item == null) return false;
        Microbot.getMouse().click(item.getBounds().getCenterX(), item.getBounds().getCenterY());
        sleep(600, 1200);
        return true;
    }

    public static boolean interact(String itemName) {
        return useItem(itemName);
    }

    public static boolean interact(String... itemNames) {
        boolean interacted = false;
        for (String itemName : itemNames) {
            Widget item = findItem(itemName);
            if (item != null) {
                Microbot.getMouse().click(item.getBounds().getCenterX(), item.getBounds().getCenterY());
                sleep(600, 1200);
                interacted = true;
                break;
            }
        }
        return interacted;
    }

    public static boolean interactContains(String itemName) {
        return useItemContains(itemName);
    }

    public static boolean interactItemContains(String... itemNames) {
        boolean interacted = false;
        for (String itemName : itemNames) {
            Widget item = findItem(itemName);
            if (item != null) {
                Microbot.getMouse().click(item.getBounds().getCenterX(), item.getBounds().getCenterY());
                sleep(600, 1200);
                interacted = true;
                break;
            }
        }
        return interacted;
    }

    public static boolean useItemOnItem(String itemName1, String itemName2) {
        if (Rs2Bank.isOpen()) return false;
        Microbot.status = "Use inventory item " + itemName1 + " with " + itemName2;
        Widget item1 = findItem(itemName1);
        Widget item2 = findItem(itemName2);
        if (item1 == null || item2 == null) return false;
        Microbot.getMouse().click(item1.getBounds().getCenterX(), item1.getBounds().getCenterY());
        sleep(600, 1200);
        Microbot.getMouse().click(item2.getBounds().getCenterX(), item2.getBounds().getCenterY());
        sleep(600, 1200);
        return true;
    }

    public static boolean useItemSafe(String itemName) {
        if (Rs2Bank.isOpen()) return false;
        Microbot.status = "Use inventory item safe " + itemName;
        Tab.switchToInventoryTab();
        if (isUsingItem())
            Microbot.getMouse().click();

        sleep(600, 1200);

        return useItem(itemName);
    }

    public static boolean useItemAction(String itemName, String actionName) {
        Microbot.status = "Use inventory item " + itemName + " with action " + actionName;
        Widget item = findItem(itemName);
        if (item == null) return false;
        return Rs2Menu.doAction(actionName, new Point((int) item.getBounds().getCenterX(), (int) item.getBounds().getCenterY()));
    }

    public static boolean useItemActionContains(String itemName, String actionName) {
        Microbot.status = "Use inventory item contains " + itemName + " with action " + actionName;
        Widget item = findItem(itemName);
        if (item == null) return false;
        return Rs2Menu.doAction(actionName, new Point((int) item.getBounds().getCenterX(), (int) item.getBounds().getCenterY()));
    }

    public static boolean useItemAction(int itemID, String actionName) {
        Microbot.status = "Use inventory item " + itemID + " with action " + actionName;
        Widget item = findItem(itemID);
        if (item == null) return false;
        return Rs2Menu.doAction(actionName, new Point((int) item.getBounds().getCenterX(), (int) item.getBounds().getCenterY()));
    }

    public static boolean useItemAction(String itemName, String[] actionNames) {
        Microbot.status = "Use inventory item " + itemName + " with actions " + Arrays.toString(actionNames);
        Tab.switchToInventoryTab();
        Widget item = findItem(itemName);
        return Rs2Menu.doAction(actionNames, new Point((int) item.getBounds().getCenterX(), (int) item.getBounds().getCenterY()));
    }

    public static boolean useItemAction(int id, String[] actionNames) {
        Microbot.status = "Use inventory item " + id + " with actions " + Arrays.toString(actionNames);
        Tab.switchToInventoryTab();
        Widget item = findItem(id);
        if (item == null) return false;
        return Rs2Menu.doAction(actionNames, new Point((int) item.getBounds().getCenterX(), (int) item.getBounds().getCenterY()));
    }

    public static boolean dropAll() {
        if (!Rs2Settings.enableDropShiftSetting()) return false;
        if (Inventory.isEmpty()) return true;
        Microbot.pauseAllScripts = true;
        for (int i = 0; i < 28; i++) {
            if (!VirtualKeyboard.isKeyPressed(KeyEvent.VK_SHIFT) || !Rs2Menu.hasAction("drop"))
                VirtualKeyboard.holdShift();
            Inventory.useItemSlot(i);
            sleep(150, 300);
        }
        Microbot.pauseAllScripts = false;
        VirtualKeyboard.releaseShift();
        return isEmpty();
    }

    // First inventory slot is 0
    public static boolean dropAllStartingFrom(int slot) {
        if (!Rs2Settings.enableDropShiftSetting()) return false;
        if (Inventory.isEmpty()) return true;
        Microbot.pauseAllScripts = true;
        for (int i = slot; i < 28; i++) {
            if (!VirtualKeyboard.isKeyPressed(KeyEvent.VK_SHIFT) || !Rs2Menu.hasAction("drop"))
                VirtualKeyboard.holdShift();
            Inventory.useItemSlot(i);
            sleep(150, 300);
        }
        Microbot.pauseAllScripts = false;
        VirtualKeyboard.releaseShift();
        return true;
    }

    public static boolean drop(String itemName) {
        if (!Rs2Settings.enableDropShiftSetting()) return false;
        if (Inventory.isEmpty()) return true;
        if (!VirtualKeyboard.isKeyPressed(KeyEvent.VK_SHIFT) || !Rs2Menu.hasAction("drop"))
            VirtualKeyboard.holdShift();

        boolean result = useItemAction(itemName, "drop");

        VirtualKeyboard.releaseShift();

        return result;
    }

    public static boolean dropAll(String itemName) {
        if (!Rs2Settings.enableDropShiftSetting()) return false;
        if (Inventory.isEmpty()) return true;
        while (hasItem(itemName)) {
            if (!VirtualKeyboard.isKeyPressed(KeyEvent.VK_SHIFT) || !Rs2Menu.hasAction("drop"))
                VirtualKeyboard.holdShift();
            useItemAction(itemName, "drop");
            sleep(150, 300);
        }
        VirtualKeyboard.releaseShift();

        return !hasItem(itemName);
    }

    public static boolean isUsingItem() {
        return Arrays.stream(Rs2Menu.getTargets()).anyMatch(x -> x.contains("->"));
    }

    public static boolean eatItem(String itemName) {
        return useItemAction(itemName, "eat");
    }

    public static long getAmountForItem(String itemName) {
        Microbot.status = "getAmountForItem: " + itemName;
        Tab.switchToInventoryTab();
        Widget inventoryWidget = getInventory();
        if (inventoryWidget == null) return 0;
        return Microbot.getClientThread().runOnClientThread(() -> Arrays.stream(inventoryWidget.getDynamicChildren())
                .filter(x ->
                        itemExistsInInventory(x) && x.getName().split(">")[1].split("</")[0].toLowerCase().contains(itemName.toLowerCase())
                ).count());
    }

    private static void useItemFastAbstract(Rs2Item rs2Item, String action) {
        if (rs2Item == null) return;
        item = rs2Item;
        itemAction = action;
        Microbot.getMouse().clickFast(1, 1);
        sleep(100);
        item = null;
        itemAction = "";
    }

    public static void useItemFast(int id, String action) {
        Rs2Item item = findItemFast(id);
        useItemFastAbstract(item, action);
    }

    public static void useItemFast(String name, String action) {
        Rs2Item item = findItemFast(name);
        useItemFastAbstract(item, action);
    }

    public static void useItemFastContains(String name, String action) {
        Rs2Item item = findItemFast(name, true);
        useItemFastAbstract(item, action);
    }

    public static void useAllItemsFastContains(String name, String action) {
        List<Rs2Item> rs2Items = findAllItemFast(name, true);
        for (Rs2Item rs2Item : rs2Items) {
            useItemFastAbstract(rs2Item, action);
        }
    }

    public static Rs2Item findItemFast(int id) {
        Microbot.status = "Searching inventory for item with id: " + id;
        ItemContainer itemContainer = Microbot.getClient().getItemContainer(InventoryID.INVENTORY);
        if (itemContainer == null) return null;
        return Microbot.getClientThread().runOnClientThread(() -> {
            for (int i = 0; i < itemContainer.getItems().length; i++) {
                Item item = itemContainer.getItems()[i];
                ItemComposition itemComposition = Microbot.getClient().getItemDefinition(itemContainer.getItems()[i].getId());
                if (itemComposition.getId() == id) {
                    return new Rs2Item(item.getId(), item.getQuantity(), itemComposition.getName(), i);
                }
            }
            return null;
        });
    }

    public static Rs2Item findItemFast(String itemName, boolean contains) {
        Microbot.status = "Searching inventory for item with name: " + itemName;
        ItemContainer itemContainer = Microbot.getClient().getItemContainer(InventoryID.INVENTORY);
        if (itemContainer == null) return null;
        return Microbot.getClientThread().runOnClientThread(() -> {
            for (int i = 0; i < itemContainer.getItems().length; i++) {
                Item item = itemContainer.getItems()[i];
                ItemComposition itemComposition = Microbot.getClient().getItemDefinition(itemContainer.getItems()[i].getId());
                if (contains) {
                    if (itemComposition.getName().toLowerCase().contains(itemName.toLowerCase())) {
                        return new Rs2Item(item.getId(), item.getQuantity(), itemComposition.getName(), i);
                    }
                } else {
                    if (itemComposition.getName().equalsIgnoreCase(itemName.toLowerCase())) {
                        return new Rs2Item(item.getId(), item.getQuantity(), itemComposition.getName(), i);
                    }
                }

            }
            return null;
        });
    }

    public static List<Rs2Item> findAllItemFast(String itemName, boolean contains) {
        Microbot.status = "Searching inventory for item with name: " + itemName;
        ItemContainer itemContainer = Microbot.getClient().getItemContainer(InventoryID.INVENTORY);
        if (itemContainer == null) return null;
        return Microbot.getClientThread().runOnClientThread(() -> {
            List<Rs2Item> rs2Items = new ArrayList<>();
            for (int i = 0; i < itemContainer.getItems().length; i++) {
                Item item = itemContainer.getItems()[i];
                ItemComposition itemComposition = Microbot.getClient().getItemDefinition(itemContainer.getItems()[i].getId());
                if (contains) {
                    if (itemComposition.getName().toLowerCase().contains(itemName.toLowerCase())) {
                        rs2Items.add(new Rs2Item(item.getId(), item.getQuantity(), itemComposition.getName(), i));
                    }
                } else {
                    if (itemComposition.getName().equalsIgnoreCase(itemName.toLowerCase())) {
                        rs2Items.add(new Rs2Item(item.getId(), item.getQuantity(), itemComposition.getName(), i));
                    }
                }

            }
            return rs2Items;
        });
    }

    public static Rs2Item findItemFast(String itemName) {
        return findItemFast(itemName, false);
    }

    public static void handleMenuSwapper(MenuEntry menuEntry) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        if (item == null) return;
        try {
            ItemComposition itemComposition = Microbot.getClient().getItemDefinition(item.id);
            int index = 0;


            Rs2Reflection.setItemId(menuEntry, item.id);

            if (itemAction.equalsIgnoreCase("use")) {
                menuEntry.setType(MenuAction.WIDGET_TARGET);
            } else if(itemComposition.getName().contains("pouch") && itemAction.equalsIgnoreCase("empty")) {
                index = 1;
                menuEntry.setType(MenuAction.CC_OP);
            } else if (itemAction.equalsIgnoreCase("drink")
                    || itemAction.equalsIgnoreCase("read")
                    || itemAction.equalsIgnoreCase("eat")
                    || itemAction.equalsIgnoreCase("view")
                    || itemAction.equalsIgnoreCase("bury")) {
                index = 2;
                menuEntry.setType(MenuAction.CC_OP);
            } else if (itemAction.equalsIgnoreCase("wield")
                    || itemAction.equalsIgnoreCase("wear")
                    || itemAction.equalsIgnoreCase("check steps")) {
                index = 3;
                menuEntry.setType(MenuAction.CC_OP);
            } else if (itemAction.equalsIgnoreCase("fill")) {
                index = 4;
                menuEntry.setType(MenuAction.CC_OP);
            } else if (itemAction.equalsIgnoreCase("empty") || itemAction.equalsIgnoreCase("rub")
                    || itemAction.equalsIgnoreCase("refund") || itemAction.equalsIgnoreCase("commune")
                    || itemAction.equalsIgnoreCase("extinguish")) {
                index = 6;
                menuEntry.setType(MenuAction.CC_OP);
            } else if (itemAction.equalsIgnoreCase("drop") || itemAction.equalsIgnoreCase("destroy")) {
                index = 7;
                menuEntry.setType(MenuAction.CC_OP);
            } else if (itemAction.equalsIgnoreCase("examine")) {
                index = 10;
                menuEntry.setType(MenuAction.CC_OP);
            }


            menuEntry.setOption(itemAction != null ? itemAction : "");
            menuEntry.setIdentifier(index);
            menuEntry.setParam0(item.slot);
            menuEntry.setParam1(9764864);
            menuEntry.setTarget("<col=ff9040>" + itemComposition.getName() + "</col>");
        } catch(Exception ex) {
            System.out.println("INVENTORY MENU SWAP FAILED WITH MESSAGE: " + ex.getMessage());
        }

    }
}
