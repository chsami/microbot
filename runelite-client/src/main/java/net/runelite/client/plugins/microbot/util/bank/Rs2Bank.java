package net.runelite.client.plugins.microbot.util.bank;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.bank.enums.BankLocation;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.menu.Rs2Menu;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.reflection.Rs2Reflection;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.microbot.util.widget.models.ItemWidget;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CopyOnWriteArrayList;

import static net.runelite.client.plugins.microbot.Microbot.updateItemContainer;
import static net.runelite.client.plugins.microbot.util.Global.*;

public class Rs2Bank {
    public static CopyOnWriteArrayList<ItemWidget> bankItems = new CopyOnWriteArrayList<>();
    private static final int BANK_CONTAINER_ID = 95;
    private static final int BANK_WIDGET_ID = 786445;
    private static final int INVENTORY_WIDGET_ID = 983043;
    private static final int X_AMOUNT_VARBIT = 3960;
    private static final int SELECTED_OPTION_VARBIT = 6590;
    private static final int HANDLE_X_SET = 5;
    private static final int HANDLE_X_UNSET = 6;
    private static final int HANDLE_ALL = 7;
    private static ItemWidget bankWidget;

    /**
     * Prepares a MenuEntry for menu swapping with specific parameters.
     *
     * @param menuEntry The MenuEntry to be modified for menu swapping.
     * @throws InvocationTargetException If there's an issue with invoking a method.
     * @throws IllegalAccessException    If there's an illegal access attempt.
     */
    public static void handleMenuSwapper(MenuEntry menuEntry) throws InvocationTargetException, IllegalAccessException {
        if (bankWidget == null) return;
        Rs2Reflection.setItemId(menuEntry, bankWidget.getItemId());
        menuEntry.setOption("Withdraw-1"); // Should probably be changed. Doesn't matter though.

        if (bankWidget.getId() == INVENTORY_WIDGET_ID) {
            menuEntry.setIdentifier(bankWidget.getSlot() + 1);
        } else {
            menuEntry.setIdentifier(bankWidget.getSlot());
        }

        menuEntry.setParam0(bankWidget.getIndex());
        menuEntry.setParam1(bankWidget.getId());
        menuEntry.setTarget(bankWidget.getName());
        menuEntry.setType(MenuAction.CC_OP);
    }

    /**
     * Executes menu swapping for a specific widget and entry index.
     *
     * @param widgetId   The ID of the widget to interact with.
     * @param entryIndex The index of the entry to swap.
     * @param widget     The ItemWidget associated with the menu swap.
     */
    public static void execMenuSwapper(int widgetId, int entryIndex, ItemWidget widget) {
        Rs2Bank.bankWidget = widget;
        Rs2Bank.bankWidget.setSlot(entryIndex);
        Rs2Bank.bankWidget.setId(widgetId);
        if (isOpen()) {
            Widget randomClickWidget = Random.random(1, 10) < 5 ? Rs2Widget.findWidget("Rearrange mode", null) : Rs2Widget.findWidget("The bank of", null);
            if (randomClickWidget == null) {
                Microbot.getMouse().clickFast((int) widget.getBounds().getCenterX(), (int) widget.getBounds().getCenterY());
            } else {
                Microbot.getMouse().clickFast((int) randomClickWidget.getBounds().getCenterX(), (int) randomClickWidget.getBounds().getCenterY());
            }
        }
        sleep(50);
        Rs2Bank.bankWidget = null;
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
    public static Widget findBankItem(String name) {
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
    public static boolean hasBankItemExact(String name) {
        return findBankItem(name, true) != null;
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
     * @param w The ItemWidget representing the item to deposit.
     */
    private static void depositOne(ItemWidget w) {
        if (!isOpen()) return;
        if (w == null) return;
        if (!Inventory.hasItem(w.getItemId())) return;

        if (Microbot.getVarbitValue(SELECTED_OPTION_VARBIT) == 0) {
            execMenuSwapper(INVENTORY_WIDGET_ID, 1, w);
        } else {
            execMenuSwapper(INVENTORY_WIDGET_ID, 2, w);
        }
    }

    /**
     * Deposits one item quickly by its ID.
     *
     * @param id The ID of the item to deposit.
     */
    public static void depositOne(int id) {
        Widget w = Inventory.findItem(id);
        if (w == null) return;
        depositOne(new ItemWidget(w));
    }

    /**
     * Deposits one item quickly by its name with a partial name match.
     *
     * @param name The name of the item to deposit.
     */
    public static void depositOne(String name, boolean exact) {
        Widget w = Inventory.findItem(name, exact);
        if (w == null) return;
        depositOne(new ItemWidget(w));
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
     * @param w      The ItemWidget representing the item to deposit.
     * @param amount The desired amount to deposit.
     */
    private static void depositX(ItemWidget w, int amount) {
        if (!isOpen()) return;
        if (w == null) return;
        if (!Inventory.hasItem(w.getItemId())) return;

        handleAmount(w, INVENTORY_WIDGET_ID, amount);
    }

    /**
     * Handles the amount for an item widget.
     * <p>
     * This method checks if the current varbit value matches the specified amount.
     * If it does, it executes the menu swapper with the HANDLE_X_SET option.
     * If it doesn't match, it executes the menu swapper with the HANDLE_X_UNSET option,
     * enters the specified amount using the VirtualKeyboard, and presses Enter.
     *
     * @param w         The ItemWidget to handle.
     * @param container The container to interact with.
     * @param amount    The desired amount to set.
     */
    private static void handleAmount(ItemWidget w, int container, int amount) {
        if (Microbot.getVarbitValue(X_AMOUNT_VARBIT) == amount) {
            execMenuSwapper(container, HANDLE_X_SET, w);
        } else {
            execMenuSwapper(container, HANDLE_X_UNSET, w);

            sleep(600, 1000);
            VirtualKeyboard.typeString(String.valueOf(amount));
            VirtualKeyboard.enter();
            sleep(50, 100);
        }
    }

    /**
     * deposit x amount of items identified by its name
     * set exact to true if you want to identify by its exact name
     *
     * @param id param amount
     */
    public static void depositX(int id, int amount) {
        Widget w = Inventory.findItem(id);
        if (w == null) return;
        depositX(new ItemWidget(w), amount);
    }

    /**
     * dpeosit x amount of items identified by its name
     * set exact to true if you want to identify by its exact name
     *
     * @param name param amount
     *             param exact
     */
    private static void depositX(String name, int amount, boolean exact) {
        Widget w = Inventory.findItem(name, exact);
        if (w == null) return;
        depositX(new ItemWidget(w), amount);
    }

    /**
     * dpeosit x amount of items identified by its name
     *
     * @param name param amount
     */
    public static void depositX(String name, int amount) {
        Widget w = Inventory.findItem(name);
        if (w == null) return;
        depositX(new ItemWidget(w), amount);
    }

    /**
     * dpeosit x amount of items identified by its exact name
     *
     * @param name param amount
     */
    public static void depositXExact(String name, int amount) {
        Widget w = Inventory.findItem(name, true);
        if (w == null) return;
        depositX(new ItemWidget(w), amount);
    }

    /**
     * deposit all items identified by its ItemWidget
     *
     * @param w
     */
    private static void depositAll(ItemWidget w) {
        if (!isOpen()) return;
        if (w == null) return;
        if (!Inventory.hasItem(w.getItemId())) return;

        execMenuSwapper(INVENTORY_WIDGET_ID, HANDLE_ALL, w);
    }

    /**
     * deposit all items identified by its id
     *
     * @param id
     */
    public static void depositAll(int id) {
        Widget w = Inventory.findItem(id);
        if (w == null) return;
        depositAll(new ItemWidget(w));
    }

    /**
     * deposit all items identified by its name
     * set exact to true if you want to be identified by its exact name
     *
     * @param name
     * @param exact
     */
    public static void depositAll(String name, boolean exact) {
        Widget w = Inventory.findItem(name, exact);
        if (w == null) return;
        depositAll(new ItemWidget(w));
    }

    /**
     * deposit all items identified by its name
     *
     * @param name
     */
    public static void depositAll(String name) {
        depositAll(name, false);
    }

    /**
     * deposit all items
     */
    public static void depositAll() {
        Microbot.status = "Deposit all";
        if (Inventory.isEmpty()) return;

        Widget widget = Rs2Widget.findWidget(SpriteID.BANK_DEPOSIT_INVENTORY, null);
        if (widget == null) return;

        Microbot.getMouse().click(widget.getBounds());
    }


    /**
     * withdraw one item identified by its ItemWidget.
     *
     * @param w
     */
    private static void withdrawOne(ItemWidget w) {
        if (!isOpen()) return;
        if (w == null) return;
        if (Inventory.isFull()) return;
        if (!hasItem(w.getItemId())) return;

        if (Microbot.getVarbitValue(SELECTED_OPTION_VARBIT) == 0) {
            execMenuSwapper(BANK_WIDGET_ID, 1, w);
        } else {
            execMenuSwapper(BANK_WIDGET_ID, 2, w);
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

    public static void withdrawItem(boolean checkInv, int id) {
        if (checkInv && Inventory.hasItem(id)) return;
        withdrawOne(id);
    }

    public static void withdrawItem(boolean checkInv, String name) {
        if (checkInv && !hasItem(name)) return;
        withdrawOne(name);
    }

    /**
     * withdraw one item identified by its name.
     * set exact to true if you want to identify by the its exact name.
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

    /**
     * withdraw x amount of items identified by its ItemWidget.
     *
     * @param w      ItemWidget
     * @param amount int
     */
    private static void withdrawX(ItemWidget w, int amount) {
        if (!isOpen()) return;
        if (w == null) return;
        if (Inventory.isFull()) return;
        if (!hasItem(w.getItemId())) return;

        handleAmount(w, BANK_WIDGET_ID, amount);
    }

    public static void withdrawItemX(boolean checkInv, int id, int amount) {
        if (checkInv && !hasItem(id)) return;
        withdrawX(id, amount);
    }

    public static void withdrawItemX(boolean checkInv, String name, int amount) {
        if (checkInv && !hasItem(name)) return;
        withdrawX(name, amount);
    }

    /**
     * withdraw x amount of items identified by its id.
     *
     * @param id
     * @param amount
     */
    public static void withdrawX(int id, int amount) {
        withdrawX(findBankItem(id), amount);
    }

    /**
     * withdraw x amount of items identified by its name.
     * set exact to true if you want to identify an item by its exact name.
     *
     * @param name
     * @param amount
     * @param exact
     */
    private static void withdrawX(String name, int amount, boolean exact) {
        withdrawX(findBankItem(name, exact), amount);
    }

    /**
     * withdraw x amount of items identified by its name
     *
     * @param name
     * @param amount
     */
    public static void withdrawX(String name, int amount) {
        withdrawX(findBankItem(name, false), amount);
    }

    /**
     * withdraw x amount of items identified by its name.
     *
     * @param name
     * @param amount
     */
    public static void withdrawXExact(String name, int amount) {
        withdrawX(findBankItem(name, false), amount);
    }

    /**
     * withdraw all items identified by its ItemWidget.
     *
     * @param w
     */
    private static void withdrawAll(ItemWidget w) {
        if (!isOpen()) return;
        if (w == null) return;
        if (Inventory.isFull()) return;

        execMenuSwapper(BANK_WIDGET_ID, HANDLE_ALL, w);
    }

    public static void withdrawItemAll(boolean checkInv, String name) {
        if (checkInv && !hasItem(name)) return;
        withdrawItemAll(name);
    }

    public static void withdrawItemAll(String name) {
        withdrawAll(name);
    }

    /**
     * withdraw all items identified by its id.
     *
     * @param id
     */
    public static void withdrawAll(int id) {
        withdrawAll(findBankItem(id));
    }

    /**
     * withdraw all items identified by its name
     * set the boolean exact to true if you want to identify the item by the exact name
     *
     * @param name
     * @param exact
     */
    public static void withdrawAll(String name, boolean exact) {
        withdrawAll(findBankItem(name, exact));
    }

    /**
     * withdraw all items identified by its name
     *
     * @param name
     */
    public static void withdrawAll(String name) {
        withdrawAll(findBankItem(name, false));
    }

    /**
     * wear an item identified by its ItemWidget.
     *
     * @param w
     */
    private static void wearItem(Widget w) {
        if (!isOpen()) return;
        if (w == null) return;

        ItemWidget itemWidget = new ItemWidget(w);

        execMenuSwapper(INVENTORY_WIDGET_ID, 8, itemWidget);
    }

    /**
     * wear an item identified by its exact name.
     *
     * @param name
     */
    public static void wearItem(String name) {
        wearItem(Inventory.findItem(name, false));
    }

    /**
     * wear an item identified by its exact name.
     *
     * @param name
     */
    public static void wearItemExact(String name) {
        wearItem(Inventory.findItem(name, true));
    }

    /**
     * withdraw all and equip item identified by its id.
     *
     * @param id
     */
    public static void withdrawAllAndEquip(int id) {
        if (Rs2Equipment.hasEquipped(id)) return;
        withdrawAll(id);
        sleepUntil(() -> Inventory.hasItem(id));
        wearItem(id);
    }

    /**
     * withdraw and equip item identified by its id.
     *
     * @param id
     */
    public static void withdrawAndEquip(int id) {
        if (Rs2Equipment.hasEquipped(id)) return;
        withdrawOne(id);
        sleepUntil(() -> Inventory.hasItem(id));
        wearItem(id);
    }

    /**
     * withdraw items identified by one ore more ids
     *
     * @param ids
     */
    public static void withdrawItems(int... ids) {
        for (int id : ids) {
            withdrawOne(id);
        }
    }

    /**
     * Deposit items identified by one ore more ids
     *
     * @param ids
     */
    public static void depositItems(int... ids) {
        for (int id : ids) {
            depositOne(id);
        }
    }

    /**
     * Opens the bank using the nearest NPC named "banker".
     *
     * @return True if bank was successfully opened, otherwise false.
     */
    public static boolean openBank() {
        Microbot.status = "Opening bank";
        try {
            if (Inventory.isUsingItem())
                Microbot.getMouse().click();
            if (isOpen()) return true;
            NPC npc = Rs2Npc.getNpc("banker");
            if (npc == null) return false;
            boolean action = Rs2Menu.doAction("bank", npc.getCanvasTilePoly());
            if (action) {
                sleepUntil(() -> isOpen() || Rs2Widget.hasWidget("Please enter your PIN"), 5000);
                sleep(600, 1000);
                return true;
            }
            return false;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return false;
    }

    public static boolean openBank(NPC npc) {
        Microbot.status = "Opening bank";
        try {
            if (isOpen()) return true;
            if (Inventory.isUsingItem()) Microbot.getMouse().click();

            if (npc == null) return false;

            if (!Rs2Menu.doAction("bank", npc.getCanvasTilePoly())) {
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
     * @param object
     * @return
     */
    public static boolean openBank(TileObject object) {
        Microbot.status = "Opening bank";
        try {
            if (isOpen()) return true;
            if (Inventory.isUsingItem()) Microbot.getMouse().click();

            if (object == null) return false;

            if (!Rs2Menu.doAction("bank", object.getCanvasTilePoly())) {
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
     * @param id
     */
    private static void handleWearItem(int id) {
        Widget w = Inventory.findItem(id);
        if (w == null) return;
        execMenuSwapper(INVENTORY_WIDGET_ID, 8, new ItemWidget(w));
    }

    /**
     * Tries to wear an item identified by its id.
     *
     * @param id
     */
    public static void wearItem(int id) {
        handleWearItem(id);
    }

    /**
     * find an item in the bank identified by its id.
     *
     * @param id
     * @return
     */
    private static ItemWidget findBankItem(int id) {
        if (bankItems == null) return null;
        if (bankItems.stream().findAny().isEmpty()) return null;

        ItemWidget bankItem = bankItems.stream().filter(x -> x.getItemId() == id).findFirst().orElse(null);

        return bankItem;
    }

    /**
     * Finds an item in the bank based on its name.
     *
     * @param name  The name of the item.
     * @param exact If true, requires an exact name match.
     * @return The item widget, or null if the item isn't found.
     */
    private static ItemWidget findBankItem(String name, boolean exact) {
        if (bankItems == null) return null;
        if (bankItems.stream().findAny().isEmpty()) return null;

        final String lowerCaseName = name.toLowerCase();

        ItemWidget bankItem = bankItems.stream().filter(x -> exact
                ? x.getName().equalsIgnoreCase(lowerCaseName)
                : x.getName().toLowerCase().contains(lowerCaseName)).findFirst().orElse(null);

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
     * @return
     */
    public static boolean walkToBank() {
        BankLocation bankLocation = getNearestBank();
        Microbot.getWalker().walkTo(bankLocation.getWorldPoint());
        return bankLocation.getWorldPoint().distanceTo(Microbot.getClient().getLocalPlayer().getWorldLocation()) <= 4;
    }

    /**
     * Use bank or chest
     *
     * @return
     */
    public static boolean useBank() {
        if (isOpen()) return true;
        GameObject bank = Rs2GameObject.findBank();
        if (bank == null) {
            GameObject chest = Rs2GameObject.findChest();
            if (chest == null) return false;
            Rs2GameObject.interact(chest, "use");
        } else {
            Rs2GameObject.interact(bank, "bank");
        }
        sleepUntil(Rs2Bank::isOpen);
        return true;
    }

    /**
     * Use bank or chest with identified action
     *
     * @return
     */
    public static void useBank(String action) {
        Microbot.status = "Banking";
        GameObject bank = Rs2GameObject.findBank(action);
        if (bank == null) return;
        Rs2GameObject.interact(bank, action);
        sleepUntil(Rs2Bank::isOpen);
    }

    /**
     * Updates the bank items in memory based on the provided event.
     *
     * @param e The event containing the latest bank items.
     */
    public static void storeBankItemsInMemory(ItemContainerChanged e) {
        CopyOnWriteArrayList<ItemWidget> list = updateItemContainer(BANK_CONTAINER_ID, e);
        if (list != null)
            bankItems = list;
    }
}
