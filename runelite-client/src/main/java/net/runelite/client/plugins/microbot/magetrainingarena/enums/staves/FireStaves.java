package net.runelite.client.plugins.microbot.magetrainingarena.enums.staves;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.ItemID;

@Getter
@AllArgsConstructor
public enum FireStaves {
    STAFF_OF_FIRE(ItemID.STAFF_OF_FIRE),
    LAVA_BATTLESTAFF(ItemID.LAVA_BATTLESTAFF),
    MYSTIC_LAVA_STAFF(ItemID.MYSTIC_LAVA_STAFF),
    STEAM_BATTLESTAFF(ItemID.STEAM_BATTLESTAFF),
    MYSTIC_STEAM_STAFF(ItemID.MYSTIC_STEAM_STAFF),
    SMOKE_BATTLESTAFF(ItemID.SMOKE_BATTLESTAFF),
    MYSTIC_SMOKE_STAFF(ItemID.MYSTIC_SMOKE_STAFF),
    TOME_OF_FIRE(ItemID.TOME_OF_FIRE);

    private final int itemId;
}
