package net.runelite.client.plugins.microbot.magetrainingarena.enums.staves;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.ItemID;

@Getter
@AllArgsConstructor
public enum WaterStaves {
    WATER_STAFF(ItemID.STAFF_OF_WATER),
    MYSTIC_WATER_STAFF(ItemID.MYSTIC_WATER_STAFF),
    MUD_BATTLESTAFF(ItemID.MUD_BATTLESTAFF),
    MYSTIC_MUD_BATTLESTAFF(ItemID.MYSTIC_MUD_STAFF),
    MYSTIC_MUD_STAFF(ItemID.MYSTIC_MUD_STAFF);

    private final int itemId;
}
