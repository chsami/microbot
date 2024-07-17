package net.runelite.client.plugins.microbot.magetrainingarena.enums.staves;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.ItemID;

@Getter
@AllArgsConstructor
public enum WaterStaves {
    MUD_BATTLESTAFF(ItemID.MUD_BATTLESTAFF),
    MYSTIC_MUD_STAFF(ItemID.MYSTIC_MUD_STAFF);

    private final int itemId;
}
