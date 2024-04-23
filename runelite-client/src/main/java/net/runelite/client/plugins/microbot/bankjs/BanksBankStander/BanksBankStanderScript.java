package net.runelite.client.plugins.microbot.bankjs.BanksBankStander;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;

import javax.inject.Inject;
import java.awt.event.KeyEvent;
import java.util.concurrent.TimeUnit;
import java.util.Random;

public class BanksBankStanderScript extends Script {
    @Inject
    private BanksBankStanderConfig config;

    public static double version = 1.0;
    private CurrentStatus currentStatus = CurrentStatus.FETCH_SUPPLIES;

    private int firstItemId;
    private int firstItemQuantity;
    private int secondItemId;
    private int secondItemQuantity;
    private int sleepMin;
    private int sleepMax;
    private int sleepTarget;

    public boolean run(BanksBankStanderConfig config) {
        this.config = config; // Initialize the config object before accessing its parameters

        // Initialize other variables
        firstItemId = config.firstItemId();
        firstItemQuantity = config.firstItemQuantity();
        secondItemId = config.secondItemId();
        secondItemQuantity = config.secondItemQuantity();
        sleepMin = config.sleepMin();
        sleepMax = config.sleepMax();
        sleepTarget = config.sleepTarget();

        Microbot.enableAutoRunOn = false;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                //start
                fetchItems();
                combineItems();

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
        return true;
    }

    private boolean hasItems() {
        // Check if the player has the required quantity of both items using the configuration
        return Rs2Inventory.hasItemAmount(firstItemId, firstItemQuantity) &&
                Rs2Inventory.hasItemAmount(secondItemId, secondItemQuantity);
    }


    private boolean fetchItems() {
        // Check if we have supplies already by calling hasItems
        if (!hasItems()) {
            if (!Rs2Bank.isOpen()) {
                // Open Bank
                Rs2Bank.useBank();
                // Deposit All if bank is not already open
                Rs2Bank.depositAll();
            } else {
                // Deposit All if bank is already open
                Rs2Bank.depositAll();
            }
            if (Rs2Bank.hasItem(firstItemId)) {
                // Withdraw Item 1 Qty
                Rs2Bank.withdrawX(firstItemId, firstItemQuantity);
            } else {
                // Microbot.showMessage("Ran out of Item 1!");
            }
            if (Rs2Bank.hasItem(secondItemId)) {
                // Withdraw Item 2 Qty
                Rs2Bank.withdrawX(secondItemId, secondItemQuantity);
            } else {
                // Microbot.showMessage("Ran out of Item 2!");
            }

            // Close Bank
            Rs2Bank.closeBank();
            currentStatus = CurrentStatus.COMBINE_ITEMS; // Set status to COMBINE_ITEMS after fetching items

            sleep(600);
        }

        return true;
    }


    private boolean combineItems() {
        // Check if we have the items, if not, fetch them
        if (!hasItems()) {
            fetchItems();
            return false; // Return false to indicate that items are being fetched
        }

        // Combine the two items together
        Rs2Inventory.combine(firstItemId, secondItemId);

        // Introduce some sleeps for synchronization
        sleep(600);

        // Simulate a key press (e.g., pressing SPACE)
        VirtualKeyboard.keyPress(KeyEvent.VK_SPACE);
        sleep(4000);
        // Sleep until animation is finished or item is no longer in inventory
        sleepUntil(() -> !Microbot.isGainingExp || !Rs2Inventory.hasItem(secondItemId), 30000);

        sleep(calculateSleepDuration());
        // Update current status to indicate fetching supplies next
        currentStatus = CurrentStatus.FETCH_SUPPLIES;

        return true;
    }


    private int calculateSleepDuration() {
        // Create a Random object
        Random random = new Random();

        // Calculate the mean (average) of sleepMin and sleepMax, adjusted by sleepTarget
        double mean = (sleepMin + sleepMax + sleepTarget) / 3.0;

        // Calculate the standard deviation with added noise
        double noiseFactor = 0.2; // Adjust the noise factor as needed (0.0 to 1.0)
        double stdDeviation = Math.abs(sleepTarget - mean) / 3.0 * (1 + noiseFactor * (random.nextDouble() - 0.5) * 2);

        // Generate a random number following a normal distribution
        int sleepDuration;
        do {
            // Generate a random number using nextGaussian method, scaled by standard deviation
            sleepDuration = (int) Math.round(mean + random.nextGaussian() * stdDeviation);
        } while (sleepDuration < sleepMin || sleepDuration > sleepMax); // Ensure the duration is within the specified range

        return sleepDuration;
    }
}
