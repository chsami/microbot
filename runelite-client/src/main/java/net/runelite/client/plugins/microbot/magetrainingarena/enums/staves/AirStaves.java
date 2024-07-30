package net.runelite.client.plugins.microbot.magetrainingarena.enums.staves;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.ItemID;

@Getter
@AllArgsConstructor
public enum AirStaves {
    STAFF_OF_AIR(ItemID.STAFF_OF_AIR),
    AIR_BATTLESTAFF(ItemID.AIR_BATTLESTAFF),
    MYSTIC_AIR_STAFF(ItemID.MYSTIC_AIR_STAFF),
    SMOKE_BATTLESTAFF(ItemID.SMOKE_BATTLESTAFF),
    MYSTIC_SMOKE_STAFF(ItemID.MYSTIC_SMOKE_STAFF);

    private final int itemId;
}
