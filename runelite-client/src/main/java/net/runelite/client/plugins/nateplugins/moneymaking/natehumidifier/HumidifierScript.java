package net.runelite.client.plugins.nateplugins.moneymaking.natehumidifier;

import net.runelite.api.ItemID;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.magic.Rs2Magic;
import net.runelite.client.plugins.skillcalculator.skills.MagicAction;
import net.runelite.client.util.QuantityFormatter;

import java.util.concurrent.TimeUnit;

public class HumidifierScript extends Script {

    public static String version = "1.6.1";
    private static long itemsProcessed = 0;
    public static String itemsProcessedMessage = "";
    public static String profitMessage = "Calculating...";

    private int profit = 0;

    private long timeBegan;

    public boolean run(HumidifierConfig config) {
        timeBegan = System.currentTimeMillis();
        int unprocessedItemPrice = Microbot.getItemManager().search(config.ITEM().getName()).get(0).getPrice();
        int processedItemPrice = Microbot.getItemManager().search(config.ITEM().getFinished()).get(0).getPrice();
        profit = processedItemPrice - unprocessedItemPrice;
        itemsProcessedMessage = config.ITEM().getFinished() + " processed: " + itemsProcessed;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            if (!Microbot.isLoggedIn()) return;
            try {
                boolean hasAstralRunesInInventory = Rs2Inventory.hasItem(ItemID.ASTRAL_RUNE);
                if (Microbot.pauseAllScripts) return;
                if (Rs2Inventory.hasItem(config.ITEM().getName(), true)
                        && hasAstralRunesInInventory) {
                    Rs2Magic.cast(MagicAction.HUMIDIFY);
                    sleepUntilOnClientThread(() -> Rs2Inventory.hasItem(config.ITEM().getFinished()));
                } else {
                    bank(config, hasAstralRunesInInventory);
                    calculateItemsProcessedPerHour(config);
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
        return true;
    }

    private void calculateItemsProcessedPerHour(HumidifierConfig  config) {
        int itemsProcessedPerHour = (int)( itemsProcessed / ((System.currentTimeMillis() - timeBegan) / 3600000.0D));
        itemsProcessedMessage = config.ITEM().getFinished() + " processed (hr): " + QuantityFormatter.quantityToRSDecimalStack((int) itemsProcessed) + " (" + QuantityFormatter.quantityToRSDecimalStack(itemsProcessedPerHour) + ")";
        profitMessage = "profit (hr): " + QuantityFormatter.quantityToRSDecimalStack((int) (profit * itemsProcessed)) + " (" + QuantityFormatter.quantityToRSDecimalStack(profit * itemsProcessedPerHour) + ")";
    }

    private void bank(HumidifierConfig config, boolean hasAstralRunes){
        if(Rs2Bank.isOpen()){
            Rs2Bank.depositAll(config.ITEM().getFinished());
            itemsProcessed += Rs2Inventory.count(config.ITEM().getName());
            sleepUntil(() -> !Rs2Inventory.hasItem(config.ITEM().getFinished()));
            if (!hasAstralRunes && !Rs2Bank.hasItem(ItemID.ASTRAL_RUNE)) {
                Microbot.showMessage("You have no astral runes left");
                shutdown();
                return;
            }
            if(!Rs2Bank.hasBankItem(config.ITEM().getName(), true)) {
                Microbot.showMessage("Ran out of Materials");
                shutdown();
                return;
            }

            if (!hasAstralRunes) {
                Rs2Bank.withdrawAll(true, "astral rune");
                sleepUntil(() -> Rs2Inventory.hasItem(ItemID.ASTRAL_RUNE));
            }

            Rs2Bank.withdrawAll(true, config.ITEM().getName(), true);
            sleepUntilOnClientThread(() -> Rs2Inventory.hasItem(config.ITEM().getName()));
            Rs2Bank.closeBank();
            sleepUntilOnClientThread(() -> !Rs2Bank.isOpen());

        } else {
            Rs2Bank.openBank();
        }
    }

    @Override
    public void shutdown() {
        super.shutdown();
        itemsProcessed = 0;
    }
}
