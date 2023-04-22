package net.runelite.client.plugins.microbot.util.inventory;

import net.runelite.api.ItemComposition;
import net.runelite.api.Point;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.menu.Menu;
import net.runelite.client.plugins.microbot.util.tabs.Tab;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static net.runelite.client.plugins.microbot.util.Global.sleep;
import static net.runelite.client.plugins.microbot.util.math.Random.random;


public class Inventory {

    private static ScheduledFuture<?> inventoryScheduler;
    private static ScheduledExecutorService scheduledExecutorService;

    public static boolean clickItem(int index) {
        Widget inventoryWidget = getInventory();
        return Microbot.getClientThread().runOnClientThread(() -> {
            Widget item = inventoryWidget.getChild(index);
            if (item == null || item.isHidden()) return false;

            Microbot.getMouse().click(item.getBounds());

            sleep(300, 1000);

            return true;
        });
    }

    public static boolean isInventoryFull() {
        Widget inventoryWidget = getInventory();
        return Microbot.getClientThread().runOnClientThread(() -> Arrays.stream(inventoryWidget.getDynamicChildren()).filter(x -> x.getName().length() > 0).count() == 28);
    }

    public static boolean isInventoryFull(String itemName) {
        return Microbot.getClientThread().runOnClientThread(() -> {
            Widget inventoryWidget = getInventory();
            if (hasItemStackable(itemName)) return false;
            return Arrays.stream(inventoryWidget.getDynamicChildren()).filter(x -> !x.isHidden()).count() == 28;
        });
    }

    public static boolean isInventoryEmpty() {
        return hasAmountInventoryItems(0);
    }

    public static boolean hasAmountInventoryItems(int items) {
        Widget inventoryWidget = getInventory();
        return Microbot.getClientThread().runOnClientThread(() -> Arrays.stream(inventoryWidget.getDynamicChildren()).filter(x -> !x.isHidden()).count() == items);
    }

    public static boolean hasItemStackable(String itemName) {
        Widget inventoryWidget = getInventory();

        return Arrays.stream(inventoryWidget.getDynamicChildren())
                .anyMatch(x -> {
                    ItemComposition itemComp = Microbot.getItemManager().getItemComposition(x.getItemId());
                    if (itemComp.getNote() == 799 || itemComp.isStackable()) {
                        return !x.isHidden() && x.getName().split(">")[1].split("</")[0].toLowerCase().equals(itemName.toLowerCase());
                    } else {
                        return false;
                    }
                });
    }

    public static boolean hasItem(String itemName) {
        Widget inventoryWidget = getInventory();

        return Microbot.getClientThread().runOnClientThread(() -> Arrays.stream(inventoryWidget.getDynamicChildren())
                .anyMatch(x ->
                        !x.isHidden() && x.getName().length() > 0 && x.getName().split(">")[1].split("</")[0].toLowerCase().equals(itemName.toLowerCase())
                ));
    }

    public static Widget getInventoryItem(String itemName) {
        Widget inventoryWidget = getInventory();
        return Arrays.stream(inventoryWidget.getDynamicChildren()).filter(x -> !x.isHidden() && x.getName().toLowerCase().contains(itemName)).findFirst().get();
    }

    public static Widget[] getInventoryItems() {
        Widget inventoryWidget = getInventory();
        return Arrays.stream(inventoryWidget.getDynamicChildren()).filter(x -> !x.isHidden()).toArray(Widget[]::new);
    }

    public static Widget[] getInventoryFood() {
        Widget inventoryWidget = getInventory();
        Widget[] items = Arrays.stream(inventoryWidget.getDynamicChildren()).filter(x -> !x.isHidden()).toArray(Widget[]::new);
        items = Arrays.stream(items).filter(x -> Arrays.stream(x.getActions()).anyMatch(c -> c != null && c.toLowerCase().equals("eat"))).toArray(Widget[]::new);
        ;
        return items;
    }

    public static Widget[] getPotions() {
        Widget inventoryWidget = getInventory();
        Widget[] items = Arrays.stream(inventoryWidget.getDynamicChildren()).filter(x -> !x.isHidden()).toArray(Widget[]::new);
        items = Arrays.stream(items).filter(x -> Arrays.stream(x.getActions()).anyMatch(c -> c != null && c.toLowerCase().equals("drink"))).toArray(Widget[]::new);
        return items;
    }

    public static Widget findItemSlot(int index) {
        Widget inventoryWidget = getInventory();
        return inventoryWidget.getChild(index);
    }

    public static Widget findItem(int itemId) {
        Widget inventoryWidget = getInventory();
        for (Widget item : inventoryWidget.getDynamicChildren()) {
            if (item.getItemId() == itemId) {
                return item;
            }
        }
        return null;
    }

    public static boolean hasItemAmount(int itemId, int amount) {
        Tab.switchToInventoryTab();
        Widget inventoryWidget = getInventory();
        int count = 0;
        for (Widget item : inventoryWidget.getDynamicChildren()) {
            if (item.getItemId() == itemId) {
                count++;
            }
        }
        return count >= amount;
    }

    public static boolean hasItemAmount(String itemName, int amount) {
        Tab.switchToInventoryTab();
        Widget inventoryWidget = getInventory();
        int count = 0;
        for (Widget item : inventoryWidget.getDynamicChildren()) {
            if (item.isSelfHidden() == false) {
                if (item.getName().length() > 0 && item.getName().split(">")[1].split("</")[0].toLowerCase().equals(itemName.toLowerCase())) {
                    count++;
                }
            }
        }
        return count >= amount;
    }

    private static Widget getInventory() {
        return Microbot.getClientThread().runOnClientThread(() -> {
            Widget inventoryWidget = Microbot.getClient().getWidget(9764864);
            Widget bankInventoryWidget = Microbot.getClient().getWidget(983043);
            if (inventoryWidget == null && bankInventoryWidget == null) {
                return null;
            }
            if (inventoryWidget.isHidden() && bankInventoryWidget.isHidden()) {
                return null;
            }
            if (inventoryWidget.getDynamicChildren() == null && bankInventoryWidget.getDynamicChildren() == null) {
                return null;
            }

            if (inventoryWidget != null && !inventoryWidget.isHidden())
                return inventoryWidget;
            else
                return bankInventoryWidget;
        });
    }

    public static Widget findItem(String itemName) {
        Widget inventoryWidget = getInventory();
        return Microbot.getClientThread().runOnClientThread(() -> {
            for (Widget item : inventoryWidget.getDynamicChildren()) {
                if (item.isSelfHidden() == false) {
                    if (item.getName().split(">")[1].split("</")[0].toLowerCase().equals(itemName.toLowerCase())) {
                        return item;
                    }
                }
            }
            return null;
        });
    }

    public static Widget findItemContains(String itemName) {
        Widget inventoryWidget = getInventory();
        return Microbot.getClientThread().runOnClientThread(() -> {
            for (Widget item : inventoryWidget.getDynamicChildren()) {
                if (item.isSelfHidden() == false) {
                    if (item.getName().split(">")[1].split("</")[0].toLowerCase().contains(itemName.toLowerCase())) {
                        return item;
                    }
                }
            }
            return null;
        });
    }

    public static void useItemOnItemSlot(int slot1, int slot2) {
        useItemSlot(slot1);
        useItemSlot(slot2);
    }

    public static void useItemOnItemSlot(int slot1, int slot2, int minWait, int maxWait) {
        useItemSlot(slot1);
        useItemSlot(slot2);
        sleep(minWait, maxWait);
    }

    public static boolean useItemSlot(int slot) {
        Widget item = findItemSlot(slot);
        if (item == null) return false;
        Microbot.getMouse().click(item.getBounds().getCenterX(), item.getBounds().getCenterY());
        return true;
    }

    public static boolean useItemContains(String itemName) {
        Widget item = findItemContains(itemName);
        if (item == null) return false;
        Microbot.getMouse().click(item.getBounds().getCenterX(), item.getBounds().getCenterY());
        return true;
    }

    public static boolean useItem(String itemName) {
        Widget item = findItem(itemName);
        if (item == null) return false;
        Microbot.getMouse().click(item.getBounds().getCenterX(), item.getBounds().getCenterY());
        sleep(600, 1200);
        return true;
    }

    public static boolean useItemOnItem(String itemName1, String itemName2) {
        Widget item1 = findItem(itemName1);
        Widget item2 = findItem(itemName2);
        if (item1 == null || item2 == null) return false;
        Microbot.getMouse().click(item1.getBounds().getCenterX(), item1.getBounds().getCenterY());
        sleep(600, 1200);
        Microbot.getMouse().click(item2.getBounds().getCenterX(), item2.getBounds().getCenterY());
        sleep(600, 1200);
        return true;
    }

    public static boolean useItemSafe(String itemName) {
        if (isUsingItem())
            Microbot.getMouse().click();

        sleep(600, 1200);

        return useItem(itemName);
    }

    public static boolean useItemAction(String itemName, String actionName) {
        Widget item = findItem(itemName);
        return Menu.doAction(actionName, new Point((int) item.getBounds().getCenterX(), (int) item.getBounds().getCenterY()));
    }

    public static boolean useItemAction(String itemName, String[] actionName) {
        Widget item = findItem(itemName);
        return Menu.doAction(actionName, new Point((int) item.getBounds().getCenterX(), (int) item.getBounds().getCenterY()));
    }

    public static boolean dropAll() {
        if (inventoryScheduler == null || inventoryScheduler.isDone()) {
            inventoryScheduler = null;
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            inventoryScheduler = scheduledExecutorService.scheduleWithFixedDelay(() -> {
                Microbot.getClientThread().invokeLater(() -> {
                    if (isInventoryEmpty()) {
                        inventoryScheduler.cancel(true);
                        return;
                    }
                    Widget widget = Arrays.stream(getInventoryItems()).filter(x -> !x.isSelfHidden()).findFirst().get();
                    if (widget == null) return;
                    Microbot.getMouse().click(widget.getBounds());
                });
            }, 0, random(10, 300), TimeUnit.MILLISECONDS);
        }
        return isInventoryEmpty();
    }

    public static boolean isUsingItem() {
        return Arrays.stream(Menu.getTargets()).anyMatch(x -> x.contains("->"));
    }

    public static boolean equipItem(String itemName) {
        return useItemAction(itemName, new String[]{"wield", "wear"});
    }

    public static boolean eatItem(String itemName) {
        return useItemAction(itemName, "eat");
    }
}
