package net.runelite.client.plugins.microbot.fletching.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FletchingItem
{
    SHORT("Short bows", '2', "shortbow", 1),
    LONG("Long bows", '3', "longbow", 1),
    STOCK("Crossbow stock", '4', "stock", 1),
    SHIELD("Shield", '4', "shield", 2);

    private final String name;
    private final char option;
    private final String containsInventoryName;
    private final int amountRequired;

    @Override
    public String toString()
    {
        return name;
    }

    public char getOption(FletchingMaterial material, FletchingMode fletchingMode) {
        if (fletchingMode == FletchingMode.STRUNG) return '1';
        //redwood is an exception
        if (material == FletchingMaterial.REDWOOD)
            return '2';
        return option;
    }
}