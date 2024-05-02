package net.runelite.client.plugins.microbot.util.shop;

import net.runelite.api.ItemComposition;
import net.runelite.api.MenuAction;
import net.runelite.api.NPC;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.grandexchange.Rs2GrandExchange;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.menu.NewMenuEntry;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.awt.*;

import static net.runelite.client.plugins.microbot.util.Global.sleepUntil;
import static net.runelite.client.plugins.microbot.util.Global.sleepUntilOnClientThread;

public class Rs2Shop {
    public static final int SHOP_INVENTORY_ITEM_CONTAINER = 19660800;
    public static final int SHOP_CLOSE_BUTTON = 196960801;


    /**
     * close the shop interface
     */
    public static void closeShop() {
        Microbot.status = "Closing Shop";
        if (!isOpen()) return;
        Rs2Widget.clickChildWidget(19660801, 11);
        sleepUntilOnClientThread(() -> Rs2Widget.getWidget(19660800) == null);
    }

    /**
     * check if the shop screen is open
     *
     * @return
     */
    public static boolean isOpen() {
        Microbot.status = "Checking if Shop is open";
        return Rs2Widget.getWidget(WidgetInfo.SHOP_INVENTORY_ITEMS_CONTAINER) != null;
    }

    /**
     * Opens the shop
     *
     * @return
     */
    public static boolean openShop(String NPC) {
        Microbot.status = "Opening Shop";
        try {
            if (isOpen()) return true;
            NPC npc = Rs2Npc.getNpc(NPC);
            if (npc == null) return false;
            Rs2Npc.interact(npc, "Trade");
            sleepUntil(Rs2GrandExchange::isOpen, 5000);
            return false;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return false;
    }

    /**
     * Sell item to the shop
     *
     * @param rs2Item item to sell
     * @param action  action to invoke on item
     * @return
     */
    public static boolean sellItem(Rs2Item rs2Item, String action) {
        try {
            if (!Rs2Inventory.hasItem(rs2Item.name)) return false;

            switch (action) {
                case "Value":
                    // Logic to check Value of item
                    Rs2Shop.invokeMenu(rs2Item, "Value");
                case "Sell 1":
                    // Logic to sell one item
                    //  Rs2Inventory.invokeMenu(rs2Item, "Sell 1");
                    break;
                case "Sell 5":
                    // Logic to sell five items
                    break;
                case "Sell 10":
                    // Logic to sell ten items
                    break;
                case "Sell 50":
                    // Logic to sell fifty items
                    break;
                default:
                    throw new IllegalArgumentException("Invalid action");
            }

            return true; // Moved inside the try block
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }

    /**
     * Method executes Menu Actions
     *
     * @param rs2Item Current item to Sell
     * @param action  Action on item. Value, Sell 1, Sell 5, Sell 10, Sell 50
     */
    private static void invokeMenu(Rs2Item rs2Item, String action) {
        if (rs2Item == null) return;

        int param0;
        int param1 = 0; // Initialize param1
        int identifier;
        String target;
        MenuAction menuAction = MenuAction.CC_OP;
        ItemComposition itemComposition = Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getItemDefinition(rs2Item.id));
        int index = 0;

        identifier = index;
        param0 = rs2Item.slot;

        // Shop Inventory
        if (action.equalsIgnoreCase("Value")) {
            identifier = 1;
            param1 = 19726336; // Set param1 for "Value" action
        }
        Microbot.doInvoke(new NewMenuEntry(param0, param1, menuAction.getId(), identifier, rs2Item.id, rs2Item.name), new Rectangle(0, 0, 1, 1));
    }

}

