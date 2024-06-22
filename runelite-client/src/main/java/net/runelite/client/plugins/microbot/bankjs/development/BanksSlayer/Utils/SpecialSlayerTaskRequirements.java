package net.runelite.client.plugins.microbot.bankjs.development.BanksSlayer.Utils;

import java.util.HashMap;
import java.util.Map;

public class SpecialSlayerTaskRequirements {
    private static final Map<String, SpecialRequirement> SPECIAL_TASK_REQUIREMENTS = new HashMap<>();

    static {
        SPECIAL_TASK_REQUIREMENTS.put("Lizards", new SpecialRequirement(6696, 4)); // Ice Cooler when below 5 health
        SPECIAL_TASK_REQUIREMENTS.put("Gargoyles", new SpecialRequirement(4155, 9)); // Rock Hammer when below 10 health
        // Add more special task requirements as needed
    }

    public static SpecialRequirement getSpecialRequirement(String taskName) {
        return SPECIAL_TASK_REQUIREMENTS.get(taskName);
    }

    public static class SpecialRequirement {
        private final int itemId;
        private final int healthThreshold;

        public SpecialRequirement(int itemId, int healthThreshold) {
            this.itemId = itemId;
            this.healthThreshold = healthThreshold;
        }

        public int getItemId() {
            return itemId;
        }

        public int getHealthThreshold() {
            return healthThreshold;
        }
    }
}
