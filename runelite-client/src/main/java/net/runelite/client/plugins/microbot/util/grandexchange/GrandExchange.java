package net.runelite.client.plugins.microbot.util.grandexchange;

import net.runelite.api.NPC;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.menu.Rs2Menu;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static net.runelite.client.plugins.microbot.util.Global.*;
import static net.runelite.client.plugins.microbot.util.widget.Rs2Widget.getWidgetChildtxt;

public class GrandExchange {

    public static final int GRAND_EXCHANGE_OFFER_CONTAINER_QTY_1 = 30474265;
    public static final int GRAND_EXCHANGE_OFFER_CONTAINER_QTY_10 = 30474265;
    public static final int GRAND_EXCHANGE_OFFER_CONTAINER_QTY_100 = 30474265;
    public static final int GRAND_EXCHANGE_OFFER_CONTAINER_QTY_1000 = 30474265;
    public static final int GRAND_EXCHANGE_OFFER_CONTAINER_QTY_X = 30474265;
    public static final int COLLECT_BUTTON = 30474246;


    public static void closeExchange() {
        Microbot.status = "Closing Grand Exchange";
        if (!isOpen()) return;
        Rs2Widget.clickChildWidget(30474242, 11);
        sleepUntilOnClientThread(() -> Rs2Widget.getWidget(30474242) == null);
    }

    public static boolean isOpen() {
        Microbot.status = "Checking if Grand Exchange is open";
        return Rs2Widget.getWidget(WidgetInfo.GRAND_EXCHANGE_WINDOW_CONTAINER) != null;
    }

    public static boolean isOfferOpen() {
        Microbot.status = "Checking if Offer is open";
        return Rs2Widget.getWidget(WidgetInfo.GRAND_EXCHANGE_OFFER_CONTAINER) != null;
    }

    public static boolean openExchange() {
        Microbot.status = "Opening Grand Exchange";
        try {
            if (Inventory.isUsingItem())
                Microbot.getMouse().click();
            if (isOpen()) return true;
            NPC npc = Rs2Npc.getNpc("Grand Exchange Clerk");
            if (npc == null) return false;
            Rs2Npc.interact(npc, "exchange");
            sleepUntil(GrandExchange::isOpen, 5000);
            return false;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return false;
    }

    /**
     *
     * @param itemName
     * @param price
     * @param quantity
     * @return true if item has been bought succesfully
     */
    public static boolean buyItem(String itemName, int price, int quantity) {
        return buyItem(itemName, itemName, price, quantity);
    }

    /**
     *
     * @param itemName
     * @param searchTerm
     * @param price
     * @param quantity
     * @return true if item has been bought succesfully
     */
    public static boolean buyItem(String itemName, String searchTerm, int price, int quantity) {
        try {
            if (!isOpen()) {
                openExchange();
            }
            GrandExchangeSlots availableSlot = getAvailableSlot();
            Widget buyOffer = getOfferBuyButton(availableSlot);
            if (buyOffer != null) {

                System.out.println("found widget");
                Rs2Widget.clickWidgetFast(buyOffer);
                // Rs2Menu.doAction("Create <col=ff9040>Buy</col> offer", buyOffer.getBounds());
                sleepUntil(() -> getOfferText() != null && !getOfferText().isSelfHidden(), 5000);
                sleep(1000);
                VirtualKeyboard.typeString(searchTerm);
                sleep(800, 1000);
                sleepUntil(() -> Rs2Widget.getWidget(10616882) != null, 5000); //GE Search Results
                sleep(800, 1000);
                Pair<Widget, Integer> itemResult = getSearchResultWidget(itemName);
                if (itemResult != null) {
                    Rs2Widget.clickWidgetFast(itemResult.getLeft(), itemResult.getRight());
                    sleep(1000);
                }
                Widget pricePerItemButtonX = getPricePerItemButton_X();
                if (pricePerItemButtonX != null) {
                    System.out.println("tried to click widget");
                    Microbot.getMouse().click(pricePerItemButtonX.getBounds());
                    sleepUntil(() -> Rs2Widget.getWidget(162, 41) != null, 5000); //GE Enter Price
                    sleep(1000);
                    VirtualKeyboard.typeString(Integer.toString(quantity));
                    VirtualKeyboard.enter();
                    sleep(300, 500);
                    if (quantity > 1) {
                        Widget quantityButtonX = getQuantityButton_X();
                        Microbot.getMouse().click(quantityButtonX.getBounds());
                        sleepUntil(() -> Rs2Widget.getWidget(162, 41) != null, 5000); //GE Enter Price/Quantity
                        sleep(600, 1000);
                        VirtualKeyboard.typeString(Integer.toString(price));
                        sleep(500, 750);
                        VirtualKeyboard.enter();
                        sleep(1000);
                    }
                    Microbot.getMouse().click(getConfirm().getBounds());
                    return true;
                } else {
                    System.out.println("unable to find widget setprice.");
                }
            }

            return false;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return false;
    }

    public static boolean sellItem(String itemName, int quantity, int price) {
        try {
            if (!isOpen()) {
                System.out.println("Grand Exchange not open");
                return false;
            }
            Widget item = Rs2Widget.getWidgetChildName(30605312, itemName);
            if (item == null) {
                System.out.println("couldn't find item");
                return false;
            }
            boolean action = Rs2Menu.doAction("offer", item.getBounds());
            System.out.println("tried to click offer");
            sleep(300, 500);
            Widget setqty = getWidgetChildtxt(30474265, "...");
            if (setqty != null) {
                System.out.println("tried to click widget");
                Rs2Menu.doAction("enter quantity", setqty.getBounds());
                sleepUntil(() -> Rs2Widget.getWidget(10616873) != null, 5000); //GE Enter Price
                VirtualKeyboard.typeString(Integer.toString(quantity));
                VirtualKeyboard.enter();
                sleep(300, 500);
                Rs2Widget.clickChildWidget(30474265, 52);
                sleepUntil(() -> Rs2Widget.getWidget(10616873) != null, 5000); //GE Enter Price/Quantity
                sleep(600, 1000);
                VirtualKeyboard.typeString(Integer.toString(price));
                sleep(500, 750);
                VirtualKeyboard.enter();
                sleep(300, 500);
                Rs2Widget.clickChildWidget(30474265, 54); //confirm widget
                return true;
            } else {
                System.out.println("unable to find widget setprice.");
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return false;
    }

    public static boolean collect(boolean collectToBank) {
        if (isAllSlotsEmpty())
        {
            System.out.println("there is nothing to collect");
            return true;
        }
        if (Inventory.isFull()) {
            if (Rs2Bank.useBank()) {
                Rs2Bank.depositAll();
            }
        }
        if (!isOpen()) {
            openExchange();
        }
        sleepUntil(GrandExchange::isOpen);
        Rs2Widget.clickWidgetFast(
                COLLECT_BUTTON, collectToBank ? 2 : 1);
        sleepUntil(() -> Rs2Widget.getWidget(COLLECT_BUTTON).isSelfHidden());
        return Rs2Widget.getWidget(COLLECT_BUTTON).isSelfHidden();
    }

    public static boolean collectToInventory() {
        return collect(false);
    }

    public static boolean collectToBank() {
        return collect(true);
    }

    public static Pair<Widget, Integer> getSearchResultWidget(String search) {
        Widget parent = Microbot.getClient().getWidget(WidgetInfo.CHATBOX_GE_SEARCH_RESULTS);

        if (parent == null || parent.getChildren() == null) return null;

        Widget child = Arrays.stream(parent.getChildren()).filter(x -> x.getText().equalsIgnoreCase(search)).findFirst().orElse(null);

        if (child != null) {
            List<Widget> children = Arrays.stream(parent.getChildren()).collect(Collectors.toList());
            int index = children.indexOf(child);
            int originalWidgetIndex = index - 1;
            return Pair.of(children.get(originalWidgetIndex), originalWidgetIndex);
        }
        return null;
    }

    public static Pair<Widget, Integer> getSearchResultWidget(int itemId) {
        Widget parent = Microbot.getClient().getWidget(WidgetInfo.CHATBOX_GE_SEARCH_RESULTS);

        if (parent == null || parent.getChildren() == null) return null;

        Widget child = Arrays.stream(parent.getChildren()).filter(x -> x.getItemId() == itemId).findFirst().orElse(null);

        if (child != null) {
            List<Widget> children = Arrays.stream(parent.getChildren()).collect(Collectors.toList());
            int index = children.indexOf(child);
            int originalWidgetIndex = index - 2;
            return Pair.of(children.get(originalWidgetIndex), originalWidgetIndex);
        }

        return null;
    }

    private static Widget getOfferContainer() {
        return Microbot.getClient().getWidget(WidgetInfo.GRAND_EXCHANGE_OFFER_CONTAINER);
    }

    public static Widget getQuantityButton_Minus() {

        var parent = getOfferContainer();

        return Optional.ofNullable(parent).map(p -> p.getChild(1)).orElse(null);
    }

    public static Widget getQuantityButton_Plus() {

        var parent = getOfferContainer();

        return Optional.ofNullable(parent).map(p -> p.getChild(2)).orElse(null);
    }

    public static Widget getQuantityButton_1() {

        var parent = getOfferContainer();

        return Optional.ofNullable(parent).map(p -> p.getChild(3)).orElse(null);
    }

    public static Widget getQuantityButton_10() {
        var parent = getOfferContainer();

        return Optional.ofNullable(parent).map(p -> p.getChild(4)).orElse(null);
    }

    public static Widget getQuantityButton_100() {
        var parent = getOfferContainer();

        return Optional.ofNullable(parent).map(p -> p.getChild(5)).orElse(null);
    }

    public static Widget getQuantityButton_1000() {
        var parent = getOfferContainer();

        return Optional.ofNullable(parent).map(p -> p.getChild(6)).orElse(null);
    }

    public static Widget getQuantityButton_X() {
        var parent = getOfferContainer();

        return Optional.ofNullable(parent).map(p -> p.getChild(7)).orElse(null);
    }

    public static Widget getPricePerItemButton_Minus() {
        var parent = getOfferContainer();

        return Optional.ofNullable(parent).map(p -> p.getChild(8)).orElse(null);
    }

    public static Widget getPricePerItemButton_Plus() {
        var parent = getOfferContainer();

        return Optional.ofNullable(parent).map(p -> p.getChild(9)).orElse(null);
    }

    public static Widget getPricePerItemButton_Minus_5Percent() {
        var parent = getOfferContainer();

        return Optional.ofNullable(parent).map(p -> p.getChild(10)).orElse(null);
    }

    public static Widget getPricePerItemButton_GuidePrice() {
        var parent = getOfferContainer();

        return Optional.ofNullable(parent).map(p -> p.getChild(11)).orElse(null);
    }

    public static Widget getPricePerItemButton_X() {
        var parent = getOfferContainer();

        return Optional.ofNullable(parent).map(p -> p.getChild(12)).orElse(null);
    }

    public static Widget getPricePerItemButton_Plus5Percent() {
        var parent = getOfferContainer();

        return Optional.ofNullable(parent).map(p -> p.getChild(13)).orElse(null);
    }

    public static Widget getChooseItem() {
        var parent = getOfferContainer();

        return Optional.ofNullable(parent).map(p -> p.getChild(20)).orElse(null);
    }

    public static Widget getConfirm() {
        var parent = getOfferContainer();

        return Optional.ofNullable(parent).map(p -> p.getChild(54)).orElse(null);
    }

    public static Widget getOfferText() {
        return Microbot.getClient().getWidget(WidgetInfo.GRAND_EXCHANGE_OFFER_TEXT);
    }

    public static Widget getItemPrice() {
        return Microbot.getClient().getWidget(465, 27);
    }

    public static Widget getSlot(GrandExchangeSlots slot) {
        switch (slot) {
            case ONE:
                return Microbot.getClient().getWidget(465, 7);
            case TWO:
                return Microbot.getClient().getWidget(465, 8);
            case THREE:
                return Microbot.getClient().getWidget(465, 9);
            case FOUR:
                return Microbot.getClient().getWidget(465, 10);
            case FIVE:
                return Microbot.getClient().getWidget(465, 11);
            case SIX:
                return Microbot.getClient().getWidget(465, 12);
            case SEVEN:
                return Microbot.getClient().getWidget(465, 13);
            case EIGHT:
                return Microbot.getClient().getWidget(465, 14);
            default:
                return null;
        }
    }

    public static boolean isSlotAvailable(GrandExchangeSlots slot) {
        Widget parent = getSlot(slot);
        return Optional.ofNullable(parent).map(p -> p.getChild(2).isSelfHidden()).orElse(false);
    }

    public static Widget getOfferBuyButton(GrandExchangeSlots slot) {
        Widget parent = getSlot(slot);
        return Optional.ofNullable(parent).map(p -> p.getChild(0)).orElse(null);
    }

    public static Widget getOfferSellButton(GrandExchangeSlots slot) {
        Widget parent = getSlot(slot);
        return Optional.ofNullable(parent).map(p -> p.getChild(1)).orElse(null);
    }

    public static GrandExchangeSlots getAvailableSlot() {
        return Arrays.stream(GrandExchangeSlots.values())
                .filter(GrandExchange::isSlotAvailable)
                .findFirst()
                .orElse(null);
    }

    public static long getAmountAvailableSlots() {
        return Arrays.stream(GrandExchangeSlots.values())
                .filter(GrandExchange::isSlotAvailable)
                .count();
    }

    public static boolean isAllSlotsEmpty() {
        return getAmountAvailableSlots() == Arrays.stream(GrandExchangeSlots.values()).count();
    }
}
