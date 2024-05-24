package net.runelite.client.plugins.microbot.giantsfoundry.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SmithableBars {
    BRONZE_BAR("Bronze bar", "1 Copper ore and 1 Tin ore", 1, 6.2),
    IRON_BAR("Iron bar", "1 Iron ore", 15, 12.5),
    STEEL_BAR("Steel bar", "1 Iron ore and 2 Coal", 30, 17.5),
    MITHRIL_BAR("Mithril bar", "1 Mithril ore and 4 Coal", 50, 30),
    ADAMANT_BAR("Adamantite bar", "1 Adamantite ore and 6 Coal", 70, 37.5),
    RUNE_BAR("Runite bar", "1 Runite ore and 8 Coal", 85, 50);

    private final String name;
    private final String oresNeeded;
    private final int levelNeeded;
    private final double experienceAcquired;
}
