package net.runelite.client.plugins.microbot.util.depositbox;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameObject;
import net.runelite.api.SpriteID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static net.runelite.api.widgets.ComponentID.DEPOSIT_BOX_INVENTORY_ITEM_CONTAINER;
import static net.runelite.client.plugins.microbot.util.Global.*;

@Slf4j
public class Rs2DepositBox {

    private static final int DEPOSIT_ALL_BUTTON_ID = 1920;
    private static final int DEPOSIT_INVENTORY_ID = 1921;
    private static final int DEPOSIT_EQUIPMENT_ID = 1922;
    private static final int CLOSE_BUTTON_PARENT_ID = DEPOSIT_BOX_INVENTORY_ITEM_CONTAINER - 1;

    /**
     * Checks if the deposit box interface is open.
     *
     * @return true if the deposit box interface is open, false otherwise.
     */
    public static boolean isOpen() {
        return Rs2Widget.isDepositBoxWidgetOpen();
    }

    /**
     * Closes the deposit box interface.
     *
     * @return true if the deposit box interface was successfully closed, false otherwise.
     */
    public static boolean closeDepositBox() {
        if (!isOpen()) return false;
        Rs2Widget.clickChildWidget(CLOSE_BUTTON_PARENT_ID, 11); // Assuming close button ID is 11
        sleepUntilOnClientThread(() -> !isOpen());
        return true;
    }

    /**
     * Opens the deposit box interface by interacting with a nearby deposit box.
     *
     * @return true if the deposit box was successfully opened, false otherwise.
     */
    public static boolean openDepositBox() {
        Microbot.status = "Opening deposit box";
        try {
            if (Microbot.getClient().isWidgetSelected())
                Microbot.getMouse().click();
            // Assuming interaction logic with a nearby deposit box
            if (isOpen()) return true;
            GameObject depositBox = Rs2GameObject.findDepositBox();
            boolean action = false;
            if (depositBox != null) {
                action = Rs2GameObject.interact(depositBox, "Deposit");
            }
            if (action) {
                sleepUntil(Rs2DepositBox::isOpen, 2500);
            }
            return action;
        } catch (Exception e) {
            Microbot.log("Error opening deposit box: " + e.getMessage());
        }
        return false;
    }

    /**
     * Deposits all items in the inventory into the deposit box.
     */
    public static void depositAll() {
        Microbot.status = "Depositing all items";
        if (Rs2Inventory.isEmpty()) return;
        if (!isOpen()) return;
        Widget depositAllWidget = Rs2Widget.findWidget(SpriteID.BANK_DEPOSIT_INVENTORY, null);
        if (depositAllWidget == null) return;

        Microbot.getMouse().click(depositAllWidget.getBounds());
        sleepUntil(Rs2Inventory::isEmpty);
    }

    public static boolean depositAll(Predicate<Rs2Item> predicate, boolean fastDeposit) {
        boolean result = false;
        List<Rs2Item> items = Rs2Inventory.items().stream().filter(predicate).distinct().collect(Collectors.toList());
        for (Rs2Item item : items) {
            if (item == null) continue;
            depositItem(item);
            if (!fastDeposit)
                sleep(100, 300);
            result = true;
        }
        return result;
    }

    public static boolean depositAll(Predicate<Rs2Item> predicate) {
        return depositAll(predicate, false);
    }

    /**
     * Deposits all items in the player's inventory into the bank, except for the items with the specified IDs.
     * This method uses a lambda function to filter out the items with the specified IDs from the deposit operation.
     *
     * @param ids The IDs of the items to be excluded from the deposit.
     *
     * @return true if any items were deposited, false otherwise.
     */
    public static boolean depositAllExcept(Integer... ids) {
        return depositAll(x -> Arrays.stream(ids).noneMatch(id -> id == x.id));
    }

    public static boolean depositAllExcept(boolean fastDeposit, Integer... ids) {
        return depositAll(x -> Arrays.stream(ids).noneMatch(id -> id == x.id), fastDeposit);
    }

    /**
     * Deposits all items in the player's inventory into the bank, except for the items with the specified names.
     * This method uses a lambda function to filter out the items with the specified names from the deposit operation.
     *
     * @param names The names of the items to be excluded from the deposit.
     *
     * @return true if any items were deposited, false otherwise.
     */
    public static boolean depositAllExcept(String... names) {
        return depositAll(x -> Arrays.stream(names).noneMatch(name -> name.equalsIgnoreCase(x.name)));
    }

    public static boolean depositAllExcept(boolean fastDeposit, String... names) {
        return depositAll(x -> Arrays.stream(names).noneMatch(name -> name.equalsIgnoreCase(x.name)), fastDeposit);
    }

    /**
     * Deposits all items in the player's inventory into the bank, except for the items with the specified names.
     * This method uses a lambda function to filter out the items with the specified names from the deposit operation.
     *
     * @param names The names of the items to be excluded from the deposit.
     *
     * @return true if any items were deposited, false otherwise.
     */
    public static boolean depositAllExcept(List<String> names) {
        return depositAll(x -> names.stream().noneMatch(name -> name.equalsIgnoreCase(x.name)));
    }

    public static boolean depositAllExcept(boolean fastDeposit, List<String> names) {
        return depositAll(x -> names.stream().noneMatch(name -> name.equalsIgnoreCase(x.name)), fastDeposit);
    }

    /**
     * Deposits all items in the player's inventory into the bank, except for the items with the specified names.
     * This method uses a lambda function to filter out the items with the specified names from the deposit operation.
     * It also allows for a delay between deposit operations.
     *
     * @param names The names of the items to be excluded from the deposit.
     *
     * @return true if any items were deposited, false otherwise.
     */
    public static boolean depositAllExcept(boolean exact, boolean fastDeposit, String... names) {
        if (!exact)
            return depositAll(x -> Arrays.stream(names).noneMatch(name -> x.name.contains(name.toLowerCase())), fastDeposit);
        else
            return depositAll(x -> Arrays.stream(names).noneMatch(name -> name.equalsIgnoreCase(x.name)), fastDeposit);
    }


    /**
     * Deposits a specific item by its name.
     *
     * @param itemName the name of the item to deposit.
     */
    public static void depositItem(String itemName) {
        Rs2Item item = Rs2Inventory.get(itemName);
        if (item == null) return;
        depositItem(item);
    }

    /**
     * Deposits a specific item by its ID.
     *
     * @param itemId the ID of the item to deposit.
     */
    public static void depositItem(int itemId) {
        Rs2Item item = Rs2Inventory.get(itemId);
        if (item == null) return;
        depositItem(item);
    }

    /**
     * Deposits a item quickly by its name with a partial or exact name match.
     * Name and a boolean to determine if the name should be an exact match.
     *
     * @param itemName   the name of the item to deposit.
     * @param exactMatch true if the name should be an exact match, false otherwise.
     */
    public static void depositItem(String itemName, boolean exactMatch) {
        Rs2Item item = Rs2Inventory.get(itemName, exactMatch);
        if (item == null) return;
        depositItem(item);
    }


    /**
     * Deposits a specific item by its Rs2Item reference.
     *
     * @param rs2Item the Rs2Item to deposit.
     */
    public static void depositItem(Rs2Item rs2Item) {
        if (rs2Item == null || !isOpen()) return;
        if (!Rs2Inventory.hasItem(rs2Item.id)) return;
        Rs2Inventory.interact(rs2Item, "Deposit-All");
    }

    /**
     * Deposits all equipment into the deposit box.
     */
    public static void depositEquipment() {
        Widget widget = Rs2Widget.findWidget(SpriteID.BANK_DEPOSIT_EQUIPMENT, null);
        if (widget == null) return;

        Microbot.getMouse().click(widget.getBounds());
    }
}
