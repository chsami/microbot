package net.runelite.client.plugins.microbot.mixology;

import lombok.AllArgsConstructor;

import java.util.Comparator;
@AllArgsConstructor
public enum PotionOrderSorting {
    VANILLA("Vanilla (random)", null),
    BY_STATION("By station", Comparator.comparing((order) -> order.potionModifier().ordinal()));

    private final String name;
    private final Comparator<PotionOrder> comparator;

    public Comparator<PotionOrder> comparator() {
        return this.comparator;
    }

    public String toString() {
        return this.name;
    }
}
