package net.runelite.client.plugins.microbot.plankmake;

import java.util.concurrent.TimeUnit;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.magic.Rs2Magic;
import net.runelite.client.plugins.skillcalculator.skills.MagicAction;
import net.runelite.client.util.QuantityFormatter;

public class PlankMakeScript extends Script {

    public static String version = "1.0.0";
    public static String combinedMessage = "";
    public static long plankMade = 0;
    private int profitPerPlank = 0;
    private long startTime;

    // State management
    private enum State {
        PLANKING,
        BANKING
    }

    private State currentState = State.PLANKING;

    public boolean run(PlankMakeConfig config) {
        startTime = System.currentTimeMillis();
        int unprocessedItemPrice = Microbot.getItemManager().search(config.ITEM().getName()).get(0).getPrice();
        int processedItemPrice = Microbot.getItemManager().search(config.ITEM().getFinished()).get(0).getPrice();
        profitPerPlank = processedItemPrice - unprocessedItemPrice;

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!super.run() || !Microbot.isLoggedIn()) return;
                if (Rs2Inventory.hasItem(config.ITEM().getName(), true)) {
                    int initialPlankCount = Rs2Inventory.count(config.ITEM().getFinished());
                    Rs2Magic.cast(MagicAction.PLANK_MAKE);
                    sleep(20,50);
                        Rs2Inventory.interact(config.ITEM().getName());

                    // Wait for the inventory count to change indicating Planks have been made
                    while (Rs2Inventory.count(config.ITEM().getFinished()) == initialPlankCount) {
                        // Check the inventory count periodically without sleeping
                        // You can adjust the delay time based on the frequency of checking
                        // This loop will exit once the count changes
                    }

                    int plankMadeThisAction = Rs2Inventory.count(config.ITEM().getFinished()) - initialPlankCount;
                    plankMade += plankMadeThisAction;
                } else {
                    bank(config);
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 50, TimeUnit.MILLISECONDS);
        return true;
    }

    // Calculate the profit and display it
    private void calculateProfitAndDisplay(PlankMakeConfig config) {
        double elapsedHours = (System.currentTimeMillis() - startTime) / 3600000.0;
        int plankPerHour = (int) (plankMade / elapsedHours);
        int totalProfit = profitPerPlank * (int)plankMade;
        int profitPerHour = profitPerPlank * plankPerHour;

        // Format the message
        combinedMessage = config.ITEM().getFinished() + ": " +
                QuantityFormatter.quantityToRSDecimalStack((int) plankMade) + " (" +
                QuantityFormatter.quantityToRSDecimalStack(plankPerHour) + "/hr) | " +
                "Profit: " + QuantityFormatter.quantityToRSDecimalStack(totalProfit) + " (" +
                QuantityFormatter.quantityToRSDecimalStack(profitPerHour) + "/hr)";
    }

    // Bank the finished planks
    private void bank(PlankMakeConfig config) {
        if (currentState != State.BANKING) {
            currentState = State.BANKING;
            if (!Rs2Bank.openBank()) return;

            Rs2Bank.depositAll(config.ITEM().getFinished());
            sleepUntilOnClientThread(() -> !Rs2Inventory.hasItem(config.ITEM().getFinished()));

            if (Rs2Bank.hasItem(config.ITEM().getName())) {
                Rs2Bank.withdrawAll(config.ITEM().getName());
                sleepUntilOnClientThread(() -> Rs2Inventory.hasItem(config.ITEM().getName()));
            } else {
                Microbot.showMessage("No more " + config.ITEM().getName() + " to plank.");
                shutdown();
                return;
            }

            Rs2Bank.closeBank();
            currentState = State.PLANKING;
            calculateProfitAndDisplay(config);
        }
    }

    @Override
    public void shutdown() {
        super.shutdown();
        plankMade = 0; // Reset the count of tanned hides
        combinedMessage = ""; // Reset the combined message
        currentState = State.PLANKING; // Reset the current state
    }
}