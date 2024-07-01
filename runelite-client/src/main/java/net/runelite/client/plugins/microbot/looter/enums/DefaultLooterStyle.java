package net.runelite.client.plugins.microbot.looter.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DefaultLooterStyle {
    ITEM_LIST("Item List"),
    GE_PRICE_RANGE("GE Price Range");

    private final String name;
}
