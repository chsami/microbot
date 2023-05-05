package net.runelite.client.plugins.microbot.util.bank;

import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.Point;
import net.runelite.api.SpriteID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.menu.Rs2Menu;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import static net.runelite.client.plugins.microbot.util.Global.*;

public class Rs2Bank {

    public static boolean depositXContains(String itemName, int amount) {
        Microbot.status = "Deposit " + amount + " -> " + itemName;
        if (!isBankOpen()) return false;
        if (!Inventory.hasItemContains(itemName)) return true;
        Widget item = Inventory.findItemContains(itemName);
        boolean action = Rs2Menu.doAction("Deposit-" + amount, item.getBounds());
        sleep(600, 1000);
        return action;
    }

    public static boolean depositAll(String itemName) {
        Microbot.status = "Deposit all " + itemName;
        if (!isBankOpen()) return false;
        if (!Inventory.hasItem(itemName)) return true;
        Widget item = Inventory.findItem(itemName);
        boolean action = Rs2Menu.doAction("Deposit-all", item.getBounds());
        sleep(600, 1000);
        return action;
    }

    public static boolean depositAllContains(String itemName) {
        Microbot.status = "Deposit all " + itemName;
        if (!isBankOpen()) return false;
        if (!Inventory.hasItemContains(itemName)) return true;
        Widget item = Inventory.findItemContains(itemName);
        boolean action = Rs2Menu.doAction("Deposit-all", item.getBounds());
        sleep(600, 1000);
        return action;
    }

    public static boolean closeBank() {
        Microbot.status = "Closing bank";
        if (!isBankOpen()) return true;
        int closeWidget = 786434;
        Rs2Widget.clickChildWidget(closeWidget, 11);
        sleepUntilOnClientThread(() -> Rs2Widget.getWidget(786445) == null);
        return true;
    }

    public static boolean isBankOpen() {
        Microbot.status = "Checking if bank is open";
        if (Rs2Widget.hasWidget("Please enter your PIN")) {
            Microbot.getNotifier().notify("[ATTENTION] Please enter your bankpin so the script can continue.");
            sleep(5000);
            return false;
        }
        return Rs2Widget.findWidget("Rearrange mode", null) != null;
    }

    public static boolean openBank() {
        Microbot.status = "Opening bank";
        try {
            if (Inventory.isUsingItem())
                Microbot.getMouse().click();
            if (isBankOpen()) return true;
            NPC npc = Rs2Npc.getNpc("banker");
            if (npc == null) return false;
            boolean action = Rs2Menu.doAction("bank", npc.getCanvasTilePoly());
            if (action) {
                sleepUntil(() -> isBankOpen() || Rs2Widget.hasWidget("Please enter your PIN"), 5000);
                sleep(600, 1000);
                return true;
            }
            return false;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return false;
    }

    public static boolean scrollTo(Widget widget) {
        Microbot.status = "Searching for item";
        if (Microbot.getClient().isClientThread()) {
            Microbot.getNotifier().notify("Could not scrollTo item because the code is ran on the main thread!");
            return false;
        }
        Widget mainWindow = Rs2Widget.getWidget(786445);
        int calc;
        Point point = new Point((int) mainWindow.getBounds().getCenterX(), (int) mainWindow.getBounds().getCenterY());
        Microbot.getMouse().move(point);
        do {
            calc = widget.getRelativeY() - mainWindow.getScrollY();

            if (calc >= 0 && calc < 640) break;

            point = new Point((int) mainWindow.getBounds().getCenterX(), (int) mainWindow.getBounds().getCenterY());

            if (calc > 0) {
                Microbot.getMouse().scrollDown(point);
            } else if (calc < 0) {
                Microbot.getMouse().scrollUp(point);
            }

            sleep(100, 300);
            mainWindow = Rs2Widget.getWidget(786445);

        } while (calc <= 0 || calc > 640);

        return true;
    }

    public static boolean withdrawItem(String itemName) {
        return withdrawItem(false, itemName);
    }

    public static boolean withdrawItem(boolean checkInventory, String itemName) {
        Microbot.status = "Withdrawing one " + itemName;
        if (checkInventory && Inventory.hasItem(itemName)) return true;
        if (Inventory.isInventoryFull()) return false;
        if (!isBankOpen()) {
            openBank();
        }
        Widget widget = Rs2Widget.findWidget(itemName, null);
        if (widget == null) return false;
        if (widget.getItemQuantity() <= 0) return false;
        if (scrollTo(widget)) {
            Rs2Menu.doAction("Withdraw-1", widget.getBounds());
            sleep(100, 1000);
            return true;
        }
        return false;
    }

    public static boolean withdrawItemX(boolean checkInventory, String itemName, int amount) {
        Microbot.status = "Withdrawing " + amount + " " + itemName;
        if (checkInventory && Inventory.hasItem(itemName)) return true;
        if (!isBankOpen()) {
            openBank();
        }
        Widget widget = Rs2Widget.findWidget(itemName, null);
        if (widget == null) return false;
        if (widget.getItemQuantity() <= 0) return false;
        if (scrollTo(widget)) {
            Rs2Menu.doAction("Withdraw-" + amount, widget.getBounds());
            sleep(600, 1000);
            return true;
        }
        return false;
    }

    public static boolean withdrawItemAll(boolean checkInventory, String itemName) {
        Microbot.status = "Withdrawing All " + itemName;
        if (checkInventory && Inventory.hasItem(itemName)) return true;
        if (Inventory.isInventoryFull()) return false;
        if (!isBankOpen()) return false;
        Widget widget = Rs2Widget.findWidget(itemName, null);
        if (widget == null) return false;
        if (widget.getItemQuantity() <= 0) return false;
        if (scrollTo(widget)) {
            Rs2Menu.doAction("Withdraw-All", widget.getBounds());
            sleep(600, 1000);
            return true;
        }
        return false;
    }

    public static void withdrawItems(String... itemNames) {
        if (isBankOpen()) {
            for (String itemName :
                    itemNames) {
                withdrawItem(itemName);
            }

        }
    }

    public static void withdrawItems(boolean checkInventory, String... itemNames) {
        if (isBankOpen()) {
            for (String itemName :
                    itemNames) {
                withdrawItem(checkInventory, itemName);
            }

        }
    }

    public static void withdrawItemsAll(String... itemNames) {
        withdrawItemsAll(false, itemNames);
    }

    public static void withdrawItemsAll(boolean checkInventory, String... itemNames) {
        if (isBankOpen()) {
            for (String itemName :
                    itemNames) {
                withdrawItemAll(checkInventory, itemName);
            }

        }
    }

    public static boolean useBank() {
        GameObject bank = Rs2GameObject.findBank();
        if (bank == null) return false;
        if (Rs2Menu.doAction("Bank", bank.getCanvasTilePoly())) {
            sleepUntil(() -> isBankOpen() == true, 5000);
        } else if (Rs2Menu.doAction("Use", bank.getCanvasTilePoly())) { //for chests
            sleepUntil(() -> isBankOpen() == true, 5000);
        }
        return false;
    }

    public static boolean useBank(String action) {
        Microbot.status = "Banking";
        GameObject bank = Rs2GameObject.findBank(action);
        if (bank == null) return false;
        if (Rs2Menu.doAction("Bank", bank.getCanvasTilePoly())) {
            sleepUntil(() -> isBankOpen() == true, 5000);
        } else if (Rs2Menu.doAction("Use", bank.getCanvasTilePoly())) { //for chests
            sleepUntil(() -> isBankOpen() == true, 5000);
        }
        return false;
    }

    public static boolean depositAll() {
        Microbot.status = "Deposit all";
        Widget widget = Rs2Widget.findWidget(SpriteID.BANK_DEPOSIT_INVENTORY, null);
        if (widget != null) {
            Microbot.getMouse().click(widget.getBounds());
            sleep(1000, 2000);
            return true;
        }
        return false;
    }

    public static boolean hasItem(String itemName) {
        Microbot.status = "Looking for " + itemName + " in the bank";
        return Rs2Widget.findWidget(itemName) != null && Rs2Widget.findWidget(itemName).getItemQuantity() > 0;
    }
}
