package net.runelite.client.plugins.microbot.util.bank;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.bank.enums.BankLocation;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.menu.NewMenuEntry;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static net.runelite.api.widgets.ComponentID.BANK_INVENTORY_ITEM_CONTAINER;
import static net.runelite.api.widgets.ComponentID.BANK_ITEM_CONTAINER;
import static net.runelite.client.plugins.microbot.Microbot.updateItemContainer;
import static net.runelite.client.plugins.microbot.util.Global.*;

@SuppressWarnings("unused")
public class Rs2Bank {
    public static List<Rs2Item> bankItems = new ArrayList<Rs2Item>();
    private static final int X_AMOUNT_VARBIT = 3960;
    private static final int SELECTED_OPTION_VARBIT = 6590;
    private static final int HANDLE_X_SET = 5;
    private static final int HANDLE_X_UNSET = 6;
    private static final int HANDLE_ALL = 7;

    /**
     * Container describes from what interface the action happens
     * eg: withdraw means the contailer will be the bank container
     * eg: deposit means that the container will be the inventory container
     * and so on...
     */
    private static int container = -1;

    /**
     * Executes menu swapping for a specific rs2Item and entry index.
     *
     * @param entryIndex The index of the entry to swap.
     * @param rs2Item    The ItemWidget associated with the menu swap.
     */
    public static void invokeMenu(int entryIndex, Rs2Item rs2Item) {
        int identifier = entryIndex;

        if (container == ComponentID.BANK_INVENTORY_ITEM_CONTAINER) {
            identifier = identifier + 1;
        }
        Microbot.doInvoke(new NewMenuEntry(rs2Item.slot, container, MenuAction.CC_OP.getId(), identifier, rs2Item.id, rs2Item.name), new Rectangle(1, 1, Microbot.getClient().getCanvasWidth(), Microbot.getClient().getCanvasHeight()));
        // MenuEntryImpl(getOption=Wear, getTarget=<col=ff9040>Amulet of glory(4)</col>, getIdentifier=9, getType=CC_OP_LOW_PRIORITY, getParam0=1, getParam1=983043, getItemId=1712, isForceLeftClick=false, isDeprioritized=false)
        // Rs2Reflection.invokeMenu(rs2Item.slot, container, MenuAction.CC_OP.getId(), identifier, rs2Item.id, "Withdraw-1", rs2Item.name, -1, -1);
    }

    /**
     * Closes the bank interface if it is open.
     *
     * @return true if the bank interface was open and successfully closed, false otherwise.
     */
    public static boolean isOpen() {
        if (Rs2Widget.hasWidget("Please enter your PIN")) {
            Microbot.getNotifier().notify("[ATTENTION] Please enter your bankpin so the script can continue.");
            sleep(5000);
            return false;
        }
        return Rs2Widget.findWidget("Rearrange mode", null) != null;
    }

    /**
     * Closes the bank interface if it is open.
     *
     * @return true if the bank interface was open and successfully closed, false otherwise.
     */
    public static boolean closeBank() {
        if (!isOpen()) return false;
        Rs2Widget.clickChildWidget(786434, 11);
        sleepUntilOnClientThread(() -> !isOpen());

        return true;
    }

    /**
     * Finds a bank item widget in the bank interface by its partial name match.
     *
     * @param name The name of the item to find.
     * @return The bank item widget if found, or null if not found.
     */
    public static Rs2Item findBankItem(String name) {
        return findBankItem(name, false);
    }

    /**
     * check if the player has a bank item identified by id
     *
     * @param id the item id
     * @return boolean
     */
    public static boolean hasItem(int id) {
        return findBankItem(id) != null;
    }

    /**
     * check if the player has a bank item identified by contains name
     *
     * @param name the item name
     * @return boolean
     */
    public static boolean hasItem(String name) {
        return findBankItem(name) != null;
    }

    /**
     * check if the player has a bank item identified by exact name.
     *
     * @param name the item name
     * @return boolean
     */
    public static boolean hasBankItem(String name) {
        return findBankItem(name, false, 1) != null;
    }

    /**
     * check if the player has a bank item identified by exact name.
     *
     * @param name the item name
     * @return boolean
     */
    public static boolean hasBankItem(String name, int amount) {
        return hasBankItem(name, amount, false);
    }

    /**
     * check if the player has a bank item identified by exact name.
     *
     * @param name the item name
     * @return boolean
     */
    public static boolean hasBankItem(String name, int amount, boolean exact) {
        return findBankItem(name, exact, amount) != null;
    }

    /**
     * check if the player has a bank item identified by exact name.
     *
     * @param name  the item name
     * @param exact exact search based on equalsIgnoreCase
     * @return boolean
     */
    public static boolean hasBankItem(String name, boolean exact) {
        return findBankItem(name, exact) != null;
    }

    /**
     * Deposits all equipped items into the bank.
     * This method finds and clicks the "Deposit Equipment" button in the bank interface.
     */
    public static void depositEquipment() {
        Widget widget = Rs2Widget.findWidget(SpriteID.BANK_DEPOSIT_EQUIPMENT, null);
        if (widget == null) return;

        Microbot.getMouse().click(widget.getBounds());
    }

    /**
     * Deposits one item quickly into the bank by its ItemWidget.
     *
     * @param rs2Item The ItemWidget representing the item to deposit.
     */
    private static void depositOne(Rs2Item rs2Item) {
        if (!isOpen()) return;
        if (rs2Item == null) return;
        if (!Rs2Inventory.hasItem(rs2Item.id)) return;
        container = BANK_INVENTORY_ITEM_CONTAINER;

        if (Microbot.getVarbitValue(SELECTED_OPTION_VARBIT) == 0) {
            invokeMenu(1, rs2Item);
        } else {
            invokeMenu(2, rs2Item);
        }
    }

    /**
     * Deposits one item quickly by its ID.
     *
     * @param id The ID of the item to deposit.
     */
    public static void depositOne(int id) {
        Rs2Item rs2Item = Rs2Inventory.get(id);
        if (rs2Item == null) return;
        depositOne(rs2Item);
    }

    /**
     * Deposits one item quickly by its name with a partial name match.
     *
     * @param name The name of the item to deposit.
     */
    public static void depositOne(String name, boolean exact) {
        Rs2Item rs2Item = Rs2Inventory.get(name, exact);
        if (rs2Item == null) return;
        depositOne(rs2Item);
    }

    /**
     * Deposits one item quickly by its name with a partial name match.
     *
     * @param name The name of the item to deposit.
     */
    public static void depositOne(String name) {
        depositOne(name, false);
    }

    /**
     * Deposits a specified amount of an item into the inventory.
     * This method checks if the bank window is open, if the provided ItemWidget is valid and
     * if the player has the item in their inventory. If all conditions are met, it calls the
     * 'handleAmount' method to deposit the specified amount of the item into the inventory.
     *
     * @param rs2Item item to handle
     * @param amount  amount to deposit
     */
    private static void depositX(Rs2Item rs2Item, int amount) {
        if (!isOpen()) return;
        if (rs2Item == null) return;
        if (!Rs2Inventory.hasItem(rs2Item.id)) return;
        container = BANK_INVENTORY_ITEM_CONTAINER;

        handleAmount(rs2Item, amount);
    }

    /**
     * Handles the amount for an item widget.
     * <p>
     * This method checks if the current varbit value matches the specified amount.
     * If it does, it executes the menu swapper with the HANDLE_X_SET option.
     * If it doesn't match, it executes the menu swapper with the HANDLE_X_UNSET option,
     * enters the specified amount using the VirtualKeyboard, and presses Enter.
     *
     * @param rs2Item The item to handle.
     * @param amount  The desired amount to set.
     */
    private static void handleAmount(Rs2Item rs2Item, int amount) {
        handleAmount(rs2Item, amount, false);
    }

    /**
     * Handles the amount for an item widget.
     * <p>
     * This method checks if the current varbit value matches the specified amount.
     * If it does, it executes the menu swapper with the HANDLE_X_SET option.
     * If it doesn't match, it executes the menu swapper with the HANDLE_X_UNSET option,
     * enters the specified amount using the VirtualKeyboard, and presses Enter.
     *
     * @param rs2Item The item to handle.
     * @param amount  The desired amount to set.
     * @param safe    will wait for item to appear in inventory before continuing if set to true
     */
    private static void handleAmount(Rs2Item rs2Item, int amount, boolean safe) {
        int inventorySize = Rs2Inventory.size();
        if (Microbot.getVarbitValue(X_AMOUNT_VARBIT) == amount) {
            invokeMenu(HANDLE_X_SET, rs2Item);
            if (safe)
                sleepUntil(() -> inventorySize != Rs2Inventory.size(), 2500);
        } else {
            invokeMenu(HANDLE_X_UNSET, rs2Item);

            sleep(1200);
            Rs2Keyboard.typeString(String.valueOf(amount));
            Rs2Keyboard.enter();
            sleepUntil(() -> Rs2Inventory.hasItem(rs2Item.id), 2500);
        }
    }

    /**
     * deposit x amount of items identified by its name
     * set exact to true if you want to identify by its exact name
     *
     * @param id param amount
     */
    public static void depositX(int id, int amount) {
        Rs2Item rs2Item = Rs2Inventory.get(id);
        if (rs2Item == null) return;
        depositX(rs2Item, amount);
    }

    /**
     * deposit x amount of items identified by its name
     * set exact to true if you want to identify by its exact name
     *
     * @param name param amount
     *             param exact
     */
    private static void depositX(String name, int amount, boolean exact) {
        Rs2Item rs2Item = Rs2Inventory.get(name, exact);
        if (rs2Item == null) return;
        depositX(rs2Item, amount);
    }

    /**
     * deposit x amount of items identified by its name
     *
     * @param name param amount
     */
    public static void depositX(String name, int amount) {
        Rs2Item rs2Item = Rs2Inventory.get(name);
        if (rs2Item == null) return;
        depositX(rs2Item, amount);
    }

    /**
     * deposit all items identified by its ItemWidget
     *
     * @param rs2Item item to deposit
     * @returns did deposit anything
     */
    private static boolean depositAll(Rs2Item rs2Item) {
        if (!isOpen()) return false;
        if (rs2Item == null) return false;
        if (!Rs2Inventory.hasItem(rs2Item.id)) return false;
        container = BANK_INVENTORY_ITEM_CONTAINER;

        invokeMenu(HANDLE_ALL, rs2Item);
        return true;
    }

    /**
     * deposit all items identified by its id
     *
     * @param id searches based on the id
     * @return true if anything deposited
     */
    public static boolean depositAll(int id) {
        Rs2Item rs2Item = Rs2Inventory.get(id);
        if (rs2Item == null) return false;
        return depositAll(rs2Item);
    }

    public static boolean depositAll(Predicate<Rs2Item> predicate) {
        for (Rs2Item item :
                Rs2Inventory.items().stream().filter(predicate).collect(Collectors.toList())) {
            if (item == null) continue;
            depositAll(item);
            return true;
        }
        return false;
    }

    /**
     * deposit all items identified by its name
     * set exact to true if you want to be identified by its exact name
     *
     * @param name  name to search
     * @param exact does an exact search equalsIgnoreCase
     */
    public static void depositAll(String name, boolean exact) {
        Rs2Item rs2Item = Rs2Inventory.get(name);
        if (rs2Item == null) return;
        depositAll(rs2Item);
    }

    /**
     * deposit all items identified by its name
     *
     * @param name item name to search
     */
    public static void depositAll(String name) {
        depositAll(name, false);
    }

    /**
     * deposit all items
     */
    public static void depositAll() {
        Microbot.status = "Deposit all";
        if (Rs2Inventory.isEmpty()) return;
        if (!Rs2Bank.isOpen()) return;

        Widget widget = Rs2Widget.findWidget(SpriteID.BANK_DEPOSIT_INVENTORY, null);
        if (widget == null) return;

        Microbot.getMouse().click(widget.getBounds());
        sleepUntil(Rs2Inventory::isEmpty);
    }

    public static boolean depositAllExcept(Integer... ids) {
        return depositAll(x -> Arrays.stream(ids).noneMatch(id -> id == x.id));
    }

    public static boolean depositAllExcept(String... names) {
        return depositAll(x -> Arrays.stream(names).noneMatch(name -> name.equalsIgnoreCase(x.name)));
    }


    /**
     * withdraw one item identified by its ItemWidget.
     *
     * @param rs2Item item to withdraw
     */
    private static void withdrawOne(Rs2Item rs2Item) {
        if (!isOpen()) return;
        if (rs2Item == null) return;
        if (Rs2Inventory.isFull()) return;
        container = BANK_ITEM_CONTAINER;

        if (Microbot.getVarbitValue(SELECTED_OPTION_VARBIT) == 0) {
            invokeMenu(1, rs2Item);
        } else {
            invokeMenu(2, rs2Item);
        }
    }

    /**
     * withdraw one item identified by its id.
     *
     * @param id the item id
     */
    public static void withdrawOne(int id) {
        withdrawOne(findBankItem(id));
    }

    public static void withdrawItem(String name) {
        withdrawOne(name);
    }

    public static void withdrawItem(int id) {
        withdrawOne(id);
    }

    public static void withdrawItem(boolean checkInv, int id) {
        if (checkInv && Rs2Inventory.hasItem(id)) return;
        withdrawOne(id);
    }

    public static void withdrawItem(boolean checkInv, String name) {
        if (checkInv && !Rs2Bank.hasItem(name)) return;
        withdrawOne(name);
    }

    /**
     * withdraw one item identified by its name.
     * set exact to true if you want to identify by the exact name.
     *
     * @param name  the item name
     * @param exact boolean
     */
    public static void withdrawOne(String name, boolean exact) {
        withdrawOne(findBankItem(name, exact));
    }

    /**
     * withdraw one item identified by its name
     *
     * @param name the item name
     */
    public static void withdrawOne(String name) {
        withdrawOne(name, false);
    }

    public static void withdrawOne(String name, int sleepTime) {
        withdrawOne(name, false);
        sleep(sleepTime);
    }

    /**
     * withdraw x amount of items identified by its ItemWidget.
     *
     * @param rs2Item Item to handle
     * @param amount  int
     */
    private static void withdrawXItem(Rs2Item rs2Item, int amount) {
        if (!isOpen()) return;
        if (rs2Item == null) return;
        if (Rs2Inventory.isFull()) return;
        container = BANK_ITEM_CONTAINER;

        handleAmount(rs2Item, amount);
    }

    /**
     * Checks inventory before withdrawing item
     *
     * @param checkInv check inventory before withdrawing item
     * @param id       item id
     * @param amount   amount to withdraw
     */
    public static void withdrawX(boolean checkInv, int id, int amount) {
        if (checkInv && !Rs2Bank.hasItem(id)) return;
        withdrawX(id, amount);
    }

    /**
     * Checks inventory before withdrawing item
     *
     * @param checkInv check inventory before withdrawing item
     * @param name     item name
     * @param amount   amount to withdraw
     */
    public static void withdrawX(boolean checkInv, String name, int amount) {
        withdrawX(checkInv, name, amount, false);
    }

    /**
     * Checks inventory before withdrawing item
     *
     * @param checkInv check inventory before withdrawing item
     * @param name     item name
     * @param amount   amount to withdraw
     * @param exact    exact search based on equalsIgnoreCase
     */
    public static void withdrawX(boolean checkInv, String name, int amount, boolean exact) {
        if (checkInv && Rs2Inventory.hasItem(name)) return;
        withdrawX(name, amount, exact);
    }

    /**
     * withdraw x amount of items identified by its id.
     *
     * @param id     item id to search
     * @param amount amount to withdraw
     */
    public static void withdrawX(int id, int amount) {
        withdrawXItem(findBankItem(id), amount);
    }

    /**
     * withdraw x amount of items identified by its name.
     * set exact to true if you want to identify an item by its exact name.
     *
     * @param name   item name to search
     * @param amount amount to withdraw
     * @param exact  exact search based on equalsIgnoreCase
     */
    private static void withdrawX(String name, int amount, boolean exact) {
        withdrawXItem(findBankItem(name, exact), amount);
    }

    /**
     * withdraw x amount of items identified by its name
     *
     * @param name   item name to search
     * @param amount amount to withdraw
     */
    public static void withdrawX(String name, int amount) {
        withdrawXItem(findBankItem(name, false), amount);
    }

    /**
     * withdraw all items identified by its ItemWidget.
     *
     * @param rs2Item Item to withdraw
     */
    private static void withdrawAll(Rs2Item rs2Item) {
        if (!isOpen()) return;
        if (rs2Item == null) return;
        if (Rs2Inventory.isFull()) return;
        container = BANK_ITEM_CONTAINER;

        invokeMenu(HANDLE_ALL, rs2Item);
    }

    public static void withdrawItemAll(boolean checkInv, String name) {
        if (checkInv && !Rs2Bank.hasItem(name)) return;
        withdrawItemAll(name);
    }

    public static void withdrawItemAll(String name) {
        withdrawAll(name);
    }

    /**
     * withdraw all items identified by its id.
     *
     * @param id item id to search
     */
    public static void withdrawAll(int id) {
        withdrawAll(findBankItem(id));
    }

    /**
     * withdraw all items identified by its name
     * set the boolean exact to true if you want to identify the item by the exact name
     *
     * @param name  item name to search
     * @param exact exact search based on equalsIgnoreCase
     */
    public static void withdrawAll(String name, boolean exact) {
        withdrawAll(findBankItem(name, exact));
    }

    /**
     * withdraw all items identified by its name
     *
     * @param name item name to search
     */
    public static void withdrawAll(String name) {
        withdrawAll(findBankItem(name, false));
    }

    /**
     * wear an item identified by its ItemWidget.
     *
     * @param rs2Item item to wear
     */
    private static void wearItem(Rs2Item rs2Item) {
        if (!isOpen()) return;
        if (rs2Item == null) return;
        container = BANK_INVENTORY_ITEM_CONTAINER;

        invokeMenu(8, rs2Item);
    }

    /**
     * wear an item identified by the name contains
     *
     * @param name item name to search based on contains(string)
     */
    public static void wearItem(String name) {
        wearItem(Rs2Inventory.get(name, false));
    }

    /**
     * wear an item identified by its exact name.
     *
     * @param name  item name to search
     * @param exact exact search based on equalsIgnoreCase
     */
    public static void wearItem(String name, boolean exact) {
        wearItem(Rs2Inventory.get(name, exact));
    }

    /**
     * withdraw all and equip item identified by its id.
     *
     * @param id item id
     */
    public static void withdrawXAndEquip(int id, int amount) {
        if (Rs2Equipment.isWearing(id)) return;
        withdrawX(id, amount);
        sleepUntil(() -> Rs2Inventory.hasItem(id));
        wearItem(id);
    }

    /**
     * withdraw all and equip item identified by its id.
     *
     * @param name item name
     */
    public static void withdrawAllAndEquip(String name) {
        if (Rs2Equipment.isWearing(name)) return;
        withdrawAll(name);
        sleepUntil(() -> Rs2Inventory.hasItem(name));
        wearItem(name);
    }

    /**
     * withdraw all and equip item identified by its id.
     *
     * @param id item id
     */
    public static void withdrawAllAndEquip(int id) {
        if (Rs2Equipment.hasEquipped(id)) return;
        withdrawAll(id);
        sleepUntil(() -> Rs2Inventory.hasItem(id));
        wearItem(id);
    }

    /**
     * withdraw and equip item identified by its id.
     *
     * @param name item name
     */
    public static void withdrawAndEquip(String name) {
        if (Rs2Equipment.isWearing(name)) return;
        withdrawOne(name);
        sleepUntil(() -> Rs2Inventory.hasItem(name), 1000);
        wearItem(name);
    }

    /**
     * withdraw and equip item identified by its id.
     *
     * @param id item id
     */
    public static void withdrawAndEquip(int id) {
        if (Rs2Equipment.hasEquipped(id)) return;
        withdrawOne(id);
        sleepUntil(() -> Rs2Inventory.hasItem(id));
        wearItem(id);
    }

    /**
     * withdraw items identified by one or more ids
     *
     * @param ids item ids
     */
    public static void withdrawItems(int... ids) {
        for (int id : ids) {
            withdrawOne(id);
        }
    }

    /**
     * Deposit items identified by one or more ids
     *
     * @param ids item ids
     */
    public static void depositItems(int... ids) {
        for (int id : ids) {
            depositOne(id);
        }
    }

    /**
     * Opens the bank searching for bank booth first, then chest and lastly npc
     *
     * @return True if bank was successfully opened, otherwise false.
     */
    public static boolean openBank() {
        Microbot.status = "Opening bank";
        try {
            if (Microbot.getClient().isWidgetSelected())
                Microbot.getMouse().click();
            if (isOpen()) return true;
            boolean action;
            GameObject bank = Rs2GameObject.findBank();
            if (bank == null) {
                GameObject chest = Rs2GameObject.findChest();
                if (chest == null) {
                    NPC npc = Rs2Npc.getNpc("banker");
                    if (npc == null) return false;
                    action = Rs2Npc.interact(npc, "bank");
                } else {
                    action = Rs2GameObject.interact(chest, "use");
                }
            } else {
                action = Rs2GameObject.interact(bank, "bank");
            }
            sleepUntil(Rs2Bank::isOpen);
            if (action) {
                sleepUntil(() -> isOpen() || Rs2Widget.hasWidget("Please enter your PIN"), 5000);
                sleep(600, 1000);
            }
            return action;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return false;
    }

    public static boolean openBank(NPC npc) {
        Microbot.status = "Opening bank";
        try {
            if (isOpen()) return true;
            if (Rs2Inventory.isItemSelected()) Microbot.getMouse().click();

            if (npc == null) return false;

            boolean interactResult = Rs2Npc.interact(npc, "bank");

            if (!interactResult) {
                return false;
            }

            sleepUntil(Rs2Bank::isOpen);
            sleep(600, 1000);
            return true;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return false;
    }

    /**
     * open bank identified by tile object.
     *
     * @param object TileObject
     * @return true if bank is open
     */
    public static boolean openBank(TileObject object) {
        Microbot.status = "Opening bank";
        try {
            if (isOpen()) return true;
            if (Rs2Inventory.isItemSelected()) Microbot.getMouse().click();

            if (object == null) return false;

            boolean interactResult = Rs2GameObject.interact(object, "bank");

            if (!interactResult) {
                return false;
            }

            sleepUntil(Rs2Bank::isOpen);
            sleep(600, 1000);
            return true;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return false;
    }

    /**
     * Sets the values of the inventoryWidget
     *
     * @param id item id
     */
    private static void handleWearItem(int id) {
        Rs2Item rs2Item = Rs2Inventory.get(id);
        if (rs2Item == null) return;
        container = ComponentID.BANK_INVENTORY_ITEM_CONTAINER;

        invokeMenu(8, rs2Item);
    }

    /**
     * Tries to wear an item identified by its id.
     *
     * @param id item id
     */
    public static void wearItem(int id) {
        handleWearItem(id);
    }

    /**
     * find an item in the bank identified by its id.
     *
     * @param id item id to find
     * @return bankItem
     */
    @SuppressWarnings("UnnecessaryLocalVariable")
    private static Rs2Item findBankItem(int id) {
        if (bankItems == null) return null;
        if (bankItems.stream().findAny().isEmpty()) return null;

        Rs2Item bankItem = bankItems.stream().filter(x -> x.id == id).findFirst().orElse(null);

        return bankItem;
    }

    /**
     * Finds an item in the bank based on its name.
     *
     * @param name  The name of the item.
     * @param exact If true, requires an exact name match.
     * @return The item widget, or null if the item isn't found.
     */
    @SuppressWarnings("UnnecessaryLocalVariable")
    private static Rs2Item findBankItem(String name, boolean exact) {
        return findBankItem(name, exact, 1);
    }

    /**
     * Finds an item in the bank based on its name.
     *
     * @param name   The name of the item.
     * @param exact  If true, requires an exact name match.
     * @param amount the amount needed to find in the bank
     * @return The item widget, or null if the item isn't found.
     */
    @SuppressWarnings("UnnecessaryLocalVariable")
    private static Rs2Item findBankItem(String name, boolean exact, int amount) {
        if (bankItems == null) return null;
        if (bankItems.stream().findAny().isEmpty()) return null;

        final String lowerCaseName = name.toLowerCase();

        Rs2Item bankItem = bankItems.stream().filter(x -> exact
                ? x.name.equalsIgnoreCase(lowerCaseName)
                : x.name.toLowerCase().contains(lowerCaseName)).findFirst().orElse(null);

        if (bankItem == null || bankItem.quantity < amount)
            return null;

        return bankItem;
    }

    /**
     * Get the nearest bank
     *
     * @return BankLocation
     */
    public static BankLocation getNearestBank() {
        BankLocation nearest = null;
        double dist = Double.MAX_VALUE;
        int y = Microbot.getClient().getLocalPlayer().getWorldLocation().getY();
        boolean isInCave = Microbot.getClient().getLocalPlayer().getWorldLocation().getY() > 9000;
        if (isInCave) {
            y -= 6300; //minus -6300 to set y to the surface
        }
        WorldPoint local = new WorldPoint(Microbot.getClient().getLocalPlayer().getWorldLocation().getX(), y, Microbot.getClient().getPlane());
        for (BankLocation bankLocation : BankLocation.values()) {
            double currDist = local.distanceTo2D(bankLocation.getWorldPoint());
            if (nearest == null || currDist < dist) {
                dist = currDist;
                nearest = bankLocation;
            }
        }
        return nearest;
    }

    /**
     * Walk to the closest bank
     *
     * @return true if player location is less than 4 tiles away from the bank location
     */
    public static boolean walkToBank() {
        if (Rs2Bank.isOpen()) return true;
        Rs2Player.toggleRunEnergy(true);
        BankLocation bankLocation = getNearestBank();
        Microbot.status = "Walking to nearest bank " + bankLocation.toString();
        Rs2Walker.walkTo(bankLocation.getWorldPoint());
        return bankLocation.getWorldPoint().distanceTo2D(Microbot.getClient().getLocalPlayer().getWorldLocation()) <= 8;
    }

    /**
     * Use bank or chest
     *
     * @return true if bank is opened
     */
    public static boolean useBank() {
        return openBank();
    }

    /**
     * Updates the bank items in memory based on the provided event.
     *
     * @param e The event containing the latest bank items.
     */
    public static void storeBankItemsInMemory(ItemContainerChanged e) {
        List<Rs2Item> list = updateItemContainer(InventoryID.BANK.getId(), e);
        if (list != null)
            bankItems = list;
    }

    public static boolean handleBankPin(String pin) {
        Widget bankPinWidget = Rs2Widget.getWidget(ComponentID.BANK_PIN_CONTAINER);

        boolean isBankPinVisible = Microbot.getClientThread().runOnClientThread(() -> bankPinWidget != null && !bankPinWidget.isHidden());

        if (isBankPinVisible) {
            Rs2Keyboard.typeString(pin);
            Rs2Keyboard.enter();
            return true;
        }
        return false;
    }
}
