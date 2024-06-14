package net.runelite.client.plugins.microbot.bankjs.development.BanksSlayer.Utils;

import java.util.HashMap;
import java.util.Map;

public class TaskInventoryMapping {
    // Define inventory item IDs
    public static final int blackCandle = 32;
    public static final int sapphireLantern = 32;
    public static final int emeraldLantern = 9065;
    public static final int bullseyeLantern = 4550;
    public static final int candle = 33;
    public static final int rope = 954;
    public static final int iceCooler = 6696;

    // Map to store the mapping between task names and required inventory IDs
    private static final Map<String, TaskInventoryRequirements> TASK_INVENTORY_MAPPING = new HashMap<>();

    static {
        // Initialize mandatory items with quantities for Cave Bugs
        Map<Integer, Integer> caveBugsMandatory = new HashMap<>();
        caveBugsMandatory.put(rope, 1);

        // Initialize optional items with quantities for Cave Bugs
        Map<Integer, Integer> caveBugsOptional = new HashMap<>();
        caveBugsOptional.put(candle, 1);
        caveBugsOptional.put(blackCandle, 1);
        caveBugsOptional.put(bullseyeLantern, 1);
        caveBugsOptional.put(emeraldLantern, 1);
        caveBugsOptional.put(sapphireLantern, 1);
        TASK_INVENTORY_MAPPING.put("Cave Bugs", new TaskInventoryRequirements(caveBugsMandatory, caveBugsOptional));

        // Initialize mandatory items with quantities for Cave Slimes, same as Cave Bugs
        TASK_INVENTORY_MAPPING.put("Cave Slimes", new TaskInventoryRequirements(caveBugsMandatory, caveBugsOptional));

        // Initialize mandatory items with quantities for Lizards
        Map<Integer, Integer> lizardsMandatory = new HashMap<>();
        lizardsMandatory.put(iceCooler, 50); // Minimum of 50 ice coolers

        // No optional items for Lizards
        Map<Integer, Integer> lizardsOptional = new HashMap<>();

        TASK_INVENTORY_MAPPING.put("Lizards", new TaskInventoryRequirements(lizardsMandatory, lizardsOptional));

        // Add mappings for other tasks as needed
    }

    // Method to get the inventory requirements for a given task name
    public static TaskInventoryRequirements getInventoryRequirements(String taskName) {
        return TASK_INVENTORY_MAPPING.get(taskName);
    }
}
