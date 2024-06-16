package net.runelite.client.plugins.microbot.lunartanner;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.magic.Rs2Magic;
import net.runelite.client.plugins.skillcalculator.skills.MagicAction;
import net.runelite.client.util.QuantityFormatter;

import java.util.concurrent.TimeUnit;

public class TanLeatherScript extends Script {

    public static String version = "1.0.0";
    static long hidesTanned = 0;
    public static String combinedMessage = "";

    private int profitPerHide = 0; // Profit per single hide
    private long startTime;

    public boolean run(TanLeatherConfig config) {
        startTime = System.currentTimeMillis();
        int unprocessedItemPrice = Microbot.getItemManager().search(config.ITEM().getName()).get(0).getPrice();
        int processedItemPrice = Microbot.getItemManager().search(config.ITEM().getFinished()).get(0).getPrice();
        profitPerHide = processedItemPrice - unprocessedItemPrice;

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            if (!Microbot.isLoggedIn()) return;
            try {
                if (Microbot.pauseAllScripts) return;
                if (Rs2Inventory.hasItem(config.ITEM().getName(), true)) {
                    Rs2Magic.cast(MagicAction.TAN_LEATHER);
                    sleepUntilOnClientThread(() -> Rs2Inventory.hasItem(config.ITEM().getFinished()));
                    hidesTanned++;
                } else {
                    bank(config);
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 300, TimeUnit.MILLISECONDS);
        return true;
    }

    private void calculateProfitAndDisplay(TanLeatherConfig config) {
        double elapsedHours = (System.currentTimeMillis() - startTime) / 3600000.0;
        int hidesPerHour = (int) (hidesTanned / elapsedHours);
        int totalProfit = profitPerHide * (int)hidesTanned;
        int profitPerHour = profitPerHide * hidesPerHour;

        combinedMessage = config.ITEM().getFinished() + ": " +
                QuantityFormatter.quantityToRSDecimalStack((int) hidesTanned) + " (" +
                QuantityFormatter.quantityToRSDecimalStack(hidesPerHour) + "/hr) | " +
                "Profit: " + QuantityFormatter.quantityToRSDecimalStack(totalProfit) + " (" +
                QuantityFormatter.quantityToRSDecimalStack(profitPerHour) + "/hr)";
    }

    private void bank(TanLeatherConfig config) {
        if (!Rs2Bank.isOpen()) {
            Rs2Bank.openBank();
            sleepUntilOnClientThread(Rs2Bank::isOpen);
        }

        Rs2Bank.depositAll(config.ITEM().getFinished());
        hidesTanned += Rs2Inventory.count(config.ITEM().getName());
        sleepUntilOnClientThread(() -> !Rs2Inventory.hasItem(config.ITEM().getFinished()));

        if (Rs2Bank.hasItem(config.ITEM().getName())) {
            Rs2Bank.withdrawAll(config.ITEM().getName());
        } else {
            System.out.print("Out of materials");
            shutdown();
            return;
        }

        Rs2Bank.closeBank();
        sleepUntilOnClientThread(() -> !Rs2Bank.isOpen());
        calculateProfitAndDisplay(config);
    }

    @Override
    public void shutdown() {
        super.shutdown();
        hidesTanned = 0;
    }
}