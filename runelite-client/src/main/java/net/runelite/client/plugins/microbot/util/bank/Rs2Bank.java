package net.runelite.client.plugins.microbot.util.bank;

import net.runelite.api.GameObject;
import net.runelite.api.Item;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.bank.enums.BankLocation;
import net.runelite.client.plugins.microbot.util.bank.models.BankItemWidget;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.menu.Rs2Menu;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static net.runelite.client.plugins.microbot.util.Global.sleep;
import static net.runelite.client.plugins.microbot.util.Global.sleepUntil;

public class Rs2Bank {
    public static List<Widget> bankItems = new ArrayList<>();
    public static CopyOnWriteArrayList<Widget> inventoryItems = new CopyOnWriteArrayList<>();

    public static boolean depositXContains(String itemName, int amount) {
        Microbot.status = "Deposit " + amount + " -> " + itemName;
        return BetterBank.depositXFast(itemName, amount);
    }

    public static boolean depositAll(String itemName) {
        Microbot.status = "Deposit all " + itemName;
        return BetterBank.depositAllFast(itemName);
    }

    public static boolean depositAll(int itemID) {
        Microbot.status = "Deposit all " + itemID;
        return BetterBank.depositAllFast(itemID);
    }

    public static boolean depositAllContains(String itemName) {
        Microbot.status = "Deposit all " + itemName;
        return depositAll(itemName);
    }
    public static boolean depositAll() {
        Microbot.status = "Deposit all";
        return BetterBank.depositAll();
    }

    public static boolean depositEquipment() {
        Microbot.status = "Deposit equipment";
        return BetterBank.depositEquipment();
    }

    public static boolean withdrawItem(String itemName) {
        return withdrawItem(false, itemName);
    }

    public static boolean withdrawItem(boolean checkInventory, String itemName) {
        Microbot.status = "Withdrawing one " + itemName;
        if (checkInventory && Inventory.hasItem(itemName)) return true;
        return BetterBank.withdrawOneFast(itemName);
    }

    public static boolean withdrawItemContains(String name) {
        Microbot.status = "Withdrawing one " + name;
        return withdrawItem(name);
    }

    public static boolean withdrawItemX(boolean checkInventory, String itemName, int amount) {
        Microbot.status = "Withdrawing " + amount + " " + itemName;
        if (checkInventory && Inventory.hasItem(itemName)) return true;
        return BetterBank.withdrawXFast(itemName, amount);
    }

    public static boolean withdrawItemAll(boolean checkInventory, String itemName) {
        Microbot.status = "Withdrawing All " + itemName;
        if (checkInventory && Inventory.hasItem(itemName)) return true;
        return BetterBank.withdrawAllFast(itemName);
    }

    public static void withdrawItems(String... itemNames) {
        if (isOpen()) {
            for (String itemName : itemNames) {
                withdrawItem(itemName);
            }
        }
    }

    public static void withdrawItems(boolean checkInventory, String... itemNames) {
        if (isOpen()) {
            for (String itemName : itemNames) {
                withdrawItem(checkInventory, itemName);
            }
        }
    }

    public static void withdrawItemsAll(String... itemNames) {
        withdrawItemsAll(false, itemNames);
    }

    public static void withdrawItemsAll(boolean checkInventory, String... itemNames) {
        if (isOpen()) {
            for (String itemName :
                    itemNames) {
                withdrawItemAll(checkInventory, itemName);
            }
        }
    }

    public static boolean withdrawItemsRequired(List<ItemRequirement> itemsRequired) {
        List<ItemRequirement> itemsMissing = new ArrayList<>();
        for (ItemRequirement item : itemsRequired) {
            if (!Inventory.hasItemAmount(item.getId(), item.getQuantity()) && !Inventory.hasItemAmountStackable(item.getName(), item.getQuantity())) {
                itemsMissing.add(item);
            }
        }
        if (!itemsMissing.isEmpty()) {
            for (ItemRequirement item : itemsMissing) {
                Rs2Bank.withdrawItemX(true, item.getName(), item.getQuantity());
            }
        }
        return itemsMissing.isEmpty();
    }
    public static void withdrawFast(int id) {
        BetterBank.withdrawOneFast(id);
    }

    public static void withdrawFast(int id, int amount) {
        for (int i = 0; i < amount; i++) {
            withdrawFast(id);
        }
    }

    public static void withdrawAllFast(int id) {
        BetterBank.withdrawAllFast(id);
    }
    public static void wearItem(int id) {
        BetterBank.wearItemFast(id);
    }

    public static void withdrawAndEquipFast(int id) {
        if (Rs2Equipment.hasEquipped(id)) return;
        withdrawFast(id);
        sleepUntil(() -> inventoryItems.stream().anyMatch(x -> x.getItemId() == id));
        wearItem(id);
    }

    public static void withdrawAllAndEquipFast(int id) {
        if (Rs2Equipment.hasEquipped(id)) return;
        withdrawAllFast(id);
        sleepUntil(() -> inventoryItems.stream().anyMatch(x -> x.getItemId() == id));
        wearItem(id);
    }

    public static boolean withdrawFast(String name, boolean exact) {
        return BetterBank.withdrawOneFast(name, exact);
    }

    public static boolean withdrawFast(String name) {
        return withdrawFast(name, false);
    }

    public static boolean withdrawXFast(String name, int amount, boolean exact) {
        return BetterBank.withdrawXFast(name, amount, exact);
    }

    public static boolean withdrawXFast(String name, int amount) {
        return withdrawXFast(name, amount, false);
    }

    public static boolean withdrawAllFast(String name, boolean exact) {
        return BetterBank.withdrawAllFast(name, exact);
    }

    public static boolean withdrawAllFast(String name) {
        return BetterBank.withdrawAllFast(name);
    }


    public static boolean closeBank() {
        Microbot.status = "Closing bank";
        return BetterBank.close();
    }

    public static boolean isOpen() {
        Microbot.status = "Checking if bank is open";
        return BetterBank.isOpen();
    }

    public static boolean openBank() {
        Microbot.status = "Opening bank";
        try {
            if (isOpen()) return true;
            if (Inventory.isUsingItem()) Microbot.getMouse().click();

            NPC npc = Rs2Npc.getNpc("banker");
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

    public static void useBank(String action) {
        Microbot.status = "Banking";
        GameObject bank = Rs2GameObject.findBank(action);
        if (bank == null) return;
        Rs2GameObject.interact(bank, action);
        sleepUntil(Rs2Bank::isOpen);
    }

    public static boolean walkToBank() {
        BankLocation bankLocation = getNearestBank();
        Microbot.getWalker().walkTo(bankLocation.getWorldPoint());
        return bankLocation.getWorldPoint().distanceTo(Microbot.getClient().getLocalPlayer().getWorldLocation()) <= 4;
    }

    public static boolean hasItem(String itemName) {
        Microbot.status = "Looking for " + itemName + " in the bank";
        Widget w = BetterBank.findBankItem(itemName);
        return w != null && w.getItemQuantity() > 0;
    }



    public static boolean hasItems(List<ItemRequirement> itemsRequired) {
        for (ItemRequirement item : itemsRequired) {
            if (!Inventory.hasItemAmount(item.getId(), item.getQuantity()) && !Inventory.hasItemAmountStackable(item.getName(), item.getQuantity())) {
                return false;
            }
        }
        return true;
    }

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



    public static void storeBankItemsInMemory(ItemContainerChanged e) {
        if (e.getContainerId() == 95) {
            int i = 0;
            bankItems.clear();
            for (Item item : e.getItemContainer().getItems()) {
                if (item == null) {
                    i++;
                    continue;
                }
                if (Microbot.getItemManager().getItemComposition(item.getId()).getPlaceholderTemplateId() == 14401) {
                    i++;
                    continue;
                }
                bankItems.add(new BankItemWidget(Microbot.getItemManager().getItemComposition(item.getId()).getName(), item.getId(), item.getQuantity(), i));
                i++;
            }
        }
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
                inventoryItems.add(new BankItemWidget(Microbot.getItemManager().getItemComposition(item.getId()).getName(), item.getId(), item.getQuantity(), i));
                i++;
            }
        }
    }
}
