package net.runelite.client.plugins.microbot.util.inventory;

import net.runelite.api.*;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.globval.enums.InterfaceTab;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.menu.NewMenuEntry;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import org.apache.commons.lang3.NotImplementedException;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.runelite.client.plugins.microbot.util.Global.sleep;
import static net.runelite.client.plugins.microbot.util.Global.sleepUntil;

public class Rs2Inventory {

    // The maximum capacity of the inventory
    private static final int CAPACITY = 28;

    public static ItemContainer inventory() {
        return Microbot.getClient().getItemContainer(InventoryID.INVENTORY);
    }

    public static List<Rs2Item> inventoryItems = new ArrayList<>();

    private static boolean isTrackingInventory = false;
    private static boolean isInventoryChanged = false;


    public static void storeInventoryItemsInMemory(ItemContainerChanged e) {
        if (e.getContainerId() == InventoryID.INVENTORY.getId() && e.getItemContainer() != null) {
            if (isTrackingInventory) {
                isInventoryChanged = true;
            }
            List<Rs2Item> _inventoryItems = new ArrayList<>();
            for (int i = 0; i < e.getItemContainer().getItems().length; i++) {
                Item item = inventory().getItems()[i];
                if (item.getId() == -1) continue;
                ItemComposition itemComposition = Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getItemDefinition(item.getId()));
                _inventoryItems.add(new Rs2Item(item, itemComposition, i));
            }
            inventoryItems = _inventoryItems;
        }
    }

    public static List<Rs2Item> items() {
        return inventoryItems;
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
        return items().stream().filter(filter).collect(Collectors.toList());
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
        boolean primaryItemInteracted = use(primaryItemId);
        sleep(100);
        boolean secondaryItemInteracted = use(secondaryItemId);
        return primaryItemInteracted && secondaryItemInteracted;
    }

    /**
     * Combines two items in the inventory by their names.
     *
     * @param primaryItemName   The name of the primary item.
     * @param secondaryItemName The name of the secondary item.
     * @return True if the combine operation was successful, false otherwise.
     */
    public static boolean combine(String primaryItemName, String secondaryItemName) {
        boolean primaryItemInteracted = use(primaryItemName);
        sleep(100);
        boolean secondaryItemInteracted = use(secondaryItemName);
        return primaryItemInteracted && secondaryItemInteracted;
    }

    /**
     * Combines two items in the inventory.
     *
     * @param primary   The primary item.
     * @param secondary The secondary item.
     * @return True if the combine operation was successful, false otherwise.
     */
    public static boolean combine(Rs2Item primary, Rs2Item secondary) {
        boolean primaryItemInteracted = use(primary);
        sleep(100);
        boolean secondaryItemInteracted = use(secondary);
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
     * @return True if the inventory contains all the specified IDs, false otherwise.
     */
    public static boolean contains(int[] ids) {
        return items().stream().anyMatch(x -> Arrays.stream(ids).anyMatch(id -> id == x.id));
    }

    /**
     * Checks if the inventory contains items with the specified IDs.
     *
     * @param ids The IDs to check for.
     * @return True if the inventory contains all the specified IDs, false otherwise.
     */
    public static boolean contains(Integer... ids) {
        return items().stream().anyMatch(x -> Arrays.stream(ids).anyMatch(i -> i == x.id));
    }

    /**
     * Checks if the inventory contains an item with the specified name.
     *
     * @param name The name to check for.
     * @return True if the inventory contains an item with the specified name, false otherwise.
     */
    public static boolean contains(String name) {
        return items().stream().anyMatch(x -> name.equalsIgnoreCase(x.name));
    }

    /**
     * Checks if the inventory contains items with the specified names.
     *
     * @param names The names to check for.
     * @return True if the inventory contains all the specified names, false otherwise.
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
     * Checks if the inventory contains all the specified IDs.
     *
     * @param ids The IDs to check for.
     * @return True if the inventory contains all the specified IDs, false otherwise.
     */
    public static boolean containsAll(int... ids) {
        return contains(ids);
    }

    /**
     * Checks if the inventory contains all the specified names.
     *
     * @param names The names to check for.
     * @return True if the inventory contains all the specified names, false otherwise.
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
     * Counts the number of items in the inventory
     *
     * @return The count of items
     */
    public static int count() {
        return items().size();
    }

    /**
     * Counts the number of items in the inventory that match the specified name.
     *
     * @param name The name to match.
     * @return The count of items that match the name.
     */
    public static int count(String name) {
        return (int) items().stream().filter(x -> x.name.toLowerCase().contains(name.toLowerCase())).count();
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
            return use(getSelectedItemId());
        }
        return false;
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

        invokeMenu(item, "Drop");

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

        invokeMenu(item, "Drop");

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

        invokeMenu(item, "Drop");

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
            invokeMenu(item, "Drop");
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
            invokeMenu(item, "Drop");
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
        for (Rs2Item item :
                items().stream().filter(x -> Arrays.stream(ids).anyMatch(id -> id == x.id)).collect(Collectors.toList())) {
            if (item == null) continue;
            invokeMenu(item, "Drop");
            sleep(300, 600);
        }
        return true;
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
            invokeMenu(item, "Drop");
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
            invokeMenu(item, "Drop");
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
            invokeMenu(item, "Drop");
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
        return dropAll(x -> Arrays.stream(ids).noneMatch(id -> id == x.id));
    }

    /**
     * Drops all items in the inventory that don't match the given names.
     *
     * @param names The names to exclude.
     * @return True if all non-matching items were successfully dropped, false otherwise.
     */
    public static boolean dropAllExcept(String... names) {
        return dropAll(x -> Arrays.stream(names).noneMatch(name -> name.equalsIgnoreCase(x.name)));
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
            invokeMenu(item, "Drop");
            sleep(300, 600);
        }
        return true;
    }

    /**
     * Drop all items that fall under the gpValue
     *
     * @param gpValue minimum amount of gp required to not drop the item
     * @return
     */
    public static boolean dropAllExcept(int gpValue) {
        return dropAllExcept(gpValue, List.of());
    }

    /**
     * Drop all items that fall under the gpValue
     *
     * @param gpValue    minimum amount of gp required to not drop the item
     * @param ignoreItems List of items to not drop
     * @return
     */
    public static boolean dropAllExcept(int gpValue, List<String> ignoreItems) {
        for (Rs2Item item :
                new ArrayList<>(items())) {
            if (item == null) continue;
            if (ignoreItems.stream().anyMatch(x -> x.equalsIgnoreCase(item.name))) continue;
            long totalPrice = (long) Microbot.getClientThread().runOnClientThread(() ->
                    Microbot.getItemManager().getItemPrice(item.id) * item.quantity);
            if (totalPrice >= gpValue) continue;

            invokeMenu(item, "Drop");
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

        return capacity() - inventory.getDynamicChildren().length;
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
     * Gets the last item in the inventory that matches the specified item ID.
     *
     * @param id The ID to match.
     * @return The last item that matches the ID, or null if not found.
     */
    public static Rs2Item getLast(int id) {

        long count = items().size();
        Stream<Rs2Item> stream = items().stream();

        return stream.skip(count - 1).findFirst().orElse(null);
    }

    /**
     * Gets the first item in the inventory that matches the specified item ID.
     *
     * @param id The ID to match.
     * @return The first item that matches the ID, or null if not found.
     */
    public static Rs2Item get(int id) {
        return items().stream().filter(x -> x.id == id).findFirst().orElse(null);
    }

    /**
     * Gets the first item in the inventory that matches one of the given IDs.
     *
     * @param ids The IDs to match.
     * @return The first item that matches one of the IDs, or null if not found.
     */
    public static Rs2Item get(Integer... ids) {
        return items().stream().filter(x -> Arrays.stream(ids).anyMatch(i -> i == x.id)).findFirst().orElse(null);
    }

    /**
     * Gets the item in the inventory with the specified name.
     * this method ignores casing
     *
     * @param name The name to match.
     * @return The item with the specified name, or null if not found.
     */
    public static Rs2Item get(String name) {
        return get(name, false);
    }

    /**
     * Gets the item in the inventory with the specified name.
     * this method ignores casing
     *
     * @param name The name to match.
     * @return The item with the specified name, or null if not found.
     */
    public static Rs2Item get(String name, boolean exact) {
        if (exact)
            return items().stream().filter(x -> x.name.equalsIgnoreCase(name)).findFirst().orElse(null);
        else
            return items().stream().filter(x -> x.name.toLowerCase().contains(name.toLowerCase())).findFirst().orElse(null);
    }

    /**
     * Gets the item in the inventory with one of the specified names.
     *
     * @param names The names to match.
     * @return The item with one of the specified names, or null if not found.
     */
    public static Rs2Item get(String... names) {
        return items().stream().filter(x -> Arrays.stream(names).anyMatch(n -> n.equalsIgnoreCase(x.name))).findFirst().orElse(null);
    }

    /**
     * Gets the item in the inventory that matches the specified filter criteria.
     *
     * @param predicate The filter to apply.
     * @return The item that matches the filter criteria, or null if not found.
     */
    public static Rs2Item get(Predicate<Rs2Item> predicate) {
        return items().stream().filter(predicate).findFirst().orElse(null);
    }

    /**
     * Checks if the player has a certain quantity of an item.
     *
     * @param id     The id of the item to check.
     * @param amount The desired quantity of the item.
     * @return True if the player has the specified quantity of the item, false otherwise.
     */
    public static boolean hasItemAmount(int id, int amount) {
        Rs2Item rs2Item = get(id);
        if (rs2Item == null) return false;
        if (rs2Item.isStackable) {
            return rs2Item.quantity >= amount;
        } else {
            return items().stream().filter(x -> x.id == id).count() >= amount;
        }
    }

    /**
     * Checks if the player has a certain quantity of an item.
     *
     * @param id        The id of the item to check.
     * @param amount    The desired quantity of the item.
     * @param stackable A boolean indicating if the item is stackable.
     * @return True if the player has the specified quantity of the item, false otherwise.
     */
    public static boolean hasItemAmount(int id, int amount, boolean stackable) {
        Rs2Item item = get(id);
        return stackable ? item.quantity >= amount : items().stream().filter(x -> x.id == id).count() >= amount;
    }

    /**
     * Checks if the player has a certain quantity of an item.
     *
     * @param name   The name of the item to check.
     * @param amount The desired quantity of the item.
     * @return True if the player has the specified quantity of the item, false otherwise.
     */
    public static boolean hasItemAmount(String name, int amount) {
        return hasItemAmount(name, amount, false);
    }

    /**
     * Checks if the player has a certain quantity of an item.
     *
     * @param name      The name of the item to check.
     * @param amount    The desired quantity of the item.
     * @param stackable A boolean indicating if the item is stackable.
     * @return True if the player has the specified quantity of the item, false otherwise.
     */
    public static boolean hasItemAmount(String name, int amount, boolean stackable) {
        return hasItemAmount(name, amount, stackable, false);
    }

    /**
     * Checks if the player has a certain quantity of an item.
     *
     * @param name      The name of the item to check.
     * @param amount    The desired quantity of the item.
     * @param stackable A boolean indicating if the item is stackable.
     * @param exact     A boolean indicating whether the check should be exact or partial for non-stackable items.
     * @return True if the player has the specified quantity of the item, false otherwise.
     */
    public static boolean hasItemAmount(String name, int amount, boolean stackable, boolean exact) {
        if (!stackable) {
            if (exact) {
                return items().stream().filter(x -> x.name.equalsIgnoreCase(name)).count() >= amount;
            } else {
                return items().stream().filter(x -> x.name.toLowerCase().contains(name.toLowerCase())).count() >= amount;
            }
        }

        Rs2Item item = get(name, exact);
        if (item == null) return false;
        return item.quantity >= amount;
    }

    /**
     * @param id
     * @return boolean
     */
    public static boolean hasItem(int id) {
        return get(id) != null;
    }

    /**
     * @param name
     * @return boolean
     */
    public static boolean hasItem(String name) {
        return get(name) != null;
    }

    /**
     * @param name
     * @return boolean
     */
    public static boolean hasItem(String name, boolean exact) {
        return get(name, true) != null;
    }

    /**
     * @param names
     * @return boolean
     */
    public static boolean hasItem(String... names) {
        return get(names) != null;
    }

    /**
     * Gets the actions available for the item in the specified slot.
     *
     * @param slot The slot to check.
     * @return An array of available actions for the item in the slot.
     */
    public static String[] getActionsForSlot(int slot) {
        return items().stream()
                .filter(x -> x.slot == slot)
                .map(x -> x.inventoryActions)
                .findFirst().orElse(new String[]{});
    }

    public static List<Rs2Item> getInventoryFood() {
        return items().stream()
                .filter(x -> Arrays.stream(x.inventoryActions).anyMatch(a -> a != null && a.equalsIgnoreCase("eat")))
                .collect(Collectors.toList());
    }

    public static List<Rs2Item> getPotions() {
        return items().stream()
                .filter(x -> Arrays.stream(x.inventoryActions).anyMatch(a -> a != null && a.equalsIgnoreCase("drink")))
                .collect(Collectors.toList());
    }

    /**
     * Gets the count of empty slots in your inventory.
     *
     * @return The number of empty slots.
     */
    public static int getEmptySlots() {
        return CAPACITY - items().size();
    }

    /**
     * Gets the index of the first empty slot in your inventory.
     * returns -1 if no empty slot is found
     *
     * @return The index of the first empty slot, or -1 if none are found.
     */
    public static int getFirstEmptySlot() {
        if (isFull()) return -1;
        for (int i = 0; i < inventory().getItems().length; i++) {
            if (inventory().getItems()[i].getId() == -1)
                return i;
        }
        return -1;
    }

    /**
     * Gets the index of the next full slot in your inventory.
     * return -1 if no full slot has been found
     *
     * @return The index of the next full slot, or -1 if none are found.
     */
    public static int getFirstFullSlot() {
        if (isEmpty()) return -1;
        return items().stream()
                .sorted()
                .findFirst()
                .map(Rs2Item::getSlot)
                .orElse(-1);
    }

    /**
     * Gets the ID of the item in the specified slot.
     * Returns -1 if the slot has not been found or no item has been found
     *
     * @param slot The slot to check.
     * @return The ID of the item in the slot, or -1 if the slot is empty.
     */
    public static int getIdForSlot(int slot) {
        Rs2Item item = items().stream().filter(x -> x.slot == slot).findFirst().orElse(null);
        if (item == null) return -1;
        return item.id;
    }

    /**
     * Gets the basic inventory widget. Basic means the bank is not open, the Grand Exchange is not open, the shop is not open, etc.
     *
     * @return The basic inventory widget.
     */
    public static Widget getInventoryWidget() {
        return Rs2Widget.getWidget(ComponentID.INVENTORY_CONTAINER);
    }

    /**
     * Gets the item in the specified slot of the inventory.
     *
     * @param slot The index of the slot to retrieve.
     * @return The item in the specified slot, or null if the slot is empty.
     */
    public static Rs2Item getItemInSlot(int slot) {
        return items().stream()
                .filter(x -> x.slot == slot)
                .findFirst()
                .orElse(null);
    }

    /**
     * Gets the name of the item in the specified slot of the inventory.
     *
     * @param slot The slot to retrieve the name for.
     * @return The name of the item in the slot, or an empty string if the slot is empty.
     */
    public static String getNameForSlot(int slot) {
        return items().stream()
                .filter(x -> x.slot == slot)
                .findFirst()
                .map(x -> x.name)
                .orElse(null);
    }

    /**
     * Gets a random item from the inventory that matches the specified item IDs.
     *
     * @param itemIDs The item IDs to match.
     * @return A random item that matches the item IDs, or null if no matching items are found.
     */
    public static Rs2Item getRandom(int... itemIDs) {
        return items().stream()
                .filter(x -> Arrays.stream(itemIDs)
                        .anyMatch(i -> i == x.id))
                .findAny()
                .orElse(null);
    }

    /**
     * Gets a random item from the inventory that matches the specified item names.
     *
     * @param itemNames The item names to match.
     * @return A random item that matches the item names, or null if no matching items are found.
     */
    public static Rs2Item getRandom(String... itemNames) {
        return items().stream()
                .filter(x -> Arrays.stream(itemNames)
                        .anyMatch(i -> i.equalsIgnoreCase(x.name)))
                .findAny()
                .orElse(null);
    }

    /**
     * Gets a random item from the inventory that matches the specified item filter.
     *
     * @param itemFilter The filter to apply.
     * @return A random item that matches the filter criteria, or null if no matching items are found.
     */
    public static Rs2Item getRandom(Predicate<Rs2Item> itemFilter) {
        return items().stream()
                .filter(itemFilter)
                .findAny()
                .orElse(null);
    }

    /**
     * Gets the ID of the currently selected item in the inventory.
     * Returns -1 if none is found
     *
     * @return The ID of the currently selected item, or -1 if no item is selected.
     */
    public static int getSelectedItemId() {
        if (Microbot.getClient().getSelectedWidget() == null) return -1;
        return Microbot.getClient().getSelectedWidget().getItemId();
    }

    /**
     * Gets the index of the currently selected item in the inventory.
     * Returns -1 if none is found
     *
     * @return The index of the currently selected item, or -1 if no item is selected.
     */
    public static int getSelectedItemIndex() {
        if (Microbot.getClient().getSelectedWidget() == null) return -1;
        return Microbot.getClient().getSelectedWidget().getIndex();
    }

    /**
     * Gets the name of the currently selected item in the inventory.
     *
     * @return The name of the currently selected item, or an empty string if no item is selected.
     */
    public static String getSelectedItemName() {
        if (Microbot.getClient().getSelectedWidget() == null) return null;
        return Microbot.getClient().getSelectedWidget().getName();
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
        invokeMenu(rs2Item, action);
        return true;
    }

    /**
     * Interacts with an item with the specified name in the inventory using the first available action.
     *
     * @param name The name of the item to interact with.
     * @return True if the interaction was successful, false otherwise.
     */
    public static boolean interact(String name) {
        interact(name, "", false);
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
        interact(name, action, false);
        return true;
    }

    /**
     * Interacts with an item with the specified name in the inventory using the specified action.
     *
     * @param name   The name of the item to interact with.
     * @param action The action to perform on the item.
     * @return True if the interaction was successful, false otherwise.
     */
    public static boolean interact(String name, String action, boolean exact) {
        Rs2Item rs2Item;
        if (exact) {
            rs2Item = items().stream().filter(x -> x.name.equalsIgnoreCase(name.toLowerCase())).findFirst().orElse(null);
        } else {
            rs2Item = items().stream().filter(x -> x.name.toLowerCase().contains(name.toLowerCase())).findFirst().orElse(null);
        }
        if (rs2Item == null) return false;
        invokeMenu(rs2Item, action);
        return true;
    }

    /**
     * Interacts with an item in the inventory using the first available action based on the specified filter.
     *
     * @param filter The filter to apply.
     * @return True if the interaction was successful, false otherwise.
     */
    public static boolean interact(Predicate<Rs2Item> filter) {
        return interact(filter, "Use");
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
        invokeMenu(rs2Item, action);
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
        if (item == null) return false;
        Rs2Item rs2Item = items().stream().filter(x -> x == item).findFirst().orElse(null);
        if (rs2Item == null) {
            rs2Item = items().stream().filter(x -> x.id == item.id).findFirst().orElse(null);
            if (rs2Item == null) return false;
        }
        invokeMenu(rs2Item, action);
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
     * Checks if the inventory is full based on the item name.
     *
     * @param name The name of the item to check.
     * @return true if the inventory is full, false otherwise.
     */
    public static boolean isFull(String name) {
        Rs2Item rs2Item = get(name);
        if (rs2Item == null && items().size() == CAPACITY) return true;
        return rs2Item != null && rs2Item.quantity <= 1 && items().size() == CAPACITY;
    }

    /**
     * Checks if the inventory is full based on the item ID.
     *
     * @param id The ID of the item to check.
     * @return true if the inventory is full, false otherwise.
     */
    public static boolean isFull(int id) {
        Rs2Item rs2Item = get(id);
        if (rs2Item == null && items().size() == CAPACITY) return true;
        return rs2Item != null && rs2Item.quantity <= 1 && items().size() == CAPACITY;
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

        return slot <= inventory.getDynamicChildren().length;
    }

    /**
     * Checks if the given slot in the inventory is empty.
     *
     * @param slots The slots to check.
     * @return True if the slot is empty, false otherwise.
     */
    public static boolean isSlotsEmpty(int... slots) {
        Widget inventory = getInventory();

        if (inventory == null) return false;

        for (int slot :
                slots) {
            if (slot > inventory.getDynamicChildren().length) return false;
        }

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
     * @param ids The IDs to check.
     * @return True if the inventory only contains items with the specified IDs, false otherwise.
     */
    public static boolean onlyContains(Integer... ids) {
        return items().stream().allMatch(x -> Arrays.stream(ids).allMatch(id -> x.id == id));
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
        Rs2Item item = items().get(slot);
        if (item == null) return false;
        return Arrays.stream(ids).anyMatch(x -> x == item.id);
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
        return Arrays.stream(names).anyMatch(x -> x.equalsIgnoreCase(item.name));
    }


    /**
     * Interacts with the specified slot in the inventory using the first available action.
     *
     * @param slot The slot to interact with.
     * @return True if the interaction is successful, false otherwise.
     */
    public static boolean slotInteract(int slot) {
        return slotInteract(slot, "");
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
        if (action == null || action.isEmpty())
            action = Arrays.stream(item.inventoryActions).findFirst().orElse("");

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
     * Uses the last item with the specified ID in the inventory.
     *
     * @param id The ID to match.
     * @return The last item that matches the ID, or null if not found.
     */
    public static boolean useLast(int id) {

        Rs2Item rs2Item = getLast(id);
        if (rs2Item == null) return false;
        return use(rs2Item);
    }

    /**
     * Uses the item with the specified name in the inventory.
     *
     * @param name The name of the item to use.
     * @return True if the item is successfully used, false otherwise.
     */
    public static boolean useUnNoted(String name) {
        Rs2Item item = items().stream().filter(x -> x.name.toLowerCase().contains(name.toLowerCase()) && !x.isNoted).findFirst().orElse(null);
        if (item == null) return false;
        return interact(item, "Use");
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
        Rs2Item item = items().stream().filter(x -> x.name.toLowerCase().contains(name.toLowerCase())).findFirst().orElse(null);
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

    /**
     * @param name
     */
    public static void equip(String name) {
        wield(name);
    }

    /**
     * @param name item name
     */
    public static void wield(String name) {
        if (!Rs2Inventory.hasItem(name)) return;
        if (Rs2Equipment.isWearing(name, true)) return;
        invokeMenu(get(name), "wield");
    }

    /**
     * @param name item name
     */
    public static void wear(String name) {
        invokeMenu(get(name), "wear");
    }

    /**
     * @param id item id
     */
    public static void equip(int id) {
        wield(id);
    }

    /**
     * @param id item id
     */
    public static void wield(int id) {
        invokeMenu(get(id), "wield");
    }

    /**
     * @param id item id
     */
    public static void wear(int id) {
        invokeMenu(get(id), "wear");
    }

    /**
     * use unnoted inventory item on ingame object
     *
     * @param item     name of the item to use
     * @param objectID to use item on
     * @return
     */
    public static boolean useUnNotedItemOnObject(String item, int objectID) {
        if (Rs2Bank.isOpen()) return false;
        useUnNoted(item);
        Rs2GameObject.interact(objectID);
        return true;
    }

    /**
     * use unnoted inventory item on ingame object
     *
     * @param item   name of the item to use
     * @param object to use item on
     * @return
     */
    public static boolean useUnNotedItemOnObject(String item, TileObject object) {
        if (Rs2Bank.isOpen()) return false;
        useUnNoted(item);
        sleep(100);
        if (!isItemSelected()) return false;
        Rs2GameObject.interact(object);
        return true;
    }

    /**
     * use inventory item on ingame object
     *
     * @param item
     * @param objectID
     * @return
     */
    public static boolean useItemOnObject(int item, int objectID) {
        if (Rs2Bank.isOpen()) return false;
        use(item);
        sleep(100);
        if (!isItemSelected()) return false;
        Rs2GameObject.interact(objectID);
        return true;
    }

    /**
     *
     * @param itemId
     * @param npcID
     * @return
     */
    public static boolean useItemOnNpc(int itemId, int npcID) {
        if (Rs2Bank.isOpen()) return false;
        use(itemId);
        sleep(100);
        if (!isItemSelected()) return false;
        Rs2Npc.interact(npcID);
        return true;
    }

    /**
     *
     * @param name
     * @param exact
     * @return
     */
    public static Rs2Item getNotedItem(String name, boolean exact) {
        if (exact)
            return items().stream().filter(x -> x.name.equalsIgnoreCase(name) && x.isNoted).findFirst().orElse(null);
        else
            return items().stream().filter(x -> x.name.toLowerCase().contains(name.toLowerCase()) && x.isNoted).findFirst().orElse(null);
    }

    /**
     * @param name
     * @return
     */
    public static boolean hasNotedItem(String name) {
        return getNotedItem(name, false) != null;
    }

    /**
     * @param name
     * @param exact
     * @return
     */
    public static boolean hasNotedItem(String name, boolean exact) {
        return getNotedItem(name, exact) != null;
    }

    public static Rs2Item getUnNotedItem(String name, boolean exact) {
        if (exact)
            return items().stream().filter(x -> x.name.equalsIgnoreCase(name) && !x.isNoted).findFirst().orElse(null);
        else
            return items().stream().filter(x -> x.name.toLowerCase().contains(name.toLowerCase()) && !x.isNoted).findFirst().orElse(null);
    }

    public static boolean hasUnNotedItem(String name) {
        return getUnNotedItem(name, false) != null;
    }

    public static boolean hasUnNotedItem(String name, boolean exact) {
        return getUnNotedItem(name, exact) != null;
    }

    /**
     * Method executes menu actions
     *
     * @param rs2Item Current item to interact with
     * @param action  Action used on the item
     */
    private static void invokeMenu(Rs2Item rs2Item, String action) {
        if (rs2Item == null) return;

        Rs2Tab.switchToInventoryTab();
        Microbot.status = action + " " + rs2Item.name;

        int param0;
        int param1;
        int identifier = 3;
        MenuAction menuAction = MenuAction.CC_OP;
        if (!action.isEmpty()) {
            String[] actions;
            actions = rs2Item.inventoryActions;

            for (int i = 0; i < actions.length; i++) {
                if (action.equalsIgnoreCase(actions[i])) {
                    identifier = i + 1;
                    break;
                }
            }
        }
        param0 = rs2Item.slot;
        if (action.equalsIgnoreCase("drop") || action.equalsIgnoreCase("empty") || action.equalsIgnoreCase("check")) {
            identifier++;
        }
        if (Rs2Bank.isOpen()) {
            if (action.equalsIgnoreCase("eat")) {
                identifier += 7;
            } else {
                identifier += 6;
            }
            param1 = 983043;
        } else {
            param1 = 9764864;
        }
        if (isItemSelected()) {
            menuAction = MenuAction.WIDGET_TARGET_ON_WIDGET;
        } else if (action.equalsIgnoreCase("use")) {
            menuAction = MenuAction.WIDGET_TARGET;
        } else if (action.equalsIgnoreCase("cast")) {
            menuAction = MenuAction.WIDGET_TARGET_ON_WIDGET;
        }


        //grandexchange inventory
        if (action.equalsIgnoreCase("offer")) {
            identifier = 1;
            param1 = 30605312;
        }

        // Shop Inventory
        switch (action) {
            case "Value":
                // Logic to check Value of item
                identifier = 1;
                param1 = 19726336;
            case "Sell 1":
                // Logic to sell one item
                identifier = 2;
                param1 = 19726336;
                break;
            case "Sell 5":
                // Logic to sell five items
                identifier = 3;
                param1 = 19726336;
                break;
            case "Sell 10":
                // Logic to sell ten items
                identifier = 4;
                param1 = 19726336;
                break;
            case "Sell 50":
                // Logic to sell fifty items
                identifier = 5;
                param1 = 19726336;
                break;
        }

        Microbot.doInvoke(new NewMenuEntry(param0, param1, menuAction.getId(), identifier, rs2Item.id, rs2Item.name), new Rectangle(0, 0, 1, 1));
        //Rs2Reflection.invokeMenu(param0, param1, menuAction.getId(), identifier, rs2Item.id, action, target, -1, -1);
    }

    private static Widget getInventory() {
        final int BANK_PIN_INVENTORY_ITEM_CONTAINER = 17563648;
        return Microbot.getClientThread().runOnClientThread(() -> {
            Widget inventoryWidget = Microbot.getClient().getWidget(ComponentID.INVENTORY_CONTAINER);
            Widget bankInventoryWidget = Microbot.getClient().getWidget(ComponentID.BANK_INVENTORY_ITEM_CONTAINER);
            Widget bankPinInventoryWidget = Microbot.getClient().getWidget(BANK_PIN_INVENTORY_ITEM_CONTAINER);
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

    /**
     * Sell item to the shop
     *
     * @param itemName item to sell
     * @param quantity STRING quantity of items to sell
     * @return true if the item was successfully sold, false otherwise
     */
    public static boolean sellItem(String itemName, String quantity) {
        try {
            // Retrieve Rs2Item object corresponding to the item name
            Rs2Item rs2Item = items().stream()
                    .filter(item -> item.name.equalsIgnoreCase(itemName))
                    .findFirst().orElse(null);

            if (rs2Item == null) {
                System.out.println("Item not found in inventory.");
                return false;
            }

            String action = "Sell ";
            String actionAndQuantity = (action + quantity);
            invokeMenu(rs2Item, actionAndQuantity);
            return true;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }

    public static boolean waitForInventoryChanges() {
        isTrackingInventory = true;
        sleepUntil(() -> isInventoryChanged);
        if (isInventoryChanged) {
            isTrackingInventory = false;
            isInventoryChanged = false;
            return true;
        }
        isTrackingInventory = false;
        isInventoryChanged = false;
        return isInventoryChanged;
    }

}




