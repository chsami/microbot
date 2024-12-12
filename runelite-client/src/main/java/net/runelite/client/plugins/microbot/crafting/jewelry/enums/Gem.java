package net.runelite.client.plugins.microbot.crafting.jewelry.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ItemID;

@Getter
@RequiredArgsConstructor
public enum Gem {
    NONE("", "", 0, 0, 0),
    OPAL("uncut opal", "opal",ItemID.UNCUT_OPAL, ItemID.OPAL, 1),
    JADE("uncut jade", "jade", ItemID.UNCUT_JADE, ItemID.JADE, 13),
    RED_TOPAZ("uncut red topaz", "red topaz",ItemID.UNCUT_RED_TOPAZ, ItemID.RED_TOPAZ, 16),
    SAPPHIRE("uncut sapphire", "sapphire", ItemID.UNCUT_SAPPHIRE, ItemID.SAPPHIRE, 20),
    EMERALD("uncut emerald", "emerald", ItemID.UNCUT_EMERALD, ItemID.EMERALD, 27),
    RUBY("uncut ruby", "ruby", ItemID.UNCUT_RUBY, ItemID.RUBY, 34),
    DIAMOND("uncut diamond", "diamond", ItemID.UNCUT_DIAMOND, ItemID.DIAMOND, 43),
    DRAGONSTONE("uncut dragonstone", "dragonstone", ItemID.UNCUT_DRAGONSTONE, ItemID.DRAGONSTONE, 55),
    ONYX("uncut onyx", "onyx", ItemID.UNCUT_ONYX, ItemID.ONYX, 67),
    ZENYTE("uncut zenyte", "zenyte", ItemID.UNCUT_ZENYTE, ItemID.ZENYTE, 89);
    
    private final String uncutItemName;
    private final String cutItemName;
    private final int uncutItemID;
    private final int cutItemID;
    private final int levelRequired;
}