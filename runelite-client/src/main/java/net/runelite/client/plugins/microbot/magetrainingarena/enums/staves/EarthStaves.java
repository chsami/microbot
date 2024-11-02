package net.runelite.client.plugins.microbot.magetrainingarena.enums.staves;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.ItemID;

@Getter
@AllArgsConstructor
public enum EarthStaves {
    EARTH_STAFF(ItemID.STAFF_OF_EARTH),
    MUD_BATTLESTAFF(ItemID.MUD_BATTLESTAFF),
    MYSTIC_MUD_STAFF(ItemID.MYSTIC_MUD_STAFF),
    DUST_BATTLESTAFF(ItemID.DUST_BATTLESTAFF),
    MYSTIC_DUST_STAFF(ItemID.MYSTIC_DUST_STAFF),
    LAVA_BATTLESTAFF(ItemID.LAVA_BATTLESTAFF),
    MYSTIC_LAVA_STAFF(ItemID.MYSTIC_LAVA_STAFF),
    TOME_OF_EARTH(ItemID.TOME_OF_EARTH);

    private final int itemId;
}
