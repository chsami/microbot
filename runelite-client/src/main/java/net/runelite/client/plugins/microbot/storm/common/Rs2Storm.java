package net.runelite.client.plugins.microbot.storm.common;

import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Rs2Storm {
    private static final int MAX_TRIES = 3;
    private static Set<Integer> recentItems = new HashSet<>();

    public static Rs2Item getRandomItemWithLimit(int itemId) {
        List<Rs2Item> matchingItems = Rs2Inventory.items().stream()
                .filter(item -> item.id == itemId)
                .collect(Collectors.toList());

        if (matchingItems.isEmpty()) {
            return null; // No items match the provided ID
        }

        Rs2Item selectedItem = null;
        int tries = 0;

        while (tries < MAX_TRIES) {
            int randomIndex = ThreadLocalRandom.current().nextInt(matchingItems.size());
            selectedItem = matchingItems.get(randomIndex);

            // Check if the item has been selected recently
            if (!recentItems.contains(selectedItem.getSlot())) {
                break;
            }

            tries++;
        }

        // Update recent items list
        recentItems.add(selectedItem.getSlot());
        if (recentItems.size() > MAX_TRIES) {
            Iterator<Integer> iterator = recentItems.iterator();
            iterator.next();
            iterator.remove(); // Remove the oldest item
        }

        return selectedItem;
    }
}
