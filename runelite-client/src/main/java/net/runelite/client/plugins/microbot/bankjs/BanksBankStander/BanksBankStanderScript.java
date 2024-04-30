package net.runelite.client.plugins.microbot.bankjs.BanksBankStander;

import net.runelite.api.ItemComposition;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

import javax.inject.Inject;
import java.awt.event.KeyEvent;
import java.util.concurrent.TimeUnit;
import java.util.Random;

public class BanksBankStanderScript extends Script {
    @Inject
    private BanksBankStanderConfig config;

    public static double version = 1.1;
    private CurrentStatus currentStatus = CurrentStatus.FETCH_SUPPLIES;

    String firstItemIdentifier;
    private int firstItemQuantity;
    private Integer firstItemId;
    String secondItemIdentifier;
    private int secondItemQuantity;
    private Integer secondItemId;
    private int sleepMin;
    private int sleepMax;
    private int sleepTarget;

    public boolean run(BanksBankStanderConfig config) {
        this.config = config; // Initialize the config object before accessing its parameters

        // Initialize other variables
        firstItemIdentifier = config.firstItemIdentifier();
        firstItemQuantity = config.firstItemQuantity();
        secondItemIdentifier = config.secondItemIdentifier();
        secondItemQuantity = config.secondItemQuantity();
        sleepMin = config.sleepMin();
        sleepMax = config.sleepMax();
        sleepTarget = config.sleepTarget();

        // Determine whether the first & second item is the ID or Name.
        firstItemId = TryParseInt(config.firstItemIdentifier());
        secondItemId = TryParseInt(config.secondItemIdentifier());

        // Print the types of firstItemIdentifier and firstItemId
        System.out.println("Type of firstItemIdentifier: " + firstItemIdentifier.getClass().getSimpleName());
        System.out.println("Type of firstItemId: " + (firstItemId != null ? firstItemId.getClass().getSimpleName() : "null"));
        // Print the types of secondItemIdentifier and secondItemId
        System.out.println("Type of secondItemIdentifier: " + secondItemIdentifier.getClass().getSimpleName());
        System.out.println("Type of secondItemId: " + (secondItemId != null ? secondItemId.getClass().getSimpleName() : "null"));

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!Microbot.isLoggedIn()) return;
            try {
                //start
                combineItems();

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
        return true;
    }

    private boolean hasItems() {
        // Check if the player has the required quantity of both items using the configuration
        if (firstItemId != null && secondItemId != null) {
            // User has inputted the item id for both items.
            System.out.println("Checking for items by ID...");
            return Rs2Inventory.hasItem(firstItemId) &&
                    Rs2Inventory.hasItem(secondItemId);
        } else if (firstItemId != null) {
            // User has inputted the item id for the first item and item identifier for the second item.
            System.out.println("Checking for first item by ID and second item by identifier...");
            return Rs2Inventory.hasItem(firstItemId) &&
                    Rs2Inventory.hasItem(secondItemIdentifier);
        } else if (secondItemId != null) {
            // User has inputted the item id for the second item and item identifier for the first item.
            System.out.println("Checking for second item by ID and first item by identifier...");
            return Rs2Inventory.hasItem(firstItemIdentifier) &&
                    Rs2Inventory.hasItem(secondItemId);
        } else {
            // User has inputted the item identifier for both items.
            System.out.println("Checking for items by identifier...");
            return Rs2Inventory.hasItem(firstItemIdentifier) &&
                    Rs2Inventory.hasItem(secondItemIdentifier);
        }
    }


    private boolean fetchItems() {
        // Check if we have supplies already by calling hasItems
        if (!hasItems()) {
            if (!Rs2Bank.isOpen()) {
                // Open Bank
                Rs2Bank.useBank();
            }
            sleepUntil(() -> Rs2Bank.isOpen());
            if (firstItemId != null && secondItemId != null) {
                Rs2Bank.depositAllExcept(firstItemId, secondItemId);
            } else if (firstItemId == null && secondItemId == null){
                Rs2Bank.depositAllExcept(firstItemIdentifier, secondItemIdentifier);
            } else {
                Rs2Bank.depositAll();
            }
            // Check the type of first item identifier
            if (firstItemId != null) {
                // User has inputted the item id for the first item.
                if (Rs2Bank.hasItem(firstItemId)) {
                    // Withdraw Item 1 Qty
                    Rs2Bank.withdrawX(true, firstItemId, firstItemQuantity);
                }
            } else {
                // User has inputted the item identifier for the first item.
                if (Rs2Bank.hasItem(firstItemIdentifier)) {
                    // Withdraw Item 1 Qty
                    Rs2Bank.withdrawX(true, firstItemIdentifier, firstItemQuantity);
                }
            }
            // Check the type of second item identifier
            if (secondItemId != null) {
                // User has inputted the item id for the second item.
                if (Rs2Bank.hasItem(secondItemId)) {
                    // Withdraw Item 2 Qty
                    Rs2Bank.withdrawX(true, secondItemId, secondItemQuantity);
                }
            } else {
                // User has inputted the item identifier for the second item.
                if (Rs2Bank.hasItem(secondItemIdentifier)) {
                    // Withdraw Item 2 Qty
                    Rs2Bank.withdrawX(true, secondItemIdentifier, secondItemQuantity);
                }
            }

            sleepUntil(() -> hasItems());

            if (hasItems()) {
                // Close Bank
                Rs2Bank.closeBank();
                sleepUntil(() -> !Rs2Bank.isOpen());
                currentStatus = CurrentStatus.COMBINE_ITEMS; // Set status to COMBINE_ITEMS after fetching items
                return true;
            }
        }

        return true;
    }

    private boolean combineItems() {
        // Check if we have the items, if not, fetch them
        if (!hasItems()) {
            boolean fetchedItems = fetchItems();
            if (!fetchedItems) {
                Microbot.showMessage("Unsufficient items found.");
                sleep(5000);
            }
            return false; // Return false to indicate that items are being fetched
        }

        if (Microbot.isAnimating() || Microbot.isGainingExp) return false;

        // Combine items based on the type of identifiers
        if (firstItemId != null && secondItemId != null) {
            // If both IDs are not null, use IDs for both items
            Rs2Inventory.use(firstItemId);
            Rs2Inventory.use(secondItemId);
        } else if (firstItemId != null) {
            // If only firstItemId is not null, use it and secondItemIdentifier
            Rs2Inventory.use(firstItemId);
            Rs2Inventory.use(secondItemIdentifier);
        } else if (secondItemId != null) {
            // If only secondItemId is not null, use it and firstItemIdentifier
            Rs2Inventory.use(firstItemIdentifier);
            Rs2Inventory.use(secondItemId);
        } else {
            // If both IDs are null, use identifiers for both items
            Rs2Inventory.use(firstItemIdentifier);
            Rs2Inventory.use(secondItemIdentifier);
        }

        // Introduce some sleeps for synchronization
        sleep(1200);

        // Simulate a key press (e.g., pressing SPACE)
        Rs2Keyboard.keyPress(KeyEvent.VK_SPACE);
        sleep(4000);
        // Sleep until animation is finished or item is no longer in inventory
        sleepUntil(() -> !Rs2Inventory.hasItem(secondItemIdentifier != null ? String.valueOf(secondItemId) : secondItemIdentifier), 40000);

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

    // method to parse string to integer, returns null if parsing fails
    public static Integer TryParseInt(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            System.out.println("Could not Parse Int from Item, using Name Instead");
            return null;
        }
    }
}
