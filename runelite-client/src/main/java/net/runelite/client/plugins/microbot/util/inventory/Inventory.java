package net.runelite.client.plugins.microbot.util.inventory;

import net.runelite.api.*;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.widget.models.ItemWidget;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.globval.enums.InterfaceTab;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.menu.Rs2Menu;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.reflection.Rs2Reflection;
import net.runelite.client.plugins.microbot.util.settings.Rs2Settings;
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import org.apache.commons.lang3.ArrayUtils;

import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static net.runelite.client.plugins.microbot.util.Global.*;
import static net.runelite.client.plugins.microbot.util.npc.Rs2Npc.getNpc;


public class Inventory {
    public static CopyOnWriteArrayList<Widget> inventoryItems = new CopyOnWriteArrayList<>();

    public static void eat(Widget widget) {
        Microbot.getMouse().click(widget.getBounds());
        sleep(1200, 2000);
    }

    private static boolean itemExistsInInventory(Widget item) {
        return item != null && !item.getName().isEmpty() && !item.isHidden() && item.getOpacity() != 255 && !item.isSelfHidden();
    }

    public static void open() {
        Microbot.status = "Open inventory";
        Rs2Tab.switchToInventoryTab();
        sleep(300, 1200);
        sleepUntilOnClientThread(() -> Rs2Tab.getCurrentTab() == InterfaceTab.INVENTORY);
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
        Rs2Tab.switchToInventoryTab();
        Widget inventoryWidget = getInventory();
        if (inventoryWidget == null) return false;
        return Microbot.getClientThread().runOnClientThread(() -> Arrays.stream(inventoryWidget.getDynamicChildren()).filter(x -> itemExistsInInventory(x)).count() == 28);
    }

    @Deprecated(since = "Use isFull method instead", forRemoval = true)
    public static boolean isInventoryFull() {
        Microbot.status = "Checking if inventory is full";
        Rs2Tab.switchToInventoryTab();
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

    public static long getItemAmount(String itemName) {
        Microbot.status = "Looking for item: " + itemName;
        Widget inventoryWidget = getInventory();
        if (inventoryWidget == null) return 0;
        return Microbot.getClientThread().runOnClientThread(() -> Arrays.stream(inventoryWidget.getDynamicChildren())
                .filter(x ->
                        itemExistsInInventory(x) && x.getName().split(">")[1].split("</")[0].toLowerCase().contains(itemName.toLowerCase())
                ).count());
    }

    public static boolean isInventoryFull(String itemName) {
        return Microbot.getClientThread().runOnClientThread(() -> {
            Widget inventoryWidget = getInventory();
            if (inventoryWidget == null) return false;
            if (hasItemStackable(itemName)) return false;
            return Microbot.getClientThread().runOnClientThread(() -> Arrays.stream(inventoryWidget.getDynamicChildren()).filter(Inventory::itemExistsInInventory).count() == 28);
        });
    }

    public static boolean isInventoryFull(int itemId) {
        return Microbot.getClientThread().runOnClientThread(() -> {
            Widget inventoryWidget = getInventory();
            if (inventoryWidget == null) return false;
            if (hasItemStackable(itemId)) return false;
            return Microbot.getClientThread().runOnClientThread(() -> Arrays.stream(inventoryWidget.getDynamicChildren()).filter(Inventory::itemExistsInInventory).count() == 28);
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
        return Microbot.getClientThread().runOnClientThread(() -> Arrays.stream(
                inventoryWidget.getDynamicChildren())
                .anyMatch(x -> {
                    ItemComposition itemComp = Microbot.getItemManager().getItemComposition(x.getItemId());
                    if (itemComp.getNote() == 799 || itemComp.isStackable()) {
                        return itemExistsInInventory(x) && x.getName().split(">")[1].split("</")[0].equalsIgnoreCase(itemName);
                    } else {
                        return false;
                    }
                })
        );
    }

    public static boolean hasItemStackable(int itemId) {
        Microbot.status = "Checking inventory has stackable item " + itemId;
        Widget inventoryWidget = getInventory();
        if (inventoryWidget == null) return false;
        return Microbot.getClientThread().runOnClientThread(() -> Arrays.stream(inventoryWidget.getDynamicChildren())
                .anyMatch(x -> {
                    ItemComposition itemComp = Microbot.getItemManager().getItemComposition(x.getItemId());
                    if (itemComp.getNote() == 799 || itemComp.isStackable()) {
                        return itemExistsInInventory(x) && x.getItemId() == itemId;
                    } else {
                        return false;
                    }
                })
        );
    }

    public static boolean hasItem(String itemName) {
        Microbot.status = "Looking for item: " + itemName;
        Rs2Tab.switchToInventoryTab();
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
        return Microbot.getClientThread().runOnClientThread(() -> Arrays.stream(inventoryWidget.getDynamicChildren()).filter(x -> itemExistsInInventory(x) && x.getName().toLowerCase().contains(itemName)).findFirst().get());
    }

    public static Widget getInventoryItem(int itemId) {
        Microbot.status = "Searching inventory item ID:" + itemId;
        Widget inventoryWidget = getInventory();
        if (inventoryWidget == null) return null;
        return Microbot.getClientThread().runOnClientThread(() -> Arrays.stream(inventoryWidget.getDynamicChildren()).filter(x -> itemExistsInInventory(x) && x.getItemId() == itemId).findFirst().get());
    }

    public static Widget[] getInventoryItems() {
        Microbot.status = "Fetching inventory items";
        Widget inventoryWidget = getInventory();
        if (inventoryWidget == null) return null;
        return Microbot.getClientThread().runOnClientThread(() -> Arrays.stream(inventoryWidget.getDynamicChildren()).filter(Inventory::itemExistsInInventory).toArray(Widget[]::new));
    }

    public static Widget[] getInventoryFood() {
        Microbot.status = "Fetching inventory food";
        Inventory.open();
        Widget inventoryWidget = getInventory();
        if (inventoryWidget == null) return null;
        return Microbot.getClientThread().runOnClientThread(() -> {
            Widget[] items = Arrays.stream(inventoryWidget.getDynamicChildren()).filter(x -> itemExistsInInventory(x)).toArray(Widget[]::new);
            items = Arrays.stream(items).filter(x -> Arrays.stream(x.getActions()).anyMatch(c -> c != null && c.toLowerCase().equals("eat"))).toArray(Widget[]::new);
            return items;
        });
    }

    public static Widget[] getPotions() {
        Microbot.status = "Fetching inventory potions";
        Widget inventoryWidget = getInventory();
        if (inventoryWidget == null) return null;
        return Microbot.getClientThread().runOnClientThread(() -> {
            Widget[] items = Arrays.stream(inventoryWidget.getDynamicChildren()).filter(x -> itemExistsInInventory(x)).toArray(Widget[]::new);
            items = Arrays.stream(items).filter(x -> Arrays.stream(x.getActions()).anyMatch(c -> c != null && c.toLowerCase().equals("drink"))).toArray(Widget[]::new);
            return items;
        });
    }

    public static Widget findItemSlot(int index) {
        Microbot.status = "Searching inventory slot " + index;
        Widget inventoryWidget = getInventory();
        if (inventoryWidget == null) return null;
        return Microbot.getClientThread().runOnClientThread(() -> inventoryWidget.getChild(index));
    }

    public static Widget findItem(int itemId) {
        Microbot.status = "Searching inventory item with id " + itemId;
        Widget inventoryWidget = getInventory();
        if (inventoryWidget == null) return null;
        return Microbot.getClientThread().runOnClientThread(() -> {
            for (Widget item : inventoryWidget.getDynamicChildren()) {
                if (item.getItemId() == itemId) {
                    return item;
                }
            }
            return null;
        });
    }
    public static Widget findLastItem(int itemId) {
        Microbot.status = "Searching inventory for last item with id " + itemId;
        Widget inventoryWidget = getInventory();
        if (inventoryWidget == null) return null;
        return Microbot.getClientThread().runOnClientThread(() -> {
            Widget lastItem = null;
            for (Widget item : inventoryWidget.getDynamicChildren()) {
                if (item.getItemId() == itemId) {
                    lastItem = item;
                }
            }
            return lastItem;
        });
    }

    public static boolean hasItemAmount(int itemId, int amount) {
        Microbot.status = "Check if inventory has item: " + itemId + " with amount: " + amount;
        Rs2Tab.switchToInventoryTab();
        Widget inventoryWidget = getInventory();
        if (inventoryWidget == null) return false;
        return Microbot.getClientThread().runOnClientThread(() -> Arrays.stream(inventoryWidget.getDynamicChildren())
                .filter(x ->
                        itemExistsInInventory(x) && x.getItemId() == itemId
                ).count() >= amount);
    }

    public static boolean hasItemAmount(String itemName, int amount) {
        Microbot.status = "Check if inventory has item: " + itemName + " with amount: " + amount;
        Rs2Tab.switchToInventoryTab();
        Widget inventoryWidget = getInventory();
        if (inventoryWidget == null) return false;
        return Microbot.getClientThread().runOnClientThread(() -> Arrays.stream(inventoryWidget.getDynamicChildren())
                .filter(x ->
                        itemExistsInInventory(x) && x.getName().split(">")[1].split("</")[0].toLowerCase().contains(itemName.toLowerCase())
                ).count() >= amount);
    }

    public static boolean hasItemAmountExact(String itemName, int amount) {
        Microbot.status = "Check if inventory has item: " + itemName + " with amount: " + amount;
        Rs2Tab.switchToInventoryTab();
        Widget inventoryWidget = getInventory();
        if (inventoryWidget == null) return false;
        return Microbot.getClientThread().runOnClientThread(() -> Arrays.stream(inventoryWidget.getDynamicChildren())
                .filter(x ->
                        itemExistsInInventory(x) && x.getName().split(">")[1].split("</")[0].equalsIgnoreCase(itemName.toLowerCase())
                ).count() == amount);
    }


    public static boolean hasItemAmountStackable(String itemName, int amount) {
        Microbot.status = "Check if inventory has item: " + itemName + " with amount: " + amount;
        Rs2Tab.switchToInventoryTab();
        Widget inventoryWidget = getInventory();
        if (inventoryWidget == null) return false;
        return Microbot.getClientThread().runOnClientThread(() -> Arrays.stream(inventoryWidget.getDynamicChildren())
                .anyMatch(x ->
                        itemExistsInInventory(x) &&
                                x.getName()
                                        .split(">")[1]
                                        .split("</")[0]
                                        .equalsIgnoreCase(itemName.toLowerCase()) &&
                                x.getItemQuantity() >= amount
                ));
    }

    private static Widget getInventory() {
        return Microbot.getClientThread().runOnClientThread(() -> {
            Widget inventoryWidget = Microbot.getClient().getWidget(9764864);
            Widget bankInventoryWidget = Microbot.getClient().getWidget(983043);
            Widget bankPinInventoryWidget = Microbot.getClient().getWidget(17563648);
            return Microbot.getClientThread().runOnClientThread(() -> {
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
        });
    }

    public static Widget findItem(String itemName, boolean exact) {
        Microbot.status = "Searching inventory for item: " + itemName;
        Rs2Tab.switchToInventoryTab();
        sleepUntil(() -> Rs2Tab.getCurrentTab() == InterfaceTab.INVENTORY);
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

    public static Widget findItemLast(int itemId) {
        Microbot.status = "Searching inventory for last item with id " + itemId;
        Widget inventoryWidget = getInventory();
        if (inventoryWidget == null) return null;
        return Microbot.getClientThread().runOnClientThread(() -> {
            Widget[] children = inventoryWidget.getDynamicChildren();
            ArrayUtils.reverse(children);
            for (Widget item : children) {
                if (item.getItemId() == itemId) {
                    return item;
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
        return useItem(itemName, false);
    }

    public static boolean useItem(String itemName, boolean exact) {
        if (Rs2Bank.isOpen()) return false;
        Microbot.status = "Use inventory item " + itemName;
        Widget item = findItem(itemName, exact);
        if (item == null) return false;
        Microbot.getMouse().click(item.getBounds().getCenterX(), item.getBounds().getCenterY());
        sleep(100, 300);
        return true;
    }

    public static boolean useItemLast(int id) {
        if (Rs2Bank.isOpen()) return false;
        Microbot.status = "Use inventory item " + id;
        Widget item = findItemLast(id);
        if (item == null) return false;
        Microbot.getMouse().click(item.getBounds().getCenterX(), item.getBounds().getCenterY());
        sleep(600, 1200);
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
    public static boolean useItemOnObject(int item, int objectID) {
        if (Rs2Bank.isOpen()) return false;
        Widget item1 = findItem(item);
        TileObject object = Rs2GameObject.findObjectById(objectID);
        if (item1 == null || object == null) return false;
        Microbot.getMouse().click(item1.getBounds().getCenterX(), item1.getBounds().getCenterY());
        sleep(600, 1200);
        Microbot.getMouse().click(object.getCanvasLocation().getX(), object.getCanvasLocation().getY());
        sleep(600, 1200);
        return true;
    }
    public static boolean useItemOnObjectFast(int item, int objectID) {
        if (Rs2Bank.isOpen()) return false;
        Widget item1 = findItem(item);
        TileObject object = Rs2GameObject.findObjectById(objectID);
        if (item1 == null || object == null) return false;
        Microbot.getMouse().click(item1.getBounds().getCenterX(), item1.getBounds().getCenterY());
        sleep(0, 200);
        Microbot.getMouse().click(object.getCanvasLocation().getX(), object.getCanvasLocation().getY());
        sleep(0, 200);
        return true;
    }

    public static boolean useItemOnNpc(String itemName, String npcName) {
        if (Rs2Bank.isOpen()) return false;
        Microbot.status = "Use inventory item " + itemName + " on " + npcName;
        Widget item1 = findItem(itemName);
        NPC item2 = getNpc(npcName);
        if (item1 == null || item2 == null) return false;
        Microbot.getMouse().click(item1.getBounds().getCenterX(), item1.getBounds().getCenterY());
        sleep(600, 1200);
        Rs2Npc.interact(npcName,"use");
        sleep(600, 1200);
        return true;
    }

    public static boolean useItemSafe(String itemName) {
        if (Rs2Bank.isOpen()) return false;
        Microbot.status = "Use inventory item safe " + itemName;
        Rs2Tab.switchToInventoryTab();
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
    public static boolean useLastItemAction(int itemID, String actionName) {
        Microbot.status = "Use inventory item " + itemID + " with action " + actionName;
        Widget item = findLastItem(itemID);
        if (item == null) return false;
        return Rs2Menu.doAction(actionName, new Point((int) item.getBounds().getCenterX(), (int) item.getBounds().getCenterY()));
    }

    public static boolean useItemAction(String itemName, String[] actionNames) {
        Microbot.status = "Use inventory item " + itemName + " with actions " + Arrays.toString(actionNames);
        Rs2Tab.switchToInventoryTab();
        Widget item = findItem(itemName);
        return Rs2Menu.doAction(actionNames, new Point((int) item.getBounds().getCenterX(), (int) item.getBounds().getCenterY()));
    }

    public static boolean useItemAction(int id, String[] actionNames) {
        Microbot.status = "Use inventory item " + id + " with actions " + Arrays.toString(actionNames);
        Rs2Tab.switchToInventoryTab();
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

    public static boolean dropAll(int itemId) {
        if (!Rs2Settings.enableDropShiftSetting()) return false;
        if (Inventory.isEmpty()) return true;
        while (hasItem(itemId)) {
            if (!VirtualKeyboard.isKeyPressed(KeyEvent.VK_SHIFT) || !Rs2Menu.hasAction("drop"))
                VirtualKeyboard.holdShift();
            useItemAction(itemId, "drop");
            sleep(150, 300);
        }
        VirtualKeyboard.releaseShift();

        return !hasItem(itemId);
    }

    public static boolean isUsingItem() {
        return Arrays.stream(Rs2Menu.getTargets()).anyMatch(x -> x.contains("->"));
    }

    public static boolean eatItem(String itemName) {
        return useItemAction(itemName, "eat");
    }

    public static long getAmountForItem(String itemName) {
        Microbot.status = "getAmountForItem: " + itemName;
        Rs2Tab.switchToInventoryTab();
        Widget inventoryWidget = getInventory();
        if (inventoryWidget == null) return 0;
        return Microbot.getClientThread().runOnClientThread(() -> Arrays.stream(inventoryWidget.getDynamicChildren())
                .filter(x ->
                        itemExistsInInventory(x) && x.getName().split(">")[1].split("</")[0].toLowerCase().contains(itemName.toLowerCase())
                ).count());
    }

    private static void useItemFastAbstract(Rs2Item rs2Item, String action) {
        if (rs2Item == null) return;

        Widget itemWidget = Rs2Widget.getWidget(rs2Item.id);

        int param0;
        int param1;
        int identifier;
        String option = "";
        String target = "";
        MenuAction menuAction = MenuAction.WALK;
        ItemComposition itemComposition = Microbot.getClient().getItemDefinition(rs2Item.id);
        int index = 0;


        if (Microbot.getClient().isWidgetSelected()) {
            menuAction = MenuAction.WIDGET_TARGET_ON_WIDGET;
        } else if (action.equalsIgnoreCase("use")) {
            menuAction = MenuAction.WIDGET_TARGET;
        } else if (action.equalsIgnoreCase("cast")) {
            menuAction = MenuAction.WIDGET_TARGET_ON_WIDGET;
        } else if(itemComposition.getName().contains("pouch") && action.equalsIgnoreCase("empty")) {
            index = 1;
            menuAction = MenuAction.CC_OP;
        } else if (action.equalsIgnoreCase("drink")
                || action.equalsIgnoreCase("read")
                || action.equalsIgnoreCase("eat")
                || action.equalsIgnoreCase("view")
                || action.equalsIgnoreCase("bury")
                || action.equalsIgnoreCase("feel")) {
            index = 2;
            menuAction = MenuAction.CC_OP;
        } else if (action.equalsIgnoreCase("wield")
                || action.equalsIgnoreCase("wear")
                || action.equalsIgnoreCase("check steps")) {
            index = 3;
            menuAction = MenuAction.CC_OP;
        } else if (action.equalsIgnoreCase("fill")) {
            index = 4;
            menuAction = MenuAction.CC_OP;
        } else if (action.equalsIgnoreCase("empty") || action.equalsIgnoreCase("rub")
                || action.equalsIgnoreCase("refund") || action.equalsIgnoreCase("commune")
                || action.equalsIgnoreCase("extinguish")
                || (action.equalsIgnoreCase("check") && rs2Item.id == ItemID.GRICOLLERS_CAN)) {
            index = 6;
            menuAction = MenuAction.CC_OP;
        } else if (action.equalsIgnoreCase("drop") || action.equalsIgnoreCase("destroy")) {
            index = 7;
            menuAction = MenuAction.CC_OP;
        } else if (action.equalsIgnoreCase("examine")) {
            index = 10;
            menuAction = MenuAction.CC_OP;
        }

        option = action != null ? action : "";
        identifier = index;
        param0 = rs2Item.slot;
        param1 = 9764864;
        target = "<col=ff9040>" + itemComposition.getName() + "</col>";


        //grandexchange inventory
        if (action.equalsIgnoreCase("offer")) {
            identifier = 1;
            param1 = 30605312;
        }

        Rs2Reflection.invokeMenu(param0, param1, menuAction.getId(), identifier, rs2Item.id, option, target, -1, -1);
    }

    public static void useItemFast(Widget item, String action) {
        Rs2Item rs2Item = new Rs2Item(item.getItemId(), item.getItemQuantity(), item.getName(), item.getIndex());
        useItemFastAbstract(rs2Item, action);
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

    public static void storeInventoryItemsInMemory(ItemContainerChanged e) {
        if (e.getContainerId() == 93) {
            int i = 0;
            inventoryItems.clear();
            for (Item item : e.getItemContainer().getItems()) {
                if (item == null) {
                    i++;
                    continue;
                }
                inventoryItems.add(new ItemWidget(Microbot.getItemManager().getItemComposition(item.getId()).getName(), item.getId(), item.getQuantity(), i));
                i++;
            }
        }
    }

    public static Widget findItemInMemory(String itemName, boolean exact) {
        Microbot.status = "Searching inventory for item: " + itemName;
        Rs2Tab.switchToInventoryTab();
        return inventoryItems
                .stream()
                .filter(x -> exact ? x.getName().equalsIgnoreCase(itemName) : x.getName().toLowerCase().contains(itemName.toLowerCase()))
                .findFirst().orElse(null);
    }
    public static Widget findItemInMemory(String itemName) {
       return findItemInMemory(itemName, false);
    }

}
