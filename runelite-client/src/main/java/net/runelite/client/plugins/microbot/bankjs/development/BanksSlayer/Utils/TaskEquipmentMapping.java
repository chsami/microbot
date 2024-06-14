package net.runelite.client.plugins.microbot.bankjs.development.BanksSlayer.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

public class TaskEquipmentMapping {
    // Define equipment IDs
    public static final int earmuffs = 4166;
    public static final int slayerHelmet = 11864; // Replace with actual ID
    public static final int imbuedSlayerHelmet = 11865; // Replace with actual ID
    public static final int bootsOfStone = 23037;

    // Map to store the mapping between task names and required equipment IDs
    private static final Map<String, TaskEquipmentRequirements> TASK_EQUIPMENT_MAPPING = new HashMap<>();

    static {
        TASK_EQUIPMENT_MAPPING.put("Banshees", new TaskEquipmentRequirements(
                Arrays.asList(), // Mandatory equipment
                Arrays.asList(earmuffs, slayerHelmet, imbuedSlayerHelmet) // Optional equipment (need 1 of these)
        ));

        TASK_EQUIPMENT_MAPPING.put("Wyrms", new TaskEquipmentRequirements(
                Arrays.asList(), // Mandatory equipment
                Arrays.asList(bootsOfStone) // Optional equipment (need 1 of these)
        ));
        // Add mappings for other tasks as needed
    }

    // Method to get the equipment requirements for a given task name
    public static TaskEquipmentRequirements getEquipmentRequirements(String taskName) {
        return TASK_EQUIPMENT_MAPPING.get(taskName);
    }
}
