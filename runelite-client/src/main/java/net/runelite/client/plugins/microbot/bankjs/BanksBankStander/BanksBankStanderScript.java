package net.runelite.client.plugins.microbot.bankjs.BanksBankStander;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;

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
        if (firstItemId != null && secondItemId != null) {
            // User has inputted the item id for both items.
            System.out.println("Checking for items by ID...");
            return Rs2Inventory.hasItemAmount(firstItemId, firstItemQuantity) &&
                    Rs2Inventory.hasItemAmount(secondItemId, secondItemQuantity);
        } else if (firstItemId != null) {
            // User has inputted the item id for the first item and item identifier for the second item.
            System.out.println("Checking for first item by ID and second item by identifier...");
            return Rs2Inventory.hasItemAmount(firstItemId, firstItemQuantity) &&
                    Rs2Inventory.hasItemAmount(secondItemIdentifier, secondItemQuantity);
        } else if (secondItemId != null) {
            // User has inputted the item id for the second item and item identifier for the first item.
            System.out.println("Checking for second item by ID and first item by identifier...");
            return Rs2Inventory.hasItemAmount(firstItemIdentifier, firstItemQuantity) &&
                    Rs2Inventory.hasItemAmount(secondItemId, secondItemQuantity);
        } else {
            // User has inputted the item identifier for both items.
            System.out.println("Checking for items by identifier...");
            return Rs2Inventory.hasItemAmount(firstItemIdentifier, firstItemQuantity) &&
                    Rs2Inventory.hasItemAmount(secondItemIdentifier, secondItemQuantity);
        }
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
            // Check the type of first item identifier
            if (firstItemId != null) {
                // User has inputted the item id for the first item.
                if (Rs2Bank.hasItem(firstItemId)) {
                    // Withdraw Item 1 Qty
                    Rs2Bank.withdrawX(firstItemId, firstItemQuantity);
                }
            } else {
                // User has inputted the item identifier for the first item.
                if (Rs2Bank.hasItem(firstItemIdentifier)) {
                    // Withdraw Item 1 Qty
                    Rs2Bank.withdrawX(firstItemIdentifier, firstItemQuantity);
                }
            }
            // Check the type of second item identifier
            if (secondItemId != null) {
                // User has inputted the item id for the second item.
                if (Rs2Bank.hasItem(secondItemId)) {
                    // Withdraw Item 2 Qty
                    Rs2Bank.withdrawX(secondItemId, secondItemQuantity);
                }
            } else {
                // User has inputted the item identifier for the second item.
                if (Rs2Bank.hasItem(secondItemIdentifier)) {
                    // Withdraw Item 2 Qty
                    Rs2Bank.withdrawX(secondItemIdentifier, secondItemQuantity);
                }
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
        sleep(600);

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
