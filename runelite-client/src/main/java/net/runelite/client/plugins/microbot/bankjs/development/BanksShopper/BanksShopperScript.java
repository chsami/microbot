package net.runelite.client.plugins.microbot.bankjs.development.BanksShopper;

import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
// import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.shop.Rs2Shop;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

public class BanksShopperScript extends Script {
    @Inject
    private BanksShopperConfig config;
    @Inject
    private OverlayManager overlayManager;

    public static double version = 1.0;

    public boolean run(BanksShopperConfig config) {
        this.config = config;

        Actions selectedAction = config.action();
        String action = selectedAction.toString();

        Quantities selectedQuantity = config.quantity();
        String quantity = selectedQuantity.toString();

        String npcName = config.npcName();
        String itemName = config.itemName();

        int minimumStock = config.minimumStock();

        Microbot.enableAutoRunOn = false;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                Rs2Shop.openShop(npcName);
                sleep(600);
                if (Rs2Shop.isOpen()) {
                    if (action.equalsIgnoreCase("Buy")) {
                        processBuyAction(itemName, quantity);
                    } else if (action.equalsIgnoreCase("Sell")) {
                        processSellAction(itemName, quantity);
                    } else {
                        System.out.println("Invalid action specified in config.");
                    }

                    // Check if stock drops below the minimum threshold
                    if (Rs2Shop.hasMinimumStock(itemName, minimumStock)) {
                        // Stock level is still above minimum, continue execution
                    } else {
                        // Stock level dropped below minimum, pause or stop execution
                        System.out.println("Stock level dropped below minimum threshold.");
                        // This is where we need to hop worlds.
                    }
                } else {
                    System.out.println("Shop is not open");
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }


    private void processBuyAction(String itemName, String quantity) {
        if (Rs2Shop.hasStock(itemName)) {
            if (Rs2Shop.buyItem(itemName, quantity)) {
                System.out.println("Successfully bought " + quantity + " " + itemName);
            } else {
                System.out.println("Failed to buy " + quantity + " " + itemName);
            }
        } else {
            System.out.println(itemName + " is not in stock");
        }
    }

    private void processSellAction(String itemName, String quantity) {
        if (Rs2Inventory.hasItem(itemName, quantity)) {
            if (Rs2Inventory.sellItem(itemName, quantity)) {
                System.out.println("Successfully sold " + quantity + " " + itemName);
            } else {
                System.out.println("Failed to sell " + quantity + " " + itemName);
            }
        } else {
            System.out.println("Item " + itemName + " not found in inventory.");
        }
    }


}
