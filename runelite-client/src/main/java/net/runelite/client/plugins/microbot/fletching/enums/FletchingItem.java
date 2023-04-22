package net.runelite.client.plugins.microbot.fletching.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FletchingItem
{
    SHORT("Short bows", '2'),
    LONG("Long bows", '3'),
    STOCK("Crossbow stock", '4'),
    SHIELD("Shield", '4');

    private final String name;
    private final char option;

    @Override
    public String toString()
    {
        return name;
    }
}