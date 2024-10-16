package net.runelite.client.plugins.microbot.mining;

import net.runelite.api.GameObject;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;

import java.util.List;
import java.util.Set;

public class CalcifiedMine {

    // Define prioritized and secondary rock IDs
    private final List<Integer> prioritizedMineableIds = List.of(51485, 51491); // High-priority rock IDs
    private final Set<Integer> secondaryMineableIds = Set.of(51488, 51492, 51487); // Lower-priority rock IDs

    public GameObject findPriorityRock() {
        GameObject rock = null;

        // Check for higher-priority rocks first
        for (int rockId : prioritizedMineableIds) {
            rock = Rs2GameObject.findObjectById(rockId, 10); // Search within 10 tiles
            if (rock != null) {
                break; // Stop searching once a high-priority rock is found
            }
        }

        // If no high-priority rocks are found, check for secondary rocks
        if (rock == null) {
            for (int rockId : secondaryMineableIds) {
                rock = Rs2GameObject.findObjectById(rockId, 10);
                if (rock != null) {
                    break;
                }
            }
        }

        return rock;
    }
}
