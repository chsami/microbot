package net.runelite.client.plugins.microbot.bankjs.development.BanksShopper;

import net.runelite.api.GameState;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
// import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.security.Login;
import net.runelite.client.plugins.microbot.util.settings.Rs2Settings;
import net.runelite.client.plugins.microbot.util.shop.Rs2Shop;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.event.KeyEvent;
import java.util.concurrent.TimeUnit;

public class BanksShopperScript extends Script {
    @Inject
    private BanksShopperConfig config;
    @Inject
    private OverlayManager overlayManager;

    public static double version = 1.1;

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
                        Rs2Shop.closeShop();
                        sleep(2400, 4800);
                        // This is where we need to hop worlds.
                        int world = Login.getRandomWorld(true, null);
                        boolean isHopped = Microbot.hopToWorld(world);
                        if (!isHopped) return;
                        boolean result = sleepUntil(() -> Rs2Widget.findWidget("Switch World") != null);
                        if (result) {
                            Rs2Keyboard.keyPress(KeyEvent.VK_SPACE);
                            sleepUntil(() -> Microbot.getClient().getGameState() == GameState.HOPPING);
                            sleepUntil(() -> Microbot.getClient().getGameState() == GameState.LOGGED_IN);
                        }
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
