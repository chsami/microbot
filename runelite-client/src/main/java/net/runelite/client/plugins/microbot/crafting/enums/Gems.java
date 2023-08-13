package net.runelite.client.plugins.microbot.crafting.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Gems
{
    NONE("NONE", 1),
    OPAL("opal", 1),
    Jade("jade", 13),
    RED_TOPAZ("red topaz", 16),
    SAPPHIRE("sapphire", 20),
    EMERALD("emerald", 27),
    RUBY("ruby", 34),
    DIAMOND("diamond", 43),
    DRAGONSTONE("dragonstone", 55),
    ONYX("onyx", 67),
    ZENYTE("zenyte", 89);

    private final String name;
    private final int levelRequired;

    @Override
    public String toString()
    {
        return name;
    }
}