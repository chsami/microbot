package net.runelite.client.plugins.ogPlugins.ogRunecrafting.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ItemID;

@Getter
@RequiredArgsConstructor
public enum Runes {
    LAVA_RUNE("Lava rune",
            ItemID.LAVA_RUNE,
            ItemID.PURE_ESSENCE,
            true,
            ItemID.EARTH_RUNE,
            ItemID.FIRE_RUNE
            );

    private final String name;
    private final int itemID;
    private final int essenceTypeRequired;
    private final boolean combinationRune;
    private final Integer primaryRequiredRune;
    private final Integer secondaryRequiredRune;

    public String getName() {
        return name;
    }

    public int getItemID() {
        return itemID;
    }

    public int getEssenceTypeRequired() {
        return essenceTypeRequired;
    }

    public boolean isCombinationRune() {
        return combinationRune;
    }

    public Integer getPrimaryRequiredRune() {
        return primaryRequiredRune;
    }

    public Integer getSecondaryRequiredRune() {
        return secondaryRequiredRune;
    }
}
