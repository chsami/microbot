package net.runelite.client.plugins.microbot.util.bank;

import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.SpriteID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.jrPlugins.autoZMIAltar.BANK;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.reflection.Rs2Reflection;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import static net.runelite.client.plugins.microbot.util.Global.sleep;
import static net.runelite.client.plugins.microbot.util.Global.sleepUntilOnClientThread;

public class BetterBank {
    private static final int BANK_WIDGET_ID = 786445;
    private static final int INVENTORY_WIDGET_ID = 983043;


    private static int widgetId;
    private static int entryIndex;
    private static int itemId;
    private static int slotId;

    public static void handleMenuSwapper(MenuEntry menuEntry) throws InvocationTargetException, IllegalAccessException {
        if (widgetId == 0) return;
        Rs2Reflection.setItemId(menuEntry, itemId);
        menuEntry.setOption("Withdraw-1"); // Should probably be changed. Doesn't matter though.
        menuEntry.setIdentifier(entryIndex);
        menuEntry.setParam0(slotId);
        menuEntry.setParam1(widgetId);
        menuEntry.setTarget("");
        menuEntry.setType(MenuAction.CC_OP);
    }

    public static void execMenuSwapper(int widgetId, int entryIndex, int itemId, int slotId) {
        BetterBank.widgetId = widgetId;
        BetterBank.entryIndex = entryIndex;
        BetterBank.itemId = itemId;
        BetterBank.slotId = slotId;

        Microbot.getMouse().clickFast(1, 1);
        sleep(50);

        BetterBank.widgetId = 0;
        BetterBank.entryIndex = 0;
        BetterBank.itemId = 0;
        BetterBank.slotId = 0;
    }

    // UTILS
    public static boolean isOpen() {
        if (Rs2Widget.hasWidget("Please enter your PIN")) {
            Microbot.getNotifier().notify("[ATTENTION] Please enter your bankpin so the script can continue.");
            sleep(5000);
            return false;
        }
        return Rs2Widget.findWidget("Rearrange mode", null) != null;
    }

    public static boolean close() {
        if (!isOpen()) return false;
        Rs2Widget.clickChildWidget(786434, 11);
        sleepUntilOnClientThread(() -> !isOpen());

        return true;
    }

    public static Widget findBankItem(int id) {
        Widget w = Microbot.getClient().getWidget(WidgetInfo.BANK_ITEM_CONTAINER);
        if (w == null) return null;

        for (Widget item : w.getDynamicChildren()) {
            if (item.getItemId() == id) {
                return item;
            }
        }

        return null;
    }

    public static Widget findBankItem(String name) {
        Widget w = Microbot.getClient().getWidget(WidgetInfo.BANK_ITEM_CONTAINER);
        if (w == null) return null;

        for (Widget item : w.getDynamicChildren()) {
            if (item.getName().toLowerCase().contains(name.toLowerCase())) {
                return item;
            }
        }

        return null;
    }


    // DEPOSIT
    public static boolean depositEquipment() {
        Widget widget = Rs2Widget.findWidget(SpriteID.BANK_DEPOSIT_EQUIPMENT, null);
        if (widget == null) return false;

        Microbot.getMouse().click(widget.getBounds());
        return true;
    }


    private static boolean depositOneFast(Widget w) {
        if (!isOpen()) return false;
        if (w == null) return false;
        if (!Inventory.hasItem(w.getItemId())) return false;

        execMenuSwapper(INVENTORY_WIDGET_ID, 3, w.getItemId(), w.getIndex());

        return true;
    }

    public static boolean depositOneFast(int id) {
        return depositOneFast(Inventory.findItem(id));
    }

    public static boolean depositOneFast(String name) {
        return depositOneFast(Inventory.findItemContains(name));
    }

    private static boolean depositXFast(Widget w, int amount) {
        if (!isOpen()) return false;
        if (w == null) return false;
        if (!Inventory.hasItem(w.getItemId())) return false;

        if (Arrays.stream(w.getActions()).noneMatch(x -> x != null && x.equals("Deposit-" + amount))) {
            execMenuSwapper(INVENTORY_WIDGET_ID, 7, w.getItemId(), w.getIndex());

            sleep(600, 1000);
            VirtualKeyboard.typeString(String.valueOf(amount));
            VirtualKeyboard.enter();
            sleep(50, 100);
        } else {
            execMenuSwapper(INVENTORY_WIDGET_ID, 6, w.getItemId(), w.getIndex());
        }

        return true;
    }

    public static boolean depositXFast(int id, int amount) {
        return depositXFast(Inventory.findItem(id), amount);
    }

    public static boolean depositXFast(String name, int amount) {
        return depositXFast(Inventory.findItemContains(name), amount);
    }

    private static boolean depositAllFast(Widget w) {
        if (!isOpen()) return false;
        if (w == null) return false;
        if (!Inventory.hasItem(w.getItemId())) return false;

        execMenuSwapper(INVENTORY_WIDGET_ID, 8, w.getItemId(), w.getIndex());

        return true;
    }

    public static boolean depositAllFast(int id) {
        return depositAllFast(Inventory.findItem(id));
    }

    public static boolean depositAllFast(String name) {
        return depositAllFast(Inventory.findItemContains(name));
    }

    public static boolean depositAll() {
        Microbot.status = "Deposit all";
        if (Inventory.count() == 0) return true;

        Widget widget = Rs2Widget.findWidget(SpriteID.BANK_DEPOSIT_INVENTORY, null);
        if (widget == null) return false;

        Microbot.getMouse().click(widget.getBounds());
        return true;
    }


    // WITHDRAW
    private static boolean withdrawOneFast(Widget w) {
        if (!isOpen()) return false;
        if (w == null) return false;
        if (Inventory.isFull()) return false;

        execMenuSwapper(BANK_WIDGET_ID, 2, w.getItemId(), w.getIndex());

        return true;
    }

    public static boolean withdrawOneFast(int id) {
        return withdrawOneFast(findBankItem(id));
    }

    public static boolean withdrawOneFast(String name) {
        return withdrawOneFast(findBankItem(name));
    }

    private static boolean withdrawXFast(Widget w, int amount) {
        if (!isOpen()) return false;
        if (w == null) return false;
        if (Inventory.isFull()) return false;

        if (Arrays.stream(w.getActions()).noneMatch(x -> x != null && x.equals("Withdraw-" + amount))) {
            execMenuSwapper(BANK_WIDGET_ID, 6, w.getItemId(), w.getIndex());

            sleep(600, 1000);
            VirtualKeyboard.typeString(String.valueOf(amount));
            VirtualKeyboard.enter();
            sleep(50, 100);
        } else {
            execMenuSwapper(BANK_WIDGET_ID, 5, w.getItemId(), w.getIndex());
        }

        return false;
    }

    public static boolean withdrawXFast(int id, int amount) {
        return withdrawXFast(findBankItem(id), amount);
    }

    public static boolean withdrawXFast(String name, int amount) {
        return withdrawXFast(findBankItem(name), amount);
    }

    private static boolean withdrawAllFast(Widget w) {
        if (!isOpen()) return false;
        if (w == null) return false;
        if (Inventory.isFull()) return false;

        execMenuSwapper(BANK_WIDGET_ID, 7, w.getItemId(), w.getIndex());

        return true;
    }

    public static boolean withdrawAllFast(int id) {
        return withdrawAllFast(findBankItem(id));
    }

    public static boolean withdrawAllFast(String name) {
        return withdrawAllFast(findBankItem(name));
    }

    private static boolean wearItemFast(Widget w) {
        if (!isOpen()) return false;
        if (w == null) return false;

        execMenuSwapper(INVENTORY_WIDGET_ID, 9, w.getItemId(), w.getIndex());

        return true;
    }

    public static boolean wearItemFast(int id) {
        return wearItemFast(Inventory.findItem(id));
    }

    public static boolean wearItemFast(String name) {
        return wearItemFast(Inventory.findItemContains(name));
    }

    public static void withdrawItems(int... ids) {
        for (int id : ids) {
            withdrawOneFast(id);
        }
    }

    public static void depositItems(int... ids) {
        for (int id : ids) {
            depositOneFast(id);
        }
    }
}
