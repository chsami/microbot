package net.runelite.client.plugins.nateplugins.natefishing.natefishing.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Fishs {
    SHRIMP("shrimp", 1530,"net"),
    TROUT("trout", 1527,"lure");

    private final String name;
    private final int fishingSpot;
    private final String action;

    @Override
    public String toString() {
        return name;
    }
}
