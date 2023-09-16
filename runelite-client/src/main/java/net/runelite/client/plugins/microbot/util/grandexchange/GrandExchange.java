package net.runelite.client.plugins.microbot.util.grandexchange;

import net.runelite.api.NPC;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.menu.Rs2Menu;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;


import static net.runelite.client.plugins.microbot.util.Global.*;
import static net.runelite.client.plugins.microbot.util.widget.Rs2Widget.getWidgetChildtxt;

public class GrandExchange {
    public static void closeExchange() {
        Microbot.status = "Closing Grand Exchange";
        if (!isOpen()) return;
        Rs2Widget.clickChildWidget(30474242, 11);
        sleepUntilOnClientThread(() -> Rs2Widget.getWidget(30474242) == null);
    }

    public static boolean isOpen() {
        Microbot.status = "Checking if Grand Exchange is open";
        return Rs2Widget.getWidget(30474242) != null;
    }

    public static boolean isBuyOfferOpen() {
        Microbot.status = "Checking if Buy Offer is open";
        return Rs2Widget.getWidgetChildText(30474265, "Buy offer");
    }

    public static boolean isSellOfferOpen() {
        Microbot.status = "Checking if Sell Offer is open";
        return Rs2Widget.getWidgetChildText(30474265, "Sell offer");
    }

    public static boolean openExchange() {
        Microbot.status = "Opening Grand Exchange";
        try {
            if (Inventory.isUsingItem())
                Microbot.getMouse().click();
            if (isOpen()) return true;
            NPC npc = Rs2Npc.getNpc("Grand Exchange Clerk");
            if (npc == null) return false;
            boolean action = Rs2Menu.doAction("exchange", npc.getCanvasTilePoly());
            if (action) {
                sleepUntil(GrandExchange::isOpen, 5000);
                sleep(600, 1000);
                return true;
            }
            return false;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return false;
    }

    public static boolean buyItem(String itemName, String searchTerm, int price, int quantity) {
        try {
            if (!isOpen()) {
                return false;
            }
            Widget buyoffer = Rs2Widget.getWidgetChildSprite(30474247, 1108);
            if (buyoffer != null) {

                System.out.println("found widget");
                Rs2Menu.doAction("Create <col=ff9040>Buy</col> offer", buyoffer.getBounds());
                sleepUntil(() -> Rs2Widget.getWidget(10616874) != null, 5000);
                sleep(350, 500);
                VirtualKeyboard.typeString(searchTerm);
                sleep(800, 1000);
                sleepUntil(() -> Rs2Widget.getWidget(10616882) != null, 5000); //GE Search Results
                sleep(800, 1000);
                Widget item = Rs2Widget.getWidgetChildtxt(10616882, itemName);
                Rs2Menu.doAction("Select", item.getBounds());
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

}
