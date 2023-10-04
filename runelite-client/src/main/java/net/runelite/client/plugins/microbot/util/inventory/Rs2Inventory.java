package net.runelite.client.plugins.microbot.util.inventory;

import net.runelite.api.*;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.globval.enums.InterfaceTab;
import net.runelite.client.plugins.microbot.util.reflection.Rs2Reflection;
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import org.apache.commons.lang3.NotImplementedException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static net.runelite.client.plugins.microbot.util.Global.sleep;

@Deprecated
public class Rs2Inventory {

    public static Rs2Item item;
    public static String itemAction;

    // The maximum capacity of the inventory
    private static final int CAPACITY = 28;

    public static ItemContainer inventory() {
        return Microbot.getClient().getItemContainer(InventoryID.INVENTORY);
    }

    public static List<Rs2Item> items() {
        List<Rs2Item> rs2Items = new ArrayList<>();
        for (int i = 0; i < inventory().getItems().length; i++) {
            Item item = inventory().getItems()[i];
            ItemComposition itemComposition = Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getItemDefinition(item.getId()));
            rs2Items.add(new Rs2Item(item.getId(), item.getQuantity(), itemComposition.getName(), i));
        }
        return rs2Items;
    }

    /**
     * Gets all the items in the inventory.
     *
     * @return A list of all items in the inventory.
     */
    public static List<Rs2Item> all() {
        return items();
    }

    /**
     * A list of all the items that meet a specified filter criteria.
     *
     * @param filter The filter to apply when selecting items.
     * @return A list of items that match the filter.
     */
    public static List<Rs2Item> all(Predicate<Rs2Item> filter) {
        return null;
    }

    /**
     * Returns the capacity of your inventory (28).
     *
     * @return The maximum number of items that can be held in the inventory.
     */
    public static int capacity() {
        return CAPACITY;
    }

    /**
     * Combines two items in the inventory by their IDs.
     *
     * @param primaryItemId   The ID of the primary item.
     * @param secondaryItemId The ID of the secondary item.
     * @return True if the combine operation was successful, false otherwise.
     */
    public static boolean combine(int primaryItemId, int secondaryItemId) {
        // Implement combine logic here
        return false;
    }

    /**
     * Combines two items in the inventory by their names.
     *
     * @param primaryItemName   The name of the primary item.
     * @param secondaryItemName The name of the secondary item.
     * @return True if the combine operation was successful, false otherwise.
     */
    public static boolean combine(String primaryItemName, String secondaryItemName) {
        boolean primaryItemInteracted = interact(primaryItemName);
        boolean secondaryItemInteracted = interact(secondaryItemName);
        return primaryItemInteracted && secondaryItemInteracted;
    }

    /**
     * Combines two items in the inventory.
     *
     * @param primary   The primary item.
     * @param secondary The secondary item.
     * @return True if the combine operation was successful, false otherwise.
     */
    public static boolean combine(Item primary, Item secondary) {
        boolean primaryItemInteracted = interact(primary.getId());
        boolean secondaryItemInteracted = interact(secondary.getId());
        return primaryItemInteracted && secondaryItemInteracted;
    }

    /**
     * Checks if the inventory contains an item with the specified ID.
     *
     * @param id The ID to check for.
     * @return True if the inventory contains an item with the given ID, false otherwise.
     */
    public static boolean contains(int id) {
        return items().stream().anyMatch(x -> x.id == id);
    }

    /**
     * Checks if the inventory contains items with the specified IDs.
     *
     * @param ids The IDs to check for.
     * @return True if the inventory contains all of the specified IDs, false otherwise.
     */
    public static boolean contains(int[] ids) {
        return items().stream().anyMatch(x -> Arrays.stream(ids).anyMatch(id -> id == x.id));
    }

    /**
     * Checks if the inventory contains items with the specified IDs.
     *
     * @param ids The IDs to check for.
     * @return True if the inventory contains all of the specified IDs, false otherwise.
     */
    public static boolean contains(Integer... ids) {
        return contains(ids);
    }

    /**
     * Checks if the inventory contains an item with the specified name.
     *
     * @param string The name to check for.
     * @return True if the inventory contains an item with the specified name, false otherwise.
     */
    public static boolean contains(String string) {
        for (Rs2Item item : items()) {
            if (item.name.equals(string)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the inventory contains items with the specified names.
     *
     * @param names The names to check for.
     * @return True if the inventory contains all of the specified names, false otherwise.
     */
    public static boolean contains(String... names) {
        return items().stream().anyMatch(x -> Arrays.stream(names).anyMatch(name -> name.equalsIgnoreCase(x.name)));
    }

    /**
     * Checks if the inventory contains an item that matches the specified filter.
     *
     * @param predicate The filter to apply.
     * @return True if the inventory contains an item that matches the filter, false otherwise.
     */
    public static boolean contains(Predicate<Rs2Item> predicate) {
        return items().stream().anyMatch(predicate);
    }

    /**
     * Checks if the inventory contains all of the specified IDs.
     *
     * @param ids The IDs to check for.
     * @return True if the inventory contains all of the specified IDs, false otherwise.
     */
    public static boolean containsAll(int... ids) {
        return contains(ids);
    }

    /**
     * Checks if the inventory contains all of the specified names.
     *
     * @param names The names to check for.
     * @return True if the inventory contains all of the specified names, false otherwise.
     */
    public static boolean containsAll(String... names) {
        return contains(names);
    }

    /**
     * Counts the number of items in the inventory that match the specified ID.
     *
     * @param id The ID to match.
     * @return The count of items that match the ID.
     */
    public static int count(int id) {
        return (int) items().stream().filter(x -> x.id == id).count();
    }

    /**
     * Counts the number of items in the inventory that match the specified name.
     *
     * @param name The name to match.
     * @return The count of items that match the name.
     */
    public static int count(String name) {
        return (int) items().stream().filter(x -> x.name.equalsIgnoreCase(name)).count();
    }

    /**
     * Counts the number of items in the inventory that match the specified filter.
     *
     * @param predicate The filter to apply.
     * @return The count of items that match the filter.
     */
    public static int count(Predicate<Rs2Item> predicate) {
        return (int) items().stream().filter(predicate).count();
    }

    /**
     * Deselects any item if it is selected.
     *
     * @return True if an item was deselected, false otherwise.
     */
    public static boolean deselect() {
        if (isItemSelected()) {
            Widget inventory = getInventory();

            if (inventory == null) return false;

            Widget item = Arrays.stream(inventory.getDynamicChildren()).filter(x -> x.getBorderType() == 2)
                    .findFirst().orElse(null);

            Microbot.getMouse().click(item.getBounds());
        }
        return true;
    }


    /**
     * Drops the item with the specified ID from the inventory.
     *
     * @param id The ID of the item to drop.
     * @return True if the item was successfully dropped, false otherwise.
     */
    public static boolean drop(int id) {
        Rs2Item item = items().stream().filter(x -> x.id == id).findFirst().orElse(null);
        if (item == null) return false;

        swapMenu(item, "Drop");

        return true;
    }

    /**
     * Drops the item with the specified name from the inventory.
     *
     * @param name The name of the item to drop.
     * @return True if the item was successfully dropped, false otherwise.
     */
    public static boolean drop(String name) {
        Rs2Item item = items().stream().filter(x -> x.name.equalsIgnoreCase(name)).findFirst().orElse(null);
        if (item == null) return false;

        swapMenu(item, "Drop");

        return true;
    }

    /**
     * Drops the item from the inventory that matches the specified filter.
     *
     * @param predicate The filter to identify the item to drop.
     * @return True if the item was successfully dropped, false otherwise.
     */
    public static boolean drop(Predicate<Rs2Item> predicate) {
        Rs2Item item = items().stream().filter(predicate).findFirst().orElse(null);
        if (item == null) return false;

        swapMenu(item, "Drop");

        return true;
    }

    /**
     * Drops all items in the inventory.
     *
     * @return True if all items were successfully dropped, false otherwise.
     */
    public static boolean dropAll() {
        for (Rs2Item item :
                items()) {
            if (item == null) continue;
            swapMenu(item, "Drop");
            sleep(300, 600);
        }
        return true;
    }

    /**
     * Drops all items in the inventory matching the specified ID.
     *
     * @param id The ID to match.
     * @return True if all matching items were successfully dropped, false otherwise.
     */
    public static boolean dropAll(int id) {
        for (Rs2Item item :
                items().stream().filter(x -> x.id == id).collect(Collectors.toList())) {
            if (item == null) continue;
            swapMenu(item, "Drop");
            sleep(300, 600);
        }
        return true;
    }

    /**
     * Drops all items in the inventory matching the specified IDs.
     *
     * @param ids The IDs to match.
     * @return True if all matching items were successfully dropped, false otherwise.
     */
    public static boolean dropAll(int[] ids) {
        for (Rs2Item item :
                items().stream().filter(x -> Arrays.stream(ids).anyMatch(id -> id == x.id)).collect(Collectors.toList())) {
            if (item == null) continue;
            swapMenu(item, "Drop");
            sleep(300, 600);
        }
        return true;
    }

    /**
     * Drops all items in the inventory matching the specified IDs.
     *
     * @param ids The IDs to match.
     * @return True if all matching items were successfully dropped, false otherwise.
     */
    public static boolean dropAll(Integer... ids) {
        return dropAll(ids);
    }

    /**
     * Drops all items in the inventory matching the specified name.
     *
     * @param name The name to match.
     * @return True if all matching items were successfully dropped, false otherwise.
     */
    public static boolean dropAll(String name) {
        for (Rs2Item item :
                items().stream().filter(x -> x.name.equalsIgnoreCase(name)).collect(Collectors.toList())) {
            if (item == null) continue;
            swapMenu(item, "Drop");
            sleep(300, 600);
        }
        return true;
    }

    /**
     * Drops all items in the inventory matching the specified names.
     *
     * @param names The names to match.
     * @return True if all matching items were successfully dropped, false otherwise.
     */
    public static boolean dropAll(String... names) {
        for (Rs2Item item :
                items().stream().filter(x -> Arrays.stream(names).anyMatch(name -> name.equalsIgnoreCase(x.name))).collect(Collectors.toList())) {
            if (item == null) continue;
            swapMenu(item, "Drop");
            sleep(300, 600);
        }
        return true;
    }

    /**
     * Drops all items in the inventory matching the specified filter.
     *
     * @param predicate The filter to apply.
     * @return True if all matching items were successfully dropped, false otherwise.
     */
    public static boolean dropAll(Predicate<Rs2Item> predicate) {
        for (Rs2Item item :
                items().stream().filter(predicate).collect(Collectors.toList())) {
            if (item == null) continue;
            swapMenu(item, "Drop");
            sleep(300, 600);
        }
        return true;
    }

    /**
     * Drops all items in the inventory that don't match the given IDs.
     *
     * @param ids The IDs to exclude.
     * @return True if all non-matching items were successfully dropped, false otherwise.
     */
    public static boolean dropAllExcept(int[] ids) {
        for (Rs2Item item :
                items().stream().filter(x -> Arrays.stream(ids).noneMatch(id -> id == x.id)).collect(Collectors.toList())) {
            if (item == null) continue;
            swapMenu(item, "Drop");
            sleep(300, 600);
        }
        return true;
    }

    /**
     * Drops all items in the inventory that don't match the given IDs.
     *
     * @param ids The IDs to exclude.
     * @return True if all non-matching items were successfully dropped, false otherwise.
     */
    public static boolean dropAllExcept(Integer... ids) {
        return dropAllExcept(ids);
    }

    /**
     * Drops all items in the inventory that don't match the given names.
     *
     * @param names The names to exclude.
     * @return True if all non-matching items were successfully dropped, false otherwise.
     */
    public static boolean dropAllExcept(String... names) {
        for (Rs2Item item :
                items().stream().filter(x -> Arrays.stream(names).noneMatch(id -> id == x.name)).collect(Collectors.toList())) {
            if (item == null) continue;
            swapMenu(item, "Drop");
            sleep(300, 600);
        }
        return true;
    }

    /**
     * Drops all items in the inventory that are not filtered.
     *
     * @param predicate The filter to apply.
     * @return True if all non-matching items were successfully dropped, false otherwise.
     */
    public static boolean dropAllExcept(Predicate<Rs2Item> predicate) {
        for (Rs2Item item :
                items().stream().filter(predicate).collect(Collectors.toList())) {
            if (item == null) continue;
            swapMenu(item, "Drop");
            sleep(300, 600);
        }
        return true;
    }

    /**
     * Returns the count of empty slots in your inventory.
     *
     * @return The number of empty slots.
     */
    public static int emptySlotCount() {
        Widget inventory = getInventory();
        if (inventory == null) return -1;

        return CAPACITY - inventory.getDynamicChildren().length;
    }

    /**
     * Returns a list of items that do not fit the given criteria based on the provided filter.
     *
     * @param predicate The filter to apply.
     * @return A list of items that do not match the filter criteria.
     */
    public static List<Rs2Item> except(Predicate<Rs2Item> predicate) {
        return items().stream().filter(predicate.negate()).collect(Collectors.toList());
    }

    /**
     * Returns the count of full slots in your inventory.
     *
     * @return The number of full slots.
     */
    public static int fullSlotCount() {
        // Implement fullSlotCount logic here
        return 0;
    }

    /**
     * Gets the first item in the inventory that matches the specified item ID.
     *
     * @param id The ID to match.
     * @return The first item that matches the ID, or null if not found.
     */
    public static Item get(int id) {
        // Implement get logic here
        return null;
    }

    /**
     * Gets the first item in the inventory that matches one of the given IDs.
     *
     * @param ids The IDs to match.
     * @return The first item that matches one of the IDs, or null if not found.
     */
    public static Item get(int[] ids) {
        // Implement get logic here
        return null;
    }

    /**
     * Gets the first item in the inventory that matches one of the given IDs.
     *
     * @param ids The IDs to match.
     * @return The first item that matches one of the IDs, or null if not found.
     */
    public static Item get(Integer... ids) {
        // Implement get logic here
        return null;
    }

    /**
     * Gets the item in the inventory with the specified name.
     *
     * @param name The name to match.
     * @return The item with the specified name, or null if not found.
     */
    public static Item get(String name) {
        // Implement get logic here
        return null;
    }

    /**
     * Gets the item in the inventory with one of the specified names.
     *
     * @param names The names to match.
     * @return The item with one of the specified names, or null if not found.
     */
    public static Item get(String... names) {
        // Implement get logic here
        return null;
    }

    /**
     * Gets the item in the inventory that matches the specified filter criteria.
     *
     * @param predicate The filter to apply.
     * @return The item that matches the filter criteria, or null if not found.
     */
    public static Item get(Predicate<Rs2Item> predicate) {
        // Implement get logic here
        return null;
    }

    /**
     * Gets the actions available for the item in the specified slot.
     *
     * @param slot The slot to check.
     * @return An array of available actions for the item in the slot.
     */
    public static String[] getActionsForSlot(int slot) {
        // Implement getActionsForSlot logic here
        return new String[0];
    }

    /**
     * Gets the count of empty slots in your inventory.
     *
     * @return The number of empty slots.
     */
    public static int getEmptySlots() {
        // Implement getEmptySlots logic here
        return 0;
    }

    /**
     * Gets the index of the first empty slot in your inventory.
     *
     * @return The index of the first empty slot, or -1 if none are found.
     */
    public static int getFirstEmptySlot() {
        // Implement getFirstEmptySlot logic here
        return -1;
    }

    /**
     * Gets the index of the next full slot in your inventory.
     *
     * @return The index of the next full slot, or -1 if none are found.
     */
    public static int getFirstFullSlot() {
        // Implement getFirstFullSlot logic here
        return -1;
    }

    /**
     * Gets the ID of the item in the specified slot.
     *
     * @param slot The slot to check.
     * @return The ID of the item in the slot, or -1 if the slot is empty.
     */
    public static int getIdForSlot(int slot) {
        // Implement getIdForSlot logic here
        return -1;
    }

    /**
     * Gets the basic inventory widget. Basic means the bank is not open, the Grand Exchange is not open, the shop is not open, etc.
     *
     * @return The basic inventory widget.
     */
    public static Widget getInventoryWidget() {
        // Implement getInventoryWidget logic here
        return null;
    }

    /**
     * Gets the item in the specified slot of the inventory.
     *
     * @param index The index of the slot to retrieve.
     * @return The item in the specified slot, or null if the slot is empty.
     */
    public static Item getItemInSlot(int index) {
        // Implement getItemInSlot logic here
        return null;
    }

    /**
     * Gets the name of the item in the specified slot of the inventory.
     *
     * @param slot The slot to retrieve the name for.
     * @return The name of the item in the slot, or an empty string if the slot is empty.
     */
    public static String getNameForSlot(int slot) {
        // Implement getNameForSlot logic here
        return "";
    }

    /**
     * Gets a random item from the inventory that matches the specified item IDs.
     *
     * @param itemIDs The item IDs to match.
     * @return A random item that matches the item IDs, or null if no matching items are found.
     */
    public static Item getRandom(int... itemIDs) {
        // Implement getRandom logic here
        return null;
    }

    /**
     * Gets a random item from the inventory that matches the specified item names.
     *
     * @param itemNames The item names to match.
     * @return A random item that matches the item names, or null if no matching items are found.
     */
    public static Item getRandom(String... itemNames) {
        // Implement getRandom logic here
        return null;
    }

    /**
     * Gets a random item from the inventory that matches the specified item filter.
     *
     * @param itemFilter The filter to apply.
     * @return A random item that matches the filter criteria, or null if no matching items are found.
     */
    public static Item getRandom(Predicate<Item> itemFilter) {
        // Implement getRandom logic here
        return null;
    }

    /**
     * Gets the ID of the currently selected item in the inventory.
     *
     * @return The ID of the currently selected item, or -1 if no item is selected.
     */
    public static int getSelectedItemId() {
        // Implement getSelectedItemId logic here
        return -1;
    }

    /**
     * Gets the index of the currently selected item in the inventory.
     *
     * @return The index of the currently selected item, or -1 if no item is selected.
     */
    public static int getSelectedItemIndex() {
        // Implement getSelectedItemIndex logic here
        return -1;
    }

    /**
     * Gets the name of the currently selected item in the inventory.
     *
     * @return The name of the currently selected item, or an empty string if no item is selected.
     */
    public static String getSelectedItemName() {
        // Implement getSelectedItemName logic here
        return "";
    }

    /**
     * Gets the widget child for the item in the specified slot of the inventory.
     *
     * @param slot The slot to retrieve the widget child for.
     * @return The widget child for the item in the slot, or null if the slot is empty.
     */
    public static Widget getWidgetForSlot(int slot) {
        // Implement getWidgetForSlot logic here
        return null;
    }

    /**
     * Interacts with an item with the specified ID in the inventory using the first available action.
     *
     * @param id The ID of the item to interact with.
     * @return True if the interaction was successful, false otherwise.
     */
    public static boolean interact(int id) {
        return interact(id, "");
    }

    /**
     * Interacts with an item with the specified ID in the inventory using the specified action.
     *
     * @param id     The ID of the item to interact with.
     * @param action The action to perform on the item.
     * @return True if the interaction was successful, false otherwise.
     */
    public static boolean interact(int id, String action) {
        Rs2Item rs2Item = items().stream().filter(x -> x.id == id).findFirst().orElse(null);
        if (rs2Item == null) return false;
        swapMenu(rs2Item, action);
        return true;
    }

    /**
     * Interacts with an item with the specified name in the inventory using the first available action.
     *
     * @param name The name of the item to interact with.
     * @return True if the interaction was successful, false otherwise.
     */
    public static boolean interact(String name) {
        Rs2Item rs2Item = items().stream().filter(x -> x.name == name).findFirst().orElse(null);
        if (rs2Item == null) return false;
        swapMenu(rs2Item, "");
        return true;
    }

    /**
     * Interacts with an item with the specified name in the inventory using the specified action.
     *
     * @param name   The name of the item to interact with.
     * @param action The action to perform on the item.
     * @return True if the interaction was successful, false otherwise.
     */
    public static boolean interact(String name, String action) {
        Rs2Item rs2Item = items().stream().filter(x -> x.name == name).findFirst().orElse(null);
        if (rs2Item == null) return false;
        swapMenu(rs2Item, action);
        return true;
    }

    /**
     * Interacts with an item in the inventory using the first available action based on the specified filter.
     *
     * @param filter The filter to apply.
     * @return True if the interaction was successful, false otherwise.
     */
    public static boolean interact(Predicate<Rs2Item> filter) {
        return interact(filter, "");
    }

    /**
     * Interacts with an item in the inventory using the specified action based on the specified filter.
     *
     * @param filter The filter to apply.
     * @param action The action to perform on the item.
     * @return True if the interaction was successful, false otherwise.
     */
    public static boolean interact(Predicate<Rs2Item> filter, String action) {
        Rs2Item rs2Item = items().stream().filter(filter).findFirst().orElse(null);
        if (rs2Item == null) return false;
        swapMenu(rs2Item, action);
        return true;
    }

    /**
     * Interacts with a given item in the inventory using the first available action.
     * If the item has an invalid slot value, it will find the slot based on the item ID.
     *
     * @param item The item to interact with.
     * @return True if the interaction was successful, false otherwise.
     */
    public static boolean interact(Rs2Item item) {
        return interact(item, "");
    }

    /**
     * Interacts with a given item in the inventory using the specified action.
     * If the item has an invalid slot value, it will find the slot based on the item ID.
     *
     * @param item   The item to interact with.
     * @param action The action to perform on the item.
     * @return True if the interaction was successful, false otherwise.
     */
    public static boolean interact(Rs2Item item, String action) {
        Rs2Item rs2Item = items().stream().filter(x -> x == item).findFirst().orElse(null);
        if (rs2Item == null) {
            rs2Item = items().stream().filter(x -> x.id == item.id).findFirst().orElse(null);
            if (rs2Item == null) return false;
        }
        swapMenu(rs2Item, action);
        return true;
    }

    /**
     * Checks whether the inventory is empty (contains no items).
     *
     * @return True if the inventory is empty, false otherwise.
     */
    public static boolean isEmpty() {
        return items().isEmpty();
    }

    /**
     * Checks whether the inventory is configured to ignore whether shift interactions are enabled or not.
     *
     * @return True if the inventory ignores shift interactions, false otherwise.
     */
    public static boolean isForceNoShift() {
        throw new NotImplementedException("TODO");
    }

    /**
     * Determines whether the inventory is full (all slots are occupied).
     *
     * @return True if the inventory is full, false otherwise.
     */
    public static boolean isFull() {
        return items().size() == CAPACITY;
    }

    /**
     * Checks whether an item is currently selected in your inventory.
     *
     * @return True if an item is selected, false otherwise.
     */
    public static boolean isItemSelected() {
        return Microbot.getClient().isWidgetSelected();
    }

    /**
     * Checks whether the inventory is open.
     *
     * @return True if the inventory is open, false otherwise.
     */
    public static boolean isOpen() {
        return Rs2Tab.getCurrentTab() == InterfaceTab.INVENTORY;
    }

    /**
     * Checks if the given slot in the inventory is empty.
     *
     * @param slot The slot to check.
     * @return True if the slot is empty, false otherwise.
     */
    public static boolean isSlotEmpty(int slot) {
        Widget inventory = getInventory();

        if (inventory == null) return false;

        if (slot > inventory.getDynamicChildren().length) return false;

        return true;
    }

    /**
     * Checks if the given slot in the inventory is full (contains an item).
     *
     * @param slot The slot to check.
     * @return True if the slot is full, false otherwise.
     */
    public static boolean isSlotFull(int slot) {
        return !isSlotEmpty(slot);
    }

    /**
     * Gets the bounding rectangle for the slot of the specified item in the inventory.
     *
     * @param rs2Item The item to get the bounds for.
     * @return The bounding rectangle for the item's slot, or null if the item is not found.
     */
    public static java.awt.Rectangle itemBounds(Rs2Item rs2Item) {
        Widget inventory = getInventory();

        if (inventory == null) return null;

        Widget item = Arrays.stream(inventory.getDynamicChildren())
                .filter(x -> x.getItemId() == rs2Item.id)
                .findFirst()
                .orElse(null);

        if (item == null) return null;

        return item.getBounds();
    }

    /**
     * Checks if your inventory only contains items with the specified ID.
     *
     * @param ids The ID to check.
     * @return True if the inventory only contains items with the specified ID, false otherwise.
     */
    public static boolean onlyContains(int[] ids) {
        return items().stream().allMatch(x -> Arrays.stream(ids).allMatch(id -> x.id == id));
    }

    /**
     * Checks if your inventory only contains items with the specified ID.
     *
     * @param ids The IDs to check.
     * @return True if the inventory only contains items with the specified IDs, false otherwise.
     */
    public static boolean onlyContains(Integer... ids) {
        return onlyContains(ids);
    }

    /**
     * Checks if your inventory only contains items with the specified names.
     *
     * @param names The names to check.
     * @return True if the inventory only contains items with the specified names, false otherwise.
     */
    public static boolean onlyContains(String... names) {
        return items().stream().allMatch(x -> Arrays.stream(names).allMatch(name -> x.name.equalsIgnoreCase(name)));
    }

    /**
     * Checks if your inventory only contains items that match the specified filter.
     *
     * @param predicate The filter to apply.
     * @return True if the inventory only contains items that match the filter, false otherwise.
     */
    public static boolean onlyContains(Predicate<Rs2Item> predicate) {
        // Implement onlyContains logic here
        return items().stream().allMatch(predicate);
    }

    /**
     * Opens the inventory.
     *
     * @return True if the inventory is successfully opened, false otherwise.
     */
    public static boolean open() {
        Rs2Tab.switchToInventoryTab();
        return true;
    }

    /**
     * Gets the size of the inventory.
     *
     * @return The size of the inventory.
     */
    public static int size() {
        return items().size();
    }

    /**
     * Gets the slot for the item with the specified ID.
     *
     * @param id The ID of the item.
     * @return The slot index for the item, or -1 if not found.
     */
    public static int slot(int id) {
        Rs2Item item = items().stream().filter(x -> x.id == id).findFirst().orElse(null);
        if (item == null) return -1;

        return items().indexOf(item);
    }

    /**
     * Gets the slot for the item with the specified name.
     *
     * @param name The name of the item.
     * @return The slot index for the item, or -1 if not found.
     */
    public static int slot(String name) {
        Rs2Item item = items().stream().filter(x -> x.name.equalsIgnoreCase(name)).findFirst().orElse(null);
        if (item == null) return -1;

        return items().indexOf(item);
    }

    /**
     * Gets the slot for the item that matches the specified filter.
     *
     * @param predicate The filter to apply.
     * @return The slot index for the item, or -1 if not found.
     */
    public static int slot(Predicate<Rs2Item> predicate) {
        Rs2Item item = items().stream().filter(predicate).findFirst().orElse(null);
        if (item == null) return -1;

        return items().indexOf(item);
    }

    /**
     * Checks if the specified slot contains items that match the given IDs.
     *
     * @param slot The slot to check.
     * @param ids  The IDs to match.
     * @return True if the slot contains items that match the IDs, false otherwise.
     */
    public static boolean slotContains(int slot, int[] ids) {
        Rs2Item item = items().get(slot);
        if (item == null) return false;
        return Arrays.stream(ids).anyMatch(x -> x == item.id);
    }

    /**
     * Checks if the specified slot contains items that match the given IDs.
     *
     * @param slot The slot to check.
     * @param ids  The IDs to match.
     * @return True if the slot contains items that match the IDs, false otherwise.
     */
    public static boolean slotContains(int slot, Integer... ids) {
        return slotContains(slot, ids);
    }

    /**
     * Checks if the specified slot contains items that match the given names.
     *
     * @param slot  The slot to check.
     * @param names The names to match.
     * @return True if the slot contains items that match the names, false otherwise.
     */
    public static boolean slotContains(int slot, String... names) {
        Rs2Item item = items().get(slot);
        if (item == null) return false;
        return Arrays.stream(names).anyMatch(x -> x == item.name);
    }


    /**
     * Interacts with the specified slot in the inventory using the first available action.
     *
     * @param slot The slot to interact with.
     * @return True if the interaction is successful, false otherwise.
     */
    public static boolean slotInteract(int slot) {
        return slotInteract(slot);
    }

    /**
     * Interacts with the specified slot in the inventory using the specified action.
     *
     * @param slot   The slot to interact with.
     * @param action The action to perform.
     * @return True if the interaction is successful, false otherwise.
     */
    public static boolean slotInteract(int slot, String action) {
        Rs2Item item = items().get(slot);
        if (item == null) return false;
        return interact(item.id, action);
    }

    /**
     * Checks if the specified slot contains items whose names contain the given substring.
     *
     * @param slot The slot to check.
     * @param sub  The substring to search for in item names.
     * @return True if the slot contains items with names containing the substring, false otherwise.
     */
    public static boolean slotNameContains(int slot, String sub) {
        Rs2Item item = items().get(slot);
        if (item == null) return false;
        return item.name.contains(sub);
    }


    /**
     * Uses the item with the specified ID in the inventory.
     *
     * @param id The ID of the item to use.
     * @return True if the item is successfully used, false otherwise.
     */
    public static boolean use(int id) {
        Rs2Item item = items().stream().filter(x -> x.id == id).findFirst().orElse(null);
        if (item == null) return false;
        return interact(id, "Use");
    }

    /**
     * Uses the item with the specified name in the inventory.
     *
     * @param name The name of the item to use.
     * @return True if the item is successfully used, false otherwise.
     */
    public static boolean use(String name) {
        Rs2Item item = items().stream().filter(x -> x.name == name).findFirst().orElse(null);
        if (item == null) return false;
        return interact(name, "Use");
    }

    /**
     * Uses the given item in the inventory.
     *
     * @param rs2Item The item to use.
     * @return True if the item is successfully used, false otherwise.
     */
    public static boolean use(Rs2Item rs2Item) {
        Rs2Item item = items().stream().filter(x -> x == rs2Item).findFirst().orElse(null);
        if (item == null) return false;
        return interact(item, "Use");
    }

    public static void handleMenuSwapper(MenuEntry menuEntry) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        if (item == null) return;
        ItemComposition itemComposition = Microbot.getClient().getItemDefinition(item.id);
        int index = 0;

        if (Microbot.getClient().isWidgetSelected()) {
            menuEntry.setType(MenuAction.WIDGET_TARGET);
        } else {
            menuEntry.setType(MenuAction.CC_OP);
        }

        Rs2Reflection.setItemId(menuEntry, item.id);

        if (itemAction.equalsIgnoreCase("use")) {
            index = 0;
        } else if (itemComposition.getName().contains("pouch") && itemAction.equalsIgnoreCase("empty")) {
            index = 1;
        } else if (itemAction.equalsIgnoreCase("drink")
                || itemAction.equalsIgnoreCase("read")
                || itemAction.equalsIgnoreCase("eat")
                || itemAction.equalsIgnoreCase("view")
                || itemAction.equalsIgnoreCase("bury")) {
            index = 2;
        } else if (itemAction.equalsIgnoreCase("wield")
                || itemAction.equalsIgnoreCase("wear")
                || itemAction.equalsIgnoreCase("check steps")) {
            index = 3;
        } else if (itemAction.equalsIgnoreCase("fill")) {
            index = 4;
        } else if (itemAction.equalsIgnoreCase("empty") || itemAction.equalsIgnoreCase("rub")
                || itemAction.equalsIgnoreCase("refund") || itemAction.equalsIgnoreCase("commune")
                || itemAction.equalsIgnoreCase("extinguish")) {
            index = 6;
        } else if (itemAction.equalsIgnoreCase("drop") || itemAction.equalsIgnoreCase("destroy")) {
            index = 7;
        } else if (itemAction.equalsIgnoreCase("examine")) {
            index = 10;
        }

        menuEntry.setOption(itemAction != null ? itemAction : "");
        menuEntry.setIdentifier(index);
        menuEntry.setParam0(item.slot);
        menuEntry.setParam1(9764864);
        menuEntry.setTarget("<col=ff9040>" + itemComposition.getName() + "</col>");
    }

    private static void swapMenu(Rs2Item rs2Item, String action) {
        item = rs2Item;
        itemAction = action;
        Widget inventory = Rs2Widget.getWidget(10551357); //click on inventory to be safe
        Microbot.getMouse().clickFast((int) inventory.getBounds().getCenterX(), (int) inventory.getBounds().getCenterY());
        sleep(100);
        item = null;
        itemAction = "";
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
}
