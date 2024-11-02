package net.runelite.client.plugins.microbot.magetrainingarena.enums.staves;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.ItemID;

@Getter
@AllArgsConstructor
public enum WaterStaves {
    WATER_STAFF(ItemID.STAFF_OF_WATER),
    STEAM_BATTLESTAFF(ItemID.STEAM_BATTLESTAFF),
    MYSTIC_STEAM_STAFF(ItemID.MYSTIC_STEAM_STAFF),
    MIST_BATTLESTAFF(ItemID.MIST_BATTLESTAFF),
    MYSTIC_MIST_STAFF(ItemID.MYSTIC_MIST_STAFF),
    MUD_BATTLESTAFF(ItemID.MUD_BATTLESTAFF),
    MYSTIC_MUD_STAFF(ItemID.MYSTIC_MUD_STAFF),
    TOME_OF_WATER(ItemID.TOME_OF_WATER);

    private final int itemId;
}
