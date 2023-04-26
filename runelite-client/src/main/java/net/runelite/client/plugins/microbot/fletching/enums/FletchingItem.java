package net.runelite.client.plugins.microbot.fletching.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FletchingItem
{
    SHORT("Short bows", '2', "short"),
    LONG("Long bows", '3', "long"),
    STOCK("Crossbow stock", '4', "stock"),
    SHIELD("Shield", '4', "shield");

    private final String name;
    private final char option;
    private final String containsInventoryName;

    @Override
    public String toString()
    {
        return name;
    }
}