package net.runelite.client.plugins.nateplugins.natefishing.natefishing.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Fishs {
    SHRIMP("shrimp", new int[] {1530},"net"),
    TROUT("trout", new int[] {1527, 1506},"lure"),
    TUNA("tuna", new int[] {7470, 1519},"harpoon"),
    SHARK("shark", new int[] {10514},"harpoon");

    private final String name;
    private final int[] fishingSpot;
    private final String action;

    @Override
    public String toString() {
        return name;
    }
}
