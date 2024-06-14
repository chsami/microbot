package net.runelite.client.plugins.microbot.bankjs.development.BanksShopper;

import lombok.Getter;
import net.runelite.api.GameState;
import net.runelite.api.ItemID;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.security.Login;
import net.runelite.client.plugins.microbot.util.shop.Rs2Shop;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class BanksShopperScript extends Script {

    public static String version = "1.2.0";

    @Getter
    private static int profit = 0;

    public boolean run(BanksShopperConfig config) {
        Microbot.pauseAllScripts = false;

        Actions selectedAction = config.action();

        Quantities selectedQuantity = config.quantity();
        String quantity = selectedQuantity.toString();

        String npcName = config.npcName();
        String configItemName = config.itemNames();

        List<String> itemNames = Arrays.stream(configItemName.split(","))
                .map(String::trim) // Trim whitespace from each item name
                .collect(Collectors.toList());

        int minimumStock = config.minimumStock();

        Microbot.enableAutoRunOn = false;
        initialPlayerLocation = null;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!super.run()) return;
                if (!Microbot.isLoggedIn()) return;
                if (initialPlayerLocation == null) {
                    initialPlayerLocation = Rs2Player.getWorldLocation();
                }
                if (!Rs2Inventory.hasItem(itemNames.toArray(String[]::new)) && selectedAction == Actions.SELL) {
                    Microbot.status = "[Shutting down] - Reason: Not enough supplies.";
                    Microbot.showMessage(Microbot.status);
                    Rs2Shop.closeShop();
                    if (config.logout()) {
                        sleep(1200);
                        Rs2Player.logout();
                    }
                    shutdown();
                    return;
                }
                if (Rs2Inventory.isFull()) {
                    if (config.useBank()) {
                        Rs2Bank.walkToBankAndUseBank();
                        if (Rs2Bank.isOpen()) {
                            addInventoryToProfit();
                            Rs2Bank.depositAll(x -> itemNames.contains(x.name.toLowerCase()));
                        }
                    }
                    return;
                }

                Rs2Walker.walkTo(getInitialPlayerLocation());
                Rs2Shop.openShop(npcName);
                sleepUntil(() -> Rs2Shop.isOpen(), Random.random(600, 1000));

                boolean allOutOfStock = true;
                boolean successfullAction = false;

                if (Rs2Shop.isOpen()) {

                    for (String itemName : itemNames) {
                        if (Microbot.pauseAllScripts) break;

                        switch(selectedAction) {
                            case BUY:
                                if (!Rs2Shop.hasMinimumStock(itemName, minimumStock)) continue;
                                successfullAction = processBuyAction(itemName, quantity);
                                break;
                            case SELL:
                                if (Rs2Shop.hasMinimumStock(itemName, minimumStock)) continue;
                                successfullAction = processSellAction(itemName, quantity);
                                break;
                            default:
                                System.out.println("Invalid action specified in config.");
                        }

                        allOutOfStock = false;

                        if (successfullAction) {
                            sleep(300, 1200); // this sleep is required to avoid buying items super fast
                        }
                    }

                    if (allOutOfStock) {
                        // All items are out of stock, hop world
                        hopWorld();
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

    @Override
    public void shutdown() {
        super.shutdown();
        Microbot.pauseAllScripts = true;
    }

    /**
     * Hops to a new world
     */
    private void hopWorld() {
        // Stock level dropped below minimum, pause or stop execution
        System.out.println("Stock level dropped below minimum threshold.");
        Rs2Shop.closeShop();
        sleep(2400, 4800); // this sleep is required to avoid the message: please finish what you're doing before using the world switcher.
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


    private boolean processBuyAction(String itemName, String quantity) {
        if (Rs2Inventory.isFull()) {
            System.out.println("Avoid buying item - Inventory is full");
            return false;
        }
        if (Rs2Shop.hasStock(itemName)) {
            boolean boughtItem = Rs2Shop.buyItem(itemName, quantity);
            if (boughtItem) {
                System.out.println("Successfully bought " + quantity + " " + itemName);
            } else {
                System.out.println("Failed to buy " + quantity + " " + itemName);
            }
            return boughtItem;
        } else {
            System.out.println(itemName + " is not in stock");
        }
        return false;
    }

    private boolean processSellAction(String itemName, String quantity) {
        if (Rs2Inventory.hasItem(itemName, quantity)) {
            boolean soldItem = Rs2Inventory.sellItem(itemName, quantity);
            if (soldItem) {
                System.out.println("Successfully sold " + quantity + " " + itemName);
            } else {
                System.out.println("Failed to sell " + quantity + " " + itemName);
            }
            return soldItem;
        } else {
            System.out.println("Item " + itemName + " not found in inventory.");
        }
        return false;
    }

    public static void addInventoryToProfit() {
        for (Rs2Item item : Rs2Inventory.items()) {
            if (item.id == ItemID.COINS_995) continue;
            profit += item.getPrice();
        }
    }
}
