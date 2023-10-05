package net.runelite.client.plugins.nateplugins.moneymaking.natehumidifier;

import net.runelite.api.ItemID;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.magic.Rs2Magic;
import net.runelite.client.plugins.skillcalculator.skills.MagicAction;
import net.runelite.client.util.QuantityFormatter;

import java.util.concurrent.TimeUnit;

import static net.runelite.client.plugins.natepainthelper.Info.timeBegan;

public class HumidifierScript extends Script {

    public static double version = 1.5;
    private static long itemsProcessed = 0;
    public static String itemsProcessedMessage = "";
    public static String profitMessage = "Calculating...";

    private int profit = 0;

    public boolean run(HumidifierConfig config) {
        int unprocessedItemPrice = Microbot.getItemManager().search(config.ITEM().getName()).get(0).getPrice();
        int processedItemPrice = Microbot.getItemManager().search(config.ITEM().getFinished()).get(0).getPrice();
        profit = processedItemPrice - unprocessedItemPrice;
        itemsProcessedMessage = config.ITEM().getFinished() + " processed: " + itemsProcessed;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;

            try {
                boolean hasAstralRunesInInventory = Inventory.hasItem(ItemID.ASTRAL_RUNE);
                if (Microbot.pauseAllScripts) return;
                if (Inventory.hasItem(config.ITEM().getName())
                        && hasAstralRunesInInventory) {
                    Rs2Magic.cast(MagicAction.HUMIDIFY);
                    sleepUntilOnClientThread(() -> Inventory.hasItem(config.ITEM().getFinished()));
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
            itemsProcessed += Inventory.getItemAmount(config.ITEM().getName());
            sleepUntil(() -> !Inventory.hasItem(config.ITEM().getFinished()));
            if (!hasAstralRunes && !Rs2Bank.hasItem(ItemID.ASTRAL_RUNE)) {
                Microbot.showMessage("You have no astral runes left");
                shutdown();
                return;
            }
            if(!Rs2Bank.hasItem(config.ITEM().getName())) {
                Microbot.showMessage("Ran out of Materials");
                shutdown();
                return;
            }

            if (!hasAstralRunes) {
                Rs2Bank.withdrawItemAll(true, "astral rune");
                sleepUntil(() -> Inventory.hasItem(ItemID.ASTRAL_RUNE));
            }

            Rs2Bank.withdrawItemAll(true, config.ITEM().getName());
            sleepUntilOnClientThread(() -> Inventory.hasItem(config.ITEM().getName()));
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
