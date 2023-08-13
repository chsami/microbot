package net.runelite.client.plugins.microbot.cooking.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CookingEnum {
    RAW_LOBSTER("raw lobster", 40, "lobster"),
    RAW_TUNA("raw tuna", 30, "tuna"),
    RAW_SALMON("raw salmon", 25, "salmon"),
    RAW_TROUT("raw trout", 15, "trout"),
    RAW_SHRIMP("raw shrimps", 1, "shrimps");

    private final String rawFoodName;
    private final Integer levelRequired;
    private final String cookedFoodName;

    @Override
    public String toString()
    {
        return rawFoodName;
    }
}
