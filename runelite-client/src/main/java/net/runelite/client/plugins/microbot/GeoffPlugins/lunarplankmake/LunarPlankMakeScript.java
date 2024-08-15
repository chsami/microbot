package net.runelite.client.plugins.microbot.GeoffPlugins.lunarplankmake;

import java.util.concurrent.TimeUnit;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.magic.Rs2Magic;
import net.runelite.client.plugins.skillcalculator.skills.MagicAction;
import net.runelite.client.util.QuantityFormatter;
import net.runelite.client.plugins.microbot.util.math.Random;

public class LunarPlankMakeScript extends Script {

    public static String version = "1.0.1";
    public static String combinedMessage = "";
    public static long plankMade = 0;
    private int profitPerPlank = 0;
    private long startTime;
    private boolean useSetDelay;
    private int setDelay;
    private boolean useRandomDelay;
    private int maxRandomDelay;

    // State management
    private enum State {
        PLANKING,
        BANKING,
        WAITING
    }

    private State currentState = State.PLANKING;

    public boolean run(LunarPlankMakeConfig config) {
        startTime = System.currentTimeMillis();
        int unprocessedItemPrice = Microbot.getItemManager().search(config.ITEM().getName()).get(0).getPrice();
        int processedItemPrice = Microbot.getItemManager().search(config.ITEM().getFinished()).get(0).getPrice();
        profitPerPlank = processedItemPrice - unprocessedItemPrice;

        useSetDelay = config.useSetDelay();
        setDelay = config.setDelay();
        useRandomDelay = config.useRandomDelay();
        maxRandomDelay = config.maxRandomDelay();

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!super.run() || !Microbot.isLoggedIn()) return;
                switch (currentState) {
                    case PLANKING:
                        plankItems(config);
                        break;
                    case BANKING:
                        bank(config);
                        break;
                    case WAITING:
                        waitUntilReady();
                        break;
                }
            } catch (Exception ex) {
                Microbot.log("Exception in LunarPlankMakeScript: " + ex.getMessage());
            }
        }, 0, 50, TimeUnit.MILLISECONDS);
        return true;
    }

    private void plankItems(LunarPlankMakeConfig config) {
        if (Rs2Inventory.hasItem(config.ITEM().getName(), true)) {
            int initialPlankCount = Rs2Inventory.count(config.ITEM().getFinished());
            Rs2Magic.cast(MagicAction.PLANK_MAKE);
            addDelay();
            Rs2Inventory.interact(config.ITEM().getName());

            // Wait for the inventory count to change indicating Planks have been made
            if (waitForInventoryChange(config.ITEM().getFinished(), initialPlankCount)) {
                int plankMadeThisAction = Rs2Inventory.count(config.ITEM().getFinished()) - initialPlankCount;
                plankMade += plankMadeThisAction;
                addDelay();
            } else {
                Microbot.log("Failed to detect plank creation.");
                currentState = State.WAITING;
            }
        } else {
            currentState = State.BANKING;
        }
    }

    private boolean waitForInventoryChange(String itemName, int initialCount) {
        long start = System.currentTimeMillis();
        while (Rs2Inventory.count(itemName) == initialCount) {
            if (System.currentTimeMillis() - start > 3000) { // 3-second timeout
                return false;
            }
            sleep(10);
        }
        return true;
    }

    private void bank(LunarPlankMakeConfig config) {
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

    private void waitUntilReady() {
        sleep(500); // Short sleep before retrying
        currentState = State.PLANKING;
    }

    private void calculateProfitAndDisplay(LunarPlankMakeConfig config) {
        double elapsedHours = (System.currentTimeMillis() - startTime) / 3600000.0;
        int plankPerHour = (int) (plankMade / elapsedHours);
        int totalProfit = profitPerPlank * (int) plankMade;
        int profitPerHour = profitPerPlank * plankPerHour;

        combinedMessage = config.ITEM().getFinished() + ": " +
                QuantityFormatter.quantityToRSDecimalStack((int) plankMade) + " (" +
                QuantityFormatter.quantityToRSDecimalStack(plankPerHour) + "/hr) | " +
                "Profit: " + QuantityFormatter.quantityToRSDecimalStack(totalProfit) + " (" +
                QuantityFormatter.quantityToRSDecimalStack(profitPerHour) + "/hr)";
    }

    private void addDelay() {
        if (useSetDelay) {
            sleep(setDelay);
        } else if (useRandomDelay) {
            sleep(Random.random(0, maxRandomDelay));
        }
    }

    @Override
    public void shutdown() {
        super.shutdown();
        plankMade = 0; // Reset the count of planks made
        combinedMessage = ""; // Reset the combined message
        currentState = State.PLANKING; // Reset the current state
    }
}
