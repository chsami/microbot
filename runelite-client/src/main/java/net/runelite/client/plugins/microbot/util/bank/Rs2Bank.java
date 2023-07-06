package net.runelite.client.plugins.microbot.util.bank;

import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.Point;
import net.runelite.api.SpriteID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.quest.QuestScript;
import net.runelite.client.plugins.microbot.util.bank.enums.BankLocation;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.menu.Rs2Menu;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;

import java.util.ArrayList;
import java.util.List;

import static net.runelite.client.plugins.microbot.util.Global.*;
import static net.runelite.client.plugins.microbot.util.globval.VarbitIndices.BANK_WITHDRAW_QUANTITY;

public class Rs2Bank {

    public static GameObject objectToBank = null;

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

    public static boolean isOpen() {
        Microbot.status = "Checking if bank is open";
        if (Rs2Widget.hasWidget("Please enter your PIN")) {
            Microbot.getNotifier().notify("[ATTENTION] Please enter your bankpin so the script can continue.");
            sleep(5000);
            return false;
        }
        return Rs2Widget.findWidget("Rearrange mode", null) != null;
    }

    @Deprecated(since = "Use isOpen instead", forRemoval = true)
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
        if (Inventory.isFull()) return false;
        if (!isBankOpen()) {
            openBank();
        }
        Widget widget = Rs2Widget.findWidgetExact(itemName);
        if (widget == null) return false;
        if (widget.getItemQuantity() <= 0) return false;
        if (Microbot.getVarbitValue(BANK_WITHDRAW_QUANTITY) != 0) {
            Rs2Widget.clickWidget(786460);
            sleep(1000);
        }
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
        Widget widget = Rs2Widget.findWidgetExact(itemName);
        if (widget == null) return false;
        if (widget.getItemQuantity() <= 0) return false;
        if (Microbot.getVarbitValue(BANK_WITHDRAW_QUANTITY) != 3 || Microbot.getVarbitValue(3960) != amount) {
            Widget withdrawX = Rs2Widget.getWidget(786466);
            if (withdrawX != null) {
                Rs2Menu.setOption("Set custom quantity");
                Microbot.getMouse().move(withdrawX.getBounds());
                sleep(150, 250);
                Microbot.getMouse().click(withdrawX.getBounds());
                sleep(1000);
                VirtualKeyboard.typeString(Integer.toString(amount));
                VirtualKeyboard.enter();
                sleep(400, 600);
            }
        }
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
        if (Inventory.isFull()) return false;
        if (!isBankOpen()) return false;
        Widget widget = Rs2Widget.findWidgetExact(itemName);
        if (widget == null) return false;
        if (widget.getItemQuantity() <= 0) return false;
        if (Microbot.getVarbitValue(BANK_WITHDRAW_QUANTITY) != 4) {
            Rs2Widget.clickWidget(786468);
            sleep(1000);
        }
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
        if (isBankOpen()) return true;
        GameObject bank = Rs2GameObject.findBank();
        if (bank == null) return false;
        Microbot.getMouse().click(bank.getClickbox().getBounds());
        sleep(200, 300);
        sleepUntil(() -> isBankOpen());
        return true;
    }

    public static boolean useBank(String action) {
        Microbot.status = "Banking";
        GameObject bank = Rs2GameObject.findBank(action);
        if (bank == null) return false;
        if (Rs2Menu.hasAction(bank.getCanvasTilePoly(), "Bank")) {
            Rs2Menu.doAction("Bank", bank.getClickbox());
            sleepUntil(() -> isBankOpen() == true, 5000);
        } else if (Rs2Menu.hasAction(bank.getCanvasTilePoly(), "Use")) { //for chests
            Rs2Menu.doAction("Use", bank.getClickbox());
            sleepUntil(() -> isBankOpen() == true, 5000);
        }
        return false;
    }

    public static boolean depositAll() {
        Microbot.status = "Deposit all";
        if (Inventory.count() == 0) return true;
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
        return Rs2Widget.findWidgetExact(itemName) != null && Rs2Widget.findWidgetExact(itemName).getItemQuantity() > 0;
    }

    public static boolean walkToBank() {
        BankLocation bankLocation = getNearestBank();
        Microbot.getWalker().walkTo(bankLocation.getWorldPoint(), true, false);
        if (bankLocation.getWorldPoint().distanceTo(Microbot.getClient().getLocalPlayer().getWorldLocation()) > 4) {
            Microbot.getWalker().walkTo(bankLocation.getWorldPoint(), false, false);
            return false;
        }
        return true;
    }

    public static boolean withdrawItemsRequired(List<ItemRequirement> itemsRequired) {
        List<ItemRequirement> itemsMissing = new ArrayList<>();
        for (ItemRequirement item: itemsRequired) {
            if (!Inventory.hasItemAmount(item.getId(), item.getQuantity()) && !Inventory.hasItemAmountStackable(item.getName(), item.getQuantity())) {
                itemsMissing.add(item);
            }
        }
        if (itemsMissing.size() > 0) {
            for (ItemRequirement item : itemsMissing) {
                Rs2Bank.withdrawItemX(true, item.getName(), item.getQuantity());
            }
        }
        return itemsMissing.size() == 0;
    }

    public static boolean hasItems(List<ItemRequirement> itemsRequired) {
        List<ItemRequirement> itemsMissing = new ArrayList<>();
        for (ItemRequirement item: itemsRequired) {
            if (!Inventory.hasItemAmount(item.getId(), item.getQuantity()) && !Inventory.hasItemAmountStackable(item.getName(), item.getQuantity())) {
                itemsMissing.add(item);
            }
        }
        return itemsMissing.size() == 0;
    }

    public static BankLocation getNearestBank() {
        BankLocation nearest = null;
        double dist = Double.MAX_VALUE;
        WorldPoint local = Microbot.getClient().getLocalPlayer().getWorldLocation();
        for (BankLocation bankLocation : BankLocation.values())
        {
            double currDist = local.distanceTo(bankLocation.getWorldPoint());
            if (nearest == null || currDist < dist)
            {
                dist = currDist;
                nearest = bankLocation;
            }
        }
        return nearest;
    }
}
