package net.runelite.client.plugins.microbot.util.inventory;

import net.runelite.api.MenuEntry;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.widgets.Widget;

import java.lang.reflect.InvocationTargetException;
import java.util.List;


@Deprecated(since = "Use Rs2Inventory instead", forRemoval = true)
public class Inventory {

    public static void eat(Widget widget) {
        Rs2Inventory.eat(widget);
    }

    public static void open() {
        Rs2Inventory.open();
    }

    public static boolean clickItem(int slot) {
        return Rs2Inventory.clickItem(slot);
    }

    public static boolean isFull() {
        return Rs2Inventory.isFull();
    }

    @Deprecated(since = "Use isFull method instead", forRemoval = true)
    public static boolean isInventoryFull() {
        return Rs2Inventory.isFull();
    }

    public static long count() {
        return Rs2Inventory.count();
    }

    public static int getItemAmount(int id) {
        return Rs2Inventory.getItemAmount(id);
    }

    public static boolean isInventoryFull(String itemName) {
        return Rs2Inventory.isInventoryFull(itemName);
    }

    public static boolean isInventoryFull(int itemId) {
        return Rs2Inventory.isInventoryFull(itemId);
    }

    public static boolean isEmpty() {
        return Rs2Inventory.isEmpty();
    }

    public static boolean hasAmountInventoryItems(int count) {
        return Rs2Inventory.hasAmountInventoryItems(count);
    }

    public static boolean hasItemStackable(String itemName) {
        return Rs2Inventory.hasItemStackable(itemName);
    }

    public static boolean hasItemStackable(int itemId) {
        return Rs2Inventory.hasItemStackable(itemId);
    }

    public static boolean hasItem(String itemName) {
        return Rs2Inventory.hasItem(itemName);
    }

    public static boolean contains(String itemName) {
        return Rs2Inventory.hasItem(itemName);
    }

    public static boolean hasItem(int id) {
        return Rs2Inventory.hasItem(id);
    }

    public static boolean hasItemContains(String itemName) {
        return Rs2Inventory.hasItemContains(itemName);
    }

    public static Widget getInventoryItem(String itemName) {
        return Rs2Inventory.getInventoryItem(itemName);
    }

    public static Widget getInventoryItem(int itemId) {
        return Rs2Inventory.getInventoryItem(itemId);
    }

    public static Widget[] getInventoryItems() {
        return Rs2Inventory.getInventoryItems();
    }

    public static Widget[] getInventoryFood() {
        return Rs2Inventory.getInventoryFood();
    }

    public static Widget[] getPotions() {
        return Rs2Inventory.getPotions();
    }

    public static Widget findItemSlot(int index) {
        return Rs2Inventory.findItemSlot(index);
    }

    public static Widget findItem(int itemId) {
        return Rs2Inventory.findItem(itemId);
    }

    public static Widget findLastItem(int itemId) {
        return Rs2Inventory.findLastItem(itemId);
    }

    public static boolean hasItemAmount(int itemId, int amount) {
        return Rs2Inventory.hasItemAmount(itemId, amount);
    }

    public static boolean hasItemAmount(String itemName, int amount) {
        return Rs2Inventory.hasItemAmount(itemName, amount);
    }

    public static boolean hasItemAmountExact(String itemName, int amount) {
        return Rs2Inventory.hasItemAmountExact(itemName, amount);
    }


    public static boolean hasItemAmountStackable(String itemName, int amount) {
        return Rs2Inventory.hasItemAmountStackable(itemName, amount);
    }

    public static Widget findItem(String itemName, boolean exact) {
        return Rs2Inventory.findItem(itemName, exact);
    }

    public static Widget findItemLast(int itemId) {
        return Rs2Inventory.findItemLast(itemId);
    }

    public static Widget findItem(String itemName) {
        return Rs2Inventory.findItem(itemName);
    }

    public static void useItemOnItemSlot(int slot1, int slot2) {
        useItemOnItemSlot(slot1, slot2, 600, 1200);
    }

    public static void useItemOnItemSlot(int slot1, int slot2, int minWait, int maxWait) {
        Rs2Inventory.useItemOnItemSlot(slot1, slot2, minWait, maxWait);
    }

    public static boolean useItemSlot(int slot) {
        return Rs2Inventory.useItemSlot(slot);
    }

    public static boolean useItemContains(String itemName) {
        return Rs2Inventory.useItemContains(itemName);
    }

    public static boolean useItem(String itemName) {
        return Rs2Inventory.useItem(itemName);
    }

    public static boolean useItemLast(int id) {
        return Rs2Inventory.useItemLast(id);
    }

    public static boolean useItemUnsafe(String itemName) {
        return Rs2Inventory.useItemUnsafe(itemName);
    }

    public static boolean useItem(int id) {
        return Rs2Inventory.useItem(id);
    }

    public static boolean interact(String itemName) {
        return Rs2Inventory.interact(itemName);
    }

    public static boolean interact(String... itemNames) {
        return Rs2Inventory.interact(itemNames);
    }

    public static boolean interactContains(String itemName) {
        return Rs2Inventory.interactContains(itemName);
    }

    public static boolean interactItemContains(String... itemNames) {
        return Rs2Inventory.interactItemContains(itemNames);
    }

    public static boolean useItemOnItem(String itemName1, String itemName2) {
        return Rs2Inventory.useItemOnItem(itemName1, itemName2);
    }

    public static boolean useItemOnObject(int item, int objectID) {
        return Rs2Inventory.useItemOnObject(item, objectID);
    }

    public static boolean useItemOnObjectFast(int item, int objectID) {
        return Rs2Inventory.useItemOnObjectFast(item, objectID);
    }

    public static boolean useItemSafe(String itemName) {
        return Rs2Inventory.useItemSafe(itemName);
    }

    public static boolean useItemAction(String itemName, String actionName) {
        return Rs2Inventory.useItemAction(itemName, actionName);
    }

    public static boolean useItemActionContains(String itemName, String actionName) {
        return Rs2Inventory.useItemActionContains(itemName, actionName);
    }

    public static boolean useItemAction(int itemID, String actionName) {
        return Rs2Inventory.useItemAction(itemID, actionName);
    }

    public static boolean useLastItemAction(int itemID, String actionName) {
        return Rs2Inventory.useLastItemAction(itemID, actionName);
    }

    public static boolean useItemAction(String itemName, String[] actionNames) {
        return Rs2Inventory.useItemAction(itemName, actionNames);
    }

    public static boolean useItemAction(int id, String[] actionNames) {
        return Rs2Inventory.useItemAction(id, actionNames);
    }

    public static boolean dropAll() {
        return Rs2Inventory.dropAll();
    }

    // First inventory slot is 0
    public static boolean dropAllStartingFrom(int slot) {
        return Rs2Inventory.dropAllStartingFrom(slot);
    }

    public static boolean drop(String itemName) {
        return Rs2Inventory.drop(itemName);
    }

    public static boolean dropAll(String itemName) {
        return Rs2Inventory.dropAll(itemName);
    }

    public static boolean isUsingItem() {
        return Rs2Inventory.isUsingItem();
    }

    public static boolean eatItem(String itemName) {
        return Rs2Inventory.eatItem(itemName);
    }

    public static long getAmountForItem(String itemName) {
        return Rs2Inventory.getAmountForItem(itemName);
    }

    public static void useItemFast(Widget item, String action) {
        Rs2Inventory.useItemFast(item, action);
    }

    public static void useItemFast(int id, String action) {
        Rs2Inventory.useItemFast(id, action);
    }

    public static void useItemFast(String name, String action) {
        Rs2Inventory.useItemFast(name, action);
    }

    public static void useItemFastContains(String name, String action) {
        Rs2Inventory.useItemFastContains(name, action);
    }

    public static void useAllItemsFastContains(String name, String action) {
        Rs2Inventory.useAllItemsFastContains(name, action);
    }

    public static Rs2Item findItemFast(int id) {
        return Rs2Inventory.findItemFast(id);
    }

    public static Rs2Item findItemFast(String itemName, boolean contains) {
        return Rs2Inventory.findItemFast(itemName, contains);
    }

    public static List<Rs2Item> findAllItemFast(String itemName, boolean contains) {
        return Rs2Inventory.findAllItemFast(itemName, contains);
    }

    public static Rs2Item findItemFast(String itemName) {
        return Rs2Inventory.findItemFast(itemName);
    }

    public static void handleMenuSwapper(MenuEntry menuEntry) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Rs2Inventory.handleMenuSwapper(menuEntry);
    }

    public static void storeInventoryItemsInMemory(ItemContainerChanged e) {
        Rs2Inventory.storeInventoryItemsInMemory(e);
    }

    public static Widget findItemInMemory(String itemName, boolean exact) {
        return Rs2Inventory.findItemInMemory(itemName, exact);
    }

    public static Widget findItemInMemory(String itemName) {
        return Rs2Inventory.findItemInMemory(itemName);
    }

}
