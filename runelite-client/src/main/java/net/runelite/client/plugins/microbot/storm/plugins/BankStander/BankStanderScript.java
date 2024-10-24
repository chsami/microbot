package net.runelite.client.plugins.microbot.storm.plugins.BankStander;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import javax.inject.Inject;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static net.runelite.client.plugins.microbot.util.Global.*;
import static net.runelite.client.plugins.microbot.util.Global.sleep;
import static net.runelite.client.plugins.microbot.util.math.Random.random;

public class BankStanderScript extends Script {
    public static long previousItemChange;
    private static final int MAX_TRIES = 3;
    private Set<Integer> recentItems = new HashSet<>();
    @Inject
    private BankStanderConfig config;

    public static double version = 1.0;
    public static CurrentStatus currentStatus = CurrentStatus.FETCH_SUPPLIES;

    public static int itemsProcessed;

    private static Integer firstItemId;
    public static Integer secondItemId;
    private static Integer thirdItemId;
    private static Integer fourthItemId;

    String firstItemIdentifier;
    static String secondItemIdentifier;
    String thirdItemIdentifier;
    String fourthItemIdentifier;

    private int firstItemQuantity;
    private int secondItemQuantity;
    private int thirdItemQuantity;
    private int fourthItemQuantity;

    private int sleepMin;
    private int sleepMax;
    private int sleepTarget;

    public static boolean isWaitingForPrompt=false;
    private static boolean sleep;

    public boolean run(BankStanderConfig config) {
        this.config = config; // Initialize the config object before accessing its parameters
        itemsProcessed = 0;
        // Determine whether the first & second item is the ID or Name.
        firstItemId = TryParseInt(config.firstItemIdentifier());
        secondItemId = TryParseInt(config.secondItemIdentifier());
        thirdItemId = TryParseInt(config.thirdItemIdentifier());
        fourthItemId = TryParseInt(config.fourthItemIdentifier());

        firstItemIdentifier = config.firstItemIdentifier();
        secondItemIdentifier = config.secondItemIdentifier();
        thirdItemIdentifier = config.thirdItemIdentifier();
        fourthItemIdentifier = config.fourthItemIdentifier();

        firstItemQuantity = config.firstItemQuantity();
        secondItemQuantity = config.secondItemQuantity();
        thirdItemQuantity = config.thirdItemQuantity();
        fourthItemQuantity = config.fourthItemQuantity();

        sleepMin = config.sleepMin();
        sleepMax = config.sleepMax();
        sleepTarget = config.sleepTarget();

        // Print the types of firstItemIdentifier and firstItemId
        System.out.println("Type of firstItemIdentifier: " + firstItemIdentifier.getClass().getSimpleName());
        System.out.println("Type of firstItemId: " + (firstItemId != null ? firstItemId.getClass().getSimpleName() : "null"));
        // Print the types of secondItemIdentifier and secondItemId
        System.out.println("Type of secondItemIdentifier: " + secondItemIdentifier.getClass().getSimpleName());
        System.out.println("Type of secondItemId: " + (secondItemId != null ? secondItemId.getClass().getSimpleName() : "null"));
        if(config.fourItems()) {
            // Print the types of thirdItemIdentifier and thirdItemId
            System.out.println("Type of thirdItemIdentifier: " + thirdItemIdentifier.getClass().getSimpleName());
            System.out.println("Type of thirdItemId: " + (thirdItemId != null ? thirdItemId.getClass().getSimpleName() : "null"));
            // Print the types of fourthItemIdentifier and fourthItemId
            System.out.println("Type of fourthItemIdentifier: " + fourthItemIdentifier.getClass().getSimpleName());
            System.out.println("Type of fourthItemId: " + (fourthItemId != null ? fourthItemId.getClass().getSimpleName() : "null"));

        }
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!Microbot.isLoggedIn()) return;
            try {
                //start
                combineItems();

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 10, TimeUnit.MILLISECONDS);
        return true;
    }

    private boolean hasItems() {
        // Check if the player has the required quantity of both items using the configuration
        if (firstItemId != null && secondItemId != null) {
            // User has inputted the item id for both items.
            return Rs2Inventory.hasItem(firstItemId) &&
                    Rs2Inventory.hasItem(secondItemId);
        } else if (firstItemId != null) {
            // User has inputted the item id for the first item and item identifier for the second item.
            //System.out.println("Checking for first item by ID and second item by identifier...");
            return Rs2Inventory.hasItem(firstItemId) &&
                    Rs2Inventory.hasItem(secondItemIdentifier);
        } else if (secondItemId != null) {
            // User has inputted the item id for the second item and item identifier for the first item.
            //System.out.println("Checking for second item by ID and first item by identifier...");
            return Rs2Inventory.hasItem(firstItemIdentifier) &&
                    Rs2Inventory.hasItem(secondItemId);
        } else {
            // User has inputted the item identifier for both items.
            //System.out.println("Checking for items by identifier...");
            return Rs2Inventory.hasItem(firstItemIdentifier) &&
                    Rs2Inventory.hasItem(secondItemIdentifier);
        }
    }


    private boolean fetchItems() {
        if(config.pause()){
            while(this.isRunning() && config.pause()){
                if(!config.pause()){ break; }
                sleep(100,1000);
            }
        }
        if (currentStatus!= CurrentStatus.FETCH_SUPPLIES){ currentStatus= CurrentStatus.FETCH_SUPPLIES; }
        sleep(calculateSleepDuration());
        // Check if we have supplies already by calling hasItems
        if (!hasItems()) {
            if (!Rs2Bank.isOpen()) {
                // Open Bank
                Rs2Bank.openBank();
            }
            sleep = sleepUntilTrue(() -> Rs2Bank.isOpen(), random(67,97), 18000);
            sleep(61,97);
            if (firstItemId != null && secondItemId != null && !config.fourItems()) {
                Rs2Bank.depositAllExcept(firstItemId, secondItemId, thirdItemId, fourthItemId);
            } else if (firstItemId == null && secondItemId == null && !config.fourItems()){
                Rs2Bank.depositAllExcept(firstItemIdentifier, secondItemIdentifier, thirdItemIdentifier, fourthItemIdentifier);
            } else {
                Rs2Bank.depositAll();
            }
            sleep = sleepUntilTrue(() -> !Rs2Inventory.isFull(), 100, 6000);
            sleep(100,300);
            if (!checkBankCount()) { return false; }
            // Check the type of first item identifier
            if (firstItemId != null) {
                // User has inputted the item id for the first item.
                if (Rs2Bank.hasItem(firstItemId) && !Rs2Inventory.hasItem(firstItemId)) {
                    // Withdraw Item 1 Qty
                    Rs2Bank.withdrawX(true, firstItemId, firstItemQuantity);
                    sleep(100,300);
                }
            } else {
                // User has inputted the item identifier for the first item.
                if (Rs2Bank.hasItem(firstItemIdentifier) && !Rs2Inventory.hasItem(firstItemIdentifier)) {
                    // Withdraw Item 1 Qty
                    Rs2Bank.withdrawX(true, firstItemIdentifier, firstItemQuantity);
                    sleep(100,300);
                }
            }
            // Check the type of second item identifier
            if (secondItemId != null) {
                // User has inputted the item id for the second item.
                if (Rs2Bank.hasItem(secondItemId)) {
                    if(!config.withdrawAll()) {
                        // Withdraw Item 2 Qty
                        Rs2Bank.withdrawX(true, secondItemId, secondItemQuantity);
                    } else {
                        Rs2Bank.withdrawAll(secondItemId);
                    }
                }
            } else {
                // User has inputted the item identifier for the second item.
                if (Rs2Bank.hasItem(secondItemIdentifier)) {
                    if(!config.withdrawAll()) {
                        // Withdraw Item 2 Qty
                        Rs2Bank.withdrawX(true, secondItemIdentifier, secondItemQuantity);
                    } else {
                        Rs2Bank.withdrawAll(true, secondItemIdentifier);
                    }
                }
            }

            if(config.fourItems()){
                if (thirdItemId != null) {
                    //TODO check item quantities as well~
                    // User has inputted the item id for the first item.
                    if (Rs2Bank.hasItem(thirdItemId) && !Rs2Inventory.hasItem(thirdItemId)) {
                        // Withdraw Item 3 Qty
                        Rs2Bank.withdrawX(true, thirdItemId, thirdItemQuantity);
                        sleep(100,300);
                    }
                } else {
                    // User has inputted the item identifier for the first item.
                    if (Rs2Bank.hasItem(thirdItemIdentifier)&& !Rs2Inventory.hasItem(thirdItemIdentifier)) {
                        // Withdraw Item 3 Qty
                        Rs2Bank.withdrawX(true, thirdItemIdentifier, thirdItemQuantity);
                        sleep(100,300);
                    }
                }
                // Check the type of second item identifier
                if (fourthItemId != null) {
                    // User has inputted the item id for the fourth item.
                    if (Rs2Bank.hasItem(fourthItemId)) {
                        if(!config.withdrawAll()) {
                            // Withdraw Item 4 Qty
                            Rs2Bank.withdrawX(true, fourthItemId, fourthItemQuantity);
                        } else {
                            Rs2Bank.withdrawAll(fourthItemId);
                        }
                    }
                } else {
                    // User has inputted the item identifier for the fourth item.
                    if (Rs2Bank.hasItem(fourthItemIdentifier)) {
                        if(!config.withdrawAll()) {
                            // Withdraw Item 4 Qty
                            Rs2Bank.withdrawX(true, fourthItemIdentifier, fourthItemQuantity);
                        } else {
                            Rs2Bank.withdrawAll(true, fourthItemIdentifier);
                        }
                    }
                }
            }
            if(!hasItems()) { Rs2Inventory.waitForInventoryChanges(700); }
            if (hasItems()) {
                previousItemChange=(System.currentTimeMillis()-2500);
                // Close Bank
                long bankCloseTime = System.currentTimeMillis();
                while(this.isRunning() && Rs2Bank.isOpen() && (System.currentTimeMillis()-bankCloseTime<32000)) {
                    closeBank();
                    sleep = sleepUntilTrue(() -> !Rs2Bank.isOpen(), random(60, 97), 5000);
                    sleep(calculateSleepDuration() - 10);
                }
                if(Rs2Bank.isOpen()){
                    sleep(calculateSleepDuration());
                    Rs2Player.logout();
                    sleep(calculateSleepDuration());
                }
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
                while(this.isRunning()){
                    if(hasItems()) {
                        break;
                    }
                    sleep(300,3000);
                }
            }
            return false; // Return false to indicate that items are being fetched
        }
        if(config.waitForAnimation()) {
            if (Rs2Player.isAnimating() /*|| Microbot.isGainingExp*/ || (System.currentTimeMillis()-previousItemChange)<2400) { return false; }
        }
        Rs2Item randomItem = null;
        if (config.randomSelection()) {
            // Use item name or ID to get random item
            if (secondItemId != null) {
                randomItem = getRandomItemWithLimit(secondItemId); // Use item ID
            } else if (secondItemIdentifier != null) {
                randomItem = getRandomItemWithLimit(secondItemIdentifier); // Use item name
            }
        }
        if(currentStatus != CurrentStatus.COMBINE_ITEMS) { currentStatus = CurrentStatus.COMBINE_ITEMS; }
        if(config.pause()){
            while(this.isRunning() && config.pause()){
                if(!config.pause()){ break; }
                sleep(100,1000);
            }
        }
        // Combine items based on the type of identifiers
        if (firstItemId != null && secondItemId != null) {
            Rs2Inventory.use(getRandomItemWithLimit(firstItemId)); // Use first Rs2Item (random or not)
            sleep(calculateSleepDuration());

            Rs2Inventory.use(randomItem != null ? randomItem : getRandomItemWithLimit(secondItemId)); // Use second Rs2Item (random or not)

        } else if (firstItemId != null) {
            Rs2Inventory.use(getRandomItemWithLimit(firstItemId)); // Use first Rs2Item (random or not)
            sleep(calculateSleepDuration());

            Rs2Inventory.use(randomItem != null ? randomItem : getRandomItemWithLimit(secondItemIdentifier)); // Use second Rs2Item (random or fallback to name)

        } else if (secondItemId != null) {
            Rs2Inventory.use(getRandomItemWithLimit(firstItemIdentifier)); // Use first Rs2Item (random or not)
            sleep(calculateSleepDuration());

            Rs2Inventory.use(randomItem != null ? randomItem : getRandomItemWithLimit(secondItemId)); // Use second Rs2Item (random or not)

        } else {
            Rs2Inventory.use(getRandomItemWithLimit(firstItemIdentifier)); // Use first Rs2Item (random or not)
            sleep(calculateSleepDuration());

            Rs2Inventory.use(randomItem != null ? randomItem : getRandomItemWithLimit(secondItemIdentifier)); // Use second Rs2Item (random or fallback to name)
        }

        if (config.needMenuEntry()) {
            sleep(calculateSleepDuration());
            isWaitingForPrompt=true;
            // Simulate a key press (e.g., pressing SPACE)
            sleep = sleepUntilTrue(() -> !isWaitingForPrompt, random(7,31), random(800,1200));
            Rs2Keyboard.keyPress(KeyEvent.VK_SPACE);
            previousItemChange=System.currentTimeMillis();
            sleep = sleepUntilTrue(() -> !Rs2Inventory.hasItem(secondItemIdentifier != null ? String.valueOf(secondItemId) : secondItemIdentifier),10, 40000);
        }
        sleep(calculateSleepDuration());
        // Update current status to indicate fetching supplies next
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
    public void closeBank() {
        if (Rs2Bank.isOpen()) {
            Rs2Widget.clickChildWidget(786434, 11);
        }
    }
    public boolean checkBankCount(){
        if(!Rs2Bank.isOpen()){
            Rs2Bank.openBank();
            sleep = sleepUntilTrue(() -> Rs2Bank.isOpen(), random(67,97), 18000);
            sleep(200,600);
        }
        //System.out.println("Attempting to check first item");
        if (firstItemId != null && (Rs2Bank.bankItems.stream().filter(item -> item.id == firstItemId).mapToInt(item -> item.quantity).sum())<config.firstItemQuantity()) {
            return false;
        } else if (firstItemId == null && Rs2Bank.count(firstItemIdentifier)<config.firstItemQuantity()) {
            return false;
        }
        //System.out.println("Attempting to check second item");
        if (secondItemId != null && (Rs2Bank.bankItems.stream().filter(item -> item.id == secondItemId).mapToInt(item -> item.quantity).sum())<config.secondItemQuantity()) {
            return false;
        } else if (secondItemId == null && Rs2Bank.count(secondItemIdentifier)<config.secondItemQuantity()) {
            return false;
        }
        if(config.fourItems()) {
            //System.out.println("Attempting to check third item");
            if (thirdItemId != null && (Rs2Bank.bankItems.stream().filter(item -> item.id == thirdItemId).mapToInt(item -> item.quantity).sum())<config.thirdItemQuantity()) {
                return false;
            } else if (thirdItemId == null && Rs2Bank.count(thirdItemIdentifier) < config.thirdItemQuantity()) {
                return false;
            }
            //System.out.println("Attempting to check fourth item");
            if (fourthItemId != null && (Rs2Bank.bankItems.stream().filter(item -> item.id == fourthItemId).mapToInt(item -> item.quantity).sum())<config.fourthItemQuantity()) {
                return false;
            } else if (fourthItemId == null && Rs2Bank.count(fourthItemIdentifier) < config.fourthItemQuantity()) {
                return false;
            }
        }
        return true;
    }
    // For item ID (int)
    public Rs2Item getRandomItemWithLimit(int itemId) {
        List<Rs2Item> matchingItems = Rs2Inventory.items().stream()
                .filter(item -> item.getId() == itemId)
                .collect(Collectors.toList());

        if (matchingItems.isEmpty()) {
            return null; // No items match the provided ID
        }

        return getRandomItemFromListWithLimit(matchingItems);
    }

    // For item name (String)
    public Rs2Item getRandomItemWithLimit(String itemName) {
        List<Rs2Item> matchingItems = Rs2Inventory.items().stream()
                .filter(item -> item.getName().equalsIgnoreCase(itemName))
                .collect(Collectors.toList());

        if (matchingItems.isEmpty()) {
            return null; // No items match the provided name
        }

        return getRandomItemFromListWithLimit(matchingItems);
    }

    // Common method for random item selection
    private Rs2Item getRandomItemFromListWithLimit(List<Rs2Item> matchingItems) {
        Rs2Item selectedItem = null;
        int tries = 0;

        while (tries < MAX_TRIES) {
            int randomIndex = ThreadLocalRandom.current().nextInt(matchingItems.size());
            selectedItem = matchingItems.get(randomIndex);

            // Ensure the item hasn't been selected recently
            if (!recentItems.contains(selectedItem.getSlot())) {
                break;
            }

            tries++;
        }

        // Update recent item history
        recentItems.add(selectedItem.getSlot());
        if (recentItems.size() > MAX_TRIES) {
            recentItems.remove(0); // Remove the oldest item
        }

        return selectedItem;
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
