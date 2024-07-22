package net.runelite.client.plugins.microbot.smelting.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;

import java.util.Map;
@Getter
@RequiredArgsConstructor
public enum Bars {
    BRONZE("bronze bar", 1, Map.of(Ores.COPPER, 1, Ores.TIN, 1)),
    IRON("iron bar", 15, Map.of(Ores.IRON, 1)),
    SILVER("silver bar", 20, Map.of(Ores.SILVER, 1)),
    STEEL("steel bar", 30, Map.of(Ores.IRON, 1, Ores.COAL, 2)),
    GOLD("gold bar", 40, Map.of(Ores.GOLD, 1)),
    MITHRIL("mithril bar", 50, Map.of(Ores.MITHRIL, 1, Ores.COAL, 4)),
    ADAMANTITE("adamantite bar", 70, Map.of(Ores.ADAMANTITE, 1, Ores.COAL, 6)),
    RUNITE("runite bar", 85, Map.of(Ores.RUNITE, 1, Ores.COAL, 8)),;

    private final String name;
    private final int requiredSmithingLevel;
    private final Map<Ores, Integer> requiredMaterials;

    @Override
    public String toString() {
        return name;
    }

    public int maxBarsForFullInventory() {
        int amountForOneBar = requiredMaterials.values().stream().reduce(0, Integer::sum);
        return Rs2Inventory.capacity() / amountForOneBar;
    }
}
