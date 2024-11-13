package net.runelite.client.plugins.microbot.util.inventory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ItemID;

@RequiredArgsConstructor
public enum RunePouchType {
    STANDARD(ItemID.RUNE_POUCH),
    UPGRADED(ItemID.RUNE_POUCH_23650),
    LARGE(ItemID.RUNE_POUCH_L),
    DIVINE(ItemID.DIVINE_RUNE_POUCH),
    DIVINE_LARGE(ItemID.DIVINE_RUNE_POUCH_L);

    @Getter
    private final int itemId;
}
