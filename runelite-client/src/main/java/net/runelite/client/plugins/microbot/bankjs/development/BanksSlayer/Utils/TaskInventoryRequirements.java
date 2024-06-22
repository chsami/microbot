package net.runelite.client.plugins.microbot.bankjs.development.BanksSlayer.Utils;

import java.util.List;
import java.util.Map;

public class TaskInventoryRequirements {
    private final Map<Integer, Integer> mandatoryItems;
    private final Map<Integer, Integer> optionalItems;

    public TaskInventoryRequirements(Map<Integer, Integer> mandatoryItems, Map<Integer, Integer> optionalItems) {
        this.mandatoryItems = mandatoryItems;
        this.optionalItems = optionalItems;
    }

    public Map<Integer, Integer> getMandatoryItems() {
        return mandatoryItems;
    }

    public Map<Integer, Integer> getOptionalItems() {
        return optionalItems;
    }
}

